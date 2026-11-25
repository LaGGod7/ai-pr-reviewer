package org.gd.backend.webhook;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ReviewDeduplicator {
    private static final Duration TTL= Duration.ofHours(24);

    private final StringRedisTemplate redis;

    public ReviewDeduplicator(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public boolean markIfNew(String owner,String repo,int number,String sha){
        Boolean isNew = redis.opsForValue().setIfAbsent(key(owner,repo,number,sha),"1",TTL);
        return Boolean.TRUE.equals(isNew);
    }

    public void release(String owner, String repo,int number ,String sha){
        redis.delete(key(owner,repo,number,sha));
    }

    private static String key(String owner,String repo,int number,String sha){
        return "review:" + owner + "/" + repo + "#" + number + ":" + sha;
    }

}
