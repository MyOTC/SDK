<?php
namespace OTC;

use OTC\Log\LoggerManager;

abstract class Base
{
    protected $conf;
    private $auth;
    protected $loggerManager;
    protected $paramBuilder;
    protected $apiName;

    // 构造函数
    public function __construct($conf = [])
    {
        $this->conf = new Config($conf);
        $this->loggerManager = new LoggerManager($this->conf->getLogPath(), $this->conf->getLogLevel());
    }

    public function getAuth()
    {
        if (empty($this->auth)){
            $this->auth = new Auth($this->conf);
        }

        return $this->auth;
    }

    protected function _getCurrentValidate()
    {
        $calledClassNameSpilt = explode('\\', get_called_class());
        $calledClassName = array_pop($calledClassNameSpilt);
        $classValidate = '\OTC\Validate\\' .$calledClassName. 'Validate';

        if (class_exists($classValidate)){
            return $classValidate;
        }
    }

    protected function _checkValidate()
    {
        if ($this->paramBuilder && ($classValidate = $this->_getCurrentValidate())){
            (new $classValidate($this->paramBuilder->getParams()))
                ->cancelCheck($this->paramBuilder->getNonValidateCheckParams())
                ->setParamsAliasName($this->paramBuilder->getParamsAliasName())
                ->checkAll();
        }
    }

}
