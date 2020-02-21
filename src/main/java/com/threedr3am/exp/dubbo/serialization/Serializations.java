package com.threedr3am.exp.dubbo.serialization;

import com.threedr3am.exp.dubbo.serialization.hessian.HessianSerialization;
import com.threedr3am.exp.dubbo.serialization.java.JavaSerialization;

/**
 * @author threedr3am
 */
public enum Serializations {
  hessian("hessian",new HessianSerialization()),
  java("java",new JavaSerialization()),
  ;

  private String name;

  private Serialization serialization;

  Serializations(String name,
      Serialization serialization) {
    this.name = name;
    this.serialization = serialization;
  }

  public static Serialization getSerialization(String name) {
    for (Serializations serializations : Serializations.values()) {
      if (serializations.name.equalsIgnoreCase(name))
        return serializations.serialization;
    }
    return null;
  }
}
