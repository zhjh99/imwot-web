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

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * 〈一句话功能简述〉
 *
 * @author    jinhong zhou
 */
public class ThTemplate implements ITemplate {

	private TemplateEngine templateEngine;

	public ThTemplate(HttpServlet servlet) {
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servlet.getServletContext());
		templateResolver.setTemplateMode("XHTML");
		templateResolver.setPrefix("/WEB-INF/templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setCacheTTLMs(Long.valueOf(3600000L));
		templateResolver.setCacheable(true);
		templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
	}

	@Override
	public void exec(String url, HttpServletRequest req, HttpServletResponse resp, HttpServlet servlet) throws IOException {
		 ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servlet.getServletContext());
	        //ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
	        // XHTML is the default mode, but we will set it anyway for better understanding of code
	        templateResolver.setTemplateMode("XHTML");
	        // This will convert "home" to "/WEB-INF/templates/home.html"
//	        templateResolver.setPrefix("/WEB-INF/templates/");
	        templateResolver.setPrefix("/WEB-INF/");
	        templateResolver.setSuffix(".html");
	        // Set template cache TTL to 1 hour. If not set, entries would live in cache until expelled by LRU
	        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));
	        // Cache is set to true by default. Set to false if you want templates to
	        // be automatically updated when modified.
	        templateResolver.setCacheable(true);
	        templateResolver.setCharacterEncoding("UTF-8");
	        //Create Template Engine
	        TemplateEngine templateEngine = new TemplateEngine();
	        templateEngine.setTemplateResolver(templateResolver);
	        //Write the response headers
	        resp.setContentType("text/html;charset=UTF-8");
	        resp.setHeader("Pragma", "no-cache");
	        resp.setHeader("Cache-Control", "no-cache");
	        resp.setDateHeader("Expires", 0);
	        //Create Servlet context
	        WebContext ctx = new WebContext(req, resp, servlet.getServletContext(), req.getLocale());
	        ctx.setVariable("helloword","hello thymeleaf,wellcome!");
	        
	        //Executing template engine
	        templateEngine.process(url, ctx, resp.getWriter());
	}
//	@Override
//	public void exec(String url, HttpServletRequest req, HttpServletResponse resp, HttpServlet servlet) throws IOException {
//		resp.setContentType("text/html;charset=UTF-8");
//		resp.setHeader("Pragma", "no-cache");
//		resp.setHeader("Cache-Control", "no-cache");
//		resp.setDateHeader("Expires", 0);
//		WebContext ctx = new WebContext(req, resp, servlet.getServletContext(), req.getLocale());
//		// ctx.setVariable("hellohello", "hellohello");
//
//		// Executing template engine
//		templateEngine.process(url, ctx, resp.getWriter());
//	}

}