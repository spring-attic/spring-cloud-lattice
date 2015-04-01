package org.springframework.cloud.lattice.discovery;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.pivotal.receptor.client.ReceptorClient;

/**
 * @author Spencer Gibb
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties
public class LatticeDiscoveryClientConfiguration implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Bean
	public ReceptorClient receptorClient() {
		return new ReceptorClient(latticeDiscoveryProperties().getReceptorHost());
	}

	@Bean
	public LatticeDiscoveryClient latticeDiscoveryClient() {
		return new LatticeDiscoveryClient();
	}

	@Bean
	public LatticeDiscoveryProperties latticeDiscoveryProperties() {
		return new LatticeDiscoveryProperties();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
}
