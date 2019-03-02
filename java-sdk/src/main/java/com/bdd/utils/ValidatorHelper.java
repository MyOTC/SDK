package com.bdd.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Payload;
import javax.validation.Validation;
import javax.validation.Validator;

import com.bdd.domain.BddPayload;
import com.bdd.exception.BddException;
import com.bdd.exception.InvalidParameterException;
import com.bdd.exception.MissingParameterException;

/**
 * 验证工具类
 */
public class ValidatorHelper {

   private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static void validator(Object object,Class<?>... groups) throws BddException {

        Set<ConstraintViolation<Object>> constraintViolations  = validator.validate(object,groups);
        List<BddException> exceptionList = new ArrayList<>();

        for(ConstraintViolation<Object> constraintViolation:constraintViolations){
            Set<Class<? extends Payload>> payloads = constraintViolation.getConstraintDescriptor().getPayload();

            if(payloads.size()==0 && ReflectionUtils.findField(constraintViolation.getRootBeanClass(),constraintViolation.getPropertyPath().toString())!=null){
                Field field = ReflectionUtils.findField(constraintViolation.getRootBeanClass(),constraintViolation.getPropertyPath().toString());
                if (field == null) {
                    continue;
                }
                for(Annotation annotation : field.getAnnotations()){
                    try {
                        Annotation tmp = annotation.annotationType().getAnnotation(constraintViolation.getConstraintDescriptor().getAnnotation().annotationType());
                        Method method = ReflectionUtils.findMethod(constraintViolation.getConstraintDescriptor().getAnnotation().annotationType(),"payload");
                        if (method != null) {
                            @SuppressWarnings("unchecked")
                            Class<? extends Payload>[] pays = (Class<? extends Payload>[])ReflectionUtils.invokeMethod(method,tmp);
                            payloads = new HashSet<>(Arrays.asList(pays));
                        }
                    }catch (Throwable ignored){
                    }
                }
            }

            for(Class payload:payloads){
                if (payload.equals(BddPayload.MissingParameter.class)) {
                    exceptionList.add(0, new MissingParameterException(constraintViolation.getMessage()));
                } else if (payload.equals(BddPayload.InvalidParameter.class)) {
                    exceptionList.add(new InvalidParameterException(constraintViolation.getPropertyPath().toString(),constraintViolation.getMessage()));
                }
            }
        }

        if(exceptionList.size()>0){
            throw exceptionList.get(0);
        }

    }

}
