package com.pay.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pay.controllers.responses.AccountResponse;
import com.pay.services.AccountService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1/users")
@Tag(name = "Contas", description = "Endpoints para gerenciamento de contas de usuários")
public class AccountController {
    private static final Logger LOG = LoggerFactory.getLogger(AccountController.class); 

    private final AccountService service;

    @Autowired
    public AccountController(AccountService service) {
        this.service = service;
    }

    @GetMapping("/{idUser}/account")
    @Operation(summary = "Retorna dados de uma conta.",
                description = "Retorna uma conta específica com base no ID do usuário e ID da conta.")
    @ApiResponse(responseCode = "200", description = "Operação bem-sucedida")
    @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content)
    @ApiResponse(responseCode = "422", description = "Erro de negócio", content = @Content)
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable("idUser") Long idUser) {
        LOG.debug("[getAccountById] Account ID: {}", idUser);
        AccountResponse account = service.getById(idUser);
        
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }
}
