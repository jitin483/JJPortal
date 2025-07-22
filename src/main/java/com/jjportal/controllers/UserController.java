package com.jjportal.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jjportal.config.AppConstants;
import com.jjportal.entites.User;
import com.jjportal.exceptions.ResourceNotFoundException;
import com.jjportal.payloads.AddressDTO;
import com.jjportal.payloads.UserDTO;
import com.jjportal.payloads.UserResponse;
import com.jjportal.repositories.UserRepo;
import com.jjportal.services.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;




 
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder; 
  

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "jjportal")
 
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private ModelMapper modelMapper;
	
	
	
	
	
	@GetMapping("/admin/users")
	public ResponseEntity<UserResponse> getUsers(
			@RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_USERS_BY, required = false) String sortBy,
			@RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
		
		UserResponse userResponse = userService.getAllUsers(pageNumber, pageSize, sortBy, sortOrder);
		
		return new ResponseEntity<UserResponse>(userResponse, HttpStatus.FOUND);
	}
	
	@GetMapping("/public/users/{userId}")
	public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
		UserDTO user = userService.getUserById(userId);
		
		return new ResponseEntity<UserDTO>(user, HttpStatus.FOUND);
	}
	
	@PutMapping("/public/users/{userId}")
	public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO, @PathVariable Long userId) {
		UserDTO updatedUser = userService.updateUser(userId, userDTO);
		
		return new ResponseEntity<UserDTO>(updatedUser, HttpStatus.OK);
	}
	
	@DeleteMapping("/admin/users/{userId}")
	public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
		String status = userService.deleteUser(userId);
		
		return new ResponseEntity<String>(status, HttpStatus.OK);
	}
	
	@PutMapping("/public/users/{userId}/address")
	public ResponseEntity<UserDTO> updateAddress(
	        @PathVariable Long userId,
	        @RequestBody AddressDTO addressDTO) {
       System.out.println(addressDTO);
	    UserDTO updatedUser = userService.updateUserAddress(userId, addressDTO);
	    return ResponseEntity.ok(updatedUser);
	}
	
	
	@GetMapping("/public/users/current-user")
	public ResponseEntity<UserDTO> getCurrentUser() {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String email = auth.getName(); // JWTFilter should set email here

	    
	    User user = userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

	    UserDTO userDTO = modelMapper.map(userService.getUserById(user.getUserId()), UserDTO.class);
	    return ResponseEntity.ok(userDTO);
	}


	
}
