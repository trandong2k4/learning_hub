package com.university.service.student.payment;

import com.university.entity.HocPhi;
import com.university.enums.HocPhiEnum;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@Component
public class MockPaymentGatewayAdapter implements PaymentGatewayPort {

    @Override
    public PaymentGatewayResult processPayment(HocPhi hocPhi, String method) {
        if (hocPhi.getTrangThai() == HocPhiEnum.QUA_HAN && "THE_NOI_DIA".equals(method)) {
            return new PaymentGatewayResult(
                    false,
                    null,
                    "Thanh toán thất bại: thẻ nội địa không hỗ trợ hóa đơn quá hạn");
        }

        String gatewayReference = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        return new PaymentGatewayResult(true, gatewayReference, "Thanh toán thành công");
    }
}
