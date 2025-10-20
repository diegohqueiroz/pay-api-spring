package com.pay.controllers.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.pay.controllers.requests.AutorizationRequest;

import feign.Response;

@FeignClient(name = "autorization-api", url = "${autorization-api.base-url}")
public interface AutorizationClient {

    @PostMapping(path = "/authorize", produces = "application/json", consumes = "application/json")
    Response authorize(AutorizationRequest request);
}