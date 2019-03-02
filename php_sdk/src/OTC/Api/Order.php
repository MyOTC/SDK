<?php
namespace OTC\Api;

use OTC\Api\ApiBase;
use OTC\Exception\InvalidArgumentException;
use OTC\Exception\HttpResponseException;
use OTC\Exception\HttpResponseBodyException;
use OTC\Http\ParamBuilder;

class Order extends ApiBase
{
    protected $apiConfigs = [
        'detail' => [
            'uri' => '/v1/api/open/order/detail',
            
        ],
        'price' => [
            'uri' => '/v1/api/open/price',
            
        ],
        'sell' => [
            'uri' => '/v1/api/open/order/sell',

        ],
    ];

    /**
     * 订单详情
     * @param $outOrderNo
     * @return array
     * @throws InvalidArgumentException|HttpResponseException|HttpResponseBodyException|\Exception
     */
    public function detail($outOrderNo)
    {
        $paramBuilder = new ParamBuilder($this, __FUNCTION__, func_get_args());

        $response = $this->request($paramBuilder);

        return $response;

    }

    /**
     * @param $variety
     * @param $currency
     * @return array
     * @throws InvalidArgumentException|HttpResponseException|HttpResponseBodyException|\Exception
     */
    public function price($variety, $currency)
    {
        $paramBuilder = new ParamBuilder($this, __FUNCTION__, func_get_args());

        $response = $this->request($paramBuilder);

        return $response;

    }

    /**
     * 出金下单
     * @param $variety
     * @param $currency
     * @param $amount
     * @param $unit
     * @param $outUid
     * @param $outOrderSn
     * @param $paymentType
     * @param $name
     * @param $number
     * @param $bank
     * @param $bankName
     * @param $idNumber
     * @return array
     * @throws InvalidArgumentException|HttpResponseException|\Exception
     */
    public function sell
    (
        $variety,
        $currency,
        $amount,
        $unit,
        $outUid,
        $outOrderSn,
        $paymentType,
        $name,
        $number,
        $bank,
        $bankName,
        $idNumber
    ) {
        $paramBuilder = new ParamBuilder($this, __FUNCTION__, func_get_args());

        $response = $this->request($paramBuilder);

        return $response;

    }

}
