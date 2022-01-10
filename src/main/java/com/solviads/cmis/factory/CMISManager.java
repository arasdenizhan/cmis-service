package com.solviads.cmis.factory;

import com.solviads.cmis.configuration.CMISRepositoryPropertiesConfiguration;
import com.solviads.cmis.exceptions.AccessTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public final class CMISManager {

    private final OAuthClient oAuthClient;
    private final OAuthClientRequest oAuthClientRequest;
    private final CMISRepositoryPropertiesConfiguration cmisRepositoryPropertiesConfiguration;
    private final SessionFactoryImpl sessionFactory = SessionFactoryImpl.newInstance();
    private TokenParameter tokenParameter;


    @PostConstruct
    public void onInit() {
        prepareToken();
    }

    public Session openSession(String repositoryId) {
        Map<String, String> parameters = prepareDefaultSessionParameter();
        parameters.put(SessionParameter.REPOSITORY_ID, repositoryId);
        return sessionFactory.createSession(parameters);
    }

    public List<Repository> getRepositories() {
        return sessionFactory.getRepositories(prepareDefaultSessionParameter());
    }

    private Map<String, String> prepareDefaultSessionParameter() {
        prepareToken();
        var sessionParameters = new HashMap<String, String>();
        sessionParameters.put(SessionParameter.OAUTH_ACCESS_TOKEN, tokenParameter.token());
        sessionParameters.put(SessionParameter.OAUTH_REFRESH_TOKEN, tokenParameter.refreshToken());
        sessionParameters.put(SessionParameter.AUTH_OAUTH_BEARER, Boolean.TRUE.toString());
        sessionParameters.put(SessionParameter.AUTH_HTTP_BASIC, Boolean.FALSE.toString());
        sessionParameters.put(SessionParameter.BROWSER_URL, cmisRepositoryPropertiesConfiguration.getUri());
        sessionParameters.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
        return sessionParameters;
    }

    //TODO: SESSION - TOKEN CHECK EDELIM.
    private void prepareToken() {
        if (tokenParameter == null || tokenParameter.isTokenExpired()) {
            try {
                OAuthJSONAccessTokenResponse oAuthJSONAccessTokenResponse = oAuthClient.accessToken(oAuthClientRequest, RequestMethod.POST.name());
                tokenParameter = new TokenParameter(oAuthJSONAccessTokenResponse.getAccessToken(),
                        oAuthJSONAccessTokenResponse.getRefreshToken(),
                        oAuthJSONAccessTokenResponse.getExpiresIn());
            } catch (OAuthSystemException | OAuthProblemException e) {
                log.error("", e);
                throw new AccessTokenException(e);
            }
        }
    }

}
