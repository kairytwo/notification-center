package com.cdfholding.notificationcenter.controller;

import com.cdfholding.notificationcenter.dto.AllowedUserApplyRequest;
import com.cdfholding.notificationcenter.dto.AllowedUserApplyResponse;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    KafkaTemplate<String, AllowedUserApplyRequest> kafkaTemplate;

    public AdminController(KafkaTemplate<String, AllowedUserApplyRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping(path = "/apply")
    public AllowedUserApplyResponse apply(@RequestBody AllowedUserApplyRequest request) {
        request.setType("apply");
        kafkaTemplate.send("allowed-user-command", request.getAdUser(), request);
        return new AllowedUserApplyResponse("cdfh3593", "success", "none");
    }
}
