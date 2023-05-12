package com.qingyun.im.common.codec;

import com.qingyun.im.common.entity.ProtoMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @description： 基于protobuf的编码器
 * @author: 張青云
 * @create: 2023-03-10 15:04
 **/
public class ProtobufEncoder  extends MessageToByteEncoder<ProtoMsg.Message> {
    //  魔数
    private final short crcCode = 0x1F1F;
    //  版本号
    private final short version = 0x01;

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ProtoMsg.Message message, ByteBuf out) throws Exception {
        //  消息分成两部分；头和体，头在发送时装配，体封装成一个protobuf类
        //  魔数
        out.writeShort(crcCode);
        //  版本号
        out.writeShort(version);
        //  消息长度（基于长度解决粘包拆包问题），这里先进行占位，当填充完以后再修改
        out.writeInt(0);

        //  消息体
        byte[] bytes = message.toByteArray();
        out.writeInt(bytes.length);  // 消息体的字节长度
        out.writeBytes(bytes);

        //  修改消息总长度
        out.setInt(4, out.readableBytes());
    }
}
