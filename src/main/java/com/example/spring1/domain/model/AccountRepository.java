package com.example.spring1.domain.model;

/**
 *
 * データの永続化
 */
public interface AccountRepository {

	/**
	 * アカウントの取得
	 *
	 * @param id
	 * @return Account
	 */
	public Account get(long id);

}
