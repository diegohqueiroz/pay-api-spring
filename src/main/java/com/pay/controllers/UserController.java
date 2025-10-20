package com.pay.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.pay.controllers.requests.UserRequest;
import com.pay.controllers.responses.UserResponse;
import com.pay.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }


    @GetMapping
    @Operation(summary = "Retorna a lista de usuários.",
               description = "Retorna a lista com todos os usuários.")
    @ApiResponse(responseCode = "200", description = "Operação bem-sucedida")
    @ApiResponse(responseCode = "422", description = "Erro de negocio")
    public ResponseEntity<List<UserResponse>> get() {
        LOG.debug("[get]");
        return ResponseEntity
                .ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retorna um usuário.",
        description = "Retorna um usuário específico com base no ID.")
    @ApiResponse(responseCode = "200", description = "Operação bem-sucedida")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    @ApiResponse(responseCode = "422", description = "Erro de negócio")
    public ResponseEntity<UserResponse> get(@PathVariable("id") Long id) {
        LOG.debug("[get] ID: {}", id);
        UserResponse user = service.getById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping
    @Operation(summary = "Cria um novo usuário.",
                description = "Cria um novo usuário no sistema.")
    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso")
    @ApiResponse(responseCode = "422", description = "Erro de validação", content = @Content)
    public ResponseEntity<Long> create(@Valid @RequestBody UserRequest request, UriComponentsBuilder uriBuilder) {
        LOG.debug("[create] Name: {}", request.getName());
        
        Long id = service.create(request);
        
        return ResponseEntity
                .created(uriBuilder.path("/api/v1/users/{id}").buildAndExpand(id).toUri())
                .body(id);
    }

    @PutMapping("/{id}") 
    @Operation(summary = "Atualizar um usuário.",
                description = "Atualizar um usuário específico com base no ID.")
    @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso")
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado para atualizar", content = @Content)
    @ApiResponse(responseCode = "422", description = "Erro de validação", content = @Content)
    public ResponseEntity<Void> update(@Valid @RequestBody UserRequest request, @PathVariable("id") Long id) {
        LOG.debug("[update] ID: {}, Name: {}", id, request.getName());
        
        service.update(request, id);
        
        return ResponseEntity.ok().build(); 
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um usuário.",
                description = "Deletar um usuário específico com base no ID.")
    @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso", content = @Content)
    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    @ApiResponse(responseCode = "422", description = "Erro de validação", content = @Content)
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        LOG.debug("[delete] ID: {}", id);
        
        service.delete(id);
        
        return ResponseEntity.noContent().build();
    }
}
