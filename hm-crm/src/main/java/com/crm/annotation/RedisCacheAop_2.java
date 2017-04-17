package com.crm.annotation;
import java.util.List;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.crm.common.util.JsonUtils;
import com.crm.common.util.RedisUtil;

@Aspect
@Component
public class RedisCacheAop_2 {

	
	ThreadLocal<Long> time=new ThreadLocal<Long>();
	ThreadLocal<String> tag=new ThreadLocal<String>();
	RedisUtil redisUtil = new RedisUtil();
	
	@Pointcut("@annotation(com.crm.annotation.RedisCache)")
	public void redisCache(){
		System.out.println("切入点-----加入缓存");
	}
	
	
	@Pointcut("@annotation(com.crm.annotation.RedisEvict)")
	public void redisEvict(){
		System.out.println("切入点-----移除缓存");
	}
	
	
//	/**
//	 * 在所有标注@Log的地方切入
//	 * @param joinPoint
//	 */
//	@Before("RedisCache()")
//	public void beforeExec(JoinPoint joinPoint){
//		
//		time.set(System.currentTimeMillis());
//		tag.set(UUID.randomUUID().toString());
//		
//		info(joinPoint);
//		
//		MethodSignature ms=(MethodSignature) joinPoint.getSignature();
//		Method method=ms.getMethod();
//		System.out.println("执行之前");
//		System.out.println(method.getAnnotation(RedisCache.class).name()+"标记"+tag.get());
//	}
//	
//	@After("RedisCache()")
//	public void afterExec(JoinPoint joinPoint){
//		MethodSignature ms=(MethodSignature) joinPoint.getSignature();
//		Method method=ms.getMethod();
//		System.out.println("执行之后");
//		System.out.println("标记为"+tag.get()+"的方法"+method.getName()+"运行消耗"+(System.currentTimeMillis()-time.get())+"ms");
//	}
//	
//	
//	@AfterThrowing(pointcut = "RedisCache()", throwing = "e")    
//    public  void doAfterThrowing(JoinPoint joinPoint, Throwable e) { 
//		 System.out.println("抛出的异常:-------" + e.getMessage()+"--------"  
//	                + e.getCause());  
//	       System.out.println("异常详细信息:-------"+e.fillInStackTrace());    
//    } 
	
/*	@AfterThrowing("log()")
	public void afterThrowing(Method method, Object[] args, Object target,  
            RuntimeException  throwable) {  
        System.out.println("产生异常的方法名称：  " + method.getName());  
          
        for(Object o:args){  
            System.out.println("方法的参数：   " + o.toString());  
        }  
          
        System.out.println("代理对象：   " + target.getClass().getName());  
        System.out.println("抛出的异常:    " + throwable.getMessage()+">>>>>>>"  
                + throwable.getCause());  
        System.out.println("异常详细信息：　　　"+throwable.fillInStackTrace());  
    } 
	*/
	
	
	
	
	
	
//	@Around("Cache()")
//	public Object aroundExec(ProceedingJoinPoint pjp) throws Throwable{
//		System.out.println("我是Around，来打酱油的  环绕");
//		pjp.proceed();
//		return pjp;
//	}
	
//	private void info(JoinPoint joinPoint){
//		System.out.println("--------------------------------------------------");
//		System.out.println("Kind:\t"+joinPoint.getKind());
//		System.out.println("Target:\t"+joinPoint.getTarget().toString());
//		Object[] os=joinPoint.getArgs();
//		System.out.println("Args:");
//		for(int i=0;i<os.length;i++){
//			System.out.println("\t==>参数["+i+"]:\t"+os[i].toString());
//		}
//		System.out.println("Signature:\t"+joinPoint.getSignature());
//		System.out.println("SourceLocation:\t"+joinPoint.getSourceLocation());
//		System.out.println("StaticPart:\t"+joinPoint.getStaticPart());
//		System.out.println("--------------------------------------------------");
//	}
	
	
	
	
	
	
	
	
	//前置由于数据库数据变更  清理redis缓存  
    @Before("@annotation(redisEvict)")  
    public void doBefore (JoinPoint jp,RedisEvict redisEvict){  
        try{  
            String keyName = redisEvict.type().getName();
            String key = keyName.substring(keyName.lastIndexOf(".")+1,keyName.length());
            // 清除对应缓存  
            Long num=redisUtil.del(key);
            System.out.println("清除缓存:"+key+"----"+num);
            // 清除对应缓存  
        }catch (Exception e) {  
            e.printStackTrace();  
            System.out.println("缓存服务器出现问题,发邮箱，发信息...");       
        }  
    }  

