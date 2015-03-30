package org.springframework.cloud.lattice.discovery;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractServerList;

import io.pivotal.receptor.client.ReceptorClient;
import io.pivotal.receptor.commands.ActualLRPResponse;

/**
 * @author Spencer Gibb
 */
public class LatticeServerList extends AbstractServerList<LatticeServer> {

	private LatticeDiscoveryProperties props;
	private ReceptorClient receptor;
	private String serviceId;

	public LatticeServerList(LatticeDiscoveryProperties props, ReceptorClient receptor,
			String serviceId) {
		this.props = props;
		this.receptor = receptor;
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
		List<ActualLRPResponse> responses = receptor
				.getActualLRPsByProcessGuid(serviceId);

		List<LatticeServer> servers = new ArrayList<>();

		for (ActualLRPResponse response : responses) {
			servers.add(new LatticeServer(response.getProcessGuid(), response
					.getInstanceGuid(), response.getAddress(), ""
					+ response.getPorts()[0].getHostPort()));
		}

		return servers;
	}
}
