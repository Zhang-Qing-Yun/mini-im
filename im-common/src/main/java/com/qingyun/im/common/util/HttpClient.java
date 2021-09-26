package com.qingyun.im.common.util;

import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import okhttp3.*;

/**
 * @description： 用来发送Http请求的工具类
 * @author: 張青云
 * @create: 2021-09-25 14:01
 **/
public final class HttpClient {

    private static MediaType mediaType = MediaType.parse("application/json");

    public static Response call(OkHttpClient okHttpClient, String params, String url) throws Exception {
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
}
