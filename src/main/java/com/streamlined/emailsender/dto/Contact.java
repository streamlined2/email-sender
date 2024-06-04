package com.streamlined.emailsender.dto;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Contact implements Serializable {

	private String name;
	@EqualsAndHashCode.Include
	private String email;

	@Override
	public String toString() {
		return "%s (%s)".formatted(name, email);
	}

}
