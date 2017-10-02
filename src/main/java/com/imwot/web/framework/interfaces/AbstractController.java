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
package com.imwot.web.framework.interfaces;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.imwot.web.framework.core.annotation.AController;
import com.imwot.web.framework.core.modle.DataModle;

/**
 * 〈一句话功能简述〉
 * 
 * @author jinhong zhou
 */
@AController
public abstract class AbstractController extends AbstractSql implements IController {

	private static final String authorizeSql = "SELECT NAME,TITLE,PATH FROM authorization WHERE PATH=? AND ID IN(  SELECT AUTHORIZATION_ID FROM role_authorization WHERE ROLE_ID =(SELECT ROLE_ID FROM role_user WHERE USER_ID=?));";

	/**
	 * -1:非法登录;-2:没有权限;1:成功
	 * 
	 * @param modle
	 * @return
	 */
	public int preprocess(DataModle modle) {
		int result = -1;
		try {
			if (!illegalLogin(modle)) {
				if (isAuthorize(modle)) {
					Method method = this.getClass().getMethod(modle.getClazz().getMethod(), new Class[] { DataModle.class });
					method.invoke(this, modle);
					result = 1;
				} else {
					modle.setUrl("famework/admin/forbidden.html");
					result = -2;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private boolean illegalLogin(DataModle modle) {
		boolean isIllegal = false;
		if (!("login".equals(modle.getClazz().getClazzName())) && !("admin".equals(modle.getClazz().getClazzName()) && "index".equals(modle.getClazz().getMethod()))) {
			if (StringUtils.isBlank((String) modle.getSession("userName"))) {
				modle.setUrl("famework/admin/reloadParent.html");
				isIllegal = true;
			}
		}
		return isIllegal;
	}

	private boolean isAuthorize(DataModle modle) {
		boolean isAuthorize = false;
		String clazz = modle.getClazz().getClazzName();
		String method = modle.getClazz().getMethod();
		if (StringUtils.isNotBlank(clazz)) {
			if (StringUtils.equalsIgnoreCase("admin", clazz) || StringUtils.equalsIgnoreCase("login", clazz)) {
				isAuthorize = true;
			} else {
				String path = "/admin/" + clazz + "/" + method;
				List<Map<String, Object>> list = this.mysql1.queryForList(authorizeSql, new Object[] { path, modle.getSession("userId") });
				if (list.size() > 0) {
					isAuthorize = true;
					Object o = list.get(0).get("NAVIGATION_ID");
					if (o != null) {
						String title = (String) list.get(0).get("TITLE");
						modle.addResult("title", title);
					}
				}
			}
		}

		return isAuthorize;
	}

	protected String getSelectSql(String table, String[] column, String queryCondition, String orderBy, Integer pageNow, Integer pageSize) {
		StringBuffer sql = new StringBuffer();
		sql.append("select ");
		if (column != null && column.length > 0) {
			for (int x = 0; x < column.length; x++) {
				if (x + 1 == column.length) {
					sql.append(column[x]);
				} else {
					sql.append(column[x]).append(",");
				}
			}
		} else {
			sql.append(" *");
		}

		sql.append(" from ").append(table);
		if (StringUtils.isNotBlank(queryCondition)) {
			sql.append(" where ").append(queryCondition);
		}
		if (StringUtils.isNotBlank(orderBy)) {
			sql.append(" order by ").append(orderBy);
		}
		if (pageNow != null && pageSize != null) {
			sql.append(" limit ").append((pageNow - 1) * pageSize).append(",").append(pageSize);
		}

		return sql.toString();
	}

	protected String getTotolSql(String table, String queryCondition) {
		StringBuffer sql = new StringBuffer();
		sql.append("select count(*) from ").append(table);
		if (StringUtils.isNotBlank(queryCondition)) {
			sql.append(" where ").append(queryCondition);
		}
		return sql.toString();
	}
}