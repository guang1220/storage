package com.light.storage.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.light.storage.entity.Account;
import com.light.storage.entity.BirthInfo;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;

@Configuration
public class RedisConfig {

    /*
     默认使用keyGenerator生成：默认使用simpleKeyGennerator生成key：
      simpleKeyGennerator默认如果没有参数：key = new SimpleKey（）
      一个参数：key = 参数值
      多个参数：key = new SimpleKey（params）
      如果你想没有参数的方法也有包含属性的key生成，则需自定义keyGenerator，然后在
      @Cacheable(value = "storage",keyGenerator = "keyGenerator" )中绑定keyGenerator，常量key不用自定义keyGenerator
      */
    //自定义缓存key生成策略
//    @Bean
//    public KeyGenerator AccountKeyGenerator() {
//        return new KeyGenerator() {
//            @Override
//            public Object generate(Object target, java.lang.reflect.Method method, Object... params) {
//                StringBuffer sb = new StringBuffer();
////                sb.append(target.getClass().getName());
//                sb.append(method.getName());
//                for (Object obj : params) {
//                    sb.append(obj.toString());
//                }
//                System.out.println(sb.toString());
//                return sb.toString();
//            }
//        };
//    }

    /*
    在引入了Redis的starter之后 默认使用的CacheManager是RedisCacheManager
    默认创建的RedisCacheManager操作Redis的时候使用的是RedisTemplate<Object,Object> 请注意 泛型是Object,Object
    而这个默认的RedisTemplate<Object,Object>使用的序列化机制正是JdkSerializationRedisSerializer 它会导致存储(中文)乱码
    此时 需要自定义CacheManager
    */
    // 定制缓存管理器规则
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        //初始化RedisCacheWriter
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory);
        //设置CacheManager的序列化方式为JSON
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        /*
        * springboot在整合Redis+spring-security时RedisTemplate设置完序列化后运行碰到报错
        * Could not read JSON: Unrecognized field “enabled”，
        * 在网上查找资料后发现是在json序列化时，不仅是根据get方法来序列化的，而是实体类中所有的有返回值的方法都会将返回的值序列化，
        * 但是反序列化时是根据set方法来实现的，所以当实体类中有非get，set方法的方法有返回值时，反序列化时就会出错。
        * 添加这个 om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);或使用JsonIgnore注解
        * */
        RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer();
        RedisSerializationContext.SerializationPair<Object> pair = RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer);
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(pair)
//                .computePrefixWith(cacheName -> "redis" + cacheName)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.string()));
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.java()));
        //设置默认超时过期时间
        defaultCacheConfig.entryTtl(Duration.ofMinutes(10));

        return new RedisCacheManager(redisCacheWriter, defaultCacheConfig);
    }



}
