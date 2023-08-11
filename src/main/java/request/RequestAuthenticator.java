package request;

import lombok.SneakyThrows;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RequestAuthenticator implements ClientRequestFilter {

    private final String username;
    private final String password;
    private final String host;

    public RequestAuthenticator(String username, String password, String host) {
        this.username = username;
        this.password = password;
        this.host = host;
    }

    @Override
    public void filter(ClientRequestContext clientRequestContext) {
        clientRequestContext.getHeaders().add(HttpHeaders.AUTHORIZATION, getBasicAuthentication());
        clientRequestContext.getHeaders().add(HttpHeaders.HOST, host);
        clientRequestContext.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    }

    @SneakyThrows
    private String getBasicAuthentication() {
        String userAndPassword = this.username + ":" + this.password;
        byte[] userAndPasswordBytes = userAndPassword.getBytes(StandardCharsets.UTF_8);
        return "Basic " + Base64.getEncoder().encodeToString(userAndPasswordBytes);
    }

}
