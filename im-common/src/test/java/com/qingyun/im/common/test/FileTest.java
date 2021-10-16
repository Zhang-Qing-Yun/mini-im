package com.qingyun.im.common.test;

import com.qingyun.im.common.constants.ClientConstants;
import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import com.qingyun.im.common.exception.IMRuntimeException;
import com.qingyun.im.common.util.FileUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @description：
 * @author: 張青云
 * @create: 2021-10-16 19:04
 **/
public class FileTest {
    private ProtoMsg.Message msg;

    @Before
    public void init() {
        msg = ProtoMsg.Message.newBuilder()
                .setSequence(4L)
                .setMsg(ProtoMsg.Msg.newBuilder().setFrom("qingyun").setTo("yunge").setContext("hello")).build();
    }

    @Test
    public void fileTest() {
        try {
            FileUtil.createDir(ClientConstants.FILE_PATH_PREFIX);
            FileUtil.createFile(ClientConstants.FILE_PATH_PREFIX + "123.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void exceptionTest() {

        try {
            throw new RuntimeException("123");
        } catch (RuntimeException e) {
            System.out.println("被捕获了");
        }
        System.out.println("0000");
    }

    @Test
    public void persistTest() {
        String path = ClientConstants.FILE_PATH_PREFIX + msg.getMsg().getTo() + "/"
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
        } catch (IOException e) {
            //  持久化文件失败
            e.printStackTrace();
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

    @Test
    public void readMsgTest() {
        try {
            List<ProtoMsg.Message> messages = getMessageWithFriendAndDelete(msg.getMsg().getFrom());
            for (ProtoMsg.Message message: messages) {
                System.out.println(message);
            }
        } catch (IMException e) {
            e.printStackTrace();
        }
    }


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

    public List<ProtoMsg.Message> getMessageWithFriendAndDelete(String username) throws IMException {
        String path = ClientConstants.FILE_PATH_PREFIX + msg.getMsg().getTo() + "/"
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
        List<ProtoMsg.Message> result = new ArrayList<>(files.length);
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

        return result;
    }
}
