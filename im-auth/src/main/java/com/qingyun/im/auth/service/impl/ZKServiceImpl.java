package com.qingyun.im.auth.service.impl;

import com.alibaba.fastjson.JSON;
import com.qingyun.im.auth.service.ZKService;
import com.qingyun.im.common.constants.ServerConstants;
import com.qingyun.im.common.entity.ImNode;
import com.qingyun.im.common.exception.IMException;
import com.qingyun.im.common.zk.CuratorZKClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-10-12 21:41
 **/
@Component
public class ZKServiceImpl implements ZKService {
    @Autowired
    private CuratorZKClient curatorZKClient;

    @Override
    public List<ImNode> getAllNode() {
        //  获取所有结点的路径
        List<String> paths = curatorZKClient.getChildren(ServerConstants.MANAGE_PATH);
        //  封装结果
        List<ImNode> nodes = new ArrayList<>();
        for (String path: paths) {
            //  读取结点数据
            byte[] payload = null;
            try {
                payload = curatorZKClient.getNodeData(path);
            } catch (IMException e) {
                //  读取结点数据出错
            }
            if (payload != null) {
                nodes.add(JSON.parseObject(payload, ImNode.class));
            }
        }
        return nodes;
    }
}
