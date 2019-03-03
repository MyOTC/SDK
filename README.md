## 开始接入
接入过程分为两步

#### Step1. 创建预支付链接
以下为php代码为示例，下载phpsdk到本地，将src文件夹复制到您的工程目录。

```
<?php
require_once('../autoload.php');
use OTC\Gateway\Order;

$conf = [
    'AccessKeyId' => 'AccessKey',
    'AccessKeySecret' => 'AccessKey Secret',
    'SignatureVersion' => 1,
    'SignatureMethod' => 'HmacSHA256',
    'Env' => 1, //0测试 1生产
    'LogLevel' => 1,
    'LogPath' => '/xxx/www/logs',
    'BuyCallbackUrl' => ''
];
$order_no = time();
//同步回调地址，pingpay支付页面第一步点击“确定”后会跳转到该地址，开发环境下可使用localhost，上线后必须换到公网可访问的地址
$host = "http://localhost/demo/callback.php";
try{
    $order = new Order($conf);
    $order_no = time();
    $payUrl = $order->genPayUrl(
        'usdt',//固定值
        'CNY',//固定值
        1,//订单数量
        2,//固定值
        1, //接入方用户ID
        $order_no,//接入方订单号并保证不重复
        '3', //支持的支付方式，1:支付宝，2:微信支付，3:银行卡
        $host,
        '张三' //用户名
    );
    // 跳转到PingPay支付页面
    header("location: ".$payUrl);
    
}catch (Exception $e){
    echo($e->getMessage());
}

?>
```

#### Step2. 接收回调参数验证签名通过后发起支付

```
//即为Step1创建预支付链接的callback参数填写的地址

try{
    $order = new Order($conf);
    $params = $order->callbackCheckAndReturnParams(); //验证签名
    // var_dump($params);
    $confirmPayUrl = $order->genConfirmPayUrl($params['outOrderSn']); //验签通过之后取出接入方外部订单号获取支付链接
    header("location: ".$confirmPayUrl);
    //$params为callback带回的参数数组

}catch (Exception $e){
    echo($e->getMessage());
}

```

具体代码请参考demo文件夹
