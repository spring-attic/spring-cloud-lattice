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

package org.springframework.cloud.lattice.discovery.vcap;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.pivotal.receptor.client.ReceptorOperations;
import io.pivotal.receptor.commands.ActualLRPResponse;
import io.pivotal.receptor.commands.DesiredLRPResponse;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates VCAP_APPLICATION and VCAP_SERVICES from environment and receptor
 * so boots VcapApplicationListener and Spring Cloud Connectors for Cloud
 * Foundry can use the information provided
 VCAP_APPLICATION={"name":"application","instance_id":"FOO"}
 VCAP_SERVICES={"mysql":[{"name":"mysql","tags":["mysql"],"credentials":{"uri":"jdbc:mysql://localhost/test"}}]}
 * @author Spencer Gibb
 */
@CommonsLog
public class VcapBootstrapApplicationListener implements
		ApplicationListener<ContextRefreshedEvent>, Ordered {

	static final String VCAP_APPLICATION = "VCAP_APPLICATION";

	static final String VCAP_SERVICES = "VCAP_SERVICES";

	// Before ConfigFileApplicationListener so values there can use these ones
	private int order = ConfigFileApplicationListener.DEFAULT_ORDER - 2;

	@Autowired
	private ReceptorOperations receptor;

	@Autowired(required = false)
	private ObjectMapper mapper = new ObjectMapper();

	public VcapBootstrapApplicationListener() {}

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

			String json = mapper.writeValueAsString(vcapApplication);

			Map<String, Object> map = new LinkedHashMap<>();
			map.put(VCAP_APPLICATION, json);
			map.put(VCAP_SERVICES, createServices());

			MapPropertySource propertySource = new MapPropertySource("latticeVcap", map);
			env.getPropertySources().addFirst(propertySource);
		}
		System.out.println(event);
	}

	private String createServices() throws Exception {
		Map<String, Object> services = new LinkedHashMap<>();

		Map<String, LRP> lrps = new LinkedHashMap<>();
		List<ActualLRPResponse> actualLRPs = receptor.getActualLRPs();
		List<DesiredLRPResponse> desiredLRPs = receptor.getDesiredLRPs();

		for (ActualLRPResponse alrp : actualLRPs) {
			lrps.put(alrp.getProcessGuid(), new LRP(alrp.getProcessGuid(), alrp));
		}

		for (DesiredLRPResponse dlrp : desiredLRPs) {
			if (lrps.containsKey(dlrp.getProcessGuid())) {
				lrps.get(dlrp.getProcessGuid()).setDlrp(dlrp);
			}
		}

		for (LRP lrp : lrps.values()) {
			if (lrp.getDlrp() != null) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("name", lrp.getProcessGuid());
				map.put("tags", Collections.singletonList(lrp.getProcessGuid()));
				HashMap<String, Object> credentials = new HashMap<>();
				String uri = String.format("http://%s:%d", lrp.getAlrp().getAddress(),
						lrp.getAlrp().getPorts()[0].getHostPort());
				credentials.put("uri", uri);
				map.put("credentials", credentials);
				services.put(lrp.getProcessGuid(), Collections.singletonList(map));
			}
		}

		return mapper.writeValueAsString(services);
	}

}
