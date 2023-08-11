package request;

import property.annotation.Property;
import requestWithCookies.RequestAuthenticatorWithCookies;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.HttpHeaders;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequestScoped
public class RequestClientCreator {

    @Inject
    @Property("username")
    private String username;
    @Inject
    @Property("password")
    private String password;
    @Inject
    @Property("orchestra.configuration.rest.api.url")
    private String orchestraConfigurationRestApiUrl;

    private String extractHostFromLink(String url) {
        Pattern pattern = Pattern.compile("(?<=http://|https://).*(?=:)");
        Matcher matcher = pattern.matcher(url);
        if(matcher.find()) {
            return matcher.group();
        } else {
            throw new RuntimeException("Orchestra host not found!");
        }

    }

    public Client createRequestClient() {

        return ClientBuilder
                .newClient()
                .register(new RequestAuthenticator(
                        username
                        , password
                        , extractHostFromLink(orchestraConfigurationRestApiUrl)
                ));
    }

}
