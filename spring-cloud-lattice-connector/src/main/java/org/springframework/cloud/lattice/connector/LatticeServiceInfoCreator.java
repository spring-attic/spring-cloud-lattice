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

import org.cloudfoundry.receptor.support.EnvironmentVariable;
import org.springframework.cloud.ServiceInfoCreator;
import org.springframework.cloud.service.ServiceInfo;

import java.lang.*;

/**
 * @author Spencer Gibb
 */
public abstract class LatticeServiceInfoCreator<SI extends ServiceInfo> implements ServiceInfoCreator<SI, Process> {

	private final String prefix;

	protected LatticeServiceInfoCreator(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public boolean accept(Process process) {
		return process.getProcessGuid().toLowerCase().startsWith(getPrefix());
	}

	protected String getPrefix() {
		return prefix;
	}

	protected String findRequiredEnvVar(Process process, String varName) {
		String value = findEnvVar(process, varName);
		if (value == null) {
			throw new IllegalStateException(varName + " env var is missing");
		}
		return value;
	}

	protected String findEnvVar(Process process, String varName, String defaultValue) {
		String value = findEnvVar(process, varName);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}

	protected String findEnvVar(Process process, String varName) {
		String value = null;
		for (EnvironmentVariable env : process.getDesired().getEnv()) {
			if (env.getName().equals(varName)) {
				value = env.getValue();
			}
		}
		return value;
	}
}
