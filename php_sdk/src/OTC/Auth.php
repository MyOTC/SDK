<?php
namespace OTC;

use OTC\Aws\Sns\Message;
use OTC\Aws\Sns\MessageValidator;
use OTC\Exception\InvalidArgumentException;
use OTC\Http\Request;

final class Auth
{
    private $conf;

    public function __construct(Config $conf)
    {
        $this->conf = $conf;
    }

    public function HmacSHA256($data, $secret = '')
    {
        if (!$secret){
            $secret = $this->conf->getAccessKeySecret();
        }

        $hmac = base64_encode(hash_hmac('sha256', $data, $secret, true));
        return $hmac;
    }

    public function signRequest(Request &$request)
    {
        $params = $this->_getPublicSignParams();
        $urlParsed = $this->_urlParse($request->url);
        $params = array_merge($params, $urlParsed['params']);

        if (!empty($request->body)){
            $params = array_merge($params, $request->body);
        }

        $paramString = $this->_paramsSortAndJoin($params);

        $signData = [
            strtoupper($request->method),
            strtolower($urlParsed['host']),
            $urlParsed['path'],
            $paramString
        ];

        $params['Signature'] = $this->HmacSHA256(implode("\n", $signData));

        $request->body = $params;
    }

    public function signGatewayAndReturnUrl($host, $uri, array $params)
    {
        $params = array_merge($params, $this->_getPublicSignParams());
        $paramString = $this->_paramsSortAndJoin($params);

        $signData = [
            $host,
            $uri,
            '?',
            $paramString,
        ];

        $paramString .= '&Signature=' . urlencode($this->HmacSHA256(implode('', $signData)));
        $url = $host . $uri . '?' . $paramString;

        return $url;
    }

    public function gatewayCallbackCheck($url)
    {
        preg_match('/&Signature=(.+)(&|$)/iU', $url ,$match);
        if (empty($match)){
            throw new InvalidArgumentException('缺少签名参数');
        }

        if (substr($match[0], -1, 1) == $match[2]){
            $match[0] = substr($match[0], -1);
        }

        $signUrl = str_replace($match[0],'', $url);
        $oSign = urldecode($match[1]);

        if (!$oSign
            || $oSign != $this->HmacSHA256($signUrl , $this->conf->getNotifyKeySecret())
        ) {
            throw new InvalidArgumentException('签名错误');
        }

        return true;
    }

    public function isValidNotify(Message $message)
    {
        $messageValidator = new MessageValidator();
        $isValid = $messageValidator->isValid($message);

        if ($isValid){
            $messageData =  $message->toArray();
            if ($messageData['Type'] == 'Notification'
                && $messageData['Subject'] != $this->HmacSHA256($messageData['Message'], $this->conf->getNotifyKeySecret())){

                $isValid = false;
            }
        }

        return $isValid;
    }

    private function _urlParse($url)
    {
        $urlParsed = parse_url($url);
        $urlParsed['params'] = [];

        if (array_key_exists('query', $urlParsed)) {
            $queryFields = explode('&' , $urlParsed['query']);
            foreach ($queryFields as $queryField){
                $queryFieldKV = explode('=', $queryField);
                $key = array_shift($queryFieldKV);
                $urlParsed['params'][$key] = implode('=', $queryFieldKV);
            }
        }

        return $urlParsed;
    }

    private function _getPublicSignParams()
    {
        $params = [
            'AccessKeyId' => $this->conf->getAccessKeyId(),
            'SignatureVersion' => $this->conf->getSignatureVersion(),
            'SignatureMethod' => $this->conf->getSignatureMethod(),
            'Timestamp' => gmdate('Y-m-d\TH:i:s'),
        ];

        return $params;
    }

    private function _paramsSortAndJoin($params, $isEncode = true)
    {
        ksort($params);
        $paramString = '';
        foreach ($params as $k => $param){
            if ($isEncode){
                $param = $this->percentEncode($param);
            }

            $paramString .= $k . '=' . $param . '&';
        }

        return  substr($paramString , 0,-1);
    }

    private function percentEncode($res)
    {
        $res = trim(utf8_encode(urlencode($res)));
        $res = str_replace(array('+','*','%7E'), array('%20','%2A','~'), $res);

        return $res;
    }

}
