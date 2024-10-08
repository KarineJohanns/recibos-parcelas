package com.gerarecibos.recibos;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("file")
public class FileStorageProperties {
    private String uploadDir;

}
