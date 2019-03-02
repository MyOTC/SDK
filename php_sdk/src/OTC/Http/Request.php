<?php
namespace OTC\Http;

final class Request
{
    public $url;
    public $headers;
    public $body;
    public $method;

    public function __construct($method, $url, array $headers = array(), $body = null)
    {
        $this->method = strtoupper($method);
        $this->url = $url;
        $this->headers = $headers;
        $this->body = $body;
    }

    public function toString()
    {
        return 'request ' . $this->method . ' ' . $this->url . ' ' . json_encode($this->body);
    }

}
