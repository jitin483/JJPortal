package com.jjportal.services;

import org.springframework.stereotype.Repository;

import com.jjportal.entites.Category;
import com.jjportal.payloads.CategoryDTO;
import com.jjportal.payloads.CategoryResponse;

@Repository
public interface CategoryService {

	CategoryDTO createCategory(Category category);

	CategoryResponse getCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

	CategoryDTO updateCategory(Category category, Long categoryId);

	String deleteCategory(Long categoryId);

	CategoryDTO getCategory(Long categoryId);
}
