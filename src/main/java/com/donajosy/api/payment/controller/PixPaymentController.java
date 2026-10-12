package com.donajosy.api.payment.controller;

import com.donajosy.api.payment.dto.PixChargeResponse;
import com.donajosy.api.payment.service.PixPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PixPaymentController {
    private final PixPaymentService pixPaymentService;

    public PixPaymentController(PixPaymentService pixPaymentService) {
        this.pixPaymentService = pixPaymentService;
    }

    @PostMapping("/pix")
    @ResponseStatus(HttpStatus.CREATED)
    public PixChargeResponse generate(@AuthenticationPrincipal UserDetails userDetails) {
        return pixPaymentService.generateForUser(userDetails.getUsername());
    }
}
