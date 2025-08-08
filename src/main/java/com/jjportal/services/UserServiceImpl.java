package com.jjportal.services;

 
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jjportal.config.AppConstants;
import com.jjportal.entites.Address;
 
import com.jjportal.entites.Role;
import com.jjportal.entites.User;
import com.jjportal.exceptions.APIException;
import com.jjportal.exceptions.ResourceNotFoundException;
import com.jjportal.payloads.AddressDTO;
 
import com.jjportal.payloads.UserDTO;
import com.jjportal.payloads.UserResponse;
import com.jjportal.repositories.AddressRepo;
import com.jjportal.repositories.RoleRepo;
import com.jjportal.repositories.UserRepo;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private RoleRepo roleRepo;

	@Autowired
	private AddressRepo addressRepo;

	 

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private ModelMapper modelMapper;
	
	
	
	
	@Override
	public UserDTO registerUser(UserDTO userDTO) {
	    try {
	        User user = modelMapper.map(userDTO, User.class);

	        // Assign default role
	        Role role = roleRepo.findById(AppConstants.USER_ID).orElseThrow(() ->
	            new ResourceNotFoundException("Role", "id", AppConstants.USER_ID));
	        user.getRoles().add(role);

	        User registeredUser = userRepo.save(user);

	        userDTO = modelMapper.map(registeredUser, UserDTO.class);
	        // Skip setting addressDTO here
	        return userDTO;

	    } catch (DataIntegrityViolationException e) {
	        throw new APIException("User already exists with emailId: " + userDTO.getEmail());
	    }
	}


	/*
	 * @Override public UserDTO registerUser(UserDTO userDTO) {
	 * 
	 * try { User user = modelMapper.map(userDTO, User.class);
	 * 
	 * 
	 * user.setRoles(new HashSet<>()); // Override any incoming roles by setting
	 * default role only Role defaultRole =
	 * roleRepo.findById(AppConstants.USER_ID).get();
	 * user.getRoles().add(defaultRole);
	 * 
	 * 
	 * 
	 * String country = userDTO.getAddress().getCountry(); String state =
	 * userDTO.getAddress().getState(); String city =
	 * userDTO.getAddress().getCity(); String pincode =
	 * userDTO.getAddress().getPincode(); String street =
	 * userDTO.getAddress().getStreet(); String buildingName =
	 * userDTO.getAddress().getBuildingName();
	 * 
	 * Address address =
	 * addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
	 * country, state, city, pincode, street, buildingName);
	 * 
	 * if (address == null) { address = new Address(country, state, city, pincode,
	 * street, buildingName);
	 * 
	 * address = addressRepo.save(address); }
	 * 
	 * user.setAddresses(List.of(address));
	 * 
	 * User registeredUser = userRepo.save(user);
	 * 
	 * 
	 * userDTO = modelMapper.map(registeredUser, UserDTO.class);
	 * 
	 * userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().
	 * get(), AddressDTO.class));
	 * 
	 * return userDTO; } catch (DataIntegrityViolationException e) { throw new
	 * APIException("User already exists with emailId: " + userDTO.getEmail()); }
	 * 
	 * }
	 */

	@Override
	public UserResponse getAllUsers(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();

		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		
		Page<User> pageUsers = userRepo.findAll(pageDetails);
		
		List<User> users = pageUsers.getContent();

		if (users.size() == 0) {
			throw new APIException("No User exists !!!");
		}

		List<UserDTO> userDTOs = users.stream().map(user -> {
			UserDTO dto = modelMapper.map(user, UserDTO.class);

			if (user.getAddresses().size() != 0) {
				dto.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));
			}
			return dto;

		}).collect(Collectors.toList());

		UserResponse userResponse = new UserResponse();
		
		userResponse.setContent(userDTOs);
		userResponse.setPageNumber(pageUsers.getNumber());
		userResponse.setPageSize(pageUsers.getSize());
		userResponse.setTotalElements(pageUsers.getTotalElements());
		userResponse.setTotalPages(pageUsers.getTotalPages());
		userResponse.setLastPage(pageUsers.isLast());
		
		return userResponse;
	}

	@Override
	public UserDTO getUserById(Long userId) {
	    User user = userRepo.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

	    UserDTO userDTO = modelMapper.map(user, UserDTO.class);

	    // Only map address if at least one exists
	    user.getAddresses().stream().findFirst().ifPresent(address -> {
	        AddressDTO addressDTO = modelMapper.map(address, AddressDTO.class);
	        userDTO.setAddress(addressDTO);
	    });

	    return userDTO;
	}

	
	@Override
	public UserDTO getUserByEmail(String email) {
	    User user = userRepo.findByEmail(email)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

	    UserDTO userDTO = modelMapper.map(user, UserDTO.class);

	    // Only map address if at least one exists
	    
	    user.getAddresses().stream().findFirst().ifPresent(address -> {
	        AddressDTO addressDTO = modelMapper.map(address, AddressDTO.class);
	        userDTO.setAddress(addressDTO);
	    });

	    return userDTO;
	}
	@Override
	public UserDTO updateUser(Long userId, UserDTO userDTO) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

		String encodedPass = passwordEncoder.encode(userDTO.getPassword());

		user.setFirstName(userDTO.getFirstName());
		user.setLastName(userDTO.getLastName());
		user.setMobileNumber(userDTO.getMobileNumber());
		user.setEmail(userDTO.getEmail());
		user.setPassword(encodedPass);

		if (userDTO.getAddress() != null) {
			String country = userDTO.getAddress().getCountry();
			String state = userDTO.getAddress().getState();
			String city = userDTO.getAddress().getCity();
			String pincode = userDTO.getAddress().getPincode();
			String street = userDTO.getAddress().getStreet();
			String buildingName = userDTO.getAddress().getBuildingName();

			Address address = addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(country, state,
					city, pincode, street, buildingName);

			if (address == null) {
				address = new Address(country, state, city, pincode, street, buildingName);

				address = addressRepo.save(address);

				user.setAddresses(List.of(address));
			}
		}

		userDTO = modelMapper.map(user, UserDTO.class);

		userDTO.setAddress(modelMapper.map(user.getAddresses().stream().findFirst().get(), AddressDTO.class));

		 

		return userDTO;
	}
	
	
	
	@Override
	public UserDTO updateUserAddress(Long userId, AddressDTO addressDTO) {
	    User user = userRepo.findById(userId)
	        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

	    String country = addressDTO.getCountry();
	    String state = addressDTO.getState();
	    String city = addressDTO.getCity();
	    String pincode = addressDTO.getPincode();
	    String street = addressDTO.getStreet();
	    String buildingName = addressDTO.getBuildingName();

	    // Check if the address already exists
	    Address address = addressRepo.findByCountryAndStateAndCityAndPincodeAndStreetAndBuildingName(
	        country, state, city, pincode, street, buildingName
	    );

	    // If not, create and save it
	    if (address == null) {
	        address = new Address(country, state, city, pincode, street, buildingName);
	        address = addressRepo.save(address);
	    }

	    // Replace old address (optional: allow multiple addresses if needed)
	    user.getAddresses().clear();
	    user.getAddresses().add(address);

	    // Save user with updated address
	    user = userRepo.save(user);

	    // Prepare response DTO
	    UserDTO userDTO = modelMapper.map(user, UserDTO.class);

	    // Set address safely
	    user.getAddresses().stream().findFirst().ifPresent(addr -> {
	        AddressDTO addrDTO = modelMapper.map(addr, AddressDTO.class);
	        userDTO.setAddress(addrDTO);
	    });

	    return userDTO;
	}




	@Override
	public String deleteUser(Long userId) {
		User user = userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

		 

		userRepo.delete(user);

		return "User with userId " + userId + " deleted successfully!!!";
	}

}
