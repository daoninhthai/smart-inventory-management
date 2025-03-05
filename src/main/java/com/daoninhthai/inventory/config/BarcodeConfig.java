package com.daoninhthai.inventory.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "inventory.barcode")
public class BarcodeConfig {

    private int width = 300;
    private int height = 100;
    private int qrSize = 250;
    private String format = "CODE_128";
    private int margin = 1;
    private String imageFormat = "PNG";
}
