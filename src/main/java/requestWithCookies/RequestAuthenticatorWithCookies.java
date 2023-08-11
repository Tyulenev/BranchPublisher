package requestWithCookies;

import lombok.SneakyThrows;
import lombok.extern.java.Log;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log
public class RequestAuthenticatorWithCookies implements ClientRequestFilter {

    private final HttpHeaders httpHeaders;
    private final String host;


    public RequestAuthenticatorWithCookies( String host, HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
        this.host = host;
    }

    @Override
    public void filter(ClientRequestContext clientRequestContext) {
        clientRequestContext.getHeaders().add(HttpHeaders.HOST, host);
        clientRequestContext.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    }



}
