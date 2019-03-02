<?php
namespace OTC\Notify;

use OTC\Config;
use OTC\Log\LoggerManager;

final class IM
{
    private $loggerManager;
    private $conf;
    private $data;

    public function __construct(array $conf = [])
    {
        $this->conf = new Config($conf);
        $this->_fromRawPostData();
    }

    private function _fromRawPostData()
    {
        $this->loggerManager = new LoggerManager($this->conf->getLogPath(), $this->conf->getLogLevel());

        $logger = $this->loggerManager->getLogger('IMNotify');

        $requestBody = file_get_contents('php://input');
        $logger->info('post data:' . $requestBody);

        $data = json_decode($requestBody, true);
        if (JSON_ERROR_NONE !== json_last_error() || !is_array($data)) {

            $logger->error('Invalid POST data['. $requestBody .']');


        }else{
            $this->data = $data;
        }

        $this->loggerManager->flush();

    }

    public function callback(callable $eventCallback)
    {
        call_user_func($eventCallback, $this->data);
    }
}
