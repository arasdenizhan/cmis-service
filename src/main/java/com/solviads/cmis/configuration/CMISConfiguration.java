package com.solviads.cmis.configuration;

import lombok.AllArgsConstructor;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class CMISConfiguration {

    private final OAuth2PropertiesConfiguration oAuth2PropertiesConfiguration;

    @Bean
    public OAuthClient oAuthClient() {
        return new OAuthClient(new URLConnectionClient());
    }

    @Bean(name = "oAuthClientRequest")
    public OAuthClientRequest oAuthClientRequest() throws OAuthSystemException {
        return OAuthClientRequest
                .tokenLocation(oAuth2PropertiesConfiguration.getUri())
                .setGrantType(GrantType.CLIENT_CREDENTIALS)
                .setClientId(oAuth2PropertiesConfiguration.getClientId())
                .setClientSecret(oAuth2PropertiesConfiguration.getSecretKey())
                .buildBodyMessage();
    }

}
