<?php
/**
 * Created by PhpStorm.
 * User: mi
 * Date: 2018/12/29
 * Time: 下午12:16
 */
require_once('../autoload.php');

use OTC\Gateway\Order;

$conf = [
    'AccessKeyId' => '',
    'AccessKeySecret' => '',
    'SignatureVersion' => 1,
    'SignatureMethod' => 'HmacSHA256',
    'Env' => 0, //0测试 1生产
    'LogLevel' => 1,
    'LogPath' => '',
    'BuyCallbackUrl' => ''
];


try{
    $order = new Order($conf);
    //网关下单&订单详情
    $order_no = time();
    $payUrl = $order->genPayUrl(
        'usdt',
        'CNY',
        1,
        2,
        '6225880100000000',
        $order_no,
        '3',
        'http://localhost/php_sdk/demo/callback.php',
        '张三'
    );

    header("location: ".$payUrl);

    //网关确认付款页面
    // $confirmPayUrl = $order->genConfirmPayUrl('outOrderSn');
    // echo $confirmPayUrl;

    // //网关出金订单详情
    // $receiveUrl = $order->genReceiveUrl('outOrderSn');
    // echo $receiveUrl;

}catch (Exception $e){
    echo($e->getMessage());
}
