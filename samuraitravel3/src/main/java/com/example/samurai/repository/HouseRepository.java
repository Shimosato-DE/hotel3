package com.example.samurai.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samurai.entity.House;

public interface HouseRepository extends JpaRepository<House, Integer> {
	
	//民宿名検索
	public Page<House> findByNameLike(String keyword, Pageable pageable);
	
	//民宿名or所在地検索
	//新着順
	public Page<House> findByNameLikeOrAddressLikeOrderByCreatedAtDesc(String nameKeyword, String addressKeyword, Pageable pageable);
	//最安順
	public Page<House> findByNameLikeOrAddressLikeOrderByPriceAsc(String nameKeywod, String addressKeyword, Pageable pageable);
	
	//所在地検索
	//新着順
	public Page<House> findByAddressLikeOrderByCreatedAtDesc(String area, Pageable pageable);
	//最安順
	public Page<House> findByAddressLikeOrderByPriceAsc(String area, Pageable pageable);
	
	//料金ソート
	//新着順
	public Page<House> findByPriceLessThanEqualOrderByCreatedAtDesc(Integer price, Pageable pageable);
	//最安順
	public Page<House> findByPriceLessThanEqualOrderByPriceAsc(Integer price, Pageable pageable);
	
	//全検索
	//新着順
	public Page<House> findAllByOrderByCreatedAtDesc(Pageable pageable);
	//最安順
	public Page<House> findAllByOrderByPriceAsc(Pageable pageable);
	
	//top10検索
	public List<House> findTop10ByOrderByCreatedAtDesc();
}
