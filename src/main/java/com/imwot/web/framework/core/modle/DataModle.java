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
package com.imwot.web.framework.core.modle;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.imwot.web.framework.frame.ResponseResult;

/**
 * 〈一句话功能简述〉
 *
 * @author    jinhong zhou
 */
public class DataModle {

	private Map<String, Object> req;
	private ResponseResult replay;
	private HttpSession httpSession;
	private Clazz clazz;

	public DataModle(Clazz clazz, Map<String, Object> req, ResponseResult replay, HttpSession httpSession) {
		this.req = req;
		this.replay = replay;
		this.httpSession = httpSession;
		this.clazz = clazz;
	}

	public int getPageItemIntValue(String key, int defaultValue) {
		int result = defaultValue;
		Object o = req.get(key);
		if (o == null) {

		} else if (o instanceof Integer) {
			result = (int) o;
		} else if (o instanceof String) {
			String tmp = (String) o;
			result = Integer.parseInt(tmp);
		}

		return result;
	}

	public String getPageItemStringValue(String key, String defaultValue) {
		String result = defaultValue;
		Object o = req.get(key);
		if (o instanceof Integer) {
			result = String.valueOf(o);
		} else if (o instanceof String) {
			result = (String) o;
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getPageItemListValue(String key) {
		return (List<Map<String, Object>>) req.get(key);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getPageItemMapValue(String key) {
		return (Map<String, Object>) req.get(key);
	}

	public Object getPageItemObjectValue(String key) {
		return req.get(key);
	}

	public void setExtension(String extension) {
		replay.setExtension(extension);
	}

	public void setMethod(String method) {
		replay.setMethod(method);
	}

	public void setUrl(String url) {
		replay.setUrl(url);
	}

	public void addResult(String key, Object value) {
		replay.addResult(key, value);
	}

	public void setSession(String key, Object value) {
		httpSession.setAttribute(key, value);
	}

	public Object getSession(String key) {
		return httpSession.getAttribute(key);
	}

	public Clazz getClazz() {
		return clazz;
	}

	public void setClazz(Clazz clazz) {
		this.clazz = clazz;
	}
}