package com.example.samurai.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samurai.entity.House;
import com.example.samurai.form.HouseEditForm;
import com.example.samurai.form.HouseRegisterForm;
import com.example.samurai.repository.HouseRepository;
import com.example.samurai.service.HouseService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/houses")
public class AdminHouseController {

	private final HouseService houseService;
	private final HouseRepository houseRepository;

	//管理者用民宿検索ページ
	@GetMapping
	public String index(
			@RequestParam(name = "keyword", required = false) String keyword,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {

		Page<House> housePage;

		if (keyword != null && !keyword.isEmpty()) {
			housePage = houseRepository.findByNameLike("%" + keyword + "%", pageable);
		} else {
			housePage = houseRepository.findAll(pageable);
		}

		model.addAttribute("housePage", housePage);
		model.addAttribute("keyword", keyword);

		return "admin/houses/index";
	}

	//idを受取、各詳細ページの作成
	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model) {

		House house = houseRepository.getReferenceById(id);

		model.addAttribute("house", house);

		return "admin/houses/show";
	}

	//民宿登録フォームの表示
	@GetMapping("/register")
	public String register(@ModelAttribute HouseRegisterForm form) {
		return "admin/houses/register";
	}

	//登録登録
	@PostMapping("/create")
	public String create(@Validated @ModelAttribute HouseRegisterForm form, BindingResult result,
			RedirectAttributes redirect, Model model) {

		if (result.hasErrors()) {
			return "admin/houses/register";
		}

		houseService.create(form);
		redirect.addFlashAttribute("successMessage", "民宿を登録しました");

		return "redirect:/admin/houses";
	}

	//編集フォームの表示
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name = "id") Integer id, Model model) {

		House house = houseRepository.getReferenceById(id);

		HouseEditForm form = new HouseEditForm(house.getId(), house.getName(), null, house.getDescription(),
				house.getPrice(), house.getCapacity(), house.getPostalCode(), house.getAddress(),
				house.getPhoneNumber());

		model.addAttribute("houseEditForm", form);

		return "admin/houses/edit";
	}

	//更新処理
	@PostMapping("/{id}/update")
	public String update(@Validated @ModelAttribute HouseEditForm form, BindingResult result,
			RedirectAttributes redirect, Model model) {

		if (result.hasErrors()) {
			return "admin/houses/edit";
		}

		houseService.update(form);
		redirect.addFlashAttribute("successMessage", "民宿を編集しました");

		return "redirect:/admin/houses";

	}

	//削除処理
	@PostMapping("/{id}/delete")
	public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirect) {

		houseRepository.deleteById(id);
		redirect.addFlashAttribute("successMessage", "民宿を削除しました");

		return "redirect:/admin/houses";
	}

}
