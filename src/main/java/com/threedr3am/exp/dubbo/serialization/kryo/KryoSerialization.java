package com.threedr3am.exp.dubbo.serialization.kryo;

import com.esotericsoftware.kryo.io.Output;
import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.serialization.Serialization;
import org.apache.dubbo.common.serialize.Constants;

import java.io.ByteArrayOutputStream;

/**
 * @author threedr3am
 */
public class KryoSerialization implements Serialization {

  private Payload payload;

  @Override
  public byte[] makeData(Payload payload, String[] args, String protocol) {
    ByteArrayOutputStream hessian2ByteArrayOutputStream = new ByteArrayOutputStream();
    com.esotericsoftware.kryo.Kryo k = makeKryo();

    try {
      try ( Output output = new Output(hessian2ByteArrayOutputStream) ) {
        k.writeClassAndObject(output, payload.getPayload(args));
      }
      return hessian2ByteArrayOutputStream.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return new byte[0];
  }

  protected com.esotericsoftware.kryo.Kryo makeKryo () {
    return new com.esotericsoftware.kryo.Kryo();
  }

  @Override
  public byte getType() {
    return Constants.KRYO_SERIALIZATION_ID;
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
