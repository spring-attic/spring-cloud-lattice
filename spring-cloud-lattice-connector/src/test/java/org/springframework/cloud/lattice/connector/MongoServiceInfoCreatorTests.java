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
import org.springframework.cloud.service.common.MongoServiceInfo;

/**
 * @author Spencer Gibb
 */
public class MongoServiceInfoCreatorTests extends AbstractServiceInfoCreatorTests {

	@Test
	public void mongoLatticeWorks() {
		LatticeConnector connector = createConnector();

		MongoServiceInfo serviceInfo = findServiceInfo(connector, MongoServiceInfo.class,
				"mongo-1");
		assertThat(serviceInfo.getUri(), equalTo("mongodb://192.168.11.11:61007/lattice"));
	}

}
