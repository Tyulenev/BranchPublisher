package controller;

import property.annotation.Property;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorsFilter implements ContainerResponseFilter {

    @Inject
    @Property("orchestra.configuration.rest.api.url")
    private String orchestraConfigurationRestApiUrl;

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        responseContext.getHeaders().add(
                "Access-Control-Allow-Origin"
                , getUrlFromOrchConfigRest(orchestraConfigurationRestApiUrl));
        responseContext.getHeaders().add(
                "Access-Control-Allow-Credentials", "true");
        responseContext.getHeaders().add(
                "Access-Control-Allow-Headers",
//                "origin, Content-Type, Accept, authorization, Accept-Language, Content-Language");
                " accept, accept-language, content-type, content-language");
        responseContext.getHeaders().add(
                "Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }

    private String getUrlFromOrchConfigRest(String inputStr) {
        String[] arrStr = inputStr.split("/");
        String outputStr = arrStr[0] + "/" + arrStr[1] + "/" + arrStr[2];
        return outputStr;
    }
}
