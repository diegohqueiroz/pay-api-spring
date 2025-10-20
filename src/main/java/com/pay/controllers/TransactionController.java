package com.pay.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.pay.controllers.requests.MovimentRequest;
import com.pay.controllers.requests.TransferRequest;
import com.pay.services.TransactionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transações", description = "Endpoints para movimentações financeiras")
public class TransactionController {
    
    private static final Logger LOG = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    @Operation(summary = "Realizar uma transferência.",
               description = "Realizar uma nova transferência entre contas.")
    @ApiResponse(responseCode = "201", description = "Transferência efetuada com sucesso")
    @ApiResponse(responseCode = "422", description = "Erro de validação", content = @Content)
    public ResponseEntity<String> transfer(@Valid @RequestBody TransferRequest request) {
        LOG.info("[transfer] Recebida requisição de transferência.");
        LOG.debug("[transfer] Valor: {}, Pagador (ID): {}, Beneficiário (ID): {}", 
        request.getValue(), request.getPayer(), request.getPayee());
        
        String id = transactionService.transfer(request);

        return ResponseEntity
                .created(UriComponentsBuilder.fromPath("/api/transactions/{id}").build(id))
                .body(id);
    }

    @PostMapping("/debit")
    @Operation(summary = "Realizar um saque.",
               description = "Realizar um saque da conta.")
    @ApiResponse(responseCode = "201", description = "Saque efetuado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro na requisição", content = @Content)
    @ApiResponse(responseCode = "422", description = "Erro de validação", content = @Content)
    public ResponseEntity<String> debit(@Valid @RequestBody MovimentRequest request) {
        LOG.info("[debit] Recebida requisição de débito.");
        LOG.debug("[debit] Valor: {}, Conta de débito (ID): {}", request.getValue(), request.getAccount());

        String id = transactionService.debit(request);

        return ResponseEntity
                .created(UriComponentsBuilder.fromPath("/api/transactions/{id}").build(id))
                .body(id);
    }

    @PostMapping("/credit")
    @Operation(summary = "Realizar um depósito.",
               description = "Realizar um depósito na conta.")
    @ApiResponse(responseCode = "201", description = "Depósito efetuado com sucesso")
    @ApiResponse(responseCode = "400", description = "Erro na requisição", content = @Content)
    @ApiResponse(responseCode = "422", description = "Erro de validação", content = @Content)
    public ResponseEntity<String> credit(@Valid @RequestBody MovimentRequest request) {
        LOG.info("[credit] Recebida requisição de crédito.");
        LOG.debug("[credit] Valor: {}, Conta de crédito (ID): {}", request.getValue(), request.getAccount());

        String id = transactionService.credit(request);

        return ResponseEntity
                .created(UriComponentsBuilder.fromPath("/api/transactions/{id}").build(id))
                .body(id);
    }
}