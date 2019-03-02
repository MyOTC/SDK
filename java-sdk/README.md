PingPay-JAVA-SDK 

API文档请访问:
https://github.com/MyOTC/API-Doc

订单相关操作:com.bdd.service.OrderService

订单通知:com.bdd.service.OrderNotifyService

使用方法:

1. 先git clone 到本地，使用maven package 后会生成如下三个jar包 
```
    bdd-java-sdk-1.0.jar
    bdd-java-sdk-1.0-sources.jar(源码包) 
    bdd-java-sdk-1.0-jar-with-dependencies.jar(带依赖的jar:避免与您项目依赖jar版本差异而无法运行的情况)
 ```
2. 在您的根目录下新建libs文件夹，将jar包放在您项目libs目录下,maven 中添加如下内容:
```
   <dependency>
      <groupId>com.bdd</groupId>
      <artifactId>bdd-java-sdk</artifactId>
      <version>1.0</version>
      <scope>system</scope>
      <systemPath>${project.basedir}/libs/bdd-java-sdk-1.0.jar</systemPath>
      <!--<systemPath>${project.basedir}/libs/bdd-java-sdk-1.0-jar-with-dependencies.jar</systemPath>-->
     </dependency>

```

3.创建相应的OrderContoller:

生产环境网关：https://gateway.pingpay.co

```
@Controller
@Slf4j
public class OrderController {

    //在商家API设置页面设置的密钥
    private static OrderNotifyService orderNotifyService = new OrderNotifyService("回调密钥");
   
    private static OrderService orderService = new OrderService("Access Key","Access SecretKey", "https://gateway.pingpay.co");


    @RequestMapping(path = "/demo/createOrder")
    public String createOrder() {
        try{
            GatewayBuyOrderRequest request = new GatewayBuyOrderRequest();
            request.setVariety("USDT");
            request.setAmount("100");
            request.setOutOrderSn(String.valueOf(new Date().getTime()));
            request.setCurrency("CNY");
            request.setIdNumber("522636199309273271");
            request.setOutUid("1");
            request.setName("张三");
            request.setCallback("http://{host}/demo/callback");//对应下面同步回调方法的地址,{host}开发者换成自己的地址
            return "redirect:" + orderService.buyUrl(request);

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 同步回调
     */
    @RequestMapping("/demo/callback")
    public String buyCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        checkIsDebug();

        BuyOrderSyncMessage buyOrderSyncMessage;
        try {
            buyOrderSyncMessage = orderNotifyService.buyCallback(request.getRequestURL().toString(),request.getQueryString(),null);
        } catch (Exception e) {
            return print(response, e.getMessage());
        }
        if (!"1".equals(buyOrderSyncMessage.getResult())) {
            return print(response, buyOrderSyncMessage.getMsg());
        }
        //发起付款
        GatewayBuyOrderConfirmPayRequest gatewayBuyOrderConfirmPayRequest = new GatewayBuyOrderConfirmPayRequest();
        gatewayBuyOrderConfirmPayRequest.setOutOrderSn(buyOrderSyncMessage.getOutOrderSn());
        return "redirect:" + orderService.confirmPayUrl(gatewayBuyOrderConfirmPayRequest);
    }

    private String print(HttpServletResponse response,String msg) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache, must-revalidate");
        PrintWriter writer = response.getWriter();
        writer.write(msg);
        return null;
    }
}

```
