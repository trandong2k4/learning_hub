package com.university.service.student.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class StaticPaymentMethodProvider implements PaymentMethodProvider {

    private final Map<String, String> supportedMethods;

    public StaticPaymentMethodProvider(
            @Value("${student.payment.methods:VI_DIEN_TU=Vi dien tu (MoMo/ZaloPay/VNPay),CHUYEN_KHOAN=Chuyen khoan ngan hang,THE_NOI_DIA=The noi dia ATM (phi 3000 VND),THE_QUOC_TE=The quoc te Visa/Mastercard (phi 5000 VND)}")
            String configuredMethods) {
        this.supportedMethods = parseConfiguredMethods(configuredMethods);
    }

    @Override
    public Map<String, String> getSupportedMethods() {
        return supportedMethods;
    }

    private Map<String, String> parseConfiguredMethods(String configuredMethods) {
        Map<String, String> methods = new LinkedHashMap<>();

        Arrays.stream(configuredMethods.split(","))
                .map(String::trim)
                .filter(entry -> !entry.isBlank() && entry.contains("="))
                .forEach(entry -> {
                    String[] parts = entry.split("=", 2);
                    methods.put(parts[0].trim().toUpperCase(), parts[1].trim());
                });

        if (methods.isEmpty()) {
            throw new IllegalStateException("Khong co phuong thuc thanh toan nao duoc cau hinh");
        }

        return Collections.unmodifiableMap(methods);
    }
}
