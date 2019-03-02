<?php
namespace OTC\Http;

use OTC\Http\Request;
use OTC\Http\Response;

final class Client
{
    public function sendRequest(Request $request)
    {
        $t1 = microtime(true);
        $ch = curl_init();
        $options = array(
            //CURLOPT_USERAGENT => '',
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_SSL_VERIFYPEER => false,
            CURLOPT_SSL_VERIFYHOST => false,
            CURLOPT_HEADER => true,
            CURLOPT_NOBODY => false,
            CURLOPT_TIMEOUT => 5,
            CURLOPT_CUSTOMREQUEST => $request->method,
            CURLOPT_URL => $request->url,
        );

        // Handle open_basedir & safe mode
        if (!ini_get('safe_mode') && !ini_get('open_basedir')) {
            $options[CURLOPT_FOLLOWLOCATION] = true;
        }

        if (!empty($request->headers)) {
            $headers = array();
            foreach ($request->headers as $key => $val) {
                array_push($headers, "$key: $val");
            }
            $options[CURLOPT_HTTPHEADER] = $headers;
        }

        if (!empty($request->body)) {
            $options[CURLOPT_POSTFIELDS] = json_encode($request->body);
        }

        curl_setopt_array($ch, $options);
        $result = curl_exec($ch);

        $t2 = microtime(true);
        $duration = round($t2 - $t1, 3);

        $ret = curl_errno($ch);
        if ($ret !== 0) {
            $r = new Response(-1, $duration, [], null, curl_error($ch));
            curl_close($ch);
            return $r;
        }

        $code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $header_size = curl_getinfo($ch, CURLINFO_HEADER_SIZE);
        $headers = self::parseHeaders(substr($result, 0, $header_size));
        $body = substr($result, $header_size);
        curl_close($ch);

        return new Response($code, $duration, $headers, $body, null);
    }

    private static function parseHeaders($raw)
    {
        $headers = array();
        $headerLines = explode("\r\n", $raw);
        foreach ($headerLines as $line) {
            $headerLine = trim($line);
            $kv = explode(':', $headerLine);
            if (count($kv) > 1) {
                $kv[0] = ucwords($kv[0], '-');
                $headers[$kv[0]] = trim($kv[1]);
            }
        }
        return $headers;
    }

}
