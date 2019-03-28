package org.tron.common.overlay.message;

import com.google.protobuf.CodedInputStream;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.lang.reflect.Field;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.tron.common.utils.Sha256Hash;
import org.tron.core.net.message.MessageTypes;

public abstract class Message {

  protected static final Logger logger = LoggerFactory.getLogger("Message");

  protected byte[] data;
  protected byte type;

  public Message() {
  }

  public Message(byte[] packed) {
    this.data = packed;
  }

  public Message(byte type, byte[] packed) {
    this.type = type;
    this.data = packed;
  }

  public ByteBuf getSendData() {
    return Unpooled.wrappedBuffer(ArrayUtils.add(this.getData(), 0, type));
  }

  public Sha256Hash getMessageId() {
    return Sha256Hash.of(getData());
  }

  public byte[] getData() {
    return this.data;
  }

  public MessageTypes getType() {
    return MessageTypes.fromByte(this.type);
  }

  public abstract Class<?> getAnswerMessage();

  @Override
  public String toString() {
    return "type: " + getType() + "\n";
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(data);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Message)) {
      return false;
    }
    Message message = (Message) o;
    return Arrays.equals(data, message.data);
  }

  public CodedInputStream getCodedInputStream() {
    CodedInputStream codedInputStream = CodedInputStream.newInstance(data);
    Field field = ReflectionUtils
        .findField(codedInputStream.getClass(), "explicitDiscardUnknownFields");
    ReflectionUtils.makeAccessible(field);
    ReflectionUtils.setField(field, codedInputStream, true);
    return codedInputStream;
  }

}