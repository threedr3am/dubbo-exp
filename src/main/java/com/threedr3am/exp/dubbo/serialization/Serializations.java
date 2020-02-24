package com.threedr3am.exp.dubbo.serialization;

import com.threedr3am.exp.dubbo.serialization.hessian.HessianSerialization;
import com.threedr3am.exp.dubbo.serialization.java.JavaSerialization;

/**
 * @author threedr3am
 */
public enum Serializations {
  hessian("hessian", HessianSerialization.class),
  java("java", JavaSerialization.class),
  ;

  private String name;

  private Class<? extends Serialization> serialization;

  Serializations(String name,
      Class<? extends Serialization> serialization) {
    this.name = name;
    this.serialization = serialization;
  }

  public static Serialization getSerialization(String name) {
    for (Serializations serializations : Serializations.values()) {
      if (serializations.name.equalsIgnoreCase(name)) {
        try {
          return serializations.serialization.newInstance();
        } catch (InstantiationException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }
    return null;
  }
}
