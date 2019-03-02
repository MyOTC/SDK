<?php
namespace OTC\Log;

use OTC\Config;

final class LoggerManager
{
    private $logPath = '/Users/bitfree/www/logs';
    private $logLevel = '';
    private static $instanceMap = [];
    public function __construct($logPath, $logLevel)
    {
        $this->setLogPath(trim($logPath));
        $this->logLevel = $logLevel;
        if ($logLevel != Config::LOG_LEVEL_CLOSE){
            $this->_checkLogPathPermission();
        }
    }

    public function getLogger($name)
    {
        $hash = md5($name);
        if (!in_array($hash , self::$instanceMap)){
            self::$instanceMap[$hash] = new Logger($name);
        }

        return  self::$instanceMap[$hash];
    }


    public function flush()
    {
        if (!$this->logLevel){
            return;
        }

        $logFilePath = $this->_getLogFilePath();
        foreach (self::$instanceMap as $k => $logger){

            $recordList = $logger->getRecordList($this->logLevel);
            $recordString = implode(PHP_EOL, $recordList) . PHP_EOL;
            file_put_contents($logFilePath, $recordString , FILE_APPEND);
        }

        self::$instanceMap = [];

    }

    public function setLogPath($path)
    {
        if (substr($path, 0, 1) == '.'){
            if (substr($path, 0, 2) == '.' . DIRECTORY_SEPARATOR){
                $path = substr($path, 1);
            }

            $path = __DIR__ . $path;
        }

        $this->logPath = $path;

    }

    private function _checkLogPathPermission()
    {
        if (!(is_dir($this->logPath) && is_writable($this->logPath))){
            exit('请创建或设置日志目录写权限,目录位置：' . $this->logPath);
        }
    }

    private function _getLogFilePath()
    {
        $this->_checkLogPathPermission();
        if (substr($this->logPath, -1) != DIRECTORY_SEPARATOR){
            $this->logPath .= DIRECTORY_SEPARATOR;
        }

        $logFile = $this->logPath . date('Ymd');

        return $logFile;
    }

}
