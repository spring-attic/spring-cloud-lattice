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

package org.springframework.cloud.lattice.discovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.receptor.client.ReceptorOperations;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import java.util.Collections;
import java.util.HashMap;

/**
 * Creates VCAP_APPLICATION and VCAP_SERVICES from environment and receptor
 * so boots VcapApplicationListener and Spring Cloud Connectors for Cloud
 * Foundry can use the information provided
 VCAP_APPLICATION={"name":"application","instance_id":"FOO"}
 VCAP_SERVICES={"mysql":[{"name":"mysql","tags":["mysql"],"credentials":{"uri":"jdbc:mysql://localhost/test"}}]}
 * @author Spencer Gibb
 */
@CommonsLog
public class LatticeVcapApplicationListener implements
		ApplicationListener<ContextRefreshedEvent>, Ordered {

	static final String VCAP_APPLICATION = "VCAP_APPLICATION";

	static final String VCAP_SERVICES = "VCAP_SERVICES";

	// Before ConfigFileApplicationListener so values there can use these ones
	private int order = ConfigFileApplicationListener.DEFAULT_ORDER - 2;

	@Autowired
	private ReceptorOperations receptor;

	@Autowired(required = false)
	private ObjectMapper mapper = new ObjectMapper();

	public LatticeVcapApplicationListener() {}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int getOrder() {
		return this.order;
	}

	@Override
	@SneakyThrows
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Environment environment = event.getApplicationContext().getEnvironment();
		if (environment instanceof ConfigurableEnvironment) {
			ConfigurableEnvironment env = (ConfigurableEnvironment) environment;
			RelaxedPropertyResolver resolver = new RelaxedPropertyResolver(env);
			HashMap<String, String> vcapApplication = new HashMap<>();
			vcapApplication.put("name", resolver.getProperty("spring.application.name"));
			vcapApplication.put("instance_id", resolver.getProperty("instance.guid"));

			Object json = mapper.writeValueAsString(vcapApplication);
			MapPropertySource propertySource = new MapPropertySource(
					"latticeVcap", Collections.singletonMap(
					VCAP_APPLICATION, json));
			env.getPropertySources().addFirst(propertySource);

			//TODO: add VCAP_SERVICES
		}
		System.out.println(event);
	}
}
