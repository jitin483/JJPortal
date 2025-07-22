package com.jjportal.services;

import com.jjportal.payloads.AddressDTO;
import com.jjportal.payloads.UserDTO;
import com.jjportal.payloads.UserResponse;

public interface UserService {
	UserDTO registerUser(UserDTO userDTO);
	
	UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
	
	UserDTO getUserById(Long userId);
	
	UserDTO updateUser(Long userId, UserDTO userDTO);
	
	String deleteUser(Long userId);

	UserDTO updateUserAddress(Long userId, AddressDTO addressDTO);
}
