package com.crm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * 
 * 此方法用于添加redis缓存
 * 
 * */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@SuppressWarnings("rawtypes")
public @interface RedisCache {
	Class type();  
	String name();
    int expire() default 0;      //缓存多少秒,默认无限期    
    String cacheKey() ;
}
