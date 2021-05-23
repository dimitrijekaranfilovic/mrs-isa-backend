package com.mrsisa.pharmacy.aspect.impl;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.hibernate.mapping.Join;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

public abstract class OwningAspectBase {

    protected Long getIdentityParameter(JoinPoint joinPoint, Class<? extends Annotation> owningAnnotation) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object owner = method.getAnnotation(owningAnnotation);
        Object[] argumentValues = joinPoint.getArgs();
        try {
            Method identifier = owner.getClass().getMethod("identifier");
            return (Long) argumentValues[Arrays.asList(methodSignature.getParameterNames()).indexOf(identifier.invoke(owner))];
        } catch (Exception exception) {
            return null;
        }
    }

    protected Object getParameter(JoinPoint joinPoint, Class<? extends Annotation> owningAnnotation, String parameterName) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object owner = method.getAnnotation(owningAnnotation);
        Object[] argumentValues = joinPoint.getArgs();
        try {
            Method param = owner.getClass().getMethod(parameterName);
            return argumentValues[Arrays.asList(methodSignature.getParameterNames()).indexOf(param.invoke(owner))];
        } catch (Exception exception) {
            return null;
        }
    }

    protected <T extends Annotation> T getMethodAnnotation(JoinPoint joinPoint, Class<T> annotation) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return method.getAnnotation(annotation);
    }

}
