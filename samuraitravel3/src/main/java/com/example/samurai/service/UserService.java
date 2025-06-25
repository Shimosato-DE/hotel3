package com.example.samurai.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samurai.entity.Role;
import com.example.samurai.entity.User;
import com.example.samurai.form.SignupForm;
import com.example.samurai.form.UserEditForm;
import com.example.samurai.repository.RoleRepository;
import com.example.samurai.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public User create(SignupForm form) {

		User user = new User();
		Role role = roleRepository.findByName("ROLE_GENERAL");

		user.setName(form.getName());
		user.setFurigana(form.getFurigana());
		user.setPostalCode(form.getPostalCode());
		user.setAddress(form.getAddress());
		user.setPhoneNumber(form.getPhoneNumber());
		user.setEmail(form.getEmail());
		user.setPassword(passwordEncoder.encode(form.getPassword()));
		user.setRole(role);
		user.setEnabled(false);

		return userRepository.save(user);

	}

	@Transactional
	public void updata(UserEditForm form) {

		User user = userRepository.getReferenceById(form.getId());

		user.setName(form.getName());
		user.setFurigana(form.getFurigana());
		user.setPostalCode(form.getPostalCode());
		user.setAddress(form.getAddress());
		user.setPhoneNumber(form.getPhoneNumber());
		user.setEmail(form.getEmail());

		userRepository.save(user);
	}

	// メールアドレスが登録済みかどうかをチェックする
	public boolean isEmailRegisterd(String email) {

		User user = userRepository.findByEmail(email);
		return user != null;

	}

	// パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
	public boolean isSamePassword(String password, String PasswordConfirmation) {
		return password.equals(PasswordConfirmation);
	}

	// ユーザーを有効にする
	public void enabledUser(User user) {
		user.setEnabled(true);
		userRepository.save(user);
	}

	// メールアドレスが変更されたかどうかをチェックする

	public boolean isEmailChanged(UserEditForm form) {
		User currentUser = userRepository.getReferenceById(form.getId());
		return !form.getEmail().equals(currentUser.getEmail());
	}
}
