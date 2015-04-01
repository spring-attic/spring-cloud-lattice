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

import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.apachecommons.CommonsLog;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Spencer Gibb
 */
@ConfigurationProperties("spring.cloud.lattice.discovery")
@Data
@CommonsLog
public class LatticeDiscoveryProperties {
	private boolean enabled = true;

	private String receptorHost = "receptor.192.168.11.11.xip.io";

	@Getter(AccessLevel.PRIVATE)
	@Setter(AccessLevel.PRIVATE)
	private HostInfo hostInfo = initHostInfo();

	private String ipAddress = this.hostInfo.getIpAddress();

	private String hostname = this.hostInfo.getHostname();

	private boolean preferIpAddress = false;

	private Map<String, Route> routes = new LinkedHashMap<>();

	public String getHostname() {
		return this.preferIpAddress ? this.ipAddress : this.hostname;
	}

	@SneakyThrows
	private HostInfo initHostInfo() {
		return new HostInfo(InetAddress.getLocalHost().getHostAddress(), InetAddress
				.getLocalHost().getHostName());
	}

	@Data
	private class HostInfo {
		private final String ipAddress;
		private final String hostname;
	}

	@Data
	protected static class Route {
		private String address = "192.168.11.1";
		private int port;
	}
}
