package com.threedr3am.exp.dubbo.payload;

/**
 *
 * dubbo攻击包类型，例 正常的调用invoke 或 心跳event等
 *
 * @author threedr3am
 */
public enum PackageType {
  INVOKE("正常的调用invoke"),
  EVENT("心跳event"),
  ;


  private String desc;

  PackageType(String desc) {
    this.desc = desc;
  }

}
