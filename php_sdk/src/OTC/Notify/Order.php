<?php
namespace OTC\Notify;

use OTC\Auth;
use OTC\Aws\Sns\Message;
use OTC\Config;
use OTC\Exception\HttpResponseException;
use OTC\Log\LoggerManager;

final class Order
{
    private $loggerManager;
    private $conf;
    private $data;

    public function __construct(array $conf = [])
    {
        $this->conf = new Config($conf);
        $this->loggerManager = new LoggerManager($this->conf->getLogPath(), $this->conf->getLogLevel());
        $this->_validateData();
    }

    private function _validateData()
    {
        $logger = $this->loggerManager->getLogger('orderNotify');

        try{
            $message = Message::fromRawPostData();
            $postData =  $message->toArray();
            $postDataJson = 'post data:' . json_encode($postData);
            $isValid = (new Auth($this->conf))->isValidNotify($message);
            $logger->info($postDataJson);

            if ($isValid){
                $this->data = $postData;
                if (in_array($postData['Type'], ['SubscriptionConfirmation', 'UnsubscribeConfirmation'])){
                    $this->_subscribeRequest($postData['SubscribeURL'], $postData['Type']);
                }
            }else{
                throw new HttpResponseException('数据验证错误');
            }
        }catch (\Exception $e){
            $eMessage = $e->getMessage();
            if (isset($postDataJson)){
                $eMessage .= ' ' . $postDataJson;
            }else{
                $eMessage .= ' post data:' . json_encode(file_get_contents('php://input'));
            }

            $logger->error($eMessage);
        }

        $this->loggerManager->flush();

        if (isset($e)){
            throw $e;
        }

    }

    private function _callback(callable $eventCallback, $notifyType)
    {
        if ($this->data['Type'] == $notifyType){
            call_user_func($eventCallback, json_decode($this->data['Message'], true));
        }
    }

    public function callback($eventCallback)
    {
        $this->_callback($eventCallback, 'Notification');
    }

    private function _subscribeRequest($subscribeURL, $subscriptionType)
    {
        $logger = $this->loggerManager->getLogger($subscriptionType);
        $logger->info('SubscribeURL:' . $subscribeURL);

        $ret = file_get_contents($subscribeURL);

        $logger->info($ret);
        $this->loggerManager->flush();


    }
}
