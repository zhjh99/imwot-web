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
package com.imwot.web.framework.frame;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imwot.web.framework.commons.utils.ClazzUtils;
import com.imwot.web.framework.core.modle.Clazz;
import com.imwot.web.framework.core.modle.DataModle;
import com.imwot.web.framework.interfaces.AbstractController;
import com.imwot.web.framework.interfaces.Beetl;
import com.imwot.web.framework.interfaces.ITemplate;
import com.imwot.web.framework.interfaces.ThTemplate;

/**
 * 〈一句话功能简述〉
 *
 * @author    jinhong zhou
 */
@WebServlet(name = "FrameController", urlPatterns = "/admin/*")
public class FrameController extends HttpServlet {

	protected Logger log = LoggerFactory.getLogger(this.getClass());

	private static final long serialVersionUID = 1L;

	private Map<String, Object> templateTable;

	private ITemplate th;
	private ITemplate beetl;

	public FrameController() {
		super();
		initTemplateTable();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			//
			setHead(req, resp);
			initTemplate();
			Map<String, Object> reqMap = preProcess(req, resp);
			//
			Clazz clazz = processClazz(req);

			exec(clazz, req, resp, reqMap);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	public void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

	}

	private Map<String, Object> preProcess(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// req.setCharacterEncoding("UTF-8");
		int port = req.getServerPort();
		String clientFullUrl = null;
		String queryString = req.getQueryString() == null ? "" : "?" + req.getQueryString();
		if (port == 80) {
			clientFullUrl = req.getScheme() + "://" + req.getServerName() + req.getRequestURI() + queryString;
		} else {
			clientFullUrl = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getRequestURI() + queryString;
		}

		String url = req.getRequestURI();
		url = url.replace(req.getContextPath(), "");
		log.info("{}->{}", req.getSession().getAttribute("nickName"),clientFullUrl);

		String ip = getIpAddr(req);
		// log.info("ip:{}",ip);
		String ua = req.getHeader("user-agent");
		// log.info("ua:{}",ua);
		Enumeration<String> para = req.getParameterNames();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ip", ip);
		map.put("ua", ua);
		map.put("clientFullUrl", clientFullUrl);
		while (para.hasMoreElements()) {
			String name = para.nextElement();
			// String value = new
			// String(req.getParameter(name).getBytes("ISO8859-1"), "UTF-8");
			String[] values = req.getParameterValues(name);
			if (values != null) {
				if (values.length == 1) {
					map.put(name, values[0]);
				} else if (values.length > 1) {
					map.put(name, values);
				}
			}
		}
		return map;
	}

	public String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	private Clazz processClazz(HttpServletRequest req) {
		Clazz clazz = new Clazz();
		String clazzName = null;
		String method = null;

		String urls = req.getRequestURL().toString();
		String methods = StringUtils.substringAfter(urls, "admin/");
		clazzName = StringUtils.substringBefore(methods, "/");
		method = StringUtils.substringAfter(methods, "/");
		if (StringUtils.indexOf(method, "/") != -1) {
			method = StringUtils.substringBefore(method, "/");
		}

		clazz.setClazzName(clazzName);
		clazz.setMethod(method);
		return clazz;
	}

	private void exec(Clazz clazz, HttpServletRequest req, HttpServletResponse resp, Map<String, Object> reqMap) throws IOException {
		String name = clazz.getClazzName().toLowerCase();
		if (templateTable.containsKey(name)) {
			Object o = templateTable.get(name);
			try {
				String methodName = clazz.getMethod();
				Method method = o.getClass().getMethod("preprocess", new Class[] { DataModle.class });
				String paths = getMethodPath(o, methodName);
				ResponseResult reply = new ResponseResult();
				reply.setUrl(paths + ".html");

				DataModle modle = new DataModle(clazz, reqMap, reply, req.getSession());
				method.invoke(o, modle);
				setSession(req, resp);
				if (StringUtils.isNotBlank(reply.getUrl())) {
					if (METHOD.SENDREDIRECT.equals(reply.getMethod()) || StringUtils.indexOf(reply.getUrl(), ".") == -1) {// 外链跳转
						resp.sendRedirect(reply.getUrl());
					} else if (METHOD.PRINT.equals(reply.getMethod())) {
						Map<String, Object> paraMap = reply.getMap();
						if (paraMap != null && paraMap.size() > 0) {
							Iterator<String> it = paraMap.keySet().iterator();
							StringBuffer sb = new StringBuffer();
							while (it.hasNext()) {
								String key = it.next();
								Object value = paraMap.get(key);
								if (value != null) {
									sb.append(value.toString());
								}
							}
							if (sb.length() > 0) {
								PrintWriter out = resp.getWriter();
								out.println(sb.toString());
								out.flush();
								out.close();
							}
						}
					} else {// 本地跳转
						Map<String, Object> paraMap = reply.getMap();
						if (paraMap != null && paraMap.size() > 0) {
							Iterator<String> it = paraMap.keySet().iterator();
							while (it.hasNext()) {
								String key = it.next();
								Object value = paraMap.get(key);
								if (StringUtils.isNotBlank(key)) {
									req.setAttribute(key, value);
								}
							}
						}
						// template.exec(reply.getUrl(), req, resp, this);
						String extension = getExtension(reply);

						switch (extension) {
						case EXTENSION.HTML:
							String url = getUrl(reply.getUrl());
							req.getRequestDispatcher(url).forward(req, resp);
							break;

						case EXTENSION.JSP:
							req.getRequestDispatcher(reply.getUrl()).forward(req, resp);
							break;

						case EXTENSION.TH:
							th.exec(reply.getUrl(), req, resp, this);
							break;

						case EXTENSION.BEETL:
							beetl.exec(reply.getUrl(), req, resp, this);
							break;

						default:
							break;
						}
					}
				} else {
					Map<String, Object> paraMap = reply.getMap();
					if (paraMap != null && paraMap.size() > 0) {
						Iterator<String> it = paraMap.keySet().iterator();
						while (it.hasNext()) {
							String key = it.next();
							Object value = paraMap.get(key);
							if (StringUtils.isNotBlank(key)) {
								PrintWriter out = resp.getWriter();
								out.println(value.toString());
								out.flush();
								out.close();
							}
						}
					}
				}
			} catch (Exception e) {
				try {
					req.getRequestDispatcher("/WEB-INF/famework/admin/frameForbidden.html").forward(req, resp);
				} catch (Exception e2) {
					log.warn(null, e2);
				}
			}
		} else {
			try {
				req.getRequestDispatcher("/WEB-INF/famework/admin/frameForbidden.html").forward(req, resp);
			} catch (Exception e) {
				log.warn(null, e);
			}
		}

	}

	private void initTemplate() {
		if (th == null) {
			th = new ThTemplate(this);
		}
		if (beetl == null) {
			beetl = new Beetl();
		}
	}

	private String getExtension(ResponseResult resp) {
		String extension = null;
		if (resp != null && resp.getExtension() != null) {
			extension = resp.getExtension();
		} else {
			extension = EXTENSION.BEETL;
		}
		return extension;
	}

	private String getUrl(String page) {
		String url = "/WEB-INF/templates/" + page;
		return url;
	}

	private String getMethodPath(Object o, String method) {
		String path = null;
		String m = o.getClass().getCanonicalName();
		path = m.replaceAll("com.", "");
		path = path.replaceAll("\\.", "/").toLowerCase();
		// path = StringUtils.substringBeforeLast(path, "/");
		return path + "/" + method;
	}

	public void initTemplateTable() {
		if (templateTable == null) {
			templateTable = new HashMap<String, Object>();
			ArrayList<Class<?>> list = ClazzUtils.getAllClassByInterface(AbstractController.class);
			for (Class<?> clazz : list) {
				try {
					Object o = clazz.newInstance();
					String name = StringUtils.substringAfterLast(clazz.getName(), ".").toLowerCase();
					templateTable.put(name, o);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setHead(HttpServletRequest req, HttpServletResponse resp) {
		resp.setHeader("Cache-Control", "no-store");
		resp.setHeader("Pragma", "no-cache");
		resp.setDateHeader("Expires", 0);
		resp.setHeader("Content-type", "text/html;charset=UTF-8");
		resp.setHeader("referer", req.getHeader("referer"));
		try {
			req.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void setSession(HttpServletRequest req, HttpServletResponse resp) {
		req.setAttribute("userName", req.getSession().getAttribute("userName"));
		req.setAttribute("userId", req.getSession().getAttribute("userId"));
		req.setAttribute("nickName", req.getSession().getAttribute("nickName"));
	}
}
