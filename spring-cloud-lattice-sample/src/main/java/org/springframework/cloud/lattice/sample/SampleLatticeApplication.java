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

import lombok.extern.apachecommons.CommonsLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Spencer Gibb
 */
@SpringCloudApplication
@RestController
@CommonsLog
public class SampleLatticeApplication {

	public static final String CLIENT_NAME = "testLatticeApp";
	//public static final String CLIENT_NAME = "lattice-app";

	@Autowired
	LoadBalancerClient loadBalancer;

	@Autowired
	DiscoveryClient discoveryClient;

	@Autowired
	Environment env;

	@Autowired(required = false)
	RelaxedPropertyResolver resolver;

	@RequestMapping("/me")
	public ServiceInstance me() {
		return discoveryClient.getLocalServiceInstance();
	}

	@RequestMapping("/")
	public ServiceInstance lb(@RequestParam(value = "service", defaultValue = CLIENT_NAME) String serviceId) {
		return loadBalancer.choose(serviceId);
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
