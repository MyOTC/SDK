<?php
/**
 * Created by PhpStorm.
 * User: mi
 * Date: 2018/12/29
 * Time: 下午12:16
 */
require_once('../autoload.php');

use OTC\Api\Order;
//可以是用配置数组，也可以到src/Config.php中直接设置
$conf = [
    'AccessKeyId' => '',
    'AccessKeySecret' => '',
    'SignatureVersion' => 1,
    'SignatureMethod' => 'HmacSHA256',
    'Env' => 0, //0测试 1生产
    'LogLevel' => 1, //0关闭 1全部 2错误
    'LogPath' => '', //日志目录
];

try{
    $order = new Order($conf);

    //获取币种单价
    $ret = $order->price('usdt', 'CNY');

    //出金下单
    $ret = $order->sell(
        'usdt',
        'CNY',
        1,
        2,
        '122336198203278371',
        'tsssddds',
        '3',
        '张三',
        '23490834',
        '招商银行',
        '亚运村支行',
        '2938409238402'
    );

    var_dump($ret);

}catch (\OTC\Exception\InvalidArgumentException $e){//参数异常
    echo($e->getMessage());

}catch (\OTC\Exception\HttpResponseBodyException $e){//业务异常
    echo($e->getMessage());

}catch (\OTC\Exception\HttpResponseException $e){//通信异常
    echo($e->getMessage());

}catch (Exception $e){//其他
    echo($e->getMessage());
}
