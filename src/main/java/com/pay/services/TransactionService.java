package com.pay.services;

import java.math.BigDecimal;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pay.comsumers.TransactionEvent;
import com.pay.controllers.clients.AutorizationClient;
import com.pay.controllers.requests.AutorizationRequest;
import com.pay.controllers.requests.MovimentRequest;
import com.pay.controllers.requests.TransferRequest;
import com.pay.exceptions.TransactionException;
import com.pay.models.AccountEntity;
import com.pay.models.TransactionEntity;
import com.pay.models.enums.TransactionType;
import com.pay.models.enums.UserType;
import com.pay.repositories.AccountRepository;
import com.pay.repositories.TransactionRepository;

import jakarta.validation.Valid;

@Service
public class TransactionService {
    private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);

    private final ApplicationEventPublisher applicationEventPublisher;
    private final AutorizationClient autorizationClient;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(ApplicationEventPublisher applicationEventPublisher, 
            AutorizationClient autorizationClient,
            AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.autorizationClient = autorizationClient;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public String debit(MovimentRequest request){
        final TransactionType transactionType = TransactionType.DEBIT;
        Optional<AccountEntity> accountEntity = accountRepository.findByUserId(request.getAccount());
        validateMoviment(request, accountEntity, transactionType);
        autorization(request.getAccount());

        TransactionEntity transactionEntity = generateTransaction(null, accountEntity.get(), request.getValue(), TransactionType.DEBIT);
        accountEntity.get().setBalance(debitAccount(request.getValue(), accountEntity.get().getBalance()));
        
        transactionRepository.save(transactionEntity);
        accountRepository.save(accountEntity.get());
        return transactionEntity.getId();
    }

    @Transactional
    public String credit(MovimentRequest request){
        final TransactionType transactionType = TransactionType.CREDIT;
        Optional<AccountEntity> accountEntity = accountRepository.findByUserId(request.getAccount());
        validateMoviment(request, accountEntity, transactionType);
        autorization(request.getAccount());

        TransactionEntity transactionEntity = generateTransaction(accountEntity.get(),null, request.getValue(), TransactionType.CREDIT);
        accountEntity.get().setBalance(creditAccount(request.getValue(), accountEntity.get().getBalance()));
        
        transactionRepository.save(transactionEntity);
        accountRepository.save(accountEntity.get());
        return transactionEntity.getId();
    }

    public String transfer(TransferRequest request){
        String id = transferPersistence(request);
        LOG.debug("[transfer] id: " + id);
        applicationEventPublisher.publishEvent(new TransactionEvent(id));
        return id;
    }

    @Transactional
    protected String transferPersistence(@Valid TransferRequest request){
        Optional<AccountEntity> accountEntitySource = accountRepository.findByUserId(request.getPayer());
        Optional<AccountEntity> accountEntityDestination = accountRepository.findByUserId(request.getPayee());
        validate(request, accountEntitySource, accountEntityDestination);
        autorization(request.getPayer());
        TransactionEntity transactionEntity = generateTransaction(accountEntitySource.get(), accountEntityDestination.get(), request.getValue(), TransactionType.TRANSFER);
        accountEntitySource.get().setBalance(debitAccount(request.getValue(), accountEntitySource.get().getBalance()));
        accountEntityDestination.get().setBalance(creditAccount(request.getValue(), accountEntityDestination.get().getBalance()));
        
        transactionRepository.save(transactionEntity);
        accountRepository.save(accountEntitySource.get());
        accountRepository.save(accountEntityDestination.get());

        return transactionEntity.getId();
    }

    protected void validateMoviment(MovimentRequest request, Optional<AccountEntity> accountEntitySource, TransactionType transactionType){
        if (accountEntitySource.isPresent() == false) {
            throw new TransactionException("Conta de origem não encontrada");
        }
        if (TransactionType.DEBIT.equals(transactionType) && accountEntitySource.get().getBalance().compareTo(request.getValue()) < 0) {
            throw new TransactionException("Saldo insuficiente");
        }
    }

    protected void validate(TransferRequest request, Optional<AccountEntity> accountEntitySource, Optional<AccountEntity> accountEntityDestination) {
        if (request.getPayee() == request.getPayer()) {
            throw new TransactionException("Não é possivel transferir para mesma conta");
        }
        if (accountEntitySource.isPresent() == false) {
            throw new TransactionException("Conta de origem não encontrada");
        }
        if (accountEntitySource.get().getUser().getType() == UserType.COMPANY.getCode()){
            throw new TransactionException("Conta não pode realizar transferencia");
        }
        if (accountEntitySource.get().getBalance().compareTo(request.getValue()) < 0) {
            throw new TransactionException("Saldo insuficiente");
        }
        if (accountEntityDestination == null) {
            throw new TransactionException("Conta de destino não encontrada");
        }
    }

    protected BigDecimal debitAccount(BigDecimal valueDebit, BigDecimal balance) {
        return balance.subtract(valueDebit);
    }

    protected BigDecimal creditAccount(BigDecimal valueCredit, BigDecimal balance) {
        return balance.add(valueCredit);
    }

    protected TransactionEntity generateTransaction(AccountEntity accountEntitySource,
            AccountEntity accountEntityDestination, BigDecimal value, TransactionType transactionType) {
        TransactionEntity entity = new TransactionEntity();
        entity.setAccountSource(accountEntitySource);
        entity.setAccountDestination(accountEntityDestination);
        entity.setValue(value);
        entity.setCreatedAt(java.time.LocalDateTime.now());
        entity.setType(transactionType.getCode());
        return entity;
    }

    protected void autorization(Long idAccount){
        try{
            autorizationClient.authorize(new AutorizationRequest(idAccount));
        }catch(RuntimeException e){
            LOG.error("Transferencia não autorizada", e);
            throw new TransactionException("Transferencia não autorizada");
        }
    }

}