    // 配置环绕方法  
    @Around("@annotation(redisCache)")  
    public Object doAround(ProceedingJoinPoint pjp,RedisCache redisCache)  
            throws Throwable {  
        //得到注解上类型  
		Class<?> modelType = redisCache.type();
		String keyName = modelType.getName();
		String key = keyName.substring(keyName.lastIndexOf(".")+1,keyName.length());
        // 去Redis中看看有没有我们的数据 包名+ 类名 + 方法名 + 参数(多个)  
        String cacheKey =getCacheKey(pjp);
        String value = null;  
        try {
        	//当取redis发生异常时，为了不影响程序正常执行，需要try..catch()...  
            //检查redis中是否有缓存  
//            value = redisUtil.get(cacheKey); 
        	value = redisUtil.hget(key,cacheKey); 
        } catch (Exception e) {  
            e.printStackTrace();  
            System.out.println("缓存服务器出现问题,发邮箱，发信息...");  
        }  
        
        
        // result是方法的最终返回结果  
        Object result = null;  
        if (null == value) {  
            // 缓存未命中  
            System.out.println("缓存未命中");  

            // 后端查询数据    
            result = pjp.proceed();  

            try {//当取redis发生异常时，为了不影响程序正常执行，需要try..catch()...  
            	
                // 序列化结果放入缓存  
                String json = serialize(result);  
//                redisUtil.set(cacheKey, json);  
                redisUtil.hset(key,cacheKey, json);
                redisUtil.expire(key,10800);
//                if(redisCache.expire()>0) {   
//                	Long ff=redisUtil.expire(cacheKey, redisCache.expire());//设置缓存时间  
//                }  
            } catch (Exception e) {  
                e.printStackTrace();  
                System.out.println("缓存服务器出现问题,----------发邮箱，发信息...");  
            }  
        } else {  
            try{//当数据转换失败发生异常时，为了不影响程序正常执行，需要try..catch()...  
                // int i =1/0;  
                // 得到被代理方法的返回值类型  
                @SuppressWarnings("rawtypes")
                Class returnType = ((MethodSignature) pjp.getSignature()).getReturnType();  
                //把json反序列化  
                result = deserialize(value, returnType, modelType);  
                if (result==null) {
                	result = pjp.proceed();  
				} 
            } catch (Exception e) {  
                //数据转换失败，到后端查询数据    
                result = pjp.proceed();  
                e.printStackTrace();  
                System.out.println("缓存命中,-------------但数据转换失败...");  
            }  
        }  
        return result;  
    }  


    protected String serialize(Object target) {  
        return JsonUtils.objectToJson(target);  
    }  

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Object deserialize(String jsonString, Class clazz, Class modelType) {  
        // 序列化结果应该是List对象  
        if (clazz.isAssignableFrom(List.class)) {  
            return JsonUtils.jsonToList(jsonString, modelType);  
        }  
        // 序列化结果是普通对象  
        return JsonUtils.jsonToPojo(jsonString, clazz);  
    }  


    // 包名+ 类名 + 方法名 + 参数(多个) 生成Key  
    public String getCacheKey(ProceedingJoinPoint pjp) {  
        StringBuffer key = new StringBuffer(); 
        
/*        // 包名+ 类名 cn.core.serice.product.ProductServiceImpl.productList  
        String packageName = pjp.getTarget().getClass().getName();  
        key.append(packageName);  */
        
        
        // 方法名  
        String methodName = pjp.getSignature().getName();  
        key.append(methodName);  

        // 参数(多个)  
        Object[] args = pjp.getArgs();  

        for (Object arg : args) {  
            // 参数  
            key.append(".").append(arg.toString());  
        }  

        return key.toString();  
    }  

	
}
