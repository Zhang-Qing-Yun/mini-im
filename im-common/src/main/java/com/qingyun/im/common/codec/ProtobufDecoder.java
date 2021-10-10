package com.qingyun.im.common.codec;

import com.qingyun.im.common.entity.ProtoMsg;
import com.qingyun.im.common.enums.Exceptions;
import com.qingyun.im.common.exception.IMException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @description： 基于protobuf的解码器，同时继承于LengthFieldBasedFrameDecoder来解决粘包拆包问题
 * @author: 張青云
 * @create: 2021-10-10 15:04
 **/
public class ProtobufDecoder extends LengthFieldBasedFrameDecoder {
    //  魔数
    private final short crcCode = 0x1F1F;

    public ProtobufDecoder() {
        super(1024*1024, 4, 4, -8, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        //  返回的是整包消息或空
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        //  如果是空则说明是个半包消息，直接返回继续读取后续的码流
        if (frame == null) {
            return null;
        }

        //  读取魔数
        int crcCode = frame.readShort();
        if (crcCode != this.crcCode) {
            throw new IMException(Exceptions.NO_MESSAGE.getCode(), Exceptions.NO_MESSAGE.getMessage());
        }
        //  读取版本号
        short version = frame.readShort();
        //  读取长度
        int length = frame.readInt();

        //  消息体长度
        int bodyLength = frame.readInt();
        byte[] bodyArray = new byte[bodyLength];
        frame.readBytes(bodyArray);
        //  字节转成对象
        ProtoMsg.Message body = ProtoMsg.Message.parseFrom(bodyArray);
        return body;
    }
}
