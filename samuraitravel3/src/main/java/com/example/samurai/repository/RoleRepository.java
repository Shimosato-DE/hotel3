package com.example.samurai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samurai.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	
	public Role findByName(String name);
	
}
