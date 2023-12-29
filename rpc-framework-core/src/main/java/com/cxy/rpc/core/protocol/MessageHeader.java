package com.cxy.rpc.core.protocol;

import com.cxy.rpc.core.constant.ProtocolConstants;
import com.cxy.rpc.core.enums.MessageType;
import com.cxy.rpc.core.enums.SerializationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求协议头部信息
 * <pre>
 *   --------------------------------------------------------------------
 *  | 魔数 (4byte) | 版本号 (1byte)  | 序列化算法 (1byte)  | 消息类型 (1byte) |
 *  -------------------------------------------------------------------
 *  |  状态类型 (1byte)  |     消息序列号 (4byte)   |     消息长度 (4byte)    |
 *  --------------------------------------------------------------------
 * </pre>
 *
 * @author cxy
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageHeader {
    private byte[] magicNum;

    private byte version;

    private byte serializerType;

    private byte messageType;

    private byte messageStatus;

    private int sequenceId;

    private int length;

    public static MessageHeader build(String serializeName) {
        return MessageHeader.builder()
                .magicNum(ProtocolConstants.MAGIC_NUM)
                .version(ProtocolConstants.VERSION)
                .serializerType(SerializationType.parseByName(serializeName).getType())
                .messageType(MessageType.REQUEST.getType())
                .sequenceId(ProtocolConstants.getSequenceId())
                .build();
    }
}
