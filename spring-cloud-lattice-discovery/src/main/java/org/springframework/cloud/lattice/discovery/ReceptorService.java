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
import java.util.List;

import org.springframework.cloud.lattice.discovery.LatticeDiscoveryProperties.Route;
import org.springframework.core.convert.converter.Converter;

import io.pivotal.receptor.client.ReceptorClient;
import io.pivotal.receptor.commands.ActualLRPResponse;
import io.pivotal.receptor.support.Port;

/**
 * @author Spencer Gibb
 */
public class ReceptorService {

	private ReceptorClient receptor;
	private LatticeDiscoveryProperties props;

	public ReceptorService(ReceptorClient receptor, LatticeDiscoveryProperties props) {
		this.receptor = receptor;
		this.props = props;
	}

	public <T> List<T> getActualLRPsByProcessGuid(String processGuid,
			Converter<ActualLRPResponse, T> converter) {
		List<ActualLRPResponse> responses = receptor
				.getActualLRPsByProcessGuid(processGuid);
		List<T> lrps = new ArrayList<>();
		for (ActualLRPResponse response : responses) {
			T converted = converter.convert(response);
			lrps.add(converted);
		}

		if (lrps.isEmpty() && props.getRoutes().containsKey(processGuid)) {
			Route route = props.getRoutes().get(processGuid);
			ActualLRPResponse response = new ActualLRPResponse();
			response.setAddress(route.getAddress());
			response.setIndex(0);
			response.setInstanceGuid(processGuid + ":" + route.getAddress() + ":"
					+ route.getPort());
			Port port = new Port();
			port.setHostPort(route.getPort());
			response.setPorts(new Port[] { port });
			response.setProcessGuid(processGuid);

			lrps.add(converter.convert(response));
		}

		return lrps;
	}
}
