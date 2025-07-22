package com.jjportal.payloads;

import lombok.Data;

@Data
public class JWTAuthRequest {
	private String username;  // email
	private String password;
}