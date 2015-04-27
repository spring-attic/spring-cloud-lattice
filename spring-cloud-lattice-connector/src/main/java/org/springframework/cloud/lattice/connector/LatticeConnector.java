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

import io.pivotal.receptor.client.ReceptorClient;
import io.pivotal.receptor.client.ReceptorOperations;
import io.pivotal.receptor.commands.ActualLRPResponse;
import io.pivotal.receptor.commands.DesiredLRPResponse;
import org.springframework.cloud.AbstractCloudConnector;
import org.springframework.cloud.FallbackServiceInfoCreator;
import org.springframework.cloud.app.ApplicationInstanceInfo;
import org.springframework.cloud.app.BasicApplicationInstanceInfo;
import org.springframework.cloud.service.BaseServiceInfo;
import org.springframework.cloud.util.EnvironmentAccessor;

import java.lang.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Spencer Gibb
 */
public class LatticeConnector extends AbstractCloudConnector<Process> {

	private EnvironmentAccessor environment = new EnvironmentAccessor();
	private ReceptorOperations receptorClient;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public LatticeConnector() {
		super((Class) LatticeServiceInfoCreator.class);
		//TODO: where to get receptor host and credentials?
		this.receptorClient = new ReceptorClient("receptor.192.168.11.11.xip.io");
	}

	public void setEnvironmentAccessor(EnvironmentAccessor environment) {
		this.environment = environment;
	}

	public void setReceptorOperations(ReceptorOperations receptorClient) {
		this.receptorClient = receptorClient;
	}

	@Override
	public boolean isInMatchingCloud() {
		return environment.getEnvValue("PROCESS_GUID") != null;
	}

	@Override
	public ApplicationInstanceInfo getApplicationInstanceInfo() {
		String instanceGuid = environment.getEnvValue("INSTANCE_GUID");
		String processGuid = environment.getEnvValue("PROCESS_GUID");
		//TODO: read receptor?
		HashMap<String, Object> map = new HashMap<>();
		return new BasicApplicationInstanceInfo(instanceGuid, processGuid, map);
	}

	@Override
	protected List<Process> getServicesData() {
		Map<String, Process> processes = new HashMap<>();
		List<DesiredLRPResponse> desiredLRPs = receptorClient.getDesiredLRPs();

		for (DesiredLRPResponse desired : desiredLRPs) {
			processes.put(desired.getProcessGuid(), new Process(desired));
		}

		List<ActualLRPResponse> actualLRPs = receptorClient.getActualLRPs();
		for (ActualLRPResponse actual : actualLRPs) {
			Process process = processes.get(actual.getProcessGuid());
			if (process != null) {
				process.add(actual);
			}
		}

		return new ArrayList<>(processes.values());
	}

	@Override
	protected FallbackServiceInfoCreator<BaseServiceInfo, Process> getFallbackServiceInfoCreator() {
		return new FallbackServiceInfoCreator<BaseServiceInfo, Process>() {
			@Override
			public BaseServiceInfo createServiceInfo(Process process) {
				return new BaseServiceInfo(process.getDesired().getProcessGuid());
			}
		};
	}
}
