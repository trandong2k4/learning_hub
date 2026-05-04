package com.university.service.student.payment;

import com.university.entity.HocPhi;

public interface PaymentGatewayPort {

    PaymentGatewayResult processPayment(HocPhi hocPhi, String method);
}
