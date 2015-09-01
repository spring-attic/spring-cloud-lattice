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

import org.cloudfoundry.receptor.commands.ActualLRPResponse;
import org.springframework.cloud.service.common.MysqlServiceInfo;
import org.springframework.cloud.util.UriInfo;

/**
 * @author Spencer Gibb
 */
public class MysqlServiceInfoCreator extends LatticeServiceInfoCreator<MysqlServiceInfo> {

	public MysqlServiceInfoCreator() {
		super("mysql");
	}

	@Override
	public MysqlServiceInfo createServiceInfo(Process process) {
		ActualLRPResponse actual = process.getFirstActual();
		String scheme = "jdbc:"+ MysqlServiceInfo.MYSQL_SCHEME;
		String address = actual.getAddress();
		int port = actual.getPorts()[0].getHostPort();
		String username = "root";
		String password = findRequiredEnvVar(process, "MYSQL_ROOT_PASSWORD");
		String path = findEnvVar(process, "MYSQL_DATABASE_NAME", "test");

		String url = new UriInfo(scheme, address, port, username, password, path).toString();
		return new MysqlServiceInfo(actual.getInstanceGuid(), url);
	}

}
