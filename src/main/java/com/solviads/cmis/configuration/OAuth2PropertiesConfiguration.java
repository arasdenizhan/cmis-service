package com.solviads.cmis.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

@ConfigurationProperties(prefix = "dms.oauth2")
@Getter
@Setter
public class OAuth2PropertiesConfiguration {

    private String uri;
    private String clientId;
    private String secretKey;

    @PostConstruct
    public void onInit() {
        Assert.hasText(uri, "");
        Assert.hasText(clientId, "");
        Assert.hasText(secretKey, "");
    }
}
