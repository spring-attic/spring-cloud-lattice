package org.springframework.cloud.lattice.discovery;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;

import io.pivotal.receptor.client.ReceptorClient;
import io.pivotal.receptor.commands.ActualLRPResponse;

/**
 * @author Spencer Gibb
 */
public class LatticeDiscoveryClient implements DiscoveryClient {

	@Autowired
	private ApplicationContext context;

	@Autowired
	private ReceptorClient receptor;

	@Value("${spring.application.name}")
	private String serviceId;

	@Value("${cf.instance.ip:127.0.0.1}")
	private String host;

	@Value("${cf.instance.port:${server.port}}")
	private int port;

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
		List<ActualLRPResponse> responses = receptor
				.getActualLRPsByProcessGuid(serviceId);
		List<ServiceInstance> instances = new ArrayList<>();
		for (ActualLRPResponse response : responses) {
			instances.add(new DefaultServiceInstance(serviceId, response.getAddress(),
					response.getPorts()[0].getHostPort(), false));
		}

		return instances;
	}

	@Override
	public List<String> getServices() {
		LinkedHashSet<String> services = new LinkedHashSet<>();
		List<ActualLRPResponse> responses = receptor.getActualLRPs();

		for (ActualLRPResponse response : responses) {
			services.add(response.getProcessGuid());
		}
		return new ArrayList<>(services);
	}
}
