package vttp2022.jsontoredis.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
    
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private Integer redisPort;

    @Value("${spring.redis.database}")
    private Integer redisDatabase;

    //@Bean annotates an object for SpringBoot to manage. Dependancy Injection
    //Managed Beans (@Service, @Controller, @Repository) - spring controls their life cycle. 
    //Scope is you tell spring how long the object lives they also do not have a constructor with parameters. 
    //so it defaults to one with no parameters. 
    
    @Bean(name="games") //most cases dont need to put name, but in this case Spring gets confused so you need name
    //template code standard never changes i think
    public RedisTemplate<String, String> createRedisTemplate() {
        //Redis Config File to configure host, port, pw etc
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        config.setDatabase(redisDatabase);

        //RedisStandaloneConfiguration 
        // |____v
        //Driver talks to the database, we use Jedis
        //we have to configure Jedis, then we hook the RedisStandConfig to Jedis
        // |____v
        //Redis Database

        //Jedis Client Config
        JedisClientConfiguration jedisConfig = JedisClientConfiguration.builder().build();
        //jedisPoolConfig (host port etc) poolConfig (new pool client)
        //i think is like take Redis config (on top), put into jedisconfig (on the right)
        JedisConnectionFactory jedisFac = new JedisConnectionFactory(config, jedisConfig);
        jedisFac.afterPropertiesSet();

        //RedisTemplate will talk to the RedisConfig for us to use
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisFac);
        
        //Performs auto serialize / deserialize
        //converts the String into UTF-8, else it will be unicode UTF-16
        //largely depend on your application, UTF-8 more common and readable
        template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new StringRedisSerializer());
		return template;
    }
}
