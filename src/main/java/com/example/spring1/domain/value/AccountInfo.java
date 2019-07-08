package com.example.spring1.domain.value;

import lombok.Getter;

/**
 * レスポンスに用いるDTO
 */
@Getter
public class AccountInfo {

	private final long id;
	private final String name;

	public AccountInfo(long id, String name) {
		this.id = id;
		this.name = name;
	}

}
