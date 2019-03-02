<?php
namespace OTC\Http;


final class ParamBuilder
{
    protected $params = [];
    protected $apiName = '';
    protected $nonValidateCheckParams = [];
    protected $paramsAliasName = [];

    public function __construct($obj, $apiName, array $paramsVar = [], array $nonValidateCheckParams = [], array $paramsAliasName = [])
    {
        $this->apiName = $apiName;
        $this->nonValidateCheckParams = $nonValidateCheckParams;
        $this->paramsAliasName = $paramsAliasName;
        $this->_genParamsMapping($apiName, $paramsVar, $obj);
    }

    public function getParams()
    {
        return $this->params;
    }

    public function setParam($name, $var)
    {
        return $this->params[$name] = $var;
    }

    public function getApiName()
    {
        return $this->apiName;
    }

    public function getNonValidateCheckParams()
    {
        return $this->nonValidateCheckParams;
    }

    public function getParamsAliasName()
    {
        return $this->paramsAliasName;
    }

    protected function _genParamsMapping($method, array $paramsVar, $obj = null)
    {
        try{
            $obj = is_null($obj) ? $this : $obj;
            $reflectionMethod = new \ReflectionMethod($obj , $method);
            $paramNames = $reflectionMethod->getParameters();
            foreach ($paramNames as $k => $paramName){
                if (!isset($this->params[$paramName->name])){
                    $this->params[$paramName->name] = (isset($paramsVar[$k]) ? trim($paramsVar[$k]) : '');
                }
            }

        }catch (\ReflectionException $e){
            exit('ReflectionMethod Error');
        }
    }
}
