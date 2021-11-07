package com.qingyun.im.client.msgCache.persistence;

import com.qingyun.im.client.imClient.ClientSession;
import com.qingyun.im.common.concurrent.CallbackTask;
import com.qingyun.im.common.concurrent.CallbackTaskScheduler;
import com.qingyun.im.common.constants.ClientConstants;
import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import com.qingyun.im.common.exception.IMRuntimeException;
import com.qingyun.im.common.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @description： 持久化在本地文件里，一条消息对应一个文件，从而在查找消息时减少了磁盘IO，方便了查找
 * @author: 張青云
 * @create: 2021-10-16 18:47
 **/
@Component
@Primary
public class OneMsgToOneFile implements Persistence {
    @Autowired
    private ClientSession session;


    /**
     * 创建目录
     */
    @PostConstruct
    private void init() {
        if (!FileUtil.isExist(ClientConstants.FILE_PATH_PREFIX)) {
            FileUtil.createDir(ClientConstants.FILE_PATH_PREFIX);
        }
    }

    /*
      使用该方法时可以异步调用，加快响应速度；
      当出现异常即持久化失败时，或者再次尝试持久化，或者再次放回缓存中，或者丢弃该消息
    */
    @Override
    public boolean persistMessage(ProtoMsg.Message msg) {
        String path = ClientConstants.FILE_PATH_PREFIX + session.getUserInfo().getUsername() + "/"
                + msg.getMsg().getFrom() + "/";
        String filename = msg.getSequence() + ClientConstants.FILE_SUFFIX;
        checkOrCreateFile(path, filename);
        File file = new File(path + filename);
        BufferedOutputStream buffer = null;

        try {
            //  写文件
            buffer = new BufferedOutputStream(new FileOutputStream(file));
            buffer.write(msg.toByteArray());
            buffer.flush();
            return true;
        } catch (IOException e) {
            //  持久化文件失败
            return false;
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException e) {
                    System.out.println("关闭文件流失败");
                }
            }
        }
    }

    @Override
    public ProtoMsg.Message getMessageAndDelete(String username, long sequence) throws IMException {
        String path = ClientConstants.FILE_PATH_PREFIX + session.getUserInfo().getUsername() + "/"
                + username + "/";
        String filename = sequence + ClientConstants.FILE_SUFFIX;

        ProtoMsg.Message message = null;
        if (FileUtil.isExist(path + filename)) {
            //  读取文件
            File file = new File(path + filename);
            BufferedInputStream buffer = null;
            byte[] data = null;
            try {
                buffer = new BufferedInputStream(new FileInputStream(file));
                data = new byte[buffer.available()];
                buffer.read(data);
                //  反序列
                message = ProtoMsg.Message.parseFrom(data);
                //  异步删除文件
                CallbackTaskScheduler.addIOTarget(new CallbackTask<Void>() {
                    @Override
                    public Void execute() throws Exception {
                        file.delete();
                        return null;
                    }

                    @Override
                    public void onBack(Void aVoid) { }

                    @Override
                    public void onException(Throwable t) {
                        //  删除文件失败时不执行任何操作，让该文件留在磁盘上
                    }
                });
            } catch (Exception e) {
                throw new IMException(Exceptions.READ_FILE_ERROR.getCode(), Exceptions.READ_FILE_ERROR.getMessage());
            } finally {
                if (buffer != null) {
                    try {
                        buffer.close();
                    } catch (IOException e) {
                        System.out.println("关闭文件流失败");
                    }
                }
            }
        }
        return message;
    }

    /*
    * 同步阻塞读文件，异步删除文件
    */
    @Override
    public Set<ProtoMsg.Message> getMessageByUsernameAndDelete(String username) throws IMException {
        String path = ClientConstants.FILE_PATH_PREFIX + session.getUserInfo().getUsername() + "/"
                + username + "/";
        File dir = new File(path);
        if (!dir.exists()) {
            return null;
        }
        //  获取该目录下所有的文件
        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        //  读取文件并反序列化
        Set<ProtoMsg.Message> result = new HashSet<>(files.length);
        BufferedInputStream buffer = null;
        byte[] data = null;
        ProtoMsg.Message message = null;
        for (File file: files) {
            try {
                buffer = new BufferedInputStream(new FileInputStream(file));
                data = new byte[buffer.available()];
                buffer.read(data);
                //  反序列
                message = ProtoMsg.Message.parseFrom(data);
                result.add(message);
            } catch (Exception e) {
                //  读取失败时抛异常，此时并不会去删除持久化文件
                throw new IMException(Exceptions.READ_FILE_ERROR.getCode(), Exceptions.READ_FILE_ERROR.getMessage());
            } finally {
                if (buffer != null) {
                    try {
                        buffer.close();
                    } catch (IOException e) {
                        System.out.println("关闭文件流失败");
                    }
                }
            }
        }

        //  异步删除文件、目录
        CallbackTaskScheduler.addIOTarget(new CallbackTask<Void>() {
            @Override
            public Void execute() throws Exception {
                //  删文件
                for (File file: files) {
                    try {
                        file.delete();
                    } catch (Exception e) {
                        //  删除文件失败时不执行任何操作，让该文件留在磁盘上
                    }
                }
                //  删目录
                if (dir.list().length == 0) {
                    dir.delete();
                }
                return null;
            }

            @Override
            public void onBack(Void aVoid) { }

            @Override
            public void onException(Throwable t) { }
        });

        return result;
    }

    @Override
    public Set<String> getUsernamesWithMessage() throws IMException {
        String path = ClientConstants.FILE_PATH_PREFIX + session.getUserInfo().getUsername() + "/";
        File dir = new File(path);
        if (!dir.exists()) {
            return null;
        }
        //  获取该路径下所有的文件夹
        String[] oneUserDir = dir.list();
        if (oneUserDir == null || oneUserDir.length == 0) {
            return null;
        }
        Set<String> usernames = new HashSet<>(oneUserDir.length);
        for (String username: oneUserDir) {
            String childPath = path + username;
            File file = new File(childPath);
            String[] msg = file.list();
            //  判断文件夹下是否有文件
            if (msg != null && msg.length > 0) {
                usernames.add(username);
            }
        }
        return usernames;
    }

    /**
     * 检查是否存在持久化文件，如果没有则创建
     * @param path 文件路径
     * @param filename 文件名
     */
    private void checkOrCreateFile(String path, String filename) {
        if (!FileUtil.isExist(path)) {
            FileUtil.createDir(path);
        }
        String filePath = path + filename;
        if (!FileUtil.isExist(filePath)) {
            try {
                FileUtil.createFile(filePath);
            } catch (IOException e) {
                throw new IMRuntimeException(Exceptions.CREATE_FILE_ERROR.getCode(), Exceptions.CREATE_FILE_ERROR.getMessage());
            }
        }
    }
}
