package com.donajosy.api.payment.dto;

import java.math.BigDecimal;

public record PixChargeResponse(
        String billingType,
        BigDecimal total,
        String txid,
        PixData pix
) {
    public record PixData(String brCode, String copyPaste) {}
}
