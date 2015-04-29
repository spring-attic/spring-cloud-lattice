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

package org.springframework.cloud.lattice.connector;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.pivotal.receptor.client.ReceptorOperations;
import io.pivotal.receptor.commands.ActualLRPResponse;
import io.pivotal.receptor.commands.DesiredLRPResponse;
import lombok.SneakyThrows;
import org.junit.Before;
import org.mockito.Mock;
import org.springframework.cloud.service.ServiceInfo;
import org.springframework.cloud.util.EnvironmentAccessor;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Scanner;

/**
 * @author Spencer Gibb
 */
public class AbstractServiceInfoCreatorTests {

	@Mock
	protected EnvironmentAccessor env;

	@Mock
	protected ReceptorOperations receptor;

	protected ObjectMapper mapper = new ObjectMapper();

	@Before
	public void setup() {
		initMocks(this);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	protected LatticeConnector createConnector() {
		LatticeConnector connector = new LatticeConnector();
		connector.setEnvironmentAccessor(env);
		connector.setReceptorOperations(receptor);

		String alrpJson = readTestDataFile("/lattice-test/alrp.json");
		String dlrpJson = readTestDataFile("/lattice-test/dlrp.json");

		List<DesiredLRPResponse> dlrpResponses = read(DesiredLRPResponse.class, dlrpJson);
		List<ActualLRPResponse> alrpResponses = read(ActualLRPResponse.class, alrpJson);

		when(receptor.getActualLRPs()).thenReturn(alrpResponses);
		when(receptor.getDesiredLRPs()).thenReturn(dlrpResponses);
		return connector;
	}

	protected String readTestDataFile(String fileName) {
		Scanner scanner = null;
		try {
			Reader fileReader = new InputStreamReader(getClass().getResourceAsStream(
					fileName));
			scanner = new Scanner(fileReader);
			return scanner.useDelimiter("\\Z").next();
		}
		finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	@SneakyThrows
	protected  <T> List<T> read(Class<T> aClass, String s) {
		CollectionType type = TypeFactory.defaultInstance().constructCollectionType(List.class, aClass);
		return mapper.readValue(s, type);
	}


	@SuppressWarnings("unchecked")
	protected <T extends ServiceInfo> T findServiceInfo(LatticeConnector connector, Class<T> type, String serviceInfoId) {
		List<ServiceInfo> serviceInfos = connector.getServiceInfos();
		assertNotNull("serviceInfos was null", serviceInfos);

		ServiceInfo serviceInfo = null;
		for (ServiceInfo si : serviceInfos) {
			if (si.getId().equals(serviceInfoId)) {
				serviceInfo = si;
			}
		}
		assertNotNull("serviceInfo is null", serviceInfo);
		assertThat(serviceInfo, is(instanceOf(type)));
		return (T) serviceInfo;
	}
}
