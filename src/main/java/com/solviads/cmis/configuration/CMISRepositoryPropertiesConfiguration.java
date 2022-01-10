package com.solviads.cmis.configuration;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

@ConfigurationProperties(prefix = "cmis.repository")
@Getter
@Setter
public class CMISRepositoryPropertiesConfiguration {

    private String uri;

    @PostConstruct
    public void onInit() {
        Assert.hasText(uri, "");
    }
}
