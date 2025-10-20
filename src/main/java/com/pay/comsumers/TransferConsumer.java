package com.pay.comsumers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.pay.models.NotificationEntity;
import com.pay.services.NotificationService;

@Component
public class TransferConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(TransferConsumer.class);

    private final NotificationService notificationService;

    @Autowired
    public TransferConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @EventListener
    public void handleUserRegistrationAudit(TransactionEvent event) {
        LOG.info("[processTransfer] Event Bus: Recebida requisição para processar transferência: " 
                 + event.idTransaction());
        
        try {
            NotificationEntity notificationEntity = notificationService.create(event.idTransaction()); 

            notificationService.send(notificationEntity);
            LOG.info("[processTransfer] Event Bus: Transferência processada com sucesso. ID: " + event.idTransaction());
            
        } catch (Exception e) {
            LOG.error("[processTransfer] Event Bus: Falha ao processar transferência.", e);
        }
    }
}