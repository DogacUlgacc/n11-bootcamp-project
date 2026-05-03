package com.dogac.payment_service.infrastructure.iyzico;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dogac.payment_service.application.port.CardInfo;
import com.dogac.payment_service.application.port.PaymentProviderClient;
import com.dogac.payment_service.application.port.ProviderPaymentResult;
import com.dogac.payment_service.domain.entities.Payment;
import com.iyzipay.Options;
import com.iyzipay.model.Address;
import com.iyzipay.model.BasketItem;
import com.iyzipay.model.BasketItemType;
import com.iyzipay.model.Buyer;
import com.iyzipay.model.Locale;
import com.iyzipay.model.PaymentCard;
import com.iyzipay.model.PaymentChannel;
import com.iyzipay.model.PaymentGroup;
import com.iyzipay.request.CreatePaymentRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class IyzicoPaymentProviderClient implements PaymentProviderClient {

    private final Options options;

    @Override
    public ProviderPaymentResult pay(Payment payment, CardInfo cardInfo) {
        log.info("Iyzico payment request started. paymentId={}, orderId={}, amount={}, currency={}",
                payment.getId().value(),
                payment.getOrderId().value(),
                payment.getMoney().amount(),
                payment.getMoney().currency().getCurrencyCode());
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setLocale(Locale.TR.getValue());
        request.setConversationId(payment.getId().value().toString());

        request.setPrice(payment.getMoney().amount());
        request.setPaidPrice(payment.getMoney().amount());
        request.setCurrency(payment.getMoney().currency().getCurrencyCode());

        request.setInstallment(1);
        request.setBasketId(payment.getOrderId().value().toString());
        request.setPaymentChannel(PaymentChannel.WEB.name());
        request.setPaymentGroup(PaymentGroup.PRODUCT.name());

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardHolderName(cardInfo.cardHolderName());
        paymentCard.setCardNumber(cardInfo.cardNumber());
        paymentCard.setExpireMonth(cardInfo.expireMonth());
        paymentCard.setExpireYear(cardInfo.expireYear());
        paymentCard.setCvc(cardInfo.cvc());
        paymentCard.setRegisterCard(0);
        request.setPaymentCard(paymentCard);

        Buyer buyer = new Buyer();
        buyer.setId(payment.getOrderId().value().toString());
        buyer.setName("Dogac");
        buyer.setSurname("Ulgac");
        buyer.setGsmNumber("+905350000000");
        buyer.setEmail("test@test.com");
        buyer.setIdentityNumber("11111111111");
        buyer.setRegistrationAddress("Gebze Kocaeli");
        buyer.setIp("85.34.78.112");
        buyer.setCity("Kocaeli");
        buyer.setCountry("Turkey");
        request.setBuyer(buyer);

        Address address = new Address();
        address.setContactName("Dogac Ulgac");
        address.setCity("Kocaeli");
        address.setCountry("Turkey");
        address.setAddress("Gebze Kocaeli");

        request.setShippingAddress(address);
        request.setBillingAddress(address);

        BasketItem basketItem = new BasketItem();
        basketItem.setId(payment.getOrderId().value().toString());
        basketItem.setName("Order Payment");
        basketItem.setCategory1("General");
        basketItem.setItemType(BasketItemType.PHYSICAL.name());
        basketItem.setPrice(payment.getMoney().amount());

        request.setBasketItems(List.of(basketItem));

        com.iyzipay.model.Payment iyzicoPayment = com.iyzipay.model.Payment.create(request, options);

        if ("success".equalsIgnoreCase(iyzicoPayment.getStatus())) {
            return new ProviderPaymentResult(
                    true,
                    iyzicoPayment.getPaymentId(),
                    null);
        }
        log.error("Payment failed. paymentId={}, error={}",
                payment.getId().value(),
                iyzicoPayment.getErrorMessage());

        return new ProviderPaymentResult(
                false,
                null,
                iyzicoPayment.getErrorMessage());
    }
}
