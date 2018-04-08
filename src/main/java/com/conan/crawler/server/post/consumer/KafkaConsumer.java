package com.conan.crawler.server.post.consumer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import com.conan.crawler.server.post.crawler.TaoBaoCommentDetailProcessor;
import com.conan.crawler.server.post.crawler.TaoBaoCommentTotalProcessor;
import com.conan.crawler.server.post.crawler.TaoBaoKeyWordProcessor;
import com.conan.crawler.server.post.crawler.TaoBaoRateUrlProcessor;
import com.conan.crawler.server.post.crawler.TaoBaoShopProcessor;
import com.conan.crawler.server.post.entity.CommentScanTb;
import com.conan.crawler.server.post.entity.CommentTb;
import com.conan.crawler.server.post.entity.GoodsTb;
import com.conan.crawler.server.post.entity.KeyWordScanTb;
import com.conan.crawler.server.post.entity.ShopScanTb;
import com.conan.crawler.server.post.entity.ShopTb;
import com.conan.crawler.server.post.mapper.CommentScanTbMapper;
import com.conan.crawler.server.post.mapper.CommentTbMapper;
import com.conan.crawler.server.post.mapper.GoodsTbMapper;
import com.conan.crawler.server.post.mapper.KeyWordScanTbMapper;
import com.conan.crawler.server.post.mapper.KeyWordTbMapper;
import com.conan.crawler.server.post.mapper.ShopScanTbMapper;
import com.conan.crawler.server.post.mapper.ShopTbMapper;
import com.conan.crawler.server.post.util.Utils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.PhantomJSDownloader;
import us.codecraft.webmagic.pipeline.CollectorPipeline;
import us.codecraft.webmagic.pipeline.ResultItemsCollectorPipeline;

/**
 * 消费者 使用@KafkaListener注解,可以指定:主题,分区,消费组
 */
@Component
public class KafkaConsumer {

	private String phantomJsExePath = "H:\\property\\phantomjs.exe";

	private String crawlJsPath = "H:\\property\\crawl.js";

	@Autowired
	private KeyWordTbMapper keyWordTbMapper;

	@Autowired
	private KeyWordScanTbMapper keyWordScanTbMapper;

	@Autowired
	private GoodsTbMapper goodsTbMapper;

	@Autowired
	private ShopTbMapper shopTbMapper;
	
	@Autowired
	private ShopScanTbMapper shopScanTbMapper;
	
	@Autowired
	private CommentScanTbMapper commentScanTbMapper;
	
	@Autowired
	private CommentTbMapper commentTbMapper;

	@Autowired
	private KafkaTemplate kafkaTemplate;

	@KafkaListener(topics = { "key-word-scan" })
	public void keyWordScan(String keyWordScanUrl) throws InterruptedException, UnsupportedEncodingException {
		System.out.println("key-word-scan-消费--" + keyWordScanUrl);
		PhantomJSDownloader phantomDownloader = new PhantomJSDownloader(phantomJsExePath, crawlJsPath).setRetryNum(3);
		CollectorPipeline<ResultItems> collectorPipeline = new ResultItemsCollectorPipeline();
		Spider.create(new TaoBaoKeyWordProcessor()).addUrl(keyWordScanUrl).setDownloader(phantomDownloader)
				.addPipeline(collectorPipeline).thread((Runtime.getRuntime().availableProcessors() - 1) << 1).run();
		List<ResultItems> resultItemsList = collectorPipeline.getCollected();
		for (ResultItems resultItems : resultItemsList) {
			if ((boolean) resultItems.get("pageExist")) {// 判断当前页面是否存在
				List<String> resultList = new ArrayList<>();
				resultList = resultItems.get("result");
				if (resultList.isEmpty()) {// 没有获取到数据需要重新获取
					ListenableFuture future = kafkaTemplate.send("key-word-scan", resultItems.get("url").toString());
					future.addCallback(
							o -> System.out.println("key-word-scan-消息发送成功：" + resultItems.get("url").toString()),
							throwable -> System.out
									.println("key-word-scan消息发送失败：" + resultItems.get("url").toString()));
				} else {
					for (String resultStr : resultList) {
						String shopName = Utils.findShopName(resultStr);
						String shopId = Utils.findShopId(resultStr);
						String shopType = resultStr.contains("icon-service-tianmao") ? "0" : "1";// 0天猫 1淘宝
						String goodsId = Utils.findGoodsId(resultStr);
						if (!StringUtils.isEmpty(shopName)) {// shopName为空表示为广告商品
																// 白名单过滤
							KeyWordScanTb keyWordScanTb = new KeyWordScanTb();
							keyWordScanTb.setId(UUID.randomUUID().toString());
							keyWordScanTb.setKeyWord(Utils.findKeyWordByUrl(keyWordScanUrl));
							keyWordScanTb.setShopName(shopName);
							keyWordScanTb.setShopId(shopId);
							keyWordScanTb.setGoodsId(goodsId);
							keyWordScanTb.setCrtUser("admin");
							keyWordScanTb.setCrtTime(new Date());
							keyWordScanTb.setCrtIp("127.0.0.1");
							keyWordScanTb.setStatus("0");
							keyWordScanTb.setShopType(shopType);
							keyWordScanTbMapper.insert(keyWordScanTb);

							GoodsTb goodsTb = new GoodsTb();
							goodsTb.setId(UUID.randomUUID().toString());
							goodsTb.setShopId(shopId);
							goodsTb.setGoodsId(goodsId);
							goodsTb.setCrtUser("admin");
							goodsTb.setCrtTime(new Date());
							goodsTb.setCrtIp("127.0.0.1");
							goodsTb.setStatus("0");
							goodsTbMapper.insert(goodsTb);

							ShopTb shopTb = new ShopTb();
							shopTb.setId(UUID.randomUUID().toString());
							shopTb.setShopName(shopName);
							shopTb.setShopId(shopId);
							shopTb.setCrtUser("admin");
							shopTb.setCrtTime(new Date());
							shopTb.setCrtIp("127.0.0.1");
							shopTb.setStatus("0");
							shopTb.setShopType(shopType);
							shopTbMapper.insert(shopTb);
						}
					}
				}
			}
		}
	}

