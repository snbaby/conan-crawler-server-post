package com.conan.crawler.server.post.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	public static String findShopName(String source) {
		String reg = "(?<=data-nick\\=\").*?(?=\")";
		Matcher m = Pattern.compile(reg).matcher(source);
		while (m.find()) {
			return m.group().trim();
		}
		return "";
	}

	public static String findUserNumberId(String source) {
		String reg = "(?<=userid\\=\").*?(?=\")";
		Matcher m = Pattern.compile(reg).matcher(source);
		while (m.find()) {
			return m.group().trim();
		}
		return "";
	}
	public static String findItemId(String source) {
		String reg = "(?<=data-nid\\=\").*?(?=\")";
		Matcher m = Pattern.compile(reg).matcher(source);
		while (m.find()) {
			return m.group().trim();
		}
		return "";
	}
	public static String findItemTitle(String source) {
		String reg = "(?<=alt\\=\").*?(?=\")";
		Matcher m = Pattern.compile(reg).matcher(source);
		while (m.find()) {
			return m.group().trim();
		}
		return "";
	}

	/**
	 * 获取指定HTML标签的指定属性的值
	 * 
	 * @param source
	 *            要匹配的源文本
	 * @param element
	 *            标签名称
	 * @param attr
	 *            标签的属性名称
	 * @return 属性值列表
	 */
	public static String findKeyWordByUrl(String keyWordScanUrl) {
		String reg = "(?<=q\\=).*?(?=&)";
		Matcher m = Pattern.compile(reg).matcher(keyWordScanUrl);
		while (m.find()) {
			try {
				return URLDecoder.decode(m.group().trim(), "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return m.group().trim();
			}
		}
		return "";
	}

	public static void main(String[] args) {
		String tianmao = "<div class=\"bd\"> <div data-spm=\"d4918101\" class=\"shop-rate\"> <h4>店铺动态评分<!--盟主aaa--> <span class=\"compare\">与同行业相比</span> </h4> <ul> <li> 描述相符：<a target=\"_blank\" href=\"//rate.taobao.com/user-rate-UvGxyMCHyvGvLvWTT.htm\"> <em class=\"count\" title=\"4.89071分\">4.8</em> <span class=\"rateinfo\" title=\" 计算规则:(店铺得分-同行业平均分)/(同行业店铺最高得分-同行业平均分) \"> <b></b><em>28.66%</em> </span> </a> </li> <li> 服务态度：<a target=\"_blank\" href=\"//rate.taobao.com/user-rate-UvGxyMCHyvGvLvWTT.htm\"> <em class=\"count\" title=\"4.77832分\">4.7</em> <span class=\"rateinfo\" title=\" 计算规则:(店铺得分-同行业平均分)/(同行业店铺最高得分-同行业平均分) \"> <b class=\"fair\"></b><em>--------</em> </span> </a> </li> <li> 发货速度：<a target=\"_blank\" href=\"//rate.taobao.com/user-rate-UvGxyMCHyvGvLvWTT.htm\"> <em class=\"count\" title=\"4.77832分\">4.7</em> <span class=\"rateinfo\" title=\" 计算规则:(店铺得分-同行业平均分)/(同行业店铺最高得分-同行业平均分) \"> <b class=\"fair\"></b><em>--------</em> </span> </a> </li> </ul> </div> <div class=\"extend\"> <h4 class=\"title\">店铺服务</h4> <ul> <li class=\"shopkeeper\"> <label>掌　　柜：</label> <div class=\"right\"> <a href=\"//rate.taobao.com/user-rate-UvGxyMCHyvGvLvWTT.htm\" data-spm=\"d4918097\">成家衣品旗舰店</a> </div> </li> <li> <label>客　　服：</label> <div class=\"right\"> <span class=\"J_WangWang ww-light ww-large\" data-nick=\"%E6%88%90%E5%AE%B6%E8%A1%A3%E5%93%81%E6%97%97%E8%88%B0%E5%BA%97\" data-tnick=\"%E6%88%90%E5%AE%B6%E8%A1%A3%E5%93%81%E6%97%97%E8%89%A6%E5%BA%97\" data-encode=\"true\" data-display=\"inline\" data-icon=\"static\"></span> </div> </li> <li class=\"locus\"> <label> 所 在 地： </label> <div class=\"right\"> </div> </li> <li> <label>工商执照：</label> <div class=\"right\"> <a href=\"//zhaoshang.tmall.com/maintaininfo/liangzhao.htm?xid=dd67934e58b9764dde815b0bd22ec566\" class=\"tm-gsLink\" target=\"_blank\"><img width=\"20\" height=\"22\" src=\"//assets.alicdn.com/app/marketing/xfile/national_emblem_light.png\" alt=\"\"></a> </div> </li> </ul> </div> <div class=\"other\"> <a class=\"enter-shop\" href=\"//chengjiayipin.tmall.com\" data-spm=\"d4918105\"><i></i><span>进店逛逛</span></a> <a id=\"xshop_collection_href\" href=\"//favorite.taobao.com/popup/add_collection.htm?id=515140602&itemid=515140602&itemtype=0&ownerid=dd67934e58b9764dde815b0bd22ec566&scjjc=2\" mercury:params=\"id=515140602&itemid=515140602&itemtype=0&ownerid=dd67934e58b9764dde815b0bd22ec566\" class=\"J_PopupTrigger collection xshop_sc J_TDialogTrigger J_TokenSign\" data-width=\"440\" data-height=\"290\" data-closebtn=\"true\"> <i></i><span>收藏本店</span> </a> </div> </div> ";
		String reg = "(?<=柜).*?(?=客)";
		Matcher m = Pattern.compile(reg).matcher(tianmao);
		while (m.find()) {
			System.out.println(m.group().trim());
		}
	}

	/**
	 * 获取指定HTML标签的指定属性的值
	 * 
	 * @param source
	 *            要匹配的源文本
	 * @param element
	 *            标签名称
	 * @param attr
	 *            标签的属性名称
	 * @return 属性值列表
	 */
	public static String findShopNameTmall(String source) {
		String reg = "(?<=strong\\>).*?(?=\\</strong)";
		Matcher m = Pattern.compile(reg).matcher(source);
		while (m.find()) {
			return m.group().trim();
		}
		return "";
	}
	
	public static String findShopIdByUrl(String shopScanUrl) {
		String reg = "(?<=user_number_id\\=).*";
		Matcher m = Pattern.compile(reg).matcher(shopScanUrl);
		while (m.find()) {
			return m.group().trim();
		}
		return "";
	}
	
	/**
	 * 获取指定HTML标签的指定属性的值
	 * 
	 * @param source
	 *            要匹配的源文本
	 * @param element
	 *            标签名称
	 * @param attr
	 *            标签的属性名称
	 * @return 属性值列表
	 */
	public static String findSellerNickTmall(String source) {
		String reg = "(?<=strong\\>).*?(?=\\</strong)";
		Matcher m = Pattern.compile(reg).matcher(source);
		while (m.find()) {
			return m.group().trim();
		}
		return "";
	}
	
	public static String formatTaoBaoDate(String source) {
		DateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
		try {
			Date date = format.parse(source);
			return String.valueOf(date.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return String.valueOf((new Date()).getTime());
		}
	}
	
	public static String findGoodsIdByUrl(String commentDetailUrl) {
		if(commentDetailUrl.contains("rate.tmall.com")) {//天猫
			String reg = "(?<=itemId\\=).*?(?=&)";
			Matcher m = Pattern.compile(reg).matcher(commentDetailUrl);
			while (m.find()) {
				return m.group().trim();
			}
			return "";
		}else {//淘宝
			String reg = "(?<=auctionNumId\\=).*?(?=&)";
			Matcher m = Pattern.compile(reg).matcher(commentDetailUrl);
			while (m.find()) {
				return m.group().trim();
			}
			return "";
		}
	}
	
	public static String findGoodsIdByCommentTotalUrl(String commentDetailUrl) {
		String reg = "(?<=itemId\\=).*?(?=&)";
		Matcher m = Pattern.compile(reg).matcher(commentDetailUrl);
		while (m.find()) {
			return m.group().trim();
		}
		return "";
	}
}
