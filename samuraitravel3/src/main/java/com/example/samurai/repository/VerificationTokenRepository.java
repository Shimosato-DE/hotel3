package com.example.samurai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samurai.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Integer>{

	public VerificationToken findByToken(String token);
	
}
