package com.conan.crawler.server.post.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.conan.crawler.server.post.crawler.TaoBaoShopProcessor;
import com.conan.crawler.server.post.entity.ResponseResult;
import com.conan.crawler.server.post.entity.SellerTb;
import com.conan.crawler.server.post.entity.ShopScanTb;
import com.conan.crawler.server.post.mapper.SellerTbMapper;
import com.conan.crawler.server.post.mapper.ShopScanTbMapper;
import com.conan.crawler.server.post.util.Utils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.PhantomJSDownloader;
import us.codecraft.webmagic.pipeline.CollectorPipeline;
import us.codecraft.webmagic.pipeline.ResultItemsCollectorPipeline;

@RestController
@RequestMapping("shop")
public class ShopController {
	private String phantomJsExePath = "/opt/phantomjs-2.1.1-linux-x86_64/bin/phantomjs";

	private String crawlJsPath = "/opt/crawl.js";

	public static LinkedBlockingQueue<Map<String, String>> linkedBlockingQueue = new LinkedBlockingQueue<>();

	@Autowired
	private SellerTbMapper sellerTbMapper;
	
	@Autowired
	private ShopScanTbMapper shopScanTbMapper;

	@RequestMapping(value = "scan", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseResult> postShopScanStart(@RequestParam("id") String id,
			@RequestParam("key") String key, @RequestParam("value") String value) {
		Map<String, String> map = new HashMap<>();
		map.put("id", id);
		map.put("key", key);
		map.put("value", value);
		linkedBlockingQueue.add(map);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Scheduled(fixedDelay = 1000, initialDelay = 60000)
	public void processShop() {
		if(linkedBlockingQueue.isEmpty()) {
			return;
		}
		Map<String, String> map = linkedBlockingQueue.poll();
		System.out.println("shop-scan-消费--" + map.get("key"));
		System.out.println("shop-scan-消费--" + map.get("value"));
		PhantomJSDownloader phantomDownloader = new PhantomJSDownloader(phantomJsExePath, crawlJsPath).setRetryNum(3);
		CollectorPipeline<ResultItems> collectorPipeline = new ResultItemsCollectorPipeline();
		Spider.create(new TaoBaoShopProcessor()).addUrl(map.get("value")).setDownloader(phantomDownloader)
				.addPipeline(collectorPipeline).thread(1).run();
		List<ResultItems> resultItemsList = collectorPipeline.getCollected();
		for (ResultItems resultItems : resultItemsList) {
			String shopType = resultItems.get("shop_type");
			if (shopType.equals("-1")) {// 失败，继续进行扫描
				//异常处理
				SellerTb sellerTb = sellerTbMapper.selectByPrimaryKey(map.get("key"));
				sellerTb.setStatus("-1");
				sellerTbMapper.updateByPrimaryKey(sellerTb);
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
				shopScanTb.setUserNumberId(map.get("key"));
				shopScanTb.setCrtUser("admin");
				shopScanTb.setCrtTime(new Date());
				shopScanTb.setCrtIp(Utils.getIp());
				shopScanTb.setStatus("0");
				shopScanTbMapper.insert(shopScanTb);
				SellerTb sellerTb = sellerTbMapper.selectByPrimaryKey(map.get("key"));
				sellerTb.setStatus("2");
				sellerTbMapper.updateByPrimaryKey(sellerTb);
			}
		}
	}
}
