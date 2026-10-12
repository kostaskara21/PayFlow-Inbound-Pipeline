package com.kara.payflow.model;

import java.math.BigDecimal;

public record TransactionCsvRow(
        String transactionId,
        String transactionType,
        String merchantCode,
        String merchantName,
        String customerCode,
        String customerName,
        String paymentMethod,
        String cardBrand,
        String cardLast4,
        BigDecimal amount,
        String currency,
        BigDecimal feeAmount
) {}
