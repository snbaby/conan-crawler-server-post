package com.conan.crawler.server.post.crawler;

import net.sf.json.JSONObject;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class TaoBaoCommentTotalProcessor implements PageProcessor {

	private Site site = Site.me().setDomain("s.taobao.com").setCharset("GBK")
			.addHeader("Referer", "http://www.taobao.com/").setRetryTimes(3).setSleepTime(5000);
	@Override
	public Site getSite() {
		// TODO Auto-generated method stub
		
		return site;
	}

	@Override
	public void process(Page page) {
		// TODO Auto-generated method stub
		Html html = page.getHtml();
		try {
			if (html.css("body").toString().contains("jsonp145")) {
				if (html.css("pre", "text").all().size() != 0) {// 淘宝评论
					Selectable selectable = html.css("pre", "text");
					JSONObject jsonObject = JSONObject
							.fromObject(selectable.toString().replace("jsonp145(", "").replace("})", "}"));
					page.putField("count", jsonObject.getString("count"));
				} else {// 天猫
					Selectable selectable = html.css("body", "text");
					JSONObject jsonObject = JSONObject
							.fromObject(selectable.toString().replace("jsonp145(", "").replace("})", "}"));
					page.putField("count", jsonObject.getJSONObject("dsr").getString("rateTotal"));
				}
			} else {
				page.putField("count", "");// 表示未抓取到，需要重新抓取
			}
		} catch (Exception e) {
			// TODO: handle exception
			page.putField("count", "");
		}
		
	}

}
