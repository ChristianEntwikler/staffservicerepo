/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.staff.service.models;

import java.io.Serializable;

/**
 *
 * @author PSB
 */
public class JwtResponseDto implements Serializable {

	private static final long serialVersionUID = -8091879091924046844L;
	private final String jwtToken;
        
        public JwtResponseDto(String jwtToken) {
		this.jwtToken = jwtToken;
	}

        public String getJwtToken() {
            return jwtToken;
        }
}
