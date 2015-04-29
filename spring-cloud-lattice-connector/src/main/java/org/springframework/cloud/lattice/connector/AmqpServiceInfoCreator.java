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

import org.springframework.cloud.service.common.AmqpServiceInfo;

import io.pivotal.receptor.commands.ActualLRPResponse;
import org.springframework.cloud.util.UriInfo;

/**
 * @author Spencer Gibb
 */
public class AmqpServiceInfoCreator extends LatticeServiceInfoCreator<AmqpServiceInfo> {

	public AmqpServiceInfoCreator() {
		super("rabbit");
	}

	@Override
	public AmqpServiceInfo createServiceInfo(Process process) {
		ActualLRPResponse actual = process.getFirstActual();
		String address = actual.getAddress();
		int port = actual.getPorts()[0].getHostPort();
		String username = "guest";
		String password = "guest";
		String virtualHost = null;
		return new LatticeAmqpServiceInfo(actual.getInstanceGuid(), address, port, username, password, virtualHost);
	}


	private static class LatticeAmqpServiceInfo extends AmqpServiceInfo {
		public LatticeAmqpServiceInfo(String id, String host, int port, String username, String password, String virtualHost) {
			super(id, host, port, username, password, virtualHost);
		}

		@Override
		protected UriInfo validateAndCleanUriInfo(UriInfo uriInfo) {
			if (uriInfo.getScheme() == null) {
				throw new IllegalArgumentException("Missing scheme in amqp URI: " + uriInfo);
			}

			if (uriInfo.getHost() == null) {
				throw new IllegalArgumentException("Missing authority in amqp URI: " + uriInfo);
			}

			if (uriInfo.getUserName() == null || uriInfo.getPassword() == null) {
				throw new IllegalArgumentException("Missing userinfo in amqp URI: " + uriInfo);
			}

			String path = uriInfo.getPath();
			if (path == null) {
				//NO-OP, this is the default vhost
			} else {
				// Check that the path only has a single segment.  As we have an authority component
				// in the URI, paths always begin with a slash.
				if (path.indexOf('/') != -1) {
					throw new IllegalArgumentException("Multiple segments in path of amqp URI: " + uriInfo);
				}
			}
			return uriInfo;
		}
	}

}