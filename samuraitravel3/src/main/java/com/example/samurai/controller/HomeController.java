package com.example.samurai.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.samurai.entity.House;
import com.example.samurai.repository.HouseRepository;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {
	
	private final HouseRepository houseRepository;

	//新着10件の表示
	@GetMapping("/")
	public String index(Model model) {
		
		List<House> newHouses = houseRepository.findTop10ByOrderByCreatedAtDesc();
		
		model.addAttribute("newHouses", newHouses);
		
		return "index";
		
	}
}
