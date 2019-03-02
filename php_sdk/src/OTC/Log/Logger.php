<?php
namespace OTC\Log;

use OTC\Config;

class Logger
{
    const LOG_TYPE_BOUNDARY = 'boundary';
    const LOG_TYPE_INFO = 'info';
    const LOG_TYPE_ERROR = 'error';

    private $recordList = [];
    private $name;

    public function __construct($name)
    {
        $this->name = $name;
        $this->_start();
    }

    private function _log($level , $message)
    {
        list($msec, $sec) = explode(' ', microtime());
        $items = [
            date('y/m/d H:i:s') . '[' .intval(floatval($msec) * 1000). ']',
            $level,
            $message,
        ];

        $this->recordList[] = [$level, implode(' ', $items)];
    }

    public function error($message)
    {
        $this->_log(self::LOG_TYPE_ERROR, $message);
    }

    public function info($message)
    {
        $this->_log(self::LOG_TYPE_INFO, $message);
    }

    public function getRecordList($level)
    {
        $this->_end();
        $retRecordList = [];

        foreach ($this->recordList as $item){
            if ($level == Config::LOG_LEVEL_ERROR && !in_array($item[0], [self::LOG_TYPE_ERROR, self::LOG_TYPE_BOUNDARY])){
                continue;
            }

            $retRecordList[] = $item[1];
        }

        return $retRecordList;
    }

    public function getName()
    {
        return $this->name;
    }

    private function _start()
    {
        $this->_log(self::LOG_TYPE_BOUNDARY, $this->name . ' start');
    }

    private function _end()
    {
        $this->_log(self::LOG_TYPE_BOUNDARY, $this->name . ' end');
    }
}
