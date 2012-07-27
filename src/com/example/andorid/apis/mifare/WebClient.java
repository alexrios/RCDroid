package com.example.andorid.apis.mifare;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public final class WebClient {

    private final String url;

    public WebClient(String url) {
        this.url = url;
    }

    public String get() throws IOException {
        HttpGet get = new HttpGet(url);
        return sendRequest(get);
    }

    public String post(String json) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setEntity(new StringEntity(json));
        return sendRequest(post);
    }

    private String sendRequest(HttpRequestBase httpRequestBase) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpRequestBase.setHeader("Accept", "application/json");
        httpRequestBase.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(httpRequestBase);
        return EntityUtils.toString(response.getEntity());
    }

}
