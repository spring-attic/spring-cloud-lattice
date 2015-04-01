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

import java.util.List;

import lombok.SneakyThrows;

import org.springframework.core.convert.converter.Converter;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractServerList;

import io.pivotal.receptor.commands.ActualLRPResponse;

/**
 * @author Spencer Gibb
 */
public class LatticeServerList extends AbstractServerList<LatticeServer> {

	private LatticeDiscoveryProperties props;
	private LatticeService latticeService;
	private String serviceId;

	public LatticeServerList(LatticeDiscoveryProperties props,
			LatticeService latticeService, String serviceId) {
		this.props = props;
		this.latticeService = latticeService;
		this.serviceId = serviceId;
	}

	@Override
	public void initWithNiwsConfig(IClientConfig clientConfig) {
		this.serviceId = clientConfig.getClientName();
	}

	@Override
	public List<LatticeServer> getInitialListOfServers() {
		return getServers();
	}

	@Override
	public List<LatticeServer> getUpdatedListOfServers() {
		return getServers();
	}

	@SneakyThrows
	private List<LatticeServer> getServers() {
		List<LatticeServer> servers = latticeService.getActualLRPsByProcessGuid(
				serviceId, new Converter<ActualLRPResponse, LatticeServer>() {
					@Override
					public LatticeServer convert(ActualLRPResponse response) {
						return new LatticeServer(response.getProcessGuid(), response
								.getInstanceGuid(), response.getAddress(), String
								.valueOf(response.getPorts()[0].getHostPort()));
					}
				});

		return servers;
	}
}
