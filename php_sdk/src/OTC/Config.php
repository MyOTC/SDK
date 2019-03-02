<?php
namespace OTC;

final class Config
{
    const SDK_VER = '1.0.0';
    const TEST_HOST = 'http://merchant.dragonscam.me';
    const PRODUCTION_HOST = 'https://www.dragonscam.me';
    const GATEWAY_TEST_HOST = 'http://gateway.dragonscam.me';
    const GATEWAY_PRODUCTION_HOST = 'https://www.bddgateway.com';
    const LOG_LEVEL_CLOSE = 0;
    const LOG_LEVEL_ALL = 1;
    const LOG_LEVEL_ERROR = 2;
    const ENV_PRODUCTION = 1;
    const EVN_TEST = 0;

    private $hostUrl = 'http://merchant.dragonscam.me';
    private $gatewayHostUrl = 'http://gateway.dragonscam.me';
    private $data = [
        'AccessKeyId' => '',
        'AccessKeySecret' => '',
        'SignatureVersion' => 1,
        'SignatureMethod' => 'HmacSHA256',
        'NotifyKeySecret' => '',//回调通知秘钥
        //'IsHttps' => false,
        'Env' => self::EVN_TEST, //0测试 1生产
        'LogLevel' => self::LOG_LEVEL_ALL, //0 关闭 1全部 2错误
        'LogPath' => './record/',
        'BuyCallbackUrl' => '',//下单成功同步回调地址
        'RedirectUrl' => '',//交易完成时要跳转到商户的地址
    ];

    // 构造函数
    public function __construct(array $conf = [])
    {
        $this->data = array_merge($this->data, $conf);
        $this->_checkConf();

        //$this->hostUrl = ($this->data['IsHttps'] ? 'https' : 'http') . '://' . ($this->data['Env'] ? self::PRODUCTION_HOST : self::TEST_HOST);
        if ($this->data['Env'] == self::ENV_PRODUCTION){
            $this->hostUrl = self::PRODUCTION_HOST;
            $this->gatewayHostUrl = self::GATEWAY_PRODUCTION_HOST;
        }else{
            $this->hostUrl = self::TEST_HOST;
            $this->gatewayHostUrl = self::GATEWAY_TEST_HOST;
        }

    }

    private function _checkConf()
    {
        if (!trim($this->getAccessKeyId())){
            exit('请设置AccessKeyId');
        }

        if (!trim($this->getAccessKeySecret())){
            exit('请设置AccessKeySecret');
        }

        /*if (!trim($this->getNotifyKeySecret())){
            exit('请设置NotifyKeySecret');
        }*/

        if (!in_array($this->getLogLevel(), [self::LOG_LEVEL_CLOSE, self::LOG_LEVEL_ALL, self::LOG_LEVEL_ERROR])){
            exit('LogLevel 设置错误');
        }
    }

    public function getHost()
    {
        return $this->hostUrl;
    }

    public function getGatewayHost()
    {
        return $this->gatewayHostUrl;
    }

    public function getAccessKeyId()
    {
        return $this->data['AccessKeyId'];
    }

    public function getAccessKeySecret()
    {
        return $this->data['AccessKeySecret'];
    }

    public function getNotifyKeySecret()
    {
        return $this->data['NotifyKeySecret'];
    }

    public function getSignatureMethod()
    {
        return $this->data['SignatureMethod'];
    }

    public function getSignatureVersion()
    {
        return $this->data['SignatureVersion'];
    }

    public function getBuyCallbackUrl()
    {
        return $this->data['BuyCallbackUrl'];
    }

    public function getRedirectUrl()
    {
        return $this->data['RedirectUrl'];
    }

    public function getLogPath()
    {
        return $this->data['LogPath'];
    }

    public function getLogLevel()
    {
        return $this->data['LogLevel'];
    }

    public function isOpenLog()
    {
        return $this->data['LogLevel'];
    }

    public static function getVarietyList()
    {
        return ['btc' => '比特币', 'eth' => '以太坊', 'ltc' => '莱特币', 'bch' => '比特现金', 'edu' => 'EDU', 'etc' => '以太经典', 'usdt' => 'USDT'];
    }

    public static function getCurrencyList()
    {
        return ['CNY' => '人民币'];
    }

    public static function getPaymentType()
    {
        return [1 => '支付宝', 2 => '', 3 => '银行卡'];
    }
}
