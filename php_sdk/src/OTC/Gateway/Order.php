<?php
namespace OTC\Gateway;

use OTC\Exception\InvalidArgumentException;
use OTC\Http\ParamBuilder;

class Order extends GatewayBase
{
    protected $gatewayConfigs = [
        'genPayUrl' => '/gateway/v1/pay',
        'genConfirmPayUrl' => '/gateway/v1/confirmPay',
        'genReceiveUrl' => '/gateway/v1/receipt',
    ];

    /**
     * 网关下单&订单详情
     * @param $variety
     * @param $currency
     * @param $amount
     * @param $unit
     * @param $outUid
     * @param $outOrderSn
     * @param $paymentType
     * @param string $callback
     * @param string $name
     * @param string $idNumber
     * @param string $mobile
     * @param string $redirect
     * @return string
     * @throws \Exception|InvalidArgumentException
     */
    public function genPayUrl
    (
        $variety,
        $currency,
        $amount,
        $unit,
        $outUid,
        $outOrderSn,
        $paymentType,
        $callback = '',
        $name = '',
        $idNumber = '',
        $mobile = '',
        $redirect = ''
    ){
        $paramBuilder = new ParamBuilder($this, __FUNCTION__, func_get_args(), ['name', 'idNumber', 'mobile'], ['outOrderSn' => 'outOrderNo']);
        if (!trim($callback)){
            if (!$this->conf->getBuyCallbackUrl()){
                throw new InvalidArgumentException('请在Config.php中设置BuyCallbackUrl或直接赋值callback参数');
            }

            $paramBuilder->setParam('callback', $this->conf->getBuyCallbackUrl());
        }

        if (!trim($redirect)){
            $paramBuilder->setParam('redirect', $this->conf->getRedirectUrl());
        }

        $url = $this->genUrl($paramBuilder);

        return $url;
    }

    /**
     * 网关确认付款页面
     * @param $outOrderSn
     * @return string
     * @throws \Exception
     */
    public function genConfirmPayUrl($outOrderSn)
    {
        $paramBuilder = new ParamBuilder($this, __FUNCTION__, func_get_args());

        $url = $this->genUrl($paramBuilder);

        return $url;
    }

    /**
     * 网关出金订单详情
     * @param $outOrderSn
     * @param $redirect
     * @return string
     * @throws \Exception|InvalidArgumentException
     */
    public function genReceiveUrl($outOrderSn, $redirect = '')
    {
        $paramBuilder = new ParamBuilder($this, __FUNCTION__, func_get_args());
        if (!trim($redirect)){
            $paramBuilder->setParam('redirect', $this->conf->getRedirectUrl());
        }

        $url = $this->genUrl($paramBuilder);

        return $url;
    }

    /**
     * 网关下单callback处理
     * @return array
     * @throws InvalidArgumentException
     */

    public function callbackCheckAndReturnParams()
    {
        $scheme = 'http';
        if(isset($_SERVER['HTTPS'])) {
            if ($_SERVER['HTTPS'] == "on") {
                $scheme = 'https';
            }
        }

        $url = $scheme . '://' . $_SERVER["SERVER_NAME"] . $_SERVER["REQUEST_URI"];

        if($this->getAuth()->gatewayCallbackCheck($url)){
            return $_GET;
        }
    }
}
