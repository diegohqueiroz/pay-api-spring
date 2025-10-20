package com.pay.services;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pay.controllers.clients.NotificationClient;
import com.pay.controllers.requests.NotificationRequest;
import com.pay.models.NotificationEntity;
import com.pay.models.TransactionEntity;
import com.pay.repositories.NotificationRepository;
import com.pay.repositories.TransactionRepository;

@Service
public class NotificationService {
    private static final Logger LOG = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationClient notificationClient;
    private final TransactionRepository transactionRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationClient notificationClient, TransactionRepository transactionRepository, NotificationRepository notificationRepository) {
        this.notificationClient = notificationClient;
        this.transactionRepository = transactionRepository;
        this.notificationRepository = notificationRepository;
    }

    public NotificationEntity create(String idTransaction){
        TransactionEntity entity = transactionRepository.getReferenceById(idTransaction);
        if (entity == null) {
            throw new RuntimeException("Transação não encontrada");
        }
        NotificationEntity notificationEntity = new NotificationEntity();
        notificationEntity.setCreatedAt(LocalDateTime.now());
        notificationEntity.setUpdatedAt(LocalDateTime.now());
        notificationEntity.setSend(false);
        notificationEntity.setEmail(entity.getAccountDestination().getUser().getEmail());
        notificationEntity.setEmail(entity.getAccountDestination().getUser().getEmail());
        notificationEntity.setTitle("Transferência recebida!");
        notificationEntity.setMessage(String.format("""
            ==============================================
            Transferencia de : %f
            Enviado por: %s
            Recebida com sucesso!
            ==============================================
            Recebido em: %s
            ==============================================
            """, entity.getValue(), entity.getAccountSource().getUser().getName(), entity.getCreatedAt()));
        notificationRepository.save(notificationEntity);
        LOG.info("[create] Criando notificação:" + notificationEntity.getId());
        return notificationEntity;
    }

    public void send(NotificationEntity entity){
        try {
            LOG.info("[send] Enviando notificação:" + entity.getId());
            NotificationRequest notificationRequest = new NotificationRequest(entity.getEmail(), entity.getMessage());
            notificationClient.notify(notificationRequest);
            entity.setSend(true);
            notificationRepository.save(entity);
        } catch (RuntimeException e) {
            entity.setUpdatedAt(LocalDateTime.now());
            notificationRepository.save(entity);            
        }
    }

    public void resend(){
        List<NotificationEntity> notifications = notificationRepository.findBySend(false);
        LOG.debug("[resend] Enviando {} notificações", notifications.size());
        for (NotificationEntity entity : notifications) {
            if (entity.getSend() == false) {
                send(entity);
            }
        }
        LOG.debug("[resend] Enviando {} notificações", notifications.size());
    }

}
