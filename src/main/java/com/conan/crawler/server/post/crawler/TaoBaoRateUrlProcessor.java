package com.conan.crawler.server.post.crawler;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class TaoBaoRateUrlProcessor implements PageProcessor {
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
		Selectable selectable= html.css("[href~=rate.taobao.com]","href");
		
		if(selectable.all().size()>0) {
			page.putField("rateUrl", "https:"+selectable.all().get(0));
		}else {
			Selectable selectableTmall= html.css("#dsr-ratelink","value");
			if(selectableTmall.all().size()>0) {
				page.putField("rateUrl", "https:"+selectableTmall.all().get(0));
			}else {
				page.putField("rateUrl", "");
			}
			
		}
	}
}
