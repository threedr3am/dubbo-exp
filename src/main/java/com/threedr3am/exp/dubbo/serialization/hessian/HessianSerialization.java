package com.threedr3am.exp.dubbo.serialization.hessian;

import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.protocol.dubbo.DubboProtocol;
import com.threedr3am.exp.dubbo.serialization.Serialization;
import java.io.ByteArrayOutputStream;
import org.apache.dubbo.common.serialize.Cleanable;
import org.apache.dubbo.common.serialize.Constants;
import org.apache.dubbo.common.serialize.hessian2.Hessian2ObjectOutput;

/**
 * @author threedr3am
 */
public class HessianSerialization implements Serialization {

  @Override
  public byte[] makeData(Payload payload, String[] args, String protocol) {
    ByteArrayOutputStream hessian2ByteArrayOutputStream = new ByteArrayOutputStream();
    Hessian2ObjectOutput out = new Hessian2ObjectOutput(hessian2ByteArrayOutputStream);

    try {
      out.writeObject(payload.getPayload(args));

      out.flushBuffer();
      if (out instanceof Cleanable) {
        ((Cleanable) out).cleanup();
      }

      return hessian2ByteArrayOutputStream.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return new byte[0];
  }

  @Override
  public byte getType() {
    return Constants.HESSIAN2_SERIALIZATION_ID;
  }
}
