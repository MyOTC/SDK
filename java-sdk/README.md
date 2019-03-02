PingPay-JAVA-SDK 

API文档请访问:
https://github.com/MyOTC/API-Doc

订单相关操作:com.bdd.service.OrderService
订单通知:com.bdd.service.OrderNotifyService

使用方法:
1.使用maven package 后会生成如下三个jar包 
    bdd-java-sdk-1.0.jar
    bdd-java-sdk-1.0-sources.jar(源码包) 
    bdd-java-sdk-1.0-jar-with-dependencies.jar(带依赖的jar:避免与您项目依赖jar版本差异而无法运行的情况)
2.将jar包放在您项目libs目录下,maven 中添加如下内容:
   <dependency>
      <groupId>com.bdd</groupId>
      <artifactId>bdd-java-sdk</artifactId>
      <version>1.0</version>
      <scope>system</scope>
      <systemPath>${project.basedir}/libs/bdd-java-sdk-1.0.jar</systemPath>
      <!--<systemPath>${project.basedir}/libs/bdd-java-sdk-1.0-jar-with-dependencies.jar</systemPath>-->
     </dependency>
3.创建相应的service:
     private static OrderService orderService = new OrderService("Access Key","Access Secret","http://gateway.dragonscam.me");
