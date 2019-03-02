<?php
/**
 * Created by PhpStorm.
 * User: mi
 * Date: 2018/12/29
 * Time: 下午12:16
 */
require_once('../autoload.php');

use OTC\Api\Account;
$conf = [
    'AccessKeyId' => '',
    'AccessKeySecret' => '',
    'SignatureVersion' => 1,
    'SignatureMethod' => 'HmacSHA256',
    'Env' => 0, //0测试 1生产
    'LogLevel' => 1,
    'LogPath' => '',
];

$account = new Account($conf);

try{
    //获取账户信息
    $ret = $account->info();

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


