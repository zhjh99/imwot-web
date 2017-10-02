/**
 [The "BSD license"]
 Copyright (c) 2013-2017 jinhong zhou (周金红)
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.imwot.web.framework.admin;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.imwot.web.framework.commons.utils.Md5Utils;
import com.imwot.web.framework.core.modle.DataModle;
import com.imwot.web.framework.interfaces.AbstractController;

/**
 * 登录
 * 
 * @author jinhong zhou
 */
public class Login extends AbstractController {

	public void index(DataModle modle) {

	}

	public void logout(DataModle modle) {
		modle.setSession("userId", null);
		modle.setSession("userName", null);
		modle.setSession("nickName", null);
		modle.setUrl("/admin/admin/main");
	}

	public void login(DataModle modle) {
		String username = modle.getPageItemStringValue("username", null);
		String password = modle.getPageItemStringValue("password", null);
		int n = login(username, password, modle);

		String info = null;
		String url = "famework/admin/login/index.html";
		switch (n) {
		case -1:
			info = "用户不存在！";
			break;
		case -2:
			info = "用户已经被冻结！";
			break;
		case 0:
			info = "密码错误！";
			break;

		default:
			url = "/admin/admin/main";
			break;
		}
		if (StringUtils.isNotBlank(url)) {
			modle.setUrl(url);
			modle.addResult("loginInfo", info);
		}
	}

	private int login(String username, String password, DataModle modle) {
		int result = 0;
		List<Map<String, Object>> list = this.mysql1.queryForList("SELECT ID,USERNAME,NICKNAME,PASSWORD,STATUS FROM user WHERE username=?", username);
		if (list != null && list.size() > 0) {
			String userName = (String) list.get(0).get("USERNAME");
			int userId = (int) list.get(0).get("ID");
			String nickName = (String) list.get(0).get("NICKNAME");
			String pwd = (String) list.get(0).get("PASSWORD");
			int status = (int) list.get(0).get("STATUS");

			if (status == -1) {
				result = -2;
				return result;
			}
			if (StringUtils.isBlank(password)) {
				result = -1;
				return result;
			}

			try {
				if (StringUtils.equals(Md5Utils.getMd5(password), pwd)) {
					modle.setSession("userId", userId);
					modle.setSession("userName", userName);
					modle.setSession("nickName", nickName);
					result = 1;
				} else {
					result = 0;
				}
			} catch (Exception e) {

			}
		} else {
			return -1;
		}
		return result;
	}
}