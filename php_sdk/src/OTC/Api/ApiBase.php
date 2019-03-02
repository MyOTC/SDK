<?php
namespace OTC\Api;

use OTC\Base;
use OTC\Exception\HttpResponseException;
use OTC\Exception\HttpResponseBodyException;
use OTC\Exception\InvalidArgumentException;
use OTC\Http\ParamBuilder;
use OTC\Http\Request;
use OTC\Http\Client;

abstract class ApiBase extends Base
{

    protected $request;
    protected $response;
    protected $apiConfigs = [];

    public function getFullUrl($uri)
    {
        return $this->conf->getHost() . $uri;
    }

    protected function request(ParamBuilder $paramBuilder)
    {
        $ret = '';
        $requestString = '';
        $logger = $this->loggerManager->getLogger('request');

        try{
            $this->paramBuilder = $paramBuilder;
            $this->_checkValidate();
            $this->_genRequestInstance();
            $requestString = $this->request->toString();
            $logger->info($requestString);

            $this->response = (new Client())->sendRequest($this->request);
            $responseToString = $this->response->toString();

            if ($this->response->ok()){
                $responseJson = $this->response->json();
                if (isset($responseJson['code']) && $responseJson['code'] != 200){
                    throw new HttpResponseBodyException($responseJson['msg']);
                }else{
                    $ret = $responseJson['body'];
                }

                $logger->info($responseToString);
            }else{
                throw new HttpResponseException($responseToString);
            }
        }catch (\Exception $e){
            $logger->error($e->getMessage() . ' ' . $requestString);

        }

        $this->loggerManager->flush();
        if (isset($e)){
            throw $e;
        }

        return $ret;
    }

    private function _genRequestInstance()
    {
        $apiConfig = $this->apiConfigs[$this->paramBuilder->getApiName()];
        $url = $this->getFullUrl($apiConfig['uri']);
        $headers = ['Content-Type' => 'application/json;charset=\'utf-8\''];
        $requestMethod = isset($apiConfig['method']) ? $apiConfig['method'] : 'POST';
        $isSign        = isset($apiConfig['isSign']) ? $apiConfig['isSign'] : true;
        $this->request = new Request($requestMethod, $url, $headers, $this->paramBuilder->getParams());
        if ($isSign){
            $this->getAuth()->signRequest($this->request);
        }
    }

    protected function _statusTransfer($status)
    {
        if ($status == 'success'){
            return true;
        }

        return false;
    }

}
