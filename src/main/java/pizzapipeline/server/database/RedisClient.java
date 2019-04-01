package pizzapipeline.server.database;

import java.time.Duration;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Component
public class RedisClient {
    private static final Logger log = LoggerFactory.getLogger(RedisClient.class);

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


    public Long lpush(String key, String... strs) {
        Jedis jedis = null;
        Long res = null;
        try {
            jedis = pool.getResource();
            res = jedis.lpush(key, strs);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            returnResource(pool, jedis);
        }
        return res;
    }

    synchronized public String rpop(String key) {
        Jedis jedis = null;
        String res = null;
        try {
            jedis = pool.getResource();
            res = jedis.rpop(key);
        } catch (Exception e) {
            log.error(e.getMessage());
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
            log.warn(e.getMessage());
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
