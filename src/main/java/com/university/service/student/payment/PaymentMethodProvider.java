package com.university.service.student.payment;

import java.util.Map;

public interface PaymentMethodProvider {

    Map<String, String> getSupportedMethods();

    default boolean supports(String method) {
        return getSupportedMethods().containsKey(method);
    }
}
