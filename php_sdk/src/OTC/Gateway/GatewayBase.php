<?php
namespace OTC\Gateway;

use OTC\Base;
use OTC\Exception\InvalidArgumentException;
use OTC\Http\ParamBuilder;
use OTC\Http\Request;

abstract class GatewayBase extends Base
{
    protected $gatewayConfigs = [];

    protected function genUrl(ParamBuilder $paramBuilder)
    {
        $retUrl = '';
        $logger = $this->loggerManager->getLogger('gateway');

        try{
            $this->paramBuilder = $paramBuilder;
            $this->_checkValidate();
            $retUrl = $this->getAuth()->signGatewayAndReturnUrl($this->conf->getGatewayHost(), $this->gatewayConfigs[$paramBuilder->getApiName()], $this->paramBuilder->getParams());

        }catch (\Exception $e){
            $logger->error($e->getMessage());

        }

        $this->loggerManager->flush();
        if (isset($e)){
            throw $e;
        }

        return $retUrl;
    }

}
