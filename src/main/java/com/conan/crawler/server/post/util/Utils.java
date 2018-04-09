package com.conan.crawler.server.post.util;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
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
		if (commentDetailUrl.contains("rate.tmall.com")) {// 天猫
			String reg = "(?<=itemId\\=).*?(?=&)";
			Matcher m = Pattern.compile(reg).matcher(commentDetailUrl);
			while (m.find()) {
				return m.group().trim();
			}
			return "";
		} else {// 淘宝
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
	
	public static String getIp() {
		try {
			String ipString = "";
			Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
			InetAddress ip = null;
			while (allNetInterfaces.hasMoreElements()) {
				NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
				Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
				while (addresses.hasMoreElements()) {
					ip = (InetAddress) addresses.nextElement();
					if (ip != null && ip instanceof Inet4Address && !ip.getHostAddress().equals("127.0.0.1")) {
						return ip.getHostAddress();
					}
				}
			}
			return ipString;
		} catch (Exception e) {
			// TODO: handle exception
			return "127.0.0.1";
		}
	}
}
