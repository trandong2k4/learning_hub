package com.university.service.student.payment;

public record PaymentGatewayResult(boolean success, String gatewayReference, String message) {
}
