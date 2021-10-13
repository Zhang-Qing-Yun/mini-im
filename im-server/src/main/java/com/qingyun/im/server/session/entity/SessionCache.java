package com.qingyun.im.server.session.entity;

import com.qingyun.im.common.entity.ImNode;
import lombok.Data;

import java.io.Serializable;

/**
 * @description： 要缓存在redis中的实体类
 * @author: 張青云
 * @create: 2021-10-13 20:34
 **/
@Data
public class SessionCache implements Serializable {
    private static final long serialVersionUID = -403010884211394856L;

    private String sessionId;

    private String username;

    private ImNode imNode;

    public SessionCache() {
    }

    public SessionCache(String sessionId, String username, ImNode imNode) {
        this.sessionId = sessionId;
        this.username = username;
        this.imNode = imNode;
    }
}
