package com.conan.crawler.server.post;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.conan.crawler.server.post.mapper")
public class ConanCrawlerServerPostApplication {
	public static void main(String[] args) {
		SpringApplication.run(ConanCrawlerServerPostApplication.class, args);
	}
}
