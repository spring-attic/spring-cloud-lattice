package org.springframework.cloud.lattice.discovery;

import java.util.Collections;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.pivotal.receptor.client.ReceptorClient;

/**
 * @author Spencer Gibb
 */
@Configuration
@EnableScheduling
@EnableConfigurationProperties
public class LatticeDiscoveryClientConfiguration implements ApplicationContextAware,
		SmartInitializingSingleton {

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

	@Override
	public void afterSingletonsInstantiated() {
		if (this.applicationContext.getEnvironment() instanceof ConfigurableEnvironment) {
			final ConfigurableEnvironment environment = (ConfigurableEnvironment) this.applicationContext
					.getEnvironment();
			Object processGuid = System.getenv("PROCESS_GUID");
			if (processGuid != null) {
				MapPropertySource propertySource = new MapPropertySource(
						"Spring Cloud Lattice Environment", Collections.singletonMap(
								"spring.application.name", processGuid));
				environment.getPropertySources().addBefore("systemProperties",
						propertySource);
			}
		}
	}
}
