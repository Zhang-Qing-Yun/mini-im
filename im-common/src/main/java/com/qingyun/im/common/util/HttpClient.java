package com.qingyun.im.common.util;

import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import okhttp3.*;

import java.util.Map;

/**
 * @description： 用来发送Http请求的工具类
 * @author: 張青云
 * @create: 2022-11-25 14:01
 **/
public final class HttpClient {

    private static MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

    /**
     * 通过OkHttp发送post请求
     */
    public static Response post(OkHttpClient okHttpClient, String params, String url) throws Exception {
        RequestBody requestBody = RequestBody.create(mediaType, params);

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        //  发送请求
        Response response = okHttpClient.newCall(request).execute();

        //  出错
        if (!response.isSuccessful()) {
            throw new IMException(Exceptions.HTTP_ERROR.getCode(), Exceptions.HTTP_ERROR.getMessage());
        }

        return response;
    }


    /**
     * 通过OkHttp发送get请求
     */
    public static Response get(OkHttpClient okHttpClient, Map<String, String> param, String baseUrl) throws Exception{
        HttpUrl.Builder httpBuilder = HttpUrl.parse(baseUrl).newBuilder();
        if (param != null) {
            for(Map.Entry<String, String> one: param.entrySet()) {
                httpBuilder.addQueryParameter(one.getKey(),one.getValue());
            }
        }
        Request request = new Request.Builder().url(httpBuilder.build()).build();
        //  发送请求
        Response response = okHttpClient.newCall(request).execute();

        //  出错
        if (!response.isSuccessful()) {
            throw new IMException(Exceptions.HTTP_ERROR.getCode(), Exceptions.HTTP_ERROR.getMessage());
        }

        return response;
    }
}
