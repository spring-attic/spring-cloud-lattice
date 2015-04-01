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

import java.util.ArrayList;
import java.util.List;

import io.pivotal.receptor.commands.CellResponse;
import io.pivotal.receptor.commands.DesiredLRPCreateRequest;
import io.pivotal.receptor.commands.DesiredLRPResponse;
import io.pivotal.receptor.commands.DesiredLRPUpdateRequest;
import io.pivotal.receptor.commands.TaskCreateRequest;
import io.pivotal.receptor.commands.TaskResponse;
import org.springframework.core.convert.converter.Converter;

import io.pivotal.receptor.client.ReceptorClient;
import io.pivotal.receptor.commands.ActualLRPResponse;

/**
 * @author Spencer Gibb
 */
public class LatticeService {

	private ReceptorClient receptor;
    private LatticeDiscoveryProperties props;

    public LatticeService(ReceptorClient receptor, LatticeDiscoveryProperties props) {
		this.receptor = receptor;
        this.props = props;
    }

	public <T> List<T> getActualLRPsByProcessGuid(String processGuid,
			Converter<ActualLRPResponse, T> converter) {
		List<ActualLRPResponse> responses = receptor
				.getActualLRPsByProcessGuid(processGuid);
		List<T> lrps = new ArrayList<>();
		for (ActualLRPResponse response : responses) {
			T converted = converter.convert(response);
			lrps.add(converted);
		}

		return lrps;
	}

    public void createDesiredLRP(DesiredLRPCreateRequest request) {
        receptor.createDesiredLRP(request);
    }

    public List<ActualLRPResponse> getActualLRPsByDomain(String domain) {
        return receptor.getActualLRPsByDomain(domain);
    }

    public List<DesiredLRPResponse> getDesiredLRPsByDomain(String domain) {
        return receptor.getDesiredLRPsByDomain(domain);
    }

    public List<ActualLRPResponse> getActualLRPsByProcessGuid(String processGuid) {
        return receptor.getActualLRPsByProcessGuid(processGuid);
    }

    public void createTask(TaskCreateRequest request) {
        receptor.createTask(request);
    }

    public void updateDesiredLRP(String processGuid, DesiredLRPUpdateRequest request) {
        receptor.updateDesiredLRP(processGuid, request);
    }

    public List<CellResponse> getCells() {
        return receptor.getCells();
    }

    public void cancelTask(String taskGuid) {
        receptor.cancelTask(taskGuid);
    }

    public List<TaskResponse> getTasksByDomain(String domain) {
        return receptor.getTasksByDomain(domain);
    }

    public void upsertDomain(String domain, int ttl) {
        receptor.upsertDomain(domain, ttl);
    }

    public List<TaskResponse> getTasks() {
        return receptor.getTasks();
    }

    public List<ActualLRPResponse> getActualLRPs() {
        return receptor.getActualLRPs();
    }

    public void killActualLRPByProcessGuidAndIndex(String processGuid, int index) {
        receptor.killActualLRPByProcessGuidAndIndex(processGuid, index);
    }

    public List<DesiredLRPResponse> getDesiredLRPs() {
        return receptor.getDesiredLRPs();
    }

    public String[] getDomains() {
        return receptor.getDomains();
    }

    public TaskResponse getTask(String taskGuid) {
        return receptor.getTask(taskGuid);
    }

    public DesiredLRPResponse getDesiredLRP(String processGuid) {
        return receptor.getDesiredLRP(processGuid);
    }

    public void deleteDesiredLRP(String processGuid) {
        receptor.deleteDesiredLRP(processGuid);
    }

    public void deleteTask(String taskGuid) {
        receptor.deleteTask(taskGuid);
    }

    public ActualLRPResponse getActualLRPByProcessGuidAndIndex(String processGuid, int index) {
        return receptor.getActualLRPByProcessGuidAndIndex(processGuid, index);
    }
}
