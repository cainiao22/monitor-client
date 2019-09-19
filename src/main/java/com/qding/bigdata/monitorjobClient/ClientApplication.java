package com.qding.bigdata.monitorjobClient;

import com.qding.bigdata.monitorjobClient.utils.MultiJobRunner;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;

//exclude 避免程序一启动就去初始化数据源，因为现在数据源有两个，初始化的时候回报错
//@ImportResource(locations = {"classpath:application-dataSource.xml"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableScheduling
public class ClientApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ClientApplication.class, args);
		ZkService.start();
		MultiJobRunner.initialize();
	}
}
