package com.example.spring1.application.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.spring1.application.AccountService;
import com.example.spring1.domain.model.Account;
import com.example.spring1.domain.model.AccountRepository;
import com.example.spring1.domain.value.AccountInfo;

/**
 * サービスの実装クラス
 */
@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	AccountRepository accountRepository;

	/**
	 * アカウントの取得
	 *
	 * @param id
	 * @return AccountInfo
	 */
	@Override
	public AccountInfo get(long id) {

		// アカウント取得
		Account account = accountRepository.get(id);

		// ドメインオブジェクトからDTOに値を詰め替え
		AccountInfo accountInfo = new AccountInfo(account.getId(), account.getName());

		return accountInfo;

	}

}
