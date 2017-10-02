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
import com.imwot.web.framework.core.modle.ComboResult;
import com.imwot.web.framework.core.modle.DataModle;
import com.imwot.web.framework.core.modle.EasyUiResult;
import com.imwot.web.framework.frame.METHOD;
import com.imwot.web.framework.interfaces.AbstractController;

/**
 * 用户管理
 * 
 * @author jinhong zhou
 */
public class User extends AbstractController {

	public void userList(DataModle modle) {
		// int pageSize = modle.getPageItemIntValue("PAGESIZE", 3);
		// int pageNow = modle.getPageItemIntValue("PAGENOW", 1);
		// String table = "user";
		// String[] column = null;
		// String queryCondition = null;
		// String orderBy = "id asc";
		// String selectSql = getSelectSql(table, column, queryCondition,
		// orderBy, pageNow, pageSize);
		// String totalSql = getTotolSql(table, queryCondition);
		//
		// List<Map<String, Object>> list = this.mysql1.queryForList(selectSql);
		// int rowCount = this.mysql1.queryForInt(totalSql);
		// String fy = fy(pageSize, pageNow, rowCount);
		//
		// modle.addResult("list", list);
		// modle.addResult("fy", fy);

	}

	public void getList(DataModle modle) {
		int page = modle.getPageItemIntValue("page", 1);
		int rows = modle.getPageItemIntValue("rows", 10);

		String STATUS = modle.getPageItemStringValue("STATUS", "");
		String USERNAME = modle.getPageItemStringValue("USERNAME", "");
		String from = modle.getPageItemStringValue("from", "");
		String to = modle.getPageItemStringValue("to", "");

		String sort = modle.getPageItemStringValue("sort", "");
		String order = !"".equals(modle.getPageItemStringValue("order", "")) ? modle.getPageItemStringValue("order", "") : "desc";

		String table = "user";
		String[] column = null;
		String orderBy = null;
		String queryCondition = null;
		if (StringUtils.isNotBlank(STATUS)) {
			if (StringUtils.isBlank(queryCondition)) {
				queryCondition = "STATUS=" + STATUS;
			} else {
				queryCondition = queryCondition + " and STATUS=" + STATUS;
			}
		}

		if (StringUtils.isNotBlank(USERNAME)) {
			if (StringUtils.isBlank(queryCondition)) {
				queryCondition = "USERNAME='" + USERNAME + "'";
			} else {
				queryCondition = queryCondition + " and USERNAME='" + USERNAME + "'";
			}
		}

		if (StringUtils.isNotBlank(from)) {
			if (StringUtils.isBlank(queryCondition)) {
				queryCondition = "CREATE_TIME>'" + from + "'";
			} else {
				queryCondition = queryCondition + " and CREATE_TIME>'" + from + "'";
			}
		}

		if (StringUtils.isNotBlank(to)) {
			if (StringUtils.isBlank(queryCondition)) {
				queryCondition = "CREATE_TIME<'" + to + "'";
			} else {
				queryCondition = queryCondition + " and CREATE_TIME<'" + to + "'";
			}
		}

		if (StringUtils.isNotBlank(sort) && StringUtils.isNotBlank(order)) {
			orderBy = sort + " " + order;
		} else {
			orderBy = "ID " + order;
		}
		String selectSql = getSelectSql(table, column, queryCondition, orderBy, page, rows);
		String totalSql = getTotolSql(table, queryCondition);

		List<Map<String, Object>> list = this.mysql1.queryForList(selectSql);
		int total = this.mysql1.queryForInt(totalSql);

		EasyUiResult result = new EasyUiResult(total, list);
		String json = gson.toJson(result);
		modle.addResult("json", json);
		modle.setMethod(METHOD.PRINT);
	}

	public void userAddPage(DataModle modle) {

	}

	public void userUpdatePage(DataModle modle) {
		String id = modle.getPageItemStringValue("id", "");
		if (StringUtils.isBlank(id)) {
			return;
		}
		String queryCondition = "id=" + id;

		String table = "user";
		String[] column = null;
		String orderBy = "ID desc";

		String selectSql = getSelectSql(table, column, queryCondition, orderBy, null, null);

		List<Map<String, Object>> list = this.mysql1.queryForList(selectSql);

		modle.addResult("list", list);
	}

	public void users(DataModle modle) {
		String queryCondition = "status=1";

		String table = "user";
		String[] column = null;
		String orderBy = "ID desc";

		String selectSql = getSelectSql(table, column, queryCondition, orderBy, null, null);

		List<Map<String, Object>> list = this.mysql1.queryForList(selectSql);

		ComboResult cr = new ComboResult();
		cr.addToList("", "全部", true);
		for (Map<String, Object> map : list) {
			String userName = (String) map.get("USERNAME");
			String nickName = (String) map.get("NICKNAME");
			// Boolean selected=StringUtils.equalsIgnoreCase(userName,
			// (String)modle.getSession("userName"))==true?true:null;
			cr.addToList(userName, nickName, false);
		}
		String json = cr.toJsonStrng();
		modle.addResult("json", json);
		modle.setMethod(METHOD.PRINT);
	}

	public void userDel(DataModle modle) {
		int sucess = 0;
		try {
			String[] idss = null;
			Object ids = modle.getPageItemObjectValue("ids[]");
			if (ids instanceof String) {
				idss = new String[1];
				idss[0] = (String) ids;
			} else if (ids instanceof String[]) {
				idss = (String[]) ids;
			}
			if (idss != null) {
				for (String id : idss) {
					this.mysql1.update("delete from role_user where USER_ID =?", id);
					this.mysql1.update("delete from user where ID =?", id);
					sucess++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		modle.addResult("json", sucess);
		modle.setMethod(METHOD.PRINT);
	}

	public void userUpdate(DataModle modle) {
		int sucess = 0;
		try {
			String username = modle.getPageItemStringValue("USERNAME", "");
			String nickname = modle.getPageItemStringValue("NICKNAME", "");
			String email = modle.getPageItemStringValue("EMAIL", "");
			String ID = modle.getPageItemStringValue("ID", "");
			String status = modle.getPageItemStringValue("STATUS", "");
			if (!"1".equals(status)) {
				status = "0";
			}

			if (StringUtils.isBlank(ID)) {
				String password = Md5Utils.getMd5("123456");
				this.mysql1.update("insert into user ( USERNAME,PASSWORD,NICKNAME,STATUS,EMAIL) values(?,md5(?),?,?,?)", new Object[] { username, password, nickname, status, email });
			} else {
				this.mysql1.update("update user set  USERNAME=?,NICKNAME=?,STATUS=?,EMAIL=? where ID=?", new Object[] { username, nickname, status, email, ID });
			}
			sucess = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		modle.addResult("json", sucess);
		modle.setMethod(METHOD.PRINT);
	}

	public void userAdd(DataModle modle) {
		String username = modle.getPageItemStringValue("USERNAME", "");
		String nickname = modle.getPageItemStringValue("NICKNAME", "");
		String password = modle.getPageItemStringValue("PASSWORD", "");
		String status = modle.getPageItemStringValue("STATUS", "");
		if (!"1".equals(status)) {
			status = "0";
		}

		password = "md5('" + password + "')";
		this.mysql1.update("insert into user ( USERNAME,PASSWORD,NICKNAME,STATUS) values(?,?,?,?)", new Object[] { username, password, nickname, status });
	}
}