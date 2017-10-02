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
package com.imwot.web.framework.commons.utils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 〈一句话功能简述〉
 * 
 * @author jinhong zhou
 */
public class SimpleDownloadUtils {

	/**
	 * 日志
	 */
	private static Logger log = LoggerFactory.getLogger(SimpleDownloadUtils.class);

	/**
	 * USER_AGENT
	 */
	private static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:25.0) Gecko/20100101 Firefox/25.0";

	/**
	 * 
	 * 简单get方式下载
	 * 
	 * @param url
	 * @return String
	 * @exception/throws
	 */
	public static String get(String url) {
		String result = null;
		HttpClient httpclient = null;
		try {
			httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, USER_AGENT);
			httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 6000);
			httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
			HttpGet httpGet = new HttpGet(url);

			HttpResponse response = httpclient.execute(httpGet);
			result = process(response);
			httpGet.abort();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpclient != null) {
				httpclient.getConnectionManager().shutdown();
			}
		}
		return result;
	}

	/**
	 * 
	 * 简单post提交
	 * 
	 * @param url
	 * @param para
	 * @return String
	 * @exception/throws
	 */
	public static String post(String url, Map<String, String> para) {
		String result = null;
		HttpClient httpclient = null;
		try {
			httpclient = new DefaultHttpClient();
			httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, USER_AGENT);
			httpclient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 6000);
			httpclient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 60000);
			HttpPost httpPost = new HttpPost(url);

			setHttpPostPara(httpPost, para);

			HttpResponse response = httpclient.execute(httpPost);
			result = process(response);
			httpPost.abort();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpclient != null) {
				httpclient.getConnectionManager().shutdown();
			}
		}
		return result;
	}

	/**
	 * 
	 * 设置post参数
	 * 
	 * @param httpPost
	 * @param para
	 * @throws UnsupportedEncodingException
	 *             void
	 * @exception/throws
	 */
	@SuppressWarnings("deprecation")
	private static void setHttpPostPara(HttpPost httpPost, Map<String, String> para) throws UnsupportedEncodingException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Iterator<String> it = para.keySet().iterator();
		while (it.hasNext()) {
			String name = it.next().toString();
			String value = para.get(name);
			params.add(new BasicNameValuePair(name, value));
		}
		httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
	}

	/**
	 * 
	 * response处理
	 * 
	 * @param response
	 * @return String
	 * @exception/throws
	 */
	@SuppressWarnings("deprecation")
	public static String process(HttpResponse response) {
		String result = null;
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			log.warn("http连接出错，错误代码:" + statusCode);
			return result;
		}
		HttpEntity entity = response.getEntity();
		if (null == entity) {
			log.warn("无法获取网页内容，网络问题:" + statusCode);
			return result;
		}

		String charset = null;
		String contentCharSet = EntityUtils.getContentCharSet(entity);
		if (StringUtils.isNotEmpty(contentCharSet)) {
			charset = contentCharSet;
		}
		try {
			if (entity != null) {
				byte[] data = null;
				Header contentEncoding = entity.getContentEncoding();
				StringBuffer sb = null;

				if (null != contentEncoding && StringUtils.equalsIgnoreCase("gzip", contentEncoding.getValue())) {
					entity = new GzipDecompressingEntity(entity);
				}
				data = EntityUtils.toByteArray(entity);
				if (StringUtils.isEmpty(charset)) {
					String str = new String(data, "ISO-8859-1");
					charset = charset(str);
				}
				if (StringUtils.isEmpty(charset)) {
					charset = "UTF-8";
				}
				sb = new StringBuffer(new String(data, charset));
				result = sb.toString().trim();
				result = StringUtils.replace(result, "&nbsp;", "");
			}
		} catch (Exception e) {
			log.warn("Response处理出错:" + e);
			log.warn("http连接出错，错误代码:" + statusCode + "错误原因:Response类处理出错");
		}
		return result;
	}

	/**
	 * 获取网页编码
	 * 
	 * @param html
	 * @return String
	 * @exception/throws
	 */
	public static String charset(String html) {
		String charset = null;
		String z = "<meta([^>]*?)charset=([\"]?)(?<charset>.*?)\"([^>]*?)>";
		Matcher m = Pattern.compile(z).matcher(html);
		if (m.find()) {
			charset = m.group("charset");
			if (charset == null || "".equals(charset)) {
				return "ISO-8859-1";
			}
		} else {
			charset = "ISO-8859-1";
		}
		return charset.toLowerCase();
	}
}