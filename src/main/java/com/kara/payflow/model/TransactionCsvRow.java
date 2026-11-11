package com.kara.payflow.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TransactionCsvRow(
        // Transaction
        String transactionId,
        String transactionType,
        BigDecimal amount,
        String currency,
        String status,
        // Merchant
        String merchantCode,
        String merchantName,
        String merchantCountry,
        // Customer
        String customerCode,
        String customerName,
        String customerEmail,
        // Payment
        String paymentMethod,
        String cardBrand,
        String cardLast4,
        String authorizationCode,
        // Adjustment (one per row)
        String adjustmentType,
        BigDecimal adjustmentAmount,
        String adjustmentCurrency,
        // TransactionStatusHistory (one per row)
        String statusHistoryStatus,
        String statusHistoryTimestamp
) {}
