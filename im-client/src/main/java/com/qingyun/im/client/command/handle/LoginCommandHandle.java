package com.qingyun.im.client.command.handle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qingyun.im.client.command.Command;
import com.qingyun.im.client.imClient.ClientSession;
import com.qingyun.im.client.imClient.ImClient;
import com.qingyun.im.client.loadBalancer.LoadBalancer;
import com.qingyun.im.common.entity.ImNode;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import com.qingyun.im.common.exception.IMRuntimeException;
import com.qingyun.im.common.util.HttpClient;
import com.qingyun.im.common.entity.R;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description： 处理登录命令
 * @author: 張青云
 * @create: 2021-09-25 10:11
 **/
@Component
public class LoginCommandHandle implements CommandHandle {
    //  用户名
    private String username;
    //  密码
    private String password;

    @Value("${auth.address}")
    private String authAddress;

    @Value("${auth.loginUrl}")
    private String loginUrl;

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private ImClient imClient;

    @Autowired
    private ClientSession session;


    @Override
    public boolean isCare(String commandValue) {
        String commandKey = CommandHandle.getCommandKey(commandValue);
        return commandKey.equals(Command.LOGIN.getCommandKey());
    }

    @Override
    public boolean isCorrect(String commandValue) {
        String[] parseValue = commandValue.trim().split(" ");
        return parseValue.length == 3;
    }

    @Override
    public void process(String commandValue) throws Exception {
        //  判断命令格式是否正确
        if (!isCorrect(commandValue)) {
            throw new IMException(Exceptions.PARSE_ERROR.getCode(), Exceptions.PARSE_ERROR.getMessage());
        }

        //  解析该命令
        String[] parseValue = commandValue.trim().split(" ");
        this.username = parseValue[1];
        this.password = parseValue[2];
        //  发HTTP请求登录的过程
        String url = authAddress + loginUrl;
        JSONObject param = new JSONObject();
        param.put("username", username);
        param.put("password", password);
        Response response = HttpClient.post(okHttpClient, param.toString(), url);
        //  判断是否登陆成功
        String string = response.body().string();
        R result = JSON.parseObject(string, R.class);
        if (!result.getSuccess()) {
            System.out.println(result.getMessage());
            throw new IMException(Exceptions.LOGIN_ERROR.getCode(), Exceptions.LOGIN_ERROR.getMessage());
        }
        //  获取Server列表
        String nodes = JSON.parseObject(JSON.parseObject(string).getString("data")).getString("imNodes");
        List<ImNode> imNodes = JSON.parseArray(nodes, ImNode.class);
        if (imNodes == null || imNodes.size() == 0) {
            throw new IMRuntimeException(Exceptions.NO_SERVER.getCode(), Exceptions.NO_SERVER.getMessage());
        }
        //  获取负载均衡策略
        int type = imClient.getLoadBalancerType();
        LoadBalancer loadBalancer = LoadBalancer.getInstance(type);
        //  选择一台Server
        //  TODO：处理Server下线但是ZK上还没来得及删除的情况
        ImNode imNode = null;
        while (!imNodes.isEmpty()) {
            imNode = loadBalancer.select(imNodes, username);
            if (imNode.isReady()) {
                break;
            } else {
                //  从集合中删除不可用结点
                imNodes.remove(imNode);
            }
        }
        if (imNode == null) {
            throw new IMRuntimeException(Exceptions.NO_SERVER.getCode(), Exceptions.NO_SERVER.getMessage());
        }
        //  将imNode设置到session当中
        session.setImNode(imNode);
        //  为ImClient设置服务端地址
        imClient.setServerIP(imNode.getIp());
        imClient.setServerPort(imNode.getPort());
    }

    public String getUsername() {
        return username;
    }
}
