package com.qingyun.im.server.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-11-15 19:02
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String password;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtUpdate;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
