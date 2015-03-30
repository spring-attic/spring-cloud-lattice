package org.springframework.cloud.lattice.discovery;

import java.net.InetAddress;

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
}
