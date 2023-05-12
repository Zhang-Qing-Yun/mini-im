package com.qingyun.im.client.pojo;

/**
 * @description： 用户信息，单例模式
 * @author: 張青云
 * @create: 2023-02-01 16:14
 **/
public class UserInfo {
    private static volatile UserInfo instance;
    private static final Object o = new Object();

    private String username;
    private String password;
    private boolean isInit = false;

    private UserInfo() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    /**
     * 使用DCL创建或获取单例对象
     * @return 单例对象
     */
    public static UserInfo getInstance() {
        if (instance == null) {
            synchronized (o) {
                if (instance == null) {
                    instance = new UserInfo();
                }
            }
        }
        return instance;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
