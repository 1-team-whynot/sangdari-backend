package com.sangdari.global.config;

import org.apache.tomcat.util.net.openssl.ciphers.Protocol;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.sound.sampled.Port;
import java.util.List;

@ConfigurationProperties(prefix = "cors")
public record CorsConfig(

        /*
          허용할 Origin:
          [Protocol] + [Host(Domain)] + [Port] 설정
          [http] + [localhost] + [5173]
         */
        List<String> allowedOrigins,
        Long maxAge
) {
}
