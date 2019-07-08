package com.example.spring1.interfaces;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.spring1.application.AccountService;
import com.example.spring1.domain.value.AccountInfo;

/**
 *
 * コントローラークラス
 *
 */
@Controller
@RequestMapping("/accounts")
public class AccountController {

	@Autowired
	AccountService accountService;

	/**
	 * アカウント取得API
	 *
	 * @param id
	 * @return ResponseEntity
	 */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<AccountInfo> get(long id) {
    	AccountInfo accountInfo = accountService.get(id);
        return new ResponseEntity<AccountInfo>(accountInfo, HttpStatus.OK);
    }

}
