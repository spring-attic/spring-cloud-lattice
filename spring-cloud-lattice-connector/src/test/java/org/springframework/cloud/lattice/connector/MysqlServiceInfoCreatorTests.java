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

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.cloud.service.common.MysqlServiceInfo;

/**
 * @author Spencer Gibb
 */
public class MysqlServiceInfoCreatorTests extends AbstractServiceInfoCreatorTests {

	@Test
	public void mysqlLatticeWorks() {
		LatticeConnector connector = createConnector();
		MysqlServiceInfo serviceInfo = findServiceInfo(connector, MysqlServiceInfo.class, "mysql-1");
		assertThat(serviceInfo.getJdbcUrl(),
				equalTo("jdbc:mysql://root:password@192.168.11.11:61003/test"));
	}

}
