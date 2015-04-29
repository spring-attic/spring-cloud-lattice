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

import io.pivotal.receptor.client.ReceptorClient;
import io.pivotal.receptor.commands.ActualLRPResponse;
import io.pivotal.receptor.commands.DesiredLRPResponse;
import io.pivotal.receptor.support.Port;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.lattice.LatticeProperties;
import org.springframework.cloud.lattice.discovery.LatticeDiscoveryProperties.Route;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Spencer Gibb
 * @author Dave Syer
 */
public class ReceptorService {

	private ReceptorClient receptor;
	private LatticeProperties latticeProperties;
	private LatticeDiscoveryProperties discoveryProperties;

	public ReceptorService(ReceptorClient receptor, LatticeProperties latticeProperties, LatticeDiscoveryProperties discoveryProperties) {
		this.receptor = receptor;
		this.latticeProperties = latticeProperties;
		this.discoveryProperties = discoveryProperties;
	}

	public <T> List<T> getActualLRPsByProcessGuid(String processGuid,
			Converter<ActualLRPResponse, T> converter) {
		List<ActualLRPResponse> responses = getResponses(processGuid);
		List<T> lrps = new ArrayList<>();
		for (ActualLRPResponse response : responses) {
			T converted = converter.convert(response);
			lrps.add(converted);
		}
		return lrps;
	}

	private List<ActualLRPResponse> getResponses(String processGuid) {
		List<ActualLRPResponse> responses = new ArrayList<>();
		if (!latticeProperties.getReceptor().isUseRouterAddress()) {
			receptor.getActualLRPsByProcessGuid(processGuid);
		}
		else {
			DesiredLRPResponse desired = receptor.getDesiredLRP(processGuid);
			if (desired != null) {
				ActualLRPResponse response = new ActualLRPResponse();
				String address = getAddress(desired);
				response.setAddress(address);
				response.setIndex(0);
				response.setInstanceGuid(processGuid + ":" + address);
				Port port = new Port();
				port.setHostPort(80);
				response.setPorts(new Port[] { port });
				response.setProcessGuid(processGuid);
				responses.add(response);
			}
		}
		if (responses.isEmpty() && discoveryProperties.getRoutes().containsKey(processGuid)) {
			Route route = discoveryProperties.getRoutes().get(processGuid);
			ActualLRPResponse response = new ActualLRPResponse();
			response.setAddress(route.getAddress());
			response.setIndex(0);
			response.setInstanceGuid(processGuid + ":" + route.getAddress() + ":"
					+ route.getPort());
			Port port = new Port();
			port.setHostPort(route.getPort());
			response.setPorts(new Port[] { port });
			response.setProcessGuid(processGuid);
		}

		return responses;
	}

	private String getAddress(DesiredLRPResponse desired) {
		Map<String, io.pivotal.receptor.support.Route[]> routes = desired.getRoutes();
		if (routes.isEmpty()) {
			return "<UNKNOWN>";
		}
		return routes.values().iterator().next()[0].getHostnames()[0];
	}
}
