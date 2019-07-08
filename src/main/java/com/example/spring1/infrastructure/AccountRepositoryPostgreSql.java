package com.example.spring1.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.example.spring1.domain.model.Account;
import com.example.spring1.domain.model.AccountRepository;
import com.example.spring1.infrastructure.mybatis.AccountManagement;
import com.example.spring1.infrastructure.mybatis.AccountManagementMapper;

/**
 * データの永続化の実装クラス(PostgreSQLに依存)
 */
@Repository
public class AccountRepositoryPostgreSql implements AccountRepository {

	@Autowired
	AccountManagementMapper accountManagementMapper; // Dependency Injection

	/**
	 * アカウントの取得
	 *
	 * @param id
	 * @return Account
	 */
	public Account get(long id) {

		// DBアクセス
		AccountManagement accountManagement = accountManagementMapper.selectByPrimaryKey(id);

		// DAOからドメインオブジェクトに詰め替え
		Account account = new Account(accountManagement.getId(), accountManagement.getName());

		return account;

	}

}
