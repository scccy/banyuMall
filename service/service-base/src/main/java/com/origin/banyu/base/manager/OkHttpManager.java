package com.origin.banyu.base.manager;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

/**
 * OkHttp3管理器
 * 
 * @author scccy
 */
@Slf4j
public class OkHttpManager {
    
    private final OkHttpClient okHttpClient;
    
    public OkHttpManager(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }
    
    /**
     * GET请求
     */
    public String get(String url) throws IOException {
        return get(url, null);
    }
    
    /**
     * GET请求（带请求头）
     */
    public String get(String url, Map<String, String> headers) throws IOException {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        
        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }
        
        Request request = requestBuilder.build();
        
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }
            
            ResponseBody responseBody = response.body();
            return responseBody != null ? responseBody.string() : "";
        }
    }
    
    /**
     * POST请求（JSON数据）
     */
    public String postJson(String url, Object data) throws IOException {
        return postJson(url, data, null);
    }
    
    /**
     * POST请求（JSON数据，带请求头）
     */
    public String postJson(String url, Object data, Map<String, String> headers) throws IOException {
        String jsonData = JSON.toJSONString(data);
        RequestBody requestBody = RequestBody.create(jsonData, MediaType.get("application/json; charset=utf-8"));
        
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(requestBody);
        
        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }
        
        Request request = requestBuilder.build();
        
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }
            
            ResponseBody responseBody = response.body();
            return responseBody != null ? responseBody.string() : "";
        }
    }
    
    /**
     * POST请求（表单数据）
     */
    public String postForm(String url, Map<String, String> formData) throws IOException {
        return postForm(url, formData, null);
    }
    
    /**
     * POST请求（表单数据，带请求头）
     */
    public String postForm(String url, Map<String, String> formData, Map<String, String> headers) throws IOException {
        FormBody.Builder formBuilder = new FormBody.Builder();
        
        if (formData != null) {
            formData.forEach(formBuilder::add);
        }
        
        RequestBody requestBody = formBuilder.build();
        
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(requestBody);
        
        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }
        
        Request request = requestBuilder.build();
        
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }
            
            ResponseBody responseBody = response.body();
            return responseBody != null ? responseBody.string() : "";
        }
    }
    
    /**
     * PUT请求（JSON数据）
     */
    public String putJson(String url, Object data) throws IOException {
        return putJson(url, data, null);
    }
    
    /**
     * PUT请求（JSON数据，带请求头）
     */
    public String putJson(String url, Object data, Map<String, String> headers) throws IOException {
        String jsonData = JSON.toJSONString(data);
        RequestBody requestBody = RequestBody.create(jsonData, MediaType.get("application/json; charset=utf-8"));
        
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .put(requestBody);
        
        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }
        
        Request request = requestBuilder.build();
        
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }
            
            ResponseBody responseBody = response.body();
            return responseBody != null ? responseBody.string() : "";
        }
    }
    
    /**
     * DELETE请求
     */
    public String delete(String url) throws IOException {
        return delete(url, null);
    }
    
    /**
     * DELETE请求（带请求头）
     */
    public String delete(String url, Map<String, String> headers) throws IOException {
        Request.Builder requestBuilder = new Request.Builder().url(url).delete();
        
        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }
        
        Request request = requestBuilder.build();
        
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response.code());
            }
            
            ResponseBody responseBody = response.body();
            return responseBody != null ? responseBody.string() : "";
        }
    }
    
    /**
     * 异步GET请求
     */
    public void getAsync(String url, Callback callback) {
        getAsync(url, null, callback);
    }
    
    /**
     * 异步GET请求（带请求头）
     */
    public void getAsync(String url, Map<String, String> headers, Callback callback) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        
        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }
        
        Request request = requestBuilder.build();
        okHttpClient.newCall(request).enqueue(callback);
    }
    
    /**
     * 异步POST请求（JSON数据）
     */
    public void postJsonAsync(String url, Object data, Callback callback) {
        postJsonAsync(url, data, null, callback);
    }
    
    /**
     * 异步POST请求（JSON数据，带请求头）
     */
    public void postJsonAsync(String url, Object data, Map<String, String> headers, Callback callback) {
        String jsonData = JSON.toJSONString(data);
        RequestBody requestBody = RequestBody.create(jsonData, MediaType.get("application/json; charset=utf-8"));
        
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(requestBody);
        
        if (headers != null) {
            headers.forEach(requestBuilder::addHeader);
        }
        
        Request request = requestBuilder.build();
        okHttpClient.newCall(request).enqueue(callback);
    }
} 