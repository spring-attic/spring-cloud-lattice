package org.springframework.cloud.lattice.discovery;

import com.netflix.loadbalancer.Server;

/**
 * @author Spencer Gibb
 */
public class LatticeServer extends Server {

	private final MetaInfo metaInfo;

	public LatticeServer(final String appName, final String instanceId, String host,
			String port) {
		super(host, new Integer(port));
		metaInfo = new MetaInfo() {
			@Override
			public String getAppName() {
				return appName;
			}

			@Override
			public String getServerGroup() {
				return null;
			}

			@Override
			public String getServiceIdForDiscovery() {
				return null;
			}

			@Override
			public String getInstanceId() {
				return instanceId;
			}
		};
	}

	@Override
	public MetaInfo getMetaInfo() {
		return metaInfo;
	}
}
