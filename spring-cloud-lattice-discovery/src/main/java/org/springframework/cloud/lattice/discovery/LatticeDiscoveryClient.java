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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.convert.converter.Converter;

import io.pivotal.receptor.commands.ActualLRPResponse;

/**
 * @author Spencer Gibb
 */
public class LatticeDiscoveryClient implements DiscoveryClient {

	private LatticeService latticeService;

	@Value("${spring.application.name}")
	private String serviceId;

	@Value("${cf.instance.ip:127.0.0.1}")
	private String host;

	@Value("${cf.instance.port:${server.port}}")
	private int port;

	public LatticeDiscoveryClient(LatticeService latticeService) {
		this.latticeService = latticeService;
	}

	@Override
	public String description() {
		return "Spring Cloud Lattice Discovery Client";
	}

	@Override
	public ServiceInstance getLocalServiceInstance() {
		return new DefaultServiceInstance(serviceId, host, port, false);
	}

	@Override
	public List<ServiceInstance> getInstances(final String serviceId) {
		List<ServiceInstance> instances = latticeService.getActualLRPsByProcessGuid(
				serviceId, new Converter<ActualLRPResponse, ServiceInstance>() {
					@Override
					public ServiceInstance convert(ActualLRPResponse response) {
						return new DefaultServiceInstance(serviceId, response
								.getAddress(), response.getPorts()[0].getHostPort(),
								false);
					}
				});

		return instances;
	}

	@Override
	public List<String> getServices() {
		LinkedHashSet<String> services = new LinkedHashSet<>();
		List<ActualLRPResponse> responses = latticeService.getActualLRPs();

		for (ActualLRPResponse response : responses) {
			services.add(response.getProcessGuid());
		}
		return new ArrayList<>(services);
	}
}
