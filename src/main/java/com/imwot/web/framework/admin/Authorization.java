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
import com.imwot.web.framework.core.modle.ComboResult;
import com.imwot.web.framework.core.modle.DataModle;
import com.imwot.web.framework.core.modle.EasyUiResult;
import com.imwot.web.framework.core.modle.Tree;
import com.imwot.web.framework.frame.METHOD;
import com.imwot.web.framework.interfaces.AbstractController;

/**
 * 权限
 * 
 * @author jinhong zhou
 */
public class Authorization extends AbstractController {

	public void listPage(DataModle modle) {

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

		String table = "authorization";
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
		modle.addResult("id", id);
		if (StringUtils.isNotBlank(id)) {
			String queryCondition = "ID=" + id;

			String table = "authorization";
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
			String[] idss=null;
			Object ids = modle.getPageItemObjectValue("ids[]");
			if (ids instanceof String) {
				idss=new String[1];
				idss[0]=(String) ids;
			} else if (ids instanceof String[]) {
				idss = (String[]) ids;
			}
			if(idss!=null){
				for (String id : idss) {
					this.mysql1.update("delete from role_authorization where AUTHORIZATION_ID =?", id);
					this.mysql1.update("delete from authorization where ID =?", id);
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
			String title = modle.getPageItemStringValue("TITLE", "");
			String path = modle.getPageItemStringValue("PATH", "");
			String SORTVALUE = modle.getPageItemStringValue("SORTVALUE", "");
			String pId = modle.getPageItemStringValue("PID", null);
			String id = modle.getPageItemStringValue("ID", "");

			if (StringUtils.isBlank(id)) {
				this.mysql1.update("insert into authorization ( NAME,TITLE,PATH,PID,SORTVALUE) values(?,?,?,?,?)", new Object[] { name, title, path, pId ,SORTVALUE});
			} else {
				this.mysql1.update("update authorization set  NAME=?,TITLE=?,PATH=?,PID=?,SORTVALUE=? where ID=?", new Object[] { name, title, path, pId, SORTVALUE, id });
			}
			sucess = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}

		modle.addResult("json", sucess);
		modle.setMethod(METHOD.PRINT);
	}

	/**
	 * 
	 * 导航信息
	 * 
	 * @param modle
	 *            void
	 * @exception/throws
	 */
	public void navigation(DataModle modle) {
		String queryCondition = "status=1";

		String table = "navigation";
		String[] column = null;
		String orderBy = "ID desc";

		String selectSql = getSelectSql(table, column, queryCondition, orderBy, null, null);

		List<Map<String, Object>> list = this.mysql1.queryForList(selectSql);

		ComboResult cr = new ComboResult();
		for (Map<String, Object> map : list) {
			String title = (String) map.get("TITLE");
			String id = ObjectUtils.getString(map.get("ID"));
			cr.addToList(id, title, false);
		}
		String json = cr.toJsonStrng();
		modle.addResult("json", json);
		modle.setMethod(METHOD.PRINT);
	}

	/**
	 * 权限
	 * 
	 * @param modle
	 */
	public void authData(DataModle modle) {
		String json="";
		try {
			String selfIds=modle.getPageItemStringValue("id", "");
			int selfId=-1;
			if(StringUtils.isNotBlank(selfIds)){
				selfId=Integer.parseInt(selfIds);
			}
			String selectNavigationSql = "SELECT * FROM authorization ORDER BY SORTVALUE ASC";

			List<Map<String, Object>> list = this.mysql1.queryForList(selectNavigationSql);

			Map<Integer, Tree> treeMap = new HashMap<Integer, Tree>();
			for (Map<String, Object> map : list) {
				int id = ObjectUtils.getInt(map.get("ID"));
				int pId = ObjectUtils.getInt(map.get("PID"));
				String title = ObjectUtils.getString(map.get("TITLE"));
				if(selfId==id){
					continue;
				}
				if (pId == 0) {
					Boolean checked = false;
					Tree tree = new Tree(id, title, null, checked);
					treeMap.put(id, tree);
				}
			}

			Map<Integer, Tree> treeMap2 = new HashMap<Integer, Tree>();
			for (Map<String, Object> map : list) {
				int id = ObjectUtils.getInt(map.get("ID"));
				int pId = ObjectUtils.getInt(map.get("PID"));
				String title = ObjectUtils.getString(map.get("TITLE"));
				if(selfId==id){
					continue;
				}
				if (treeMap.containsKey(pId)) {
					Boolean checked = false;
					Tree tree = new Tree(id, title, null, checked);
					Tree Ptree = treeMap.get(pId);
					Ptree.addChildren(tree);
					treeMap2.put(id, tree);
				}
			}

			Tree tree = new Tree(0, "doc", null, false);
			Iterator<Integer> it = treeMap.keySet().iterator();
			while (it.hasNext()) {
				int key = it.next();
				Tree tmp = treeMap.get(key);
				tree.addChildren(tmp);
			}
			json = gson.toJson(tree);
		} catch (Exception e) {
			// TODO: handle exception
		}
		

		
		modle.addResult("json", "[" + json + "]");
		modle.setMethod(METHOD.PRINT);
	}
}