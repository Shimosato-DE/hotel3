package com.example.samurai.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samurai.entity.User;
import com.example.samurai.form.UserEditForm;
import com.example.samurai.repository.UserRepository;
import com.example.samurai.security.UserDetailsImpl;
import com.example.samurai.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;
	private final UserRepository userRepository;

	@GetMapping
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {

		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		model.addAttribute("user", user);
		
		return "user/index";

	}
	
	@GetMapping("/edit")
	public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
		
		User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
		
		UserEditForm form = new UserEditForm(
				user.getId(),
				user.getName(),
				user.getFurigana(),
				user.getPostalCode(),
				user.getAddress(),
				user.getPhoneNumber(),
				user.getEmail());
		
		model.addAttribute("userEditForm", form);
		
		return "user/edit";
	}
	
	@PostMapping("/update")
	public String update(@Validated  @ModelAttribute UserEditForm form, BindingResult result, RedirectAttributes redirect, Model model) {
		
		if(userService.isEmailChanged(form) && userService.isEmailRegisterd(form.getEmail())) {
			
			FieldError fieldError = new FieldError(result.getObjectName(), "email", "登録済みのメールアドレスです。");
			result.addError(fieldError);
			
		}
		if(result.hasErrors()) {
			return "user/edit";
		}
		
		userService.updata(form);
		redirect.addFlashAttribute("successMessage", "ユーザ情報の編集が完了しました。");
		
		return "redirect:/user";
				
	}

}
