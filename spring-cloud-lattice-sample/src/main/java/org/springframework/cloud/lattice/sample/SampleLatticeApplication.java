/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.lattice.sample;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.apachecommons.CommonsLog;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

/**
 * @author Spencer Gibb
 */
@SpringCloudApplication
@RestController
@CommonsLog
public class SampleLatticeApplication {

	final static String queueName = "spring-boot";

	public static final String CLIENT_NAME = "myservice";
	// public static final String CLIENT_NAME = "lattice-app";

	@Autowired
	LoadBalancerClient loadBalancer;

	@Autowired
	DiscoveryClient discoveryClient;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	Environment env;

	@Autowired(required = false)
	RelaxedPropertyResolver resolver;

	@Autowired
	DataSource dataSource;

	@Autowired
	RedisConnectionFactory redisConnectionFactory;

	@Autowired
	StringRedisTemplate redis;

	@Autowired
	RabbitTemplate rabbit;

	@Configuration
	protected static class LatticeConfig extends AbstractCloudConfig {
		@Bean
		DataSource dataSource() {
			return connectionFactory().dataSource();
		}

		@Bean
		RedisConnectionFactory redisConnectionFactory() {
			return connectionFactory().redisConnectionFactory();
		}

		@Bean
		StringRedisTemplate template(RedisConnectionFactory connectionFactory) {
			return new StringRedisTemplate(connectionFactory);
		}

		@Bean
		Queue queue() {
			return new Queue(queueName, false);
		}

		@Bean
		TopicExchange exchange() {
			return new TopicExchange("spring-boot-exchange");
		}

		@Bean
		Binding binding(Queue queue, TopicExchange exchange) {
			return BindingBuilder.bind(queue).to(exchange).with(queueName);
		}

		@Bean
		ConnectionFactory rabbitConnectionFactory() {
			return connectionFactory().rabbitConnectionFactory();
		}
	}

	@RequestMapping("/me")
	public ServiceInstance me() {
		return discoveryClient.getLocalServiceInstance();
	}

	@RequestMapping("/services")
	public List<ServiceInstance> services() {
		List<ServiceInstance> list = new ArrayList<ServiceInstance>();
		for (String id : discoveryClient.getServices()) {
			list.addAll(discoveryClient.getInstances(id));
		}
		return list ;
	}

	@RequestMapping("/")
	public ServiceInstance lb(
			@RequestParam(value = "service", defaultValue = CLIENT_NAME) String serviceId) {
		return loadBalancer.choose(serviceId);
	}

	@RequestMapping("/ds")
	public String ds() {
		return dataSource.toString();
	}

	@RequestMapping("/ds/select")
	public Integer selectOne() {
		JdbcTemplate jdbc = new JdbcTemplate(dataSource);
		return jdbc.queryForObject("SELECT 1 FROM DUAL", Integer.class);
	}

	@RequestMapping("/redis")
	public String redis() {
		return redisConnectionFactory.toString();
	}

	@RequestMapping("/redis/test")
	public String redisTemplate() {
		redis.opsForValue().set("redisTest", "redisTestValue");
		return redis.opsForValue().get("redisTest");
	}

	@RequestMapping("/rabbit")
	public String rabbit() {
		String message = "Sending hi";
		rabbit.convertAndSend(queueName, message);
		return "Sent: "+message;
	}

	@RequestMapping("/hi")
	public String hi() {
		ServiceInstance instance = discoveryClient.getLocalServiceInstance();
		String msg = instance.getServiceId() + ":" + instance.getHost() + ":"
				+ instance.getPort();
		log.info("/hi called: " + msg);
		return msg;
	}

	@RequestMapping("/call")
	public String call() {
		return "myservice says: "
				+ restTemplate.getForObject("http://myservice/hi", String.class);
	}

	@RequestMapping("/myenv")
	public String env(@RequestParam("prop") String prop) {
		String property = new RelaxedPropertyResolver(env).getProperty(prop, "Not Found");
		return property;
	}

	public static void main(String[] args) {
		SpringApplication.run(SampleLatticeApplication.class, args);
	}
}
