package com.jjportal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jjportal.entites.Category;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {

	Category findByCategoryName(String categoryName);

}