	@KafkaListener(topics = { "shop-scan" })
	public void shopScan(String rateUrl) {
		System.out.println("shop-scan-消费---" + rateUrl);
		PhantomJSDownloader phantomDownloader = new PhantomJSDownloader(phantomJsExePath, crawlJsPath).setRetryNum(3);
		CollectorPipeline<ResultItems> collectorPipeline = new ResultItemsCollectorPipeline();
		Spider.create(new TaoBaoShopProcessor()).addUrl(rateUrl).setDownloader(phantomDownloader)
				.addPipeline(collectorPipeline).thread((Runtime.getRuntime().availableProcessors() - 1) << 1).run();
		System.out.println("start---------------shop-scan-消费---" + rateUrl);
		List<ResultItems> resultItemsList = collectorPipeline.getCollected();
		System.out.println("middle---------------shop-scan-消费---resultItemsList" + resultItemsList.size());
		for (ResultItems resultItems : resultItemsList) {
			String shopType = resultItems.get("shop_type");
			if (shopType.equals("-1")) {// 失败，继续进行扫描
				System.out.println("-1-1-1-1-1-1-1-1-1-1-1" );
				ListenableFuture future = kafkaTemplate.send("shop-scan", rateUrl);
				future.addCallback(
						o -> System.out.println("shop-scan-消息发送成功：" + rateUrl),
						throwable -> System.out
								.println("shop-scan消息发送失败：" + rateUrl));
			}else {
				ShopScanTb shopScanTb = new ShopScanTb();
				shopScanTb.setId(UUID.randomUUID().toString());
				shopScanTb.setShopId(resultItems.get("shop_id"));
				shopScanTb.setShopName(resultItems.get("shop_name"));
				shopScanTb.setSellerNick(resultItems.get("seller_nick"));
				shopScanTb.setShopStar(resultItems.get("shop_star"));
				shopScanTb.setDsrDesc(resultItems.get("dsr_desc"));
				shopScanTb.setDsrLogi(resultItems.get("dsr_logi"));
				shopScanTb.setDsrServ(resultItems.get("dsr_serv"));
				shopScanTb.setCrtUser("admin");
				shopScanTb.setCrtTime(new Date());
				shopScanTb.setCrtIp("127.0.0.1");
				shopScanTb.setStatus("0");
				shopScanTbMapper.insert(shopScanTb);
			}
		}
		System.out.println("stop---------------shop-scan-消费---" + rateUrl);
	}
	
	@KafkaListener(topics = { "rate-scan" })
	public void rateScan(String rateScanUrl) {
		System.out.println("rate-scan-消费---" + rateScanUrl);
		PhantomJSDownloader phantomDownloader = new PhantomJSDownloader(phantomJsExePath, crawlJsPath).setRetryNum(3);
		CollectorPipeline<ResultItems> collectorPipeline = new ResultItemsCollectorPipeline();
		Spider.create(new TaoBaoRateUrlProcessor()).addUrl(rateScanUrl).setDownloader(phantomDownloader)
				.addPipeline(collectorPipeline).thread((Runtime.getRuntime().availableProcessors() - 1) << 1).run();
		List<ResultItems> resultItemsList = collectorPipeline.getCollected();
		for (ResultItems resultItems : resultItemsList) {
			String rateUrl = resultItems.get("rateUrl");
			if (StringUtils.isEmpty(rateUrl)) {// 重新扫描此URL
				//TO-DO 记录此事件
				ListenableFuture future = kafkaTemplate.send("rate-scan", rateScanUrl);
				future.addCallback(
						o -> System.out.println("rate-scan-消息发送成功：" + resultItems.get("url").toString()),
						throwable -> System.out
								.println("rate-scan消息发送失败：" + resultItems.get("url").toString()));
			} else {
				ListenableFuture future = kafkaTemplate.send("shop-scan", rateUrl);
				future.addCallback(
						o -> System.out.println("shop-scan-消息发送成功：" + rateUrl),
						throwable -> System.out
								.println("shop-scan消息发送失败：" + rateUrl));
			}
		}
	}
	
