package com.conan.crawler.server.post.crawler;

import java.net.URLDecoder;

import org.apache.commons.lang3.StringUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class TaoBaoShopProcessor implements PageProcessor {

	private Site site = Site.me().setDomain("taobao.com").setCharset("GBK")
			.addHeader("Referer", "http://www.taobao.com/")
			.setRetryTimes(3).setSleepTime(30000);

	@Override
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

	@Override
	public void process(Page page) {
		// TODO Auto-generated method stub
		Html html = page.getHtml();
		String b = html.smartContent().toString();
		Selectable selectableTaobao= html.css("[href~=rate.taobao.com]","href");
		Selectable selectableTmall= html.css("#dsr-ratelink","value");
		
		if(selectableTaobao.all().size()>0) {//淘宝
			String microscopeData = html.css("[name=microscope-data]", "content").toString();
			String shop_id = microscopeData.split(";")[4].split("=")[1];
			String seller_nick = StringUtils.isEmpty(html.css("[data-nick]", "data-nick").toString())
					? html.css("[data-tnick]", "data-tnick").toString()
					: html.css("[data-nick]", "data-nick").toString();
			String shop_star = "";// 天猫店铺无等级
			String dsr_desc = "";
			String dsr_serv = "";
			String dsr_logi = "";
			String shop_name = "";
			if (html.css(".dsr-num", "text").all().size() > 0) {
				dsr_desc = html.css(".dsr-num", "text").all().get(0);
				dsr_serv = html.css(".dsr-num", "text").all().get(1);
				dsr_logi = html.css(".dsr-num", "text").all().get(2);
				shop_name = html.css(".shop-name .J_TGoldlog", "text").toString();
			} else {
				dsr_desc = html.css(".rateinfo em", "text").all().get(0);
				dsr_serv = html.css(".rateinfo em", "text").all().get(1);
				dsr_logi = html.css(".rateinfo em", "text").all().get(2);
				shop_name = html.css(".shop-name .shop-name-link", "text").toString();
			}
			page.putField("shop_type", "1");
			page.putField("shop_id", shop_id);
			page.putField("shop_name", shop_name);
			page.putField("seller_nick", URLDecoder.decode(seller_nick));
			page.putField("shop_star", shop_star);
			page.putField("dsr_desc", dsr_desc);
			page.putField("dsr_serv", dsr_serv);
			page.putField("dsr_logi", dsr_logi);
		}else if(selectableTmall.all().size()>0){//天猫
			String microscopeData = html.css("[name=microscope-data]", "content").toString();
			String shop_id = microscopeData.split(";")[4].split("=")[1];
			String shop_name = html.css("strong", "text").toString();
			String seller_nick = StringUtils.isEmpty(html.css("[data-nick]", "data-nick").toString())
					? html.css("[data-tnick]", "data-tnick").toString()
					: html.css("[data-nick]", "data-nick").toString();
			String shop_star = "";// 天猫店铺无等级
			String dsr_desc = html.css(".shopdsr-score-con", "text").all().get(0);
			String dsr_serv = html.css(".shopdsr-score-con", "text").all().get(1);
			String dsr_logi = html.css(".shopdsr-score-con", "text").all().get(2);
			page.putField("shop_type", "0");
			page.putField("shop_id", shop_id);
			page.putField("shop_name", shop_name);
			page.putField("seller_nick", URLDecoder.decode(seller_nick));
			page.putField("shop_star", shop_star);
			page.putField("dsr_desc", dsr_desc);
			page.putField("dsr_serv", dsr_serv);
			page.putField("dsr_logi", dsr_logi);
		}else {
			page.putField("shop_type", "-1");
		}
	}
}
