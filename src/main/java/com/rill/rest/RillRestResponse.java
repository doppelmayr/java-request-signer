package com.rill.rest;

import javax.ws.rs.core.Response;

/**
 * User: nelkanova
 * Date: split from com.rill.ws.rest.json on Feb 23 2014
 */
public class RillRestResponse {

    private Response.Status status;
    private String url;
    private Object dataPayload;
    private String message;
    
    public RillRestResponse(String url){
        this.url = url;
    }

    public int getStatusCode() {
        return this.status.getStatusCode();
    }

    public Response.Status getStatus() {
        return this.status;
    }

    public void setStatus(Response.Status status) {
        this.status = status;
    }

    public String getUrl(){
        return this.url;
    }

    public Object getDataPayload(){
        return dataPayload;
    }
    
    public String getMessage(){
        return message;
    }
    
    public RillRestResponse withSuccess() {
        this.status = Response.Status.OK;
        return this;
    }
    
    public RillRestResponse withSuccess(Object data) {
        this.status = Response.Status.OK;
        this.dataPayload = data;
        this.message="OK";
        return this;
    }
    
    public RillRestResponse withError(Response.Status status, String message){
        this.status = status;
        this.message = message;
        return this;
    }
}
