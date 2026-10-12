package com.donajosy.api.payment.service;

import com.donajosy.api.cart.entity.Cart;
import com.donajosy.api.cart.entity.StatusCart;
import com.donajosy.api.cart.repository.CartRepository;
import com.donajosy.api.payment.dto.PixChargeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
public class PixPaymentService {
    private final CartRepository cartRepository;
    private final PixBrCodeService pixBrCodeService;

    public PixPaymentService(CartRepository cartRepository, PixBrCodeService pixBrCodeService) {
        this.cartRepository = cartRepository;
        this.pixBrCodeService = pixBrCodeService;
    }

    @Transactional(readOnly = true)
    public PixChargeResponse generateForUser(String email) {
        List<Cart> items = cartRepository.findAllByUser_EmailAndStatusCart(email, StatusCart.ANDAMENTO);
        if (items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The authenticated user's cart is empty");
        }

        BigDecimal total = items.stream()
                .map(item -> item.getProduct().getPrice())
                .filter(price -> price != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        if (total.signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The cart total must be greater than zero");
        }

        String txid = UUID.randomUUID().toString().replace("-", "").substring(0, 25);
        String brCode = pixBrCodeService.build(total, txid);
        return new PixChargeResponse(
                "PIX",
                total,
                txid,
                new PixChargeResponse.PixData(brCode, brCode)
        );
    }
}
