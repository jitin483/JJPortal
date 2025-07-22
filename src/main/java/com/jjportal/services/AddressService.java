package com.jjportal.services;

import java.util.List;

import com.jjportal.entites.Address;
import com.jjportal.payloads.AddressDTO;

public interface AddressService {
	
	AddressDTO createAddress(AddressDTO addressDTO);
	
	List<AddressDTO> getAddresses();
	
	AddressDTO getAddress(Long addressId);
	
	AddressDTO updateAddress(Long addressId, Address address);
	
	String deleteAddress(Long addressId);
}
