package com.example.samurai.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.samurai.entity.House;
import com.example.samurai.form.HouseEditForm;
import com.example.samurai.form.HouseRegisterForm;
import com.example.samurai.repository.HouseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HouseService {

	private final HouseRepository houseRepository;

	@Transactional
	public void create(HouseRegisterForm form) {

		House house = new House();
		MultipartFile imageFile = form.getImageFile();

		if (!imageFile.isEmpty()) {
			String imageName = imageFile.getOriginalFilename();
			String hashedImageName = generateNewFileName(imageName);

			Path filePath = Paths.get("src/main/resources/static/storage/" + hashedImageName);
			copyImageFile(imageFile, filePath);

			house.setImageName(hashedImageName);

		}

		house.setName(form.getName());
		house.setDescription(form.getDescription());
		house.setPrice(form.getPrice());
		house.setCapacity(form.getCapacity());
		house.setPostalCode(form.getPostalCode());
		house.setAddress(form.getAddress());
		house.setPhoneNumber(form.getPhoneNumber());

		houseRepository.save(house);

	}

	@Transactional
	public void update(HouseEditForm form) {

		House house = houseRepository.getReferenceById(form.getId());
		MultipartFile imageFile = form.getImageFile();

		if (!imageFile.isEmpty()) {
			String imageFileName = imageFile.getOriginalFilename();
			String hashedImageFileName = generateNewFileName(imageFileName);

			Path imageFilePath = Paths.get("src/main/resources/static/storage/" + hashedImageFileName);
			copyImageFile(imageFile, imageFilePath);

			house.setImageName(hashedImageFileName);

		}

		house.setName(form.getName());
		house.setDescription(form.getDescription());
		house.setPrice(form.getPrice());
		house.setCapacity(form.getCapacity());
		house.setPostalCode(form.getPostalCode());
		house.setAddress(form.getAddress());
		house.setPhoneNumber(form.getPhoneNumber());

		houseRepository.save(house);

	}

	public String generateNewFileName(String imageName) {

		String[] imageNames = imageName.split("\\.");
		for (int i = 0; i < imageNames.length - 1; i++) {
			imageNames[i] = UUID.randomUUID().toString();
		}

		String hashedImageName = String.join(".", imageNames);
		return hashedImageName;

	}

	public void copyImageFile(MultipartFile imageFile, Path filePath) {

		try {

			Files.copy(imageFile.getInputStream(), filePath);

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

}