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
    $params = $order->callbackCheckAndReturnParams();
    // var_dump($params);
    $confirmPayUrl = $order->genConfirmPayUrl($params['outOrderSn']);
    header("location: ".$confirmPayUrl);
    //$params为callback带回的参数数组

}catch (Exception $e){
    echo($e->getMessage());
}
