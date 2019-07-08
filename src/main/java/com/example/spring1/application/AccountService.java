package com.example.spring1.application;

import com.example.spring1.domain.value.AccountInfo;

/**
 * サービス
 */
public interface AccountService {

	/**
	 * アカウントの取得
	 *
	 * @param id
	 * @return AccountInfo
	 */
	AccountInfo get(long id);


}
