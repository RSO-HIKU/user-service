package com.hiku.userService;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Full CORS filter for JAX-RS: handles OPTIONS preflight and adds headers
 * without duplicates.
 */
@Provider
public class CORSFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Handle preflight OPTIONS requests
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            Response.ResponseBuilder builder = Response.ok();
            builder.header("Access-Control-Allow-Origin", "http://localhost:8081");
            builder.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
            builder.header("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS, HEAD");
            builder.header("Access-Control-Allow-Credentials", "true");
            requestContext.abortWith(builder.build());
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // Remove duplicates
        responseContext.getHeaders().remove("Access-Control-Allow-Origin");
        responseContext.getHeaders().remove("Access-Control-Allow-Headers");
        responseContext.getHeaders().remove("Access-Control-Allow-Methods");
        responseContext.getHeaders().remove("Access-Control-Allow-Credentials");

        // Add CORS headers to all responses
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "http://localhost:8081");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS, HEAD");
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
    }
}
