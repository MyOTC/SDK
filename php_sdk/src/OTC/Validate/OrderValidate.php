<?php
namespace OTC\Validate;

use OTC\Config;

class OrderValidate extends Validate
{
    public function varietyValidate($var)
    {
        if (!array_key_exists($var, Config::getVarietyList())){
            return false;
        }
        
    }

    public function currencyValidate($var)
    {
        if (!array_key_exists($var, Config::getCurrencyList())){
            return false;
        }
        
    }

    public function amountValidate($var)
    {
        if (!(is_numeric($var) && $var > 0)){
            return false;
        }

    }

    public function paymentTypeValidate($var)
    {
        $paymentTypes = explode(',', $var);
        foreach ($paymentTypes as $paymentType){
            if (!array_key_exists($paymentType, Config::getPaymentType())){
                return false;
            }
        }
        
    }

    public function outOrderNoValidate($var)
    {
        if (!trim($var)){
            return false;
        }
        
    }

    public function unitValidate($var)
    {
        if ($var != 2){
            return false;
        }
    }

    public function outUidValidate($var)
    {
        if (!trim($var)){
            return false;
        }
    }

    public function nameValidate($var)
    {
        if (!trim($var)){
            return false;
        }
    }

    public function idNumberValidate($var)
    {
        if (!trim($var)){
            return false;
        }
    }

    public function bankValidate($var)
    {
        if (!trim($var)){
            return false;
        }
        
    }

    public function bankNameValidate($var)
    {
        if (!trim($var)){
            return false;
        }
        
    }

    public function callbackValidate($var)
    {
        if (!preg_match('/(http:\/\/)|(https:\/\/)/i', $var)) {
            return false;
        }
    }
}
