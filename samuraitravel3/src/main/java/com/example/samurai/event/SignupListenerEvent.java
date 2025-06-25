package com.example.samurai.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.samurai.entity.User;
import com.example.samurai.service.VerificationTokenService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SignupListenerEvent {

	private final VerificationTokenService verificationTokenService;
	private final JavaMailSender javaMailsender;

	@EventListener
	private void onSignupEvent(SignupEvent signupEvent) {
		User user = signupEvent.getUser();
		String token = UUID.randomUUID().toString();

		verificationTokenService.create(user, token);

		String recipientAddress = user.getEmail();

		String subject = "メール認証";

		String confirmationUrl = signupEvent.getRequestUrl() + "/verify?token=" + token;

		String message = "以下のリンクをクリックして会員登録を完了してください。";

		SimpleMailMessage mailMessage = new SimpleMailMessage();

		mailMessage.setTo(recipientAddress);
		mailMessage.setSubject(subject);
		mailMessage.setText(message + "\n" + confirmationUrl);
		javaMailsender.send(mailMessage);

	}

}
