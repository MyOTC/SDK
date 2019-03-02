<?php
namespace OTC\Api;

use OTC\Api\ApiBase;
use OTC\Exception\InvalidArgumentException;
use OTC\Exception\HttpResponseException;
use OTC\Exception\HttpResponseBodyException;

class Account extends ApiBase
{
    protected $apiConfigs = [
        'info' => [
            'uri' => '/v1/api/open/account',
        ],
    ];

    /**
     * 获取账户信息
     * @return array
     * @throws InvalidArgumentException|HttpResponseException|HttpResponseBodyException|\Exception
     */
    public function info()
    {
        $response = $this->request(__FUNCTION__ , []);

        return $response;

    }
}
