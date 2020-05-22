package com.coding.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties("app")
public class AppProperties {
    /**
     * appId
     */
    private String title;


    private String endPoint = "http://39.97.219.174:9000";
    private String ak = "admin";
    private String sk = "admin123";
    private String bn = "aviation";
}
