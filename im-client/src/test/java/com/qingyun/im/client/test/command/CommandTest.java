package com.qingyun.im.client.test.command;

import com.alibaba.fastjson.JSONObject;
import com.qingyun.im.client.command.CommandContext;
import okhttp3.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @description：
 * @author: 張青云
 * @create: 2022-11-25 09:34
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class CommandTest {

    @Autowired
    private CommandContext context;

    @Autowired
    private OkHttpClient okHttpClient;

    @Test
    public void helpCommandTest() {
        context.invokeHandle("help");
    }

    @Test
    public void loginCommandTest() {
        context.invokeHandle("login qingyun 123456");
    }

    @Test
    public void okHttpTest() throws IOException {
        String url = "http://localhost:8080/user/login";
        JSONObject param = new JSONObject();
        param.put("username", "qingyun");
        param.put("password", "123456");
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType, param.toString());
        final Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response res = okHttpClient.newCall(request).execute();
        System.out.println(res.body().string());
    }
}
