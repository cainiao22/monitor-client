package com.qding.bigdata.monitorjobClient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author yanpf
 * @date 2018/8/23 11:16
 * @description
 */

@Configuration
@Component
@PropertySource("classpath:/config/wechat_alarm.properties")
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "map.wechat.alarm")
public class WechatAlarmConfig {

    private Map<String, String> common;

    private Map<String, String> owners;

    public Map<String, String> getCommon() {
        return common;
    }

    public void setCommon(Map<String, String> common) {
        this.common = common;
    }

    public Map<String, String> getOwners() {
        return owners;
    }

    public void setOwners(Map<String, String> owners) {
        this.owners = owners;
    }
}
