package com.example.samurai.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samurai.entity.User;
import com.example.samurai.entity.VerificationToken;
import com.example.samurai.event.SignupEventPublisher;
import com.example.samurai.form.SignupForm;
import com.example.samurai.service.UserService;
import com.example.samurai.service.VerificationTokenService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;
	private final SignupEventPublisher signupEventPublisher;
	private final VerificationTokenService verificationTokenService;

	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}

	//会員登録画面の表示
	@GetMapping("/signup")
	public String signup(@ModelAttribute SignupForm form) {
		return "/auth/signup";
	}

	@PostMapping("/signup")
	public String signup(@Validated @ModelAttribute SignupForm form, BindingResult bindingResult,
			RedirectAttributes redirect, HttpServletRequest http,
			Model model) {

		// メールアドレスが登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
		if (userService.isEmailRegisterd(form.getEmail())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
			bindingResult.addError(fieldError);
		}

		// パスワードとパスワード（確認用）の入力値が一致しなければ、BindingResultオブジェクトにエラー内容を追加する
		if (!userService.isSamePassword(form.getPassword(), form.getPasswordConfirmation())) {
			FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "パスワードが一致しません。");
			bindingResult.addError(fieldError);
		}

		if (bindingResult.hasErrors()) {
			return "auth/signup";
		}

		User createdUser = userService.create(form);
		String requestUrl = new String(http.getRequestURL());
		signupEventPublisher.publicSignupevent(createdUser, requestUrl);
		redirect.addFlashAttribute("successMessage",
				"\"ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。\"");

		return "redirect:/";

	}

    @GetMapping("/signup/verify")
    public String verify(@RequestParam(name = "token") String token, Model model) {
        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
        
        if (verificationToken != null) {
            User user = verificationToken.getUser();  
            userService.enabledUser(user);
            String successMessage = "会員登録が完了しました。";
            model.addAttribute("successMessage", successMessage);            
        } else {
            String errorMessage = "トークンが無効です。";
            model.addAttribute("errorMessage", errorMessage);
        }
        
        return "auth/verify";         
    }    

}
