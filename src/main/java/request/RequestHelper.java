package request;

import lombok.Data;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@RequestScoped
@Log
public class RequestHelper {

    @Inject
    private RequestClientCreator requestClientCreator;


    public <T> T getRequestAndReceiveObject(Class<T> classOfObject, String url) {
        Client client = requestClientCreator.createRequestClient();
        Response response = client.target(url)
                .request()
                .get();
        T responseEntity = response.readEntity(classOfObject);
        response.close();
        client.close();
        return responseEntity;
    }

    public <T> T getRequestAndReceiveGeneric(GenericType<T> genericType, String url) {
        Client client = requestClientCreator.createRequestClient();
        Response response = client.target(url)
                .request()
                .get();
        T responseEntity = response.readEntity(genericType);
        response.close();
        client.close();
        return responseEntity;
    }

    public <T> T postRequestAndReceiveObject(Class<T> classOfObject, String url, String body) {
        Client client = requestClientCreator.createRequestClient();
        Response response = client.target(url)
                .request()
                .post(Entity.entity(body, MediaType.APPLICATION_JSON_TYPE));
        T responseEntity = response.readEntity(classOfObject);
        response.close();
        client.close();
        return responseEntity;
    }

    public <T> T postRequestAndReceiveGeneric(GenericType<T> genericType, String url, String body) {
        Client client = requestClientCreator.createRequestClient();
        Response response = client.target(url)
                .request()
                .post(Entity.entity(body, MediaType.APPLICATION_JSON_TYPE));
        T responseEntity = response.readEntity(genericType);
        response.close();
        client.close();
        return responseEntity;
    }

    public Response postRequest(String url, String body) {
        Client client = requestClientCreator.createRequestClient();
        Response response = client.target(url)
                .request()
                .post(Entity.entity(body, MediaType.APPLICATION_JSON_TYPE));
        client.close();
        return response;
    }

    public Response getRequest(String url, String body) {
        Client client = requestClientCreator.createRequestClient();
        Response response = client.target(url)
                .request()
                .get();
        client.close();
        return response;
    }

}
