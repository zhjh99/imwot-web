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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.imwot.web.framework.core.modle.DataModle;
import com.imwot.web.framework.interfaces.AbstractController;

/**
 * admin
 * 
 * @author jinhong zhou
 */
public class Admin extends AbstractController {

	private static final String navigationSql = "SELECT * FROM authorization WHERE PID=0 AND ID IN(  SELECT AUTHORIZATION_ID FROM role_authorization WHERE ROLE_ID =(SELECT ROLE_ID FROM role_user WHERE USER_ID=?)) ORDER BY sortvalue ASC";
	private static final String navigationSql2 = "SELECT * FROM authorization WHERE ID IN(  SELECT AUTHORIZATION_ID FROM role_authorization WHERE ROLE_ID =(SELECT ROLE_ID FROM role_user WHERE USER_ID=?)) ORDER BY sortvalue ASC";

	public void main(DataModle modle) {
		int userId = (int) modle.getSession("userId");
		String userName = (String) modle.getSession("userName");

		List<Map<String, Object>> firstList = this.mysql1.queryForList(navigationSql, new Object[] { userId });
		List<Map<String, Object>> list = this.mysql1.queryForList(navigationSql2, new Object[] { userId });

		Map<String, List<Map<String, Object>>> secondMap = new HashMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> map : firstList) {
			int nId = (int) map.get("ID");
			List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> srcData : list) {
				int pId = (int) srcData.get("PID");
				if (nId == pId) {
					Object title = srcData.get("TITLE");
					Object path = srcData.get("PATH");
					Map<String, Object> tmpMap = new HashMap<String, Object>();
					tmpMap.put("TITLE", title);
					tmpMap.put("PATH", path);
					tmpList.add(tmpMap);
				}
			}
			secondMap.put(String.valueOf(nId), tmpList);
		}

		modle.addResult("firstList", firstList);
		modle.addResult("secondMap", secondMap);
		modle.addResult("userName", userName);
	}

	public void top(DataModle modle) {

	}

	public void left(DataModle modle) {

	}

	public void footer(DataModle modle) {

	}

	public void defaults(DataModle modle) {

	}

	public Map<String, Object> add(DataModle modle) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> delete(DataModle modle) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, Object> update(DataModle modle) {
		// TODO Auto-generated method stub
		return null;
	}
}
