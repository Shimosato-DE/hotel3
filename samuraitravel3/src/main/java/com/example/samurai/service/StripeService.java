package com.example.samurai.service;

import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.samurai.form.ReservationRegisterForm;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeService {

	private final ReservationService reservationService;

	@Value("${stripe.api-key}")
	private String StripeApiKey;

	public String createStripeSession(String houseName, ReservationRegisterForm form, HttpServletRequest http) {

		//Stripe APIの設定
		Stripe.apiKey = StripeApiKey;
		//現在のurlの取得
		String requestUrl = http.getRequestURL().toString();

		SessionCreateParams params = SessionCreateParams.builder()

				.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)

				.addLineItem(SessionCreateParams.LineItem.builder()

						.setPriceData(SessionCreateParams.LineItem.PriceData.builder()

								.setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()

										.setName(houseName).build())

								.setUnitAmount((long) form.getAmount())
								.setCurrency("jpy").build())

						.setQuantity(1L).build())

				.setMode(SessionCreateParams.Mode.PAYMENT)

				.setSuccessUrl(
						requestUrl.replaceAll("/house/[0-9]+/reservations/confirm", "") + "reservations?reserved")
				.setCancelUrl(requestUrl.replace("/reservations/confirm", ""))

				.setPaymentIntentData(SessionCreateParams.PaymentIntentData.builder()
						.putMetadata("houseId", form.getHouseId().toString())
						.putMetadata("userId", form.getUserId().toString())
						.putMetadata("checkinDate", form.getCheckinDate().toString())
						.putMetadata("checkoutDate", form.getCheckoutDate().toString())
						.putMetadata("numberOfPeople", form.getNumberOfPeople().toString())
						.putMetadata("amount", form.getAmount().toString()).build())

				.build();

		try {

			Session session = Session.create(params);
			return session.getId();

		} catch (StripeException e) {

			e.printStackTrace();
			return "";

		}

	}

	public void proccessSessionCompleted(Event event) {

		//イベントからデータをオブジェクト型にして取得
		Optional<StripeObject> optionalStripeObject = event.getDataObjectDeserializer().getObject();

		optionalStripeObject.ifPresent(stripeObject -> {
			Session session = (Session) stripeObject;

			SessionRetrieveParams params = SessionRetrieveParams.builder().addExpand("payment_Intent").build();

			try {
				session = Session.retrieve(session.getId(), params, null);
				Map<String, String> paymentIntentObject = session.getPaymentIntentObject().getMetadata();
				reservationService.create(paymentIntentObject);
				
			} catch (StripeException e) {
				e.printStackTrace();
			}

		});

	}

}