	@KafkaListener(topics = { "comment-total-scan" })
	public void commentTotalScan(String commentTotalScanUrl) {
		System.out.println("comment-total-scan-消费---" + commentTotalScanUrl);
		PhantomJSDownloader phantomDownloader = new PhantomJSDownloader(phantomJsExePath, crawlJsPath).setRetryNum(3);
		CollectorPipeline<ResultItems> collectorPipeline = new ResultItemsCollectorPipeline();
		Spider.create(new TaoBaoCommentTotalProcessor()).addUrl(commentTotalScanUrl).setDownloader(phantomDownloader)
				.addPipeline(collectorPipeline).thread((Runtime.getRuntime().availableProcessors() - 1) << 1).run();
		List<ResultItems> resultItemsList = collectorPipeline.getCollected();
		for (ResultItems resultItems : resultItemsList) {
			String count = resultItems.get("count");
			if (StringUtils.isEmpty(count)) {// 重新扫描此URL
				//TO-DO 记录此事件
				ListenableFuture future = kafkaTemplate.send("comment-total-scan", commentTotalScanUrl);
				future.addCallback(
						o -> System.out.println("comment-total-scan-消息发送成功：" + commentTotalScanUrl),
						throwable -> System.out
								.println("comment-total-scan-消息发送失败：" + commentTotalScanUrl));
			} else {
				//TO-DO 把此数据记录在表中
				CommentTb commentTb = new  CommentTb();
				commentTb.setId(UUID.randomUUID().toString());
				commentTb.setCommentCount(Integer.parseInt(count));
				commentTb.setGoodsId(Utils.findGoodsIdByCommentTotalUrl(commentTotalScanUrl));
				commentTb.setCrtUser("admin");
				commentTb.setCrtTime(new Date());
				commentTb.setCrtIp("127.0.0.1");
				commentTb.setStatus("0");
				commentTbMapper.insert(commentTb);
			}
		}
	}
	
	@KafkaListener(topics = { "comment-detail-scan" })
	public void commentDetailScan(String commentDetailScanUrl) {
		System.out.println("comment-detail-scan-消费---" + commentDetailScanUrl);
		PhantomJSDownloader phantomDownloader = new PhantomJSDownloader(phantomJsExePath, crawlJsPath).setRetryNum(3);
		CollectorPipeline<ResultItems> collectorPipeline = new ResultItemsCollectorPipeline();
		Spider.create(new TaoBaoCommentDetailProcessor()).addUrl(commentDetailScanUrl).setDownloader(phantomDownloader)
				.addPipeline(collectorPipeline).thread((Runtime.getRuntime().availableProcessors() - 1) << 1).run();
		List<ResultItems> resultItemsList = collectorPipeline.getCollected();
		for (ResultItems resultItems : resultItemsList) {
			String antiCrawler = resultItems.get("antiCrawler");
			if (antiCrawler.equals("1")) {// 重新扫描此URL
				//TO-DO 记录此事件
				ListenableFuture future = kafkaTemplate.send("comment-detail-scan", commentDetailScanUrl);
				future.addCallback(
						o -> System.out.println("comment-detail-scan-消息发送成功：" + commentDetailScanUrl),
						throwable -> System.out
								.println("comment-detail-scan-消息发送失败：" + commentDetailScanUrl));
			} else {
				//TO-DO 把此数据记录在表中
				List<CommentScanTb> commentScanTbList = resultItems.get("commentScanTbList");
				for(CommentScanTb commentScanTb:commentScanTbList) {
					commentScanTb.setId(UUID.randomUUID().toString());
					commentScanTb.setGoodsId(Utils.findGoodsIdByUrl(commentDetailScanUrl));
					commentScanTb.setCrtUser("admin");
					commentScanTb.setCrtTime(new Date());
					commentScanTb.setCrtIp("127.0.0.1");
					commentScanTb.setStatus("0");
					commentScanTbMapper.insert(commentScanTb);
				}
			}
		}
	}

}
