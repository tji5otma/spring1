package com.example.spring1.domain.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Account {

	private long id;
	private String name;

	public Account(Long id, String name) {
		this.id = id;
		this.name = name;
	}
}
