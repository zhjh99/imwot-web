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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.imwot.web.framework.commons.utils.ObjectUtils;
import com.imwot.web.framework.core.modle.DataModle;
import com.imwot.web.framework.core.modle.EasyUiResult;
import com.imwot.web.framework.core.modle.Tree;
import com.imwot.web.framework.frame.METHOD;
import com.imwot.web.framework.interfaces.AbstractController;

/**
 * 角色管理
 * 
 * @author jinhong zhou
 */
public class Role extends AbstractController {

	public void listPage(DataModle modle) {

	}

	public void authManager(DataModle modle) {
		String roleId = modle.getPageItemStringValue("role_id", "");
		modle.addResult("role_id", roleId);
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

		String table = "role";
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

	public void addPage(DataModle modle) {
		String id = modle.getPageItemStringValue("id", "");
		if (StringUtils.isNotBlank(id)) {
			String queryCondition = "ID=" + id;

			String table = "role";
			String[] column = null;
			String orderBy = null;

			String selectSql = getSelectSql(table, column, queryCondition, orderBy, null, null);

			List<Map<String, Object>> list = this.mysql1.queryForList(selectSql);
			if (list.size() > 0) {
				modle.addResult("list", list);
			}
		}
	}

	/**
	 * 
	 * 删除
	 * 
	 * @param modle
	 *            void
	 * @exception/throws
	 */
	public void delete(DataModle modle) {
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
					this.mysql1.update("delete from role_authorization where ROLE_ID =?", id);
					this.mysql1.update("delete from role_user where ROLE_ID =?", id);
					this.mysql1.update("delete from role where ID =?", id);
					sucess++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		modle.addResult("json", sucess);
		modle.setMethod(METHOD.PRINT);
	}

	/**
	 * 
	 * 添加和更新
	 * 
	 * @param modle
	 *            void
	 * @exception/throws
	 */
	public void update(DataModle modle) {
		int sucess = 0;
		try {
			String name = modle.getPageItemStringValue("NAME", "");
			String id = modle.getPageItemStringValue("ID", "");
			String status = modle.getPageItemStringValue("STATUS", "");
			if (!"1".equals(status)) {
				status = "0";
			}

			if (StringUtils.isBlank(id)) {
				this.mysql1.update("insert into role ( NAME,STATUS) values(?,?)", new Object[] { name, status });
			} else {
				this.mysql1.update("update role set  NAME=?,STATUS=? where ID=?", new Object[] { name, status, id });
			}
			sucess = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		modle.addResult("json", sucess);
		modle.setMethod(METHOD.PRINT);
	}

	public void userManager(DataModle modle) {
		String roleId = modle.getPageItemStringValue("role_id", "");
		modle.addResult("role_id", roleId);
	}

	/**
	 * 
	 * 用户列表
	 * 
	 * @param modle
	 *            void
	 * @exception/throws
	 */
	public void users(DataModle modle) {
		String sRoleId = modle.getPageItemStringValue("role_id", null);

		String selectSql = "SELECT u.ID,u.NICKNAME,r.ROLE_ID FROM USER u LEFT JOIN role_user r ON u.ID=r.USER_ID";
		String totalSql = "SELECT count(*) FROM USER u LEFT JOIN role_user r ON u.ID=r.USER_ID";

		List<Map<String, Object>> list = this.mysql1.queryForList(selectSql);

		for (Map<String, Object> map : list) {
			String roleId = ObjectUtils.getString(map.get("ROLE_ID"));
			if (null != sRoleId && sRoleId.equals(roleId)) {
				map.put("checked", true);
			} else {
				map.put("checked", false);
			}
		}

		int total = this.mysql1.queryForInt(totalSql);

		EasyUiResult result = new EasyUiResult(total, list);
		String json = gson.toJson(result);
		modle.addResult("json", json);
		modle.setMethod(METHOD.PRINT);
	}

	/**
	 * 用户-角色
	 * 
	 * @param modle
	 */
	public void authorize(DataModle modle) {
		String roleId = modle.getPageItemStringValue("ROLE_ID", "");
		List<Map<String, Object>> list = this.mysql1.queryForList("select * from role_user where ROLE_ID=?", roleId);
		int sucess = 0;
		try {
			Map<String, Object> idss = new HashMap<String, Object>();
			Object ids = modle.getPageItemObjectValue("ids[]");
			if (ids instanceof String) {
				idss.put((String) ids, null);
			} else if (ids instanceof String[]) {
				String[] idsa = (String[]) ids;
				for (String str : idsa) {
					idss.put(str, null);
				}
			}

			for (Map<String, Object> map : list) {
				String role = ObjectUtils.getString(map.get("ROLE_ID"));
				String user = ObjectUtils.getString(map.get("USER_ID"));
				if (!idss.containsKey(user)) {
					this.mysql1.update("delete from role_user where ROLE_ID =? and USER_ID=?", role, user);
					sucess++;
				} else {
					idss.remove(user);
				}
			}

			Iterator<String> it = idss.keySet().iterator();
			while (it.hasNext()) {
				this.mysql1.update("insert into role_user (ROLE_ID,USER_ID) values(?,?)", roleId, it.next());
				sucess++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		modle.addResult("json", sucess);
		modle.setMethod(METHOD.PRINT);
	}

	public void authData(DataModle modle) {
		String sRoleId = modle.getPageItemStringValue("role_id", null);

		String selectNavigationSql = "SELECT * FROM authorization ORDER BY sortvalue ASC";
		String selectroleSql = "SELECT * FROM role_authorization where ROLE_ID=?";

		List<Map<String, Object>> list = this.mysql1.queryForList(selectNavigationSql);
		List<Map<String, Object>> list2 = this.mysql1.queryForList(selectroleSql, sRoleId);

		Map<Integer, Integer> roleMap = new HashMap<Integer, Integer>();
		for (Map<String, Object> map : list2) {
			int authorizationId = ObjectUtils.getInt(map.get("AUTHORIZATION_ID"));
			int roleId = ObjectUtils.getInt(map.get("ROLE_ID"));
			roleMap.put(authorizationId, roleId);
		}

		Map<Integer, Tree> treeMap = new HashMap<Integer, Tree>();
		for (Map<String, Object> map : list) {
			int id = ObjectUtils.getInt(map.get("ID"));
			int pId = ObjectUtils.getInt(map.get("PID"));
			String title = ObjectUtils.getString(map.get("TITLE"));
			if (pId == 0) {
				Boolean checked = false;
				if (roleMap.containsKey(id)) {
					checked = true;
				}
				Tree tree = new Tree(id, title, null, checked);
				treeMap.put(id, tree);
			}
		}

		Map<Integer, Tree> treeMap2 = new HashMap<Integer, Tree>();
		for (Map<String, Object> map : list) {
			int id = ObjectUtils.getInt(map.get("ID"));
			int pId = ObjectUtils.getInt(map.get("PID"));
			String title = ObjectUtils.getString(map.get("TITLE"));
			if (treeMap.containsKey(pId)) {
				Boolean checked = false;
				if (roleMap.containsKey(id)) {
					checked = true;
				}
				Tree tree = new Tree(id, title, null, checked);
				Tree Ptree = treeMap.get(pId);
				Ptree.addChildren(tree);
				treeMap2.put(id, tree);
			}
		}

		for (Map<String, Object> map : list) {
			int id = ObjectUtils.getInt(map.get("ID"));
			int pId = ObjectUtils.getInt(map.get("PID"));
			String title = ObjectUtils.getString(map.get("TITLE"));
			if (treeMap2.containsKey(pId)) {
				Boolean checked = false;
				if (roleMap.containsKey(id)) {
					checked = true;
				}
				Tree tree = new Tree(id, title, null, checked);
				Tree Ptree = treeMap2.get(pId);
				Ptree.addChildren(tree);
			}
		}

		Tree tree = new Tree(0, "doc", null, false);
		Iterator<Integer> it = treeMap.keySet().iterator();
		while (it.hasNext()) {
			int key = it.next();
			Tree tmp = treeMap.get(key);
			tree.addChildren(tmp);
		}

		String json = gson.toJson(tree);
		modle.addResult("json", "[" + json + "]");
		modle.setMethod(METHOD.PRINT);
	}

	/**
	 * 权限
	 * 
	 * @param modle
	 */
	public void auth(DataModle modle) {
		String roleId = modle.getPageItemStringValue("ROLE_ID", "");
		List<Map<String, Object>> list = this.mysql1.queryForList("select * from role_authorization where ROLE_ID=?", roleId);
		int sucess = 0;
		try {
			Map<String, Object> idss = new HashMap<String, Object>();
			Object ids = modle.getPageItemObjectValue("ids[]");
			if (ids instanceof String) {
				idss.put((String) ids, null);
			} else if (ids instanceof String[]) {
				String[] idsa = (String[]) ids;
				for (String str : idsa) {
					idss.put(str, null);
				}
			}

			for (Map<String, Object> map : list) {
				String role = ObjectUtils.getString(map.get("ROLE_ID"));
				String authorization = ObjectUtils.getString(map.get("AUTHORIZATION_ID"));
				if (!idss.containsKey(authorization)) {
					this.mysql1.update("delete from role_authorization where ROLE_ID =? and AUTHORIZATION_ID=?", role, authorization);
					sucess++;
				} else {
					idss.remove(authorization);
				}
			}

			Iterator<String> it = idss.keySet().iterator();
			while (it.hasNext()) {
				this.mysql1.update("insert into role_authorization (ROLE_ID,AUTHORIZATION_ID) values(?,?)", roleId, it.next());
				sucess++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		modle.addResult("json", sucess);
		modle.setMethod(METHOD.PRINT);
	}
}