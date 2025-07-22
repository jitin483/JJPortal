package com.jjportal.payloads;

import lombok.Data;

@Data
public class JWTAuthResponse {
	private String token;
	
	private UserDTO user;
}