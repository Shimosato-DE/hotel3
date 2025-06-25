package com.example.samurai.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samurai.entity.House;
import com.example.samurai.entity.Reservation;
import com.example.samurai.entity.User;
import com.example.samurai.form.ReservationInputForm;
import com.example.samurai.repository.HouseRepository;
import com.example.samurai.repository.ReservationRepository;
import com.example.samurai.security.UserDetailsImpl;
import com.example.samurai.service.ReservationService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ReservationController {
	private final ReservationService reservationService;
	private final ReservationRepository reservationRepository;
	private final HouseRepository houseRepoistory;

	@GetMapping("/reservations")
	public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
			@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
			Model model) {

		User user = userDetailsImpl.getUser();
		Page<Reservation> reservationPage = reservationRepository.findByUserOrderByCreatedAtDesc(user, pageable);

		model.addAttribute(reservationPage);

		return "reservations/index";

	}

	@GetMapping("/houses/{id}/reservations/input")
	public String input(@PathVariable(name = "id") Integer id,
			@Validated @ModelAttribute ReservationInputForm form,
			BindingResult result, RedirectAttributes redirect, Model model) {

		House house = houseRepoistory.getReferenceById(id);
		Integer numberOfPeople = form.getNumberOfPeople();
		Integer capacity = house.getCapacity();

		if (numberOfPeople != null) {
			
			if (!reservationService.isWithinCapacity(numberOfPeople, capacity)) {
				FieldError fieldError = new FieldError(result.getObjectName(), "numberOfPeople", "宿泊人数が定員を超えています。");
				result.addError(fieldError);
			}

		}

		if (result.hasErrors()) {
			model.addAttribute("house", house);
			model.addAttribute("errorMessage", "予約に不備があります。");
			return "houses/show";

		}

		redirect.addFlashAttribute("reservationInputForm", form);
		return "redirect:/houses/{id}/reservations/confirm";
	
	}
	
//	@PoatMapping("houses/{id}/reservations/confirm")
	
//	@GetMapping("houses/{id}/reservations/confirm")
//	public String confirm(@PathVariable(name = "id") Integer id,
//			@ModelAttribute ReservationInputForm form, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl, HttpServletRequest http, Model model) {
//		
//		House house = houseRepoistory.getReferenceById(id);
//		User user = userDetailsImpl.getUser();
//		
//		//チェックイン日とチェックアウト日を取得
//		LocalDate checkinDate = form.checkinDate();
//		LocalDate checkoutDate = form.checkoutDate();
//		
//		//料金計算
//		Integer amount = reservationService.calculateAmount(checkinDate, checkoutDate, house.getPrice());
//		
//		ReservationRegisterForm registerForm = new ReservationRegisterForm();
//		
//		String sessionId = stripe.createStripeSession()
//		
//	}

}
