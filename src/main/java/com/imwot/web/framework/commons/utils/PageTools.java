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

/**
 * 分页工具
 * 
 * @author jinhong zhou
 */
public class PageTools {
	public static String paging(int fenyePageSize, int pageSize, int rowCount, int pageNow, String url, String queryconditions) {
		int maxPage = 100;
		// 开始分页rowCount
		// pageSize一页有多少记录
		// rowCount总共多少条记录
		// pageNow当前的记录
		// fenyePageSize:分页下面显示的分页数字为多少个
		int pageCount = rowCount / pageSize;
		if (rowCount % pageSize > 0) {
			pageCount++;
		}
		if (pageCount > maxPage) {
			pageCount = maxPage;
		}

		int start = 1;
		int end = 10;
		// 如果分页总数大于一页的数字，则当前页的结束记录数=规定的记录数
		// 如果分页总数小于一页的数字，则当前页的结束记录数=分页总数
		if (pageCount > fenyePageSize) {
			// 如果记录总数countnum大于pagenow+(pagesize/2)-1，则当前页的结束记录数=pagenow+(pagesize/2)-1
			// 如果pagenow-(pagesize/2)>0，则当前页的开始记录数=规定的记录数pagenow-(pagesize/2)
			if (pageNow > (fenyePageSize / 2) && pageNow < pageCount - (fenyePageSize / 2) + 1) {
				start = pageNow - (fenyePageSize / 2);
				end = pageNow + (fenyePageSize / 2) - 1;
			} else if (pageNow > (fenyePageSize / 2) && pageNow >= pageCount - (fenyePageSize / 2) + 1) {
				start = pageCount - fenyePageSize + 1;
				end = pageCount;
			}
		} else {
			end = pageCount;
		}
		StringBuffer sb = new StringBuffer(1024);
		try {
			if (rowCount != 0) {
				// String urlstr = url;
				// 上一页
				// if (pageNow > 1) {
				// String urlstr1 = urlstr + "pageNum=" + (pageNow -
				// 1)+queryconditions;
				// // sb.append("<a href='" + urlstr1 +
				// "' class='fl prev'>Previous</a>");
				// sb.append("<span class='first paginate_button paginate_button_disabled' id='dyntable2_first'>First</span>");
				// sb.append("<span class='previous paginate_button paginate_button_disabled' id='dyntable2_previous'>Previous</span>");
				// }
				int n1 = (pageNow - 1) == 0 ? 1 : (pageNow - 1);
				String urlstr1 = getJsString(1);
				String urlstr2 = getJsString(n1);
				// sb.append("<a href='" + urlstr1 +
				// "' class='fl prev'>Previous</a>");
				sb.append("<span class='first paginate_button paginate_button_disabled' id='dyntable2_first' " + urlstr1 + ">First</span>");
				sb.append("<span class='previous paginate_button paginate_button_disabled' id='dyntable2_previous' " + urlstr2 + ">Previous</span>");
				// 中间的页
				// sb.append("<ul class='fl'>");
				sb.append("<span>");
				for (; start <= end; start++) {
					if (pageNow == 1) {
						if (start == 6) {
							break;
						}
					}
					if (pageNow == 2) {
						if (start == 7) {
							break;
						}
					}
					if (pageNow == 3) {
						if (start == 8) {
							break;
						}
					}
					if (pageNow == 4) {
						if (start == 9) {
							break;
						}
					}
					if (pageNow == 5) {
						if (start == 10) {
							break;
						}
					}
					String urlstr3 = getJsString(start);
					if (start == pageNow) {
						sb.append("<span class='paginate_active'>" + start + "</span>");
					} else {
						sb.append("<span class='paginate_button' " + urlstr3 + ">" + start + "</span>");
					}
				}
				sb.append("</span>");
				// 下一页
				int n4 = pageNow + 1 > pageCount ? pageNow : pageNow + 1;
				int n5 = pageCount;

				String urlstr4 = getJsString(n4);
				String urlstr5 = getJsString(n5);
				sb.append("<span class='next paginate_button' id='dyntable2_next' " + urlstr4 + ">Next</span>");
				sb.append("<span class='last paginate_button' id='dyntable2_last' " + urlstr5 + ">Last</span>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	private static String getJsString(int n) {
		return "onclick=\"javascript:fy('" + n + "');\"";
	}
}
