package com.threedr3am.exp.dubbo.serialization.hessian;

import com.caucho.hessian.io.Hessian2Output;
import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.serialization.Serialization;
import com.threedr3am.exp.dubbo.support.NoWriteReplaceSerializerFactory;
import java.io.ByteArrayOutputStream;
import org.apache.dubbo.common.serialize.Cleanable;
import org.apache.dubbo.common.serialize.Constants;

/**
 * @author threedr3am
 */
public class HessianSerialization implements Serialization {

  @Override
  public byte[] makeData(Payload payload, String[] args, String protocol) {
    ByteArrayOutputStream hessian2ByteArrayOutputStream = new ByteArrayOutputStream();
    Hessian2Output out = new Hessian2Output(hessian2ByteArrayOutputStream);
    NoWriteReplaceSerializerFactory sf = new NoWriteReplaceSerializerFactory();
    sf.setAllowNonSerializable(true);
    out.setSerializerFactory(sf);

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
