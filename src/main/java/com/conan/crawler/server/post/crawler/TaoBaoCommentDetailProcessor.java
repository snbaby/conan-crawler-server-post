package com.conan.crawler.server.post.crawler;

import java.util.ArrayList;
import java.util.List;

import com.conan.crawler.server.post.entity.CommentScanTb;
import com.conan.crawler.server.post.util.Utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

public class TaoBaoCommentDetailProcessor implements PageProcessor {
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
		if (html.css("body").toString().contains("jsonp145")) {
			Selectable selectable = html.css("body", "text");
			try {
				JSONObject jsonObject = JSONObject
						.fromObject(selectable.toString().replace("jsonp145(", "").replace("})", "}"));
				if (jsonObject.containsKey("rateDetail")) {// 天猫
					List<CommentScanTb> commentScanTbList = new ArrayList<>();
					JSONArray rateList;
					try {
						rateList = jsonObject.getJSONObject("rateDetail").getJSONArray("rateList");
					} catch (Exception e) {
						// TODO: handle exception
						rateList = new JSONArray();
					}
					
					for (int i = 0; i < rateList.size(); i++) {
						JSONObject rateJsonObject = rateList.getJSONObject(i);
						CommentScanTb commentScanTb = new CommentScanTb();
						commentScanTb.setBuyerNick(rateJsonObject.getString("displayUserNick"));
						commentScanTb.setFirstReviewTime(String.valueOf(rateJsonObject.getLong("gmtCreateTime")));
						commentScanTb.setReviewContent(rateJsonObject.getString("rateContent").contains("此用户没有填写") ? ""
								: rateJsonObject.getString("rateContent"));
						commentScanTb.setIsSuperVip(String.valueOf(rateJsonObject.getBoolean("goldUser")));
						commentScanTb.setBuyerStar("");
						commentScanTbList.add(commentScanTb);
					}
					page.putField("antiCrawler", "0");// 非反爬虫
					page.putField("commentScanTbList", commentScanTbList);
				} else if (jsonObject.containsKey("qnaDisabled")) {// 淘宝
					List<CommentScanTb> commentScanTbList = new ArrayList<>();
					JSONArray commentsList;
					try {
						commentsList = jsonObject.getJSONArray("comments");
					} catch (Exception e) {
						// TODO: handle exception
						commentsList = new JSONArray();
					}
					for (int i = 0; i < commentsList.size(); i++) {
						JSONObject commentsJsonObject = commentsList.getJSONObject(i);
						CommentScanTb commentScanTb = new CommentScanTb();
						commentScanTb.setBuyerNick(commentsJsonObject.getJSONObject("user").getString("nick"));
						commentScanTb.setFirstReviewTime(Utils.formatTaoBaoDate(commentsJsonObject.getString("date")));
						commentScanTb.setReviewContent(commentsJsonObject.getString("content").contains("此用户没有填写") ? ""
								: commentsJsonObject.getString("content"));
						commentScanTb.setIsSuperVip(commentsJsonObject.getJSONObject("user").getString("vip"));
						commentScanTb.setBuyerStar(
								String.valueOf(commentsJsonObject.getJSONObject("user").getString("displayRatePic")));
						commentScanTbList.add(commentScanTb);
					}
					page.putField("antiCrawler", "0");// 非反爬虫
					page.putField("commentScanTbList", commentScanTbList);
				} else {
					page.putField("antiCrawler", "1");// 反爬虫 需重新获取数据
				}
			} catch (Exception e) {
				// TODO: handle exception
				page.putField("antiCrawler", "2");// json解析错误
			}
		} else {
			page.putField("antiCrawler", "1");// 反爬虫 需重新获取数据
		}
	}

}
