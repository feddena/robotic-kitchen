package pizzapipeline.server.database;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PreDestroy;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class RedisClient {

    private static final String JEDIS_MASTER_HOST = "localhost";

    private static final JedisPool pool = new JedisPool(buildPoolConfig(), JEDIS_MASTER_HOST);

    private static JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(60).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(30).toMillis());
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }

    @PreDestroy
    public static void destroy() {
        pool.destroy();
    }

    @Nullable
    public String get(String key) {
        Jedis jedis = null;
        String value = null;
        try {
            jedis = pool.getResource();
            value = jedis.get(key);
        } catch (Exception e) {
            System.out.println("Fail to get " + key + " due to " + e.getMessage());
        } finally {
            returnResource(pool, jedis);
        }
        return value;
    }

    @Nullable
    public String set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.set(key, value);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            returnResource(pool, jedis);
        }
    }

    @Nullable
    public String set(String key, String value, int expire) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            int time = jedis.ttl(key).intValue() + expire;
            String result = jedis.set(key, value);
            jedis.expire(key, time);
            return result;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            returnResource(pool, jedis);
        }
    }

    @Nullable
    public Long delPre(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            Set<String> set = jedis.keys(key + "*");
            int result = set.size();
            Iterator<String> it = set.iterator();
            while (it.hasNext()) {
                String keyStr = it.next();
                jedis.del(keyStr);
            }
            return Long.valueOf(result);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            returnResource(pool, jedis);
        }
    }

    @Nullable
    public Long del(String... keys) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.del(keys);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        } finally {
            returnResource(pool, jedis);
        }
    }

    public Long append(String key, String str) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = pool.getResource();
            res = jedis.append(key, str);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return res;
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }

    public Boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.exists(key);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        } finally {
            returnResource(pool, jedis);
        }
    }

    @Nullable
    public Long setnx(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.setnx(key, value);
        } catch (Exception e) {

            System.out.println(e.getMessage());
            return null;
        } finally {
            returnResource(pool, jedis);
        }
    }

    public String setex(String key, String value, int seconds) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = pool.getResource();
            res = jedis.setex(key, seconds, value);
        } catch (Exception e) {

            System.out.println(e.getMessage());
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }

    public Long setrange(String key, String str, int offset) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.setrange(key, offset, str);
        } catch (Exception e) {

            System.out.println(e.getMessage());
            return 0L;
        } finally {
            returnResource(pool, jedis);
        }
    }

    public List<String> mget(String... keys) {
        Jedis jedis = null;
        List<String> values = null;
        try {
            jedis = pool.getResource();
            values = jedis.mget(keys);
        } catch (Exception e) {

            System.out.println(e.getMessage());
        } finally {
            returnResource(pool, jedis);
        }
        return values;
    }

    public String mset(String... keysvalues) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = pool.getResource();
            res = jedis.mset(keysvalues);
        } catch (Exception e) {

            System.out.println(e.getMessage());
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }

    public Long msetnx(String... keysvalues) {
        Jedis jedis = null;
        Long res = 0L;
        try {
            jedis = pool.getResource();
            res = jedis.msetnx(keysvalues);
        } catch (Exception e) {

            System.out.println(e.getMessage());
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }

    public String getset(String key, String value) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = pool.getResource();
            res = jedis.getSet(key, value);
        } catch (Exception e) {

            System.out.println(e.getMessage());
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }

    public String getrange(String key, int startOffset, int endOffset) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = pool.getResource();
            res = jedis.getrange(key, startOffset, endOffset);
        } catch (Exception e) {

            System.out.println(e.getMessage());
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }

    @Nullable
    public Long incr(String key) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = pool.getResource();
            res = jedis.incr(key);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }

    @Nullable
    public Long incrBy(String key, Long integer) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = pool.getResource();
            res = jedis.incrBy(key, integer);
        } catch (Exception e) {

            System.out.println(e.getMessage());
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }


    public Long decr(String key) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = pool.getResource();
            res = jedis.decr(key);
        } catch (Exception e) {

            System.out.println(e.getMessage());
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }


    public Long decrBy(String key, Long integer) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = pool.getResource();
            res = jedis.decrBy(key, integer);
        } catch (Exception e) {

            System.out.println(e.getMessage());
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }

    public String type(String key) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = pool.getResource();
            res = jedis.type(key);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }

    private static void returnResource(JedisPool pool, Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }


}
