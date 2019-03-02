<?php
namespace OTC\Validate;

use OTC\Exception\InvalidArgumentException;

abstract class Validate
{
    private $params = [];

    // 构造函数
    public function __construct(array $params)
    {
        foreach ($params as $k => $param){
            $this->params[$k] = [
                'alias' => $k,
                'var' => $param,
                'isCheck' => true,
            ];
        }
    }

    public function setParamsAliasName(array $namesMapping = [])
    {
        foreach ($namesMapping as $oName => $tName){
            $this->params[$oName]['alias'] = $tName;
        }

        return $this;
    }

    public function cancelCheck(array $oNames = [])
    {
        foreach ($oNames as $oName){
            $this->params[$oName]['isCheck'] = false;
        }

        return $this;
    }

    public function checkAll()
    {
        $params = [];
        foreach ($this->params as $k => $param){
            $checkMethodName = $param['alias'] . 'Validate';
            if ($param['isCheck'] && method_exists($this , $checkMethodName)){
                if ($this->$checkMethodName($param['var']) === false){
                     throw new InvalidArgumentException($k . '参数错误');
                }

                $params[$k] = $param['var'];
            }
        }

        return $params;
    }
}
