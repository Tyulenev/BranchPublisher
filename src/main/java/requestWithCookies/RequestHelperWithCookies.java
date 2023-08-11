package requestWithCookies;

import controller.exceptions.AuthFailedException;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import request.RequestClientCreator;

import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Map;

@RequestScoped
@Log
public class RequestHelperWithCookies {

    @Inject
    private RequestClientCreatorWithCookies requestClientCreator;


    @SneakyThrows
    public <T> T getRequestAndReceiveObject(Class<T> classOfObject, String url, HttpHeaders httpHeaders) {
//        try {
        Client client = requestClientCreator.createRequestClient(httpHeaders);
        Response response = client.target(url)
                .request()
                .cookie(getCookieFromHttpHeaders(httpHeaders))
                .get();
        T responseEntity = response.readEntity(classOfObject);
        response.close();
        client.close();
        return responseEntity;
//        } catch (Exception ex) {
//            throw new AuthFailedException("Authentication failed");
//        }
    }

    @SneakyThrows
    public <T> T getRequestAndReceiveGeneric(GenericType<T> genericType, String url, HttpHeaders httpHeaders) {
        try {
            Client client = requestClientCreator.createRequestClient(httpHeaders);
            Response response = client.target(url)
                    .request()
                    .cookie(getCookieFromHttpHeaders(httpHeaders))
                    .get();

            T responseEntity = response.readEntity(genericType);
            response.close();
            client.close();
            return responseEntity;
        } catch (Exception ex) {
            throw new AuthFailedException("Authentication failed");
        }
    }

    public <T> T postRequestAndReceiveObject(Class<T> classOfObject, String url, String body, HttpHeaders httpHeaders) {
        Client client = requestClientCreator.createRequestClient(httpHeaders);
        Response response = client.target(url)
                .request()
                .cookie(getCookieFromHttpHeaders(httpHeaders))
                .post(Entity.entity(body, MediaType.APPLICATION_JSON_TYPE));
        T responseEntity = response.readEntity(classOfObject);
        response.close();
        client.close();
        return responseEntity;
    }

    public <T> T postRequestAndReceiveGeneric(GenericType<T> genericType, String url, String body
            , HttpHeaders httpHeaders) {
        Client client = requestClientCreator.createRequestClient(httpHeaders);
        Response response = client.target(url)
                .request()
                .cookie(getCookieFromHttpHeaders(httpHeaders))
                .post(Entity.entity(body, MediaType.APPLICATION_JSON_TYPE));
        T responseEntity = response.readEntity(genericType);
        response.close();
        client.close();
        return responseEntity;
    }

    @SneakyThrows
    public Response postRequest(String url, String body, HttpHeaders httpHeaders) {
        try {
            Client client = requestClientCreator.createRequestClient(httpHeaders);
            Response response = client.target(url)
                    .request()
                    .cookie(getCookieFromHttpHeaders(httpHeaders))
                    .post(Entity.entity(body, MediaType.APPLICATION_JSON_TYPE));
            client.close();
            return response;
        }  catch (Exception ex) {
            throw new AuthFailedException("Authentication failed");
        }
    }

    public Response getRequest(String url, String body, HttpHeaders httpHeaders) {
        Client client = requestClientCreator.createRequestClient(httpHeaders);
        Response response = client.target(url)
                .request()
                .cookie(getCookieFromHttpHeaders(httpHeaders))
                .get();
        client.close();
        return response;
    }

    private Cookie getCookieFromHttpHeaders(HttpHeaders httpHeaders) {
        MultivaluedMap<String, String> requestHeaders = httpHeaders.getRequestHeaders();
        String SSOcook ="";
        for (Map.Entry<String, List<String>> entry : requestHeaders.entrySet())
        {
            if (entry.getKey().contains("Cookie")) {
                for (String cookieString:entry.getValue()) {
                    if (cookieString.contains("SSOcookie=")) {
                        SSOcook = entry.getValue().toString();
                        break;
                    }
                }
                break;
            }
        };
//        SSOcook split to arr
        String cook = "";
        for (String str : SSOcook.split(";")) {
            if (str.contains("SSOcookie")) {
                cook = str.replace("SSOcookie=", "")
                        .replace("]", "")
                        .replace("[", "")
                        .trim();
            }
        }
        Cookie returnCookObj =  new Cookie("SSOcookie", cook);
//        log.info("returnCookObj = " + returnCookObj);
        return returnCookObj;
    }
}
