package com.conan.crawler.server.post.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.conan.crawler.server.post.crawler.TaoBaoKeyWordProcessor;
import com.conan.crawler.server.post.entity.GoodsTb;
import com.conan.crawler.server.post.entity.KeyWordScanTb;
import com.conan.crawler.server.post.entity.KeyWordTb;
import com.conan.crawler.server.post.entity.ResponseResult;
import com.conan.crawler.server.post.entity.SellerTb;
import com.conan.crawler.server.post.mapper.GoodsTbMapper;
import com.conan.crawler.server.post.mapper.KeyWordScanTbMapper;
import com.conan.crawler.server.post.mapper.KeyWordTbMapper;
import com.conan.crawler.server.post.mapper.SellerTbMapper;
import com.conan.crawler.server.post.util.Utils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.PhantomJSDownloader;
import us.codecraft.webmagic.pipeline.CollectorPipeline;
import us.codecraft.webmagic.pipeline.ResultItemsCollectorPipeline;

@RestController
@RequestMapping("key-word")
@Component
public class KeyWordController {

	private String phantomJsExePath = "/opt/phantomjs-2.1.1-linux-x86_64/bin/phantomjs";

	private String crawlJsPath = "/opt/crawl.js";

	public static LinkedBlockingQueue<Map<String, String>> linkedBlockingQueue = new LinkedBlockingQueue<>();

	@Autowired
	private KeyWordTbMapper keyWordTbMapper;

	@Autowired
	private KeyWordScanTbMapper keyWordScanTbMapper;

	@Autowired
	private GoodsTbMapper goodsTbMapper;

	@Autowired
	private SellerTbMapper sellerTbMapper;

	@RequestMapping(value = "scan", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseResult> postKeyWordScanStart(@RequestBody Map<String, String> map) {
		linkedBlockingQueue.add(map);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Scheduled(fixedDelay = 1000, initialDelay = 60000)
	public void processKeyWord() {
		if(linkedBlockingQueue.isEmpty()) {
			return;
		}
		Map<String, String> map = linkedBlockingQueue.poll();
		System.out.println("key-word-scan-消费--" + map.get("key"));
		System.out.println("key-word-scan-消费--" + map.get("value"));
		PhantomJSDownloader phantomDownloader = new PhantomJSDownloader(phantomJsExePath, crawlJsPath).setRetryNum(3);
		CollectorPipeline<ResultItems> collectorPipeline = new ResultItemsCollectorPipeline();
		Spider.create(new TaoBaoKeyWordProcessor()).addUrl(map.get("value")).setDownloader(phantomDownloader)
				.addPipeline(collectorPipeline).thread(1).run();
		List<ResultItems> resultItemsList = collectorPipeline.getCollected();
		for (ResultItems resultItems : resultItemsList) {
			if ((boolean) resultItems.get("pageExist")) {// 判断当前页面是否存在
				List<String> resultList = new ArrayList<>();
				resultList = resultItems.get("result");
				if (resultList.isEmpty()) {// 没有获取到数据需要重新获取
					// to-do 处理异常
					KeyWordTb keyWordTb = keyWordTbMapper.selectByPrimaryKey(map.get("id"));
					keyWordTb.setStatus("-1");
					keyWordTbMapper.updateByPrimaryKey(keyWordTb);
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
							keyWordScanTb.setKeyWord(map.get("key"));
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
					KeyWordTb keyWordTb = keyWordTbMapper.selectByPrimaryKey(map.get("id"));
					keyWordTb.setStatus("2");
					keyWordTbMapper.updateByPrimaryKey(keyWordTb);
				}
			}
		}
	}

}
