package com.conan.crawler.server.post.consumer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import com.conan.crawler.server.post.crawler.TaoBaoCommentDetailProcessor;
import com.conan.crawler.server.post.crawler.TaoBaoCommentTotalProcessor;
import com.conan.crawler.server.post.crawler.TaoBaoKeyWordProcessor;
import com.conan.crawler.server.post.crawler.TaoBaoShopProcessor;
import com.conan.crawler.server.post.entity.CommentScanTb;
import com.conan.crawler.server.post.entity.CommentTb;
import com.conan.crawler.server.post.entity.GoodsTb;
import com.conan.crawler.server.post.entity.KeyWordScanTb;
import com.conan.crawler.server.post.entity.SellerTb;
import com.conan.crawler.server.post.entity.ShopScanTb;
import com.conan.crawler.server.post.mapper.CommentScanTbMapper;
import com.conan.crawler.server.post.mapper.CommentTbMapper;
import com.conan.crawler.server.post.mapper.GoodsTbMapper;
import com.conan.crawler.server.post.mapper.KeyWordScanTbMapper;
import com.conan.crawler.server.post.mapper.KeyWordTbMapper;
import com.conan.crawler.server.post.mapper.SellerTbMapper;
import com.conan.crawler.server.post.mapper.ShopScanTbMapper;
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

	private String phantomJsExePath = "/opt/phantomjs-2.1.1-linux-x86_64/bin/phantomjs";

	private String crawlJsPath = "/opt/crawl.js";

	@Autowired
	private KeyWordTbMapper keyWordTbMapper;

	@Autowired
	private KeyWordScanTbMapper keyWordScanTbMapper;

	@Autowired
	private GoodsTbMapper goodsTbMapper;

	@Autowired
	private SellerTbMapper sellerTbMapper;

	@Autowired
	private ShopScanTbMapper shopScanTbMapper;

	@Autowired
	private CommentScanTbMapper commentScanTbMapper;

	@Autowired
	private CommentTbMapper commentTbMapper;

	@Autowired
	private KafkaTemplate kafkaTemplate;

	@KafkaListener(topics = { "key-word-scan" })
	public void keyWordScan(ConsumerRecord<String, String> record)
			throws InterruptedException, UnsupportedEncodingException {
		System.out.println("key-word-scan-消费--" + record.key());
		System.out.println("key-word-scan-消费--" + record.value());
		PhantomJSDownloader phantomDownloader = new PhantomJSDownloader(phantomJsExePath, crawlJsPath).setRetryNum(3);
		CollectorPipeline<ResultItems> collectorPipeline = new ResultItemsCollectorPipeline();
		Spider.create(new TaoBaoKeyWordProcessor()).addUrl(record.value()).setDownloader(phantomDownloader)
				.addPipeline(collectorPipeline).thread(1).run();
		List<ResultItems> resultItemsList = collectorPipeline.getCollected();
		for (ResultItems resultItems : resultItemsList) {
			if ((boolean) resultItems.get("pageExist")) {// 判断当前页面是否存在
				List<String> resultList = new ArrayList<>();
				resultList = resultItems.get("result");
				if (resultList.isEmpty()) {// 没有获取到数据需要重新获取
					System.out.println("comsumer start---key-word-scan---" + record.key() + "---" + record.value());
					ListenableFuture future = kafkaTemplate.send("key-word-scan", record.key(), record.value());
					System.out.println("comsumer end---key-word-scan---" + record.key() + "---" + record.value());
				} else {
					for (String resultStr : resultList) {
						String itemId = Utils.findItemId(resultStr);
						String itemTitle = Utils.findItemTitle(resultStr);
						String userNumberId = Utils.findUserNumberId(resultStr);
						String shopType = resultStr.contains("icon-service-tianmao") ? "0" : "1";// 0天猫 1淘宝

						if (!StringUtils.isEmpty(Utils.findShopName(resultStr))) {// shopName为空表示为广告商品
							// 白名单过滤
							KeyWordScanTb keyWordScanTb = new KeyWordScanTb();
							keyWordScanTb.setId(UUID.randomUUID().toString());
							keyWordScanTb.setKeyWord(record.key());
							keyWordScanTb.setItemId(itemId);
							keyWordScanTb.setItemTitle(itemTitle);
							keyWordScanTb.setUserNumberId(userNumberId);
							keyWordScanTb.setShopType(shopType);

							keyWordScanTb.setCrtUser("admin");
							keyWordScanTb.setCrtTime(new Date());
							keyWordScanTb.setCrtIp(Utils.getIp());
							keyWordScanTb.setStatus("0");
							keyWordScanTbMapper.insert(keyWordScanTb);

							GoodsTb goodsTb = new GoodsTb();
							goodsTb.setId(UUID.randomUUID().toString());
							goodsTb.setItemId(itemId);
							goodsTb.setItemTitle(itemTitle);
							goodsTb.setUserNumberId(userNumberId);
							goodsTb.setShopType(shopType);

							goodsTb.setCrtUser("admin");
							goodsTb.setCrtTime(new Date());
							goodsTb.setCrtIp(Utils.getIp());
							goodsTb.setStatus("0");
							goodsTbMapper.insert(goodsTb);

							SellerTb sellerTb = new SellerTb();
							sellerTb.setId(UUID.randomUUID().toString());
							sellerTb.setUserNumberId(userNumberId);
							sellerTb.setShopType(shopType);

							sellerTb.setCrtUser("admin");
							sellerTb.setCrtTime(new Date());
							sellerTb.setCrtIp(Utils.getIp());
							sellerTb.setStatus("0");

							sellerTbMapper.insert(sellerTb);
						}
					}
				}
			}
		}
	}

	@KafkaListener(topics = { "shop-scan" })
	public void shopScan(ConsumerRecord<String, String> record) {
		System.out.println("shop-scan-消费--" + record.key());
		System.out.println("shop-scan-消费--" + record.value());
		PhantomJSDownloader phantomDownloader = new PhantomJSDownloader(phantomJsExePath, crawlJsPath).setRetryNum(3);
		CollectorPipeline<ResultItems> collectorPipeline = new ResultItemsCollectorPipeline();
		Spider.create(new TaoBaoShopProcessor()).addUrl(record.value()).setDownloader(phantomDownloader)
				.addPipeline(collectorPipeline).thread(1).run();
		List<ResultItems> resultItemsList = collectorPipeline.getCollected();
		for (ResultItems resultItems : resultItemsList) {
			String shopType = resultItems.get("shop_type");
			if (shopType.equals("-1")) {// 失败，继续进行扫描
				System.out.println("producer shopScan start---shop-scan---" + record.key() + "---" + record.value());
				ListenableFuture future = kafkaTemplate.send("shop-scan", record.key(), record.value());
				System.out.println("producer shopScan start---shop-scan---" + record.key() + "---" + record.value());
			} else {
				ShopScanTb shopScanTb = new ShopScanTb();
				shopScanTb.setId(UUID.randomUUID().toString());
				shopScanTb.setShopId(resultItems.get("shop_id"));
				shopScanTb.setShopName(resultItems.get("shop_name"));
				shopScanTb.setSellerNick(resultItems.get("seller_nick"));
				shopScanTb.setShopStar(resultItems.get("shop_star"));
				shopScanTb.setDsrDesc(resultItems.get("dsr_desc"));
				shopScanTb.setDsrLogi(resultItems.get("dsr_logi"));
				shopScanTb.setDsrServ(resultItems.get("dsr_serv"));
				shopScanTb.setUserNumberId(record.key());
				shopScanTb.setCrtUser("admin");
				shopScanTb.setCrtTime(new Date());
				shopScanTb.setCrtIp(Utils.getIp());
				shopScanTb.setStatus("0");
				shopScanTbMapper.insert(shopScanTb);
			}
		}
	}

	@KafkaListener(topics = { "comment-total-scan" })
	public void commentTotalScan(ConsumerRecord<String, String> record) {
		System.out.println("comment-total-scan-消费--" + record.key() + "-------" + record.value());
		PhantomJSDownloader phantomDownloader = new PhantomJSDownloader(phantomJsExePath, crawlJsPath).setRetryNum(3);
		CollectorPipeline<ResultItems> collectorPipeline = new ResultItemsCollectorPipeline();
		Spider.create(new TaoBaoCommentTotalProcessor()).addUrl(record.value()).setDownloader(phantomDownloader)
				.addPipeline(collectorPipeline).thread(1).run();
		List<ResultItems> resultItemsList = collectorPipeline.getCollected();
		for (ResultItems resultItems : resultItemsList) {
			String count = resultItems.get("count");
			if (StringUtils.isEmpty(count)) {// 重新扫描此URL
				// TO-DO 记录此事件
				System.out.println("produce commentTotalScan start---comment-total-scan---" + record.key() + "---"
						+ record.value());
				ListenableFuture future = kafkaTemplate.send("comment-total-scan", record.key(), record.value());
				System.out.println("produce commentTotalScan start---comment-total-scan---" + record.key() + "---"
						+ record.value());
			} else {
				// TO-DO 把此数据记录在表中
				CommentTb commentTb = new CommentTb();
				commentTb.setId(UUID.randomUUID().toString());
				commentTb.setItemId(record.key());
				commentTb.setTotal(count);
				commentTb.setCrtUser("admin");
				commentTb.setCrtTime(new Date());
				commentTb.setCrtIp(Utils.getIp());
				commentTb.setStatus("0");
				commentTbMapper.insert(commentTb);
			}
		}
	}

	@KafkaListener(topics = { "comment-detail-scan" })
	public void commentDetailScan(ConsumerRecord<String, String> record) {
		System.out.println("comment-detail-scan-消费--" + record.key() + "-------" + record.value());
		PhantomJSDownloader phantomDownloader = new PhantomJSDownloader(phantomJsExePath, crawlJsPath).setRetryNum(3);
		CollectorPipeline<ResultItems> collectorPipeline = new ResultItemsCollectorPipeline();
		Spider.create(new TaoBaoCommentDetailProcessor()).addUrl(record.value()).setDownloader(phantomDownloader)
				.addPipeline(collectorPipeline).thread(1).run();
		List<ResultItems> resultItemsList = collectorPipeline.getCollected();
		for (ResultItems resultItems : resultItemsList) {
			String antiCrawler = resultItems.get("antiCrawler");
			if (antiCrawler.equals("1")) {// 重新扫描此URL
				System.out.println("produce commentDetailScan start---comment-detail-scan---" + record.key() + "---"
						+ record.value());
				ListenableFuture future = kafkaTemplate.send("comment-detail-scan", record.key(), record.value());
				System.out.println("produce commentDetailScan end---comment-detail-scan---" + record.key() + "---"
						+ record.value());
			} else if (antiCrawler.equals("0")) {
				// TO-DO 把此数据记录在表中
				List<CommentScanTb> commentScanTbList = resultItems.get("commentScanTbList");
				for (CommentScanTb commentScanTb : commentScanTbList) {
					commentScanTb.setId(UUID.randomUUID().toString());
					commentScanTb.setItemId(record.key());
					commentScanTb.setCrtUser("admin");
					commentScanTb.setCrtTime(new Date());
					commentScanTb.setCrtIp(Utils.getIp());
					commentScanTb.setStatus("0");
					commentScanTbMapper.insert(commentScanTb);
				}
			}
		}
	}

}
