<?php
/**
 * Created by PhpStorm.
 * User: mi
 * Date: 2018/12/29
 * Time: 下午12:16
 */
require_once('../autoload.php');

use OTC\Notify\Order;

$conf = [
    'AccessKeyId' => '',
    'AccessKeySecret' => '',
    'SignatureVersion' => 1,
    'SignatureMethod' => 'HmacSHA256',
    'Env' => 0, //0测试 1生产
    'LogLevel' => 1,
    'LogPath' => '',
];

try{

    $order = new Order($conf);
    $order->callback(function ($data){
        //$data为业务数据的array格式
        //{
        //    "noticeTimestamp": "2019-01-02T02:22:53.509Z",
        //    "noticeType":"OrderNotice",
        //    "noticeAction":"102",
        //    "status": 3, //3-确认收款
        //    "outOrderSn":"O234234234",
        //    "amount": "100",
        //    "total": "700",
        //    "price": "7.0000",
        //
        //}
        //业务处理
        var_dump($data);
    });

}catch (Exception $e){
    echo($e->getMessage());
}
