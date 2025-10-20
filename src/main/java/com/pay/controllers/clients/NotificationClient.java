package com.pay.controllers.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.pay.controllers.requests.NotificationRequest;

@FeignClient(name = "notification-client", url = "${notification-api.base-url}")
public interface NotificationClient {

    @PostMapping(path = "/notify", produces = "text/plain", consumes = "application/json")
    void notify(@RequestBody NotificationRequest request);
}