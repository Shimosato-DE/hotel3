package com.example.samurai.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HouseEditForm {
	
	@NotNull
	private Integer id;
	
	@NotBlank(message = "名前を入力してください。")
	private String name;

	private MultipartFile imageFile;

	@NotBlank(message = "説明を記載してください。")
	private String description;

	@NotNull(message = "金額を入力してください。")
	@Min(value = 1, message = "1円以上で入力してください。")
	private Integer price;

	@NotNull(message = "定員を入力してください。")
	@Min(value = 1, message = "1人以上で入力してください。")
	private Integer capacity;

	@NotBlank(message = "郵便番号を入力してください。")
	private String postalCode;

	@NotBlank(message = "住所を入力してください。")
	private String address;

	@NotBlank(message = "電話番号を入力してください。")
	private String phoneNumber;

}
