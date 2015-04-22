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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import io.pivotal.receptor.client.ReceptorClient;
import io.pivotal.receptor.client.ReceptorOperations;
import io.pivotal.receptor.commands.ActualLRPResponse;
import io.pivotal.receptor.commands.DesiredLRPResponse;

/**
 * @author Spencer Gibb
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = VcapBootstrapApplicationListenerTests.TestConfig.class)
@TestPropertySource(properties = {"spring.application.name="+ VcapBootstrapApplicationListenerTests.APP_NAME,
		"instance.guid="+ VcapBootstrapApplicationListenerTests.INSTANCE_GUID})
public class VcapBootstrapApplicationListenerTests {

	public static final String APP_NAME = "testVcapListenerApp";
	public static final String INSTANCE_GUID = "abcd1234";


	@Autowired
	private ReceptorOperations receptor;

	@Autowired
	private ConfigurableApplicationContext context;

	@Autowired
	private VcapBootstrapApplicationListener applicationListener;

	@Autowired
	private ObjectMapper mapper;

	@Test
	public void addsVcapApplicationToEnvironment() throws Exception {
		String vcapApplicationJson =  context.getEnvironment().getProperty(VcapBootstrapApplicationListener.VCAP_APPLICATION);

		assertThat(vcapApplicationJson, is(notNullValue()));
		Map vcapApplication = mapper.readValue(vcapApplicationJson, Map.class);
		assertThat(APP_NAME, equalTo(vcapApplication.get("name")));
		assertThat(INSTANCE_GUID, equalTo(vcapApplication.get("instance_id")));

		String vcapServicesJson =  context.getEnvironment().getProperty(VcapBootstrapApplicationListener.VCAP_SERVICES);
		assertThat(vcapServicesJson, is(notNullValue()));

		Map vcapServices = mapper.readValue(vcapServicesJson, Map.class);

		assertService(vcapServices, "redis");
		assertService(vcapServices, "mysql");
		assertService(vcapServices, "rabbit");
	}

	private void assertService(Map vcapServices, String serviceName) {
		Object o = vcapServices.get(serviceName);
		assertThat(o, is(notNullValue()));
		assertThat(o, is(instanceOf(List.class)));
		List services = List.class.cast(o);
		assertThat(services.size(), equalTo(1));
		Map service = Map.class.cast(services.get(0));
		assertThat(service.get("name"), equalTo((Object) serviceName));
	}


	@Configuration
	protected static class TestConfig {
		@Value("classpath:/vcap-test/dlrp.json")
		private Resource dlrpJson;

		@Value("classpath:/vcap-test/alrp.json")
		private Resource alrpJson;


		@Bean
		public ObjectMapper objectMapper() {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			return mapper;
		}

		@Bean
		public ReceptorClient receptorClient() throws Exception {
			ReceptorClient receptor = mock(ReceptorClient.class);

			List<DesiredLRPResponse> dlrpResponses = read(DesiredLRPResponse.class,
					dlrpJson.getInputStream());
			List<ActualLRPResponse> alrpResponses = read(ActualLRPResponse.class,
					alrpJson.getInputStream());

			when(receptor.getActualLRPs()).thenReturn(alrpResponses);
			when(receptor.getDesiredLRPs()).thenReturn(dlrpResponses);

			return receptor;
		}

		private <T> List<T> read(Class<T> aClass, InputStream inputStream)
				throws java.io.IOException {
			return objectMapper().readValue(inputStream, TypeFactory.defaultInstance()
					.constructCollectionType(List.class, aClass));
		}

		@Bean
		public VcapBootstrapApplicationListener latticeVcapApplicationListener() {
			return new VcapBootstrapApplicationListener();
		}
	}

}
