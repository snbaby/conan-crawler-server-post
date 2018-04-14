package com.conan.crawler.server.post.rest;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.conan.crawler.server.post.crawler.TaoBaoCommentDetailProcessor;
import com.conan.crawler.server.post.crawler.TaoBaoCommentTotalProcessor;
import com.conan.crawler.server.post.entity.CommentScanTb;
import com.conan.crawler.server.post.entity.CommentTb;
import com.conan.crawler.server.post.entity.GoodsTb;
import com.conan.crawler.server.post.entity.ResponseResult;
import com.conan.crawler.server.post.mapper.CommentScanTbMapper;
import com.conan.crawler.server.post.mapper.CommentTbMapper;
import com.conan.crawler.server.post.mapper.GoodsTbMapper;
import com.conan.crawler.server.post.util.Utils;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.PhantomJSDownloader;
import us.codecraft.webmagic.pipeline.CollectorPipeline;
import us.codecraft.webmagic.pipeline.ResultItemsCollectorPipeline;

@RestController
@RequestMapping("comment")
public class CommentController {

	private String phantomJsExePath = "/opt/phantomjs-2.1.1-linux-x86_64/bin/phantomjs";

	private String crawlJsPath = "/opt/crawl.js";

	public static LinkedBlockingQueue<Map<String, String>> totalLinkedBlockingQueue = new LinkedBlockingQueue<>();

	public static LinkedBlockingQueue<Map<String, String>> detailLinkedBlockingQueue = new LinkedBlockingQueue<>();

	@Autowired
	private GoodsTbMapper goodsTbMapper;
	@Autowired
	private CommentTbMapper commentTbMapper;
	@Autowired
	private CommentScanTbMapper commentScanTbMapper;

	@RequestMapping(value = "total-scan", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseResult> postCommentScanTotalStart(@RequestParam("id") String id,
			@RequestParam("key") String key, @RequestParam("value") String value) {
		Map<String, String> map = new HashMap<>();
		map.put("id", id);
		map.put("key", key);
		map.put("value", value);
		totalLinkedBlockingQueue.add(map);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Scheduled(fixedDelay = 1000, initialDelay = 60000)
	public void processCommentTotal() {
		if (totalLinkedBlockingQueue.isEmpty()) {
			return;
		}
		Map<String, String> map = totalLinkedBlockingQueue.poll();
		System.out.println("comment-total-scan-消费--" + map.get("key") + "-------" + map.get("value"));
		PhantomJSDownloader phantomDownloader = new PhantomJSDownloader(phantomJsExePath, crawlJsPath).setRetryNum(3);
		CollectorPipeline<ResultItems> collectorPipeline = new ResultItemsCollectorPipeline();
		Spider.create(new TaoBaoCommentTotalProcessor()).addUrl(map.get("value")).setDownloader(phantomDownloader)
				.addPipeline(collectorPipeline).thread(1).run();
		List<ResultItems> resultItemsList = collectorPipeline.getCollected();
		for (ResultItems resultItems : resultItemsList) {
			String count = resultItems.get("count");
			if (StringUtils.isEmpty(count)) {// 重新扫描此URL
				// TO-DO 记录此事件
				GoodsTb goodsTb = goodsTbMapper.selectByPrimaryKey(map.get("id"));
				goodsTb.setStatus("-1");
				goodsTbMapper.updateByPrimaryKey(goodsTb);
			} else {
				// TO-DO 把此数据记录在表中
				CommentTb commentTb = new CommentTb();
				commentTb.setId(UUID.randomUUID().toString());
				commentTb.setItemId(map.get("key"));
				commentTb.setTotal(count);
				commentTb.setCrtUser("admin");
				commentTb.setCrtTime(new Date());
				commentTb.setCrtIp(Utils.getIp());
				commentTb.setStatus("0");
				commentTbMapper.insert(commentTb);
				GoodsTb goodsTb = goodsTbMapper.selectByPrimaryKey(map.get("id"));
				goodsTb.setStatus("2");
				goodsTbMapper.updateByPrimaryKey(goodsTb);
			}
		}
	}

	@RequestMapping(value = "detail-scan", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<ResponseResult> postCommentScanDetailStart(@RequestParam("id") String id,
			@RequestParam("key") String key, @RequestParam("value") String value) {
		Map<String, String> map = new HashMap<>();
		map.put("id", id);
		map.put("key", key);
		map.put("value", value);
		detailLinkedBlockingQueue.add(map);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Scheduled(fixedDelay = 1000, initialDelay = 60000)
	public void processDetail() {
		if (detailLinkedBlockingQueue.isEmpty()) {
			return;
		}
		Map<String, String> map = detailLinkedBlockingQueue.poll();
		System.out.println("comment-detail-scan-消费--" + map.get("key") + "-------" + map.get("value"));
		PhantomJSDownloader phantomDownloader = new PhantomJSDownloader(phantomJsExePath, crawlJsPath).setRetryNum(3);
		CollectorPipeline<ResultItems> collectorPipeline = new ResultItemsCollectorPipeline();
		Spider.create(new TaoBaoCommentDetailProcessor()).addUrl(map.get("value")).setDownloader(phantomDownloader)
				.addPipeline(collectorPipeline).thread(1).run();
		List<ResultItems> resultItemsList = collectorPipeline.getCollected();
		for (ResultItems resultItems : resultItemsList) {
			String antiCrawler = resultItems.get("antiCrawler");
			if (antiCrawler.equals("1")) {// 重新扫描此URL
				// to-do
				CommentTb commentTb = commentTbMapper.selectByPrimaryKey(map.get("id"));
				commentTb.setStatus("-1");
				commentTbMapper.updateByPrimaryKey(commentTb);
			} else if (antiCrawler.equals("0")) {
				// TO-DO 把此数据记录在表中
				List<CommentScanTb> commentScanTbList = resultItems.get("commentScanTbList");
				for (CommentScanTb commentScanTb : commentScanTbList) {
					commentScanTb.setId(UUID.randomUUID().toString());
					commentScanTb.setItemId(map.get("key"));
					commentScanTb.setCrtUser("admin");
					commentScanTb.setCrtTime(new Date());
					commentScanTb.setCrtIp(Utils.getIp());
					commentScanTb.setStatus("0");
					commentScanTbMapper.insert(commentScanTb);
				}
				CommentTb commentTb = commentTbMapper.selectByPrimaryKey(map.get("id"));
				commentTb.setStatus("2");
				commentTbMapper.updateByPrimaryKey(commentTb);
			}
		}
	}
}
