package com.jsumt.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.ailk.cache.redismscache.JedisFactory;
import com.ailk.cache.redismscache.RedisAPI;

public class RedisUtil
{
    public static final String getRedisCountScript =
            "local times = redis.call('incr',KEYS[1]);\nif times == 1 then\n   redis.call('expire',KEYS[1], tonumber(ARGV[1]));\nend;\nif times > tonumber(ARGV[2]) then\n    return tostring(1)..'@'..tostring(times);\nend;\nreturn tostring(0)..'@'..tostring(times);\n";
    public static final RedisUtil aipassUtil = new RedisUtil();
    private JedisPool jedisPool;
    private Logger logger = Logger.getLogger(RedisUtil.class);

    public RedisUtil()
    {
        if (this.jedisPool == null)
        {
            try
            {
                this.jedisPool = JedisFactory.getJedisPool("aipass_cache");
            }
            catch (IllegalArgumentException e) // 捕获耿友生的客户端runTimeException
            {
                logger.error("redis客户端异常:" + e.getMessage());
            }
        }
    }

    public String del(String shardedKey, String key)
    {
        return RedisAPI.del(this.jedisPool, key).longValue() == 0L ? "删除失败!" : "删除成功!";
    }

    public boolean isCacheExists(String key)
    {
        return RedisAPI.exists(this.jedisPool, key).booleanValue();
    }

    public boolean isCacheTimeOut(String key)
    {
        Long a = RedisAPI.ttl(this.jedisPool, key);

        if (a.longValue() == -2L)
        {
            return true;
        }
        return false;
    }

    public String getStringValue(String key)
    {
        String value = RedisAPI.get(this.jedisPool, key);
        return value;
    }
    
    public Long incr(String key)
    {
        Long val=0L;
        Jedis jedis = null;
        try
        {
            jedis = this.jedisPool.getResource();
            val=jedis.incr(key);
            return val;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            jedis.close();
        }
        return val;
    }
    
    public Long decr(String key)
    {
        Long val=0L;
        Jedis jedis = null;
        try
        {
            jedis = this.jedisPool.getResource();
            val=jedis.decr(key);
            return val;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            jedis.close();
        }
        return val;
    }
    
    public String setStringValue(String key, String value)
    {
        String v = RedisAPI.set(this.jedisPool, key, value);
        return v;
    }
    /**
     * //将值value关联到key，并将key的生存时间设为seconds(秒)。 
     * setStringValue:(这里用一句话描述这个方法的作用). <br/>
     * TODO(这里描述这个方法适用条件 - 可选).<br/>
     * TODO(这里描述这个方法的执行流程 - 可选).<br/>
     * TODO(这里描述这个方法的使用方法 - 可选).<br/>
     * TODO(这里描述这个方法的注意事项 - 可选).<br/>
     *
     * @author wyf
     * @param key
     * @param value
     * @param seconds
     * @return
     * @since JDK 1.6
     */
    public String setStringValue(String key, String value,int seconds)
    {
        String v = RedisAPI.setex(this.jedisPool, key, seconds, value);
        return v;
    }

    public String getMapValue(String key, String field)
    {
        String value = RedisAPI.hget(this.jedisPool, key, field);
        return value;
    }

    public Long setMapValue(String key, String field, String value)
    {
        long v = RedisAPI.hset(this.jedisPool, key, field, value).longValue();
        return Long.valueOf(v);
    }

    public String[] isLimitMsgCounts(String key, String seconds, String limitNums)
    {
        List keys = new ArrayList();
        keys.add(key);

        List params = new ArrayList();
        params.add(seconds);
        params.add(limitNums);
        Object v = eval(getRedisCountScript, keys, params);
        String[] value = v.toString().split("@");
        return value;
    }

    private Object eval(String script, List<String> keys, List<String> params)
    {
        String scriptSha = RedisAPI.scriptLoad(this.jedisPool, script);
        return RedisAPI.evalsha(this.jedisPool, scriptSha, keys, params);
    }

    public static final RedisUtil getRedisUtil()
    {
        return aipassUtil;
    }

    public static void main(String[] args)
    {
        String v = getRedisUtil().getMapValue("xxtest", "key");
        System.out.println(v);
    }
}
