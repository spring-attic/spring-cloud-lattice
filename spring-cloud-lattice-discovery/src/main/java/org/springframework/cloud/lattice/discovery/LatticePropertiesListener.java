package org.springframework.cloud.lattice.discovery;

import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Collections;

/**
 * @author Spencer Gibb
 */
public class LatticePropertiesListener implements
        ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered {

    // After ConfigFileApplicationListener so values here can use those ones
    private int order = ConfigFileApplicationListener.DEFAULT_ORDER + 1;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        Object processGuid = System.getenv("PROCESS_GUID");
        if (processGuid != null) {
            MapPropertySource propertySource = new MapPropertySource(
                    "latticeBridgeEnvironment", Collections.singletonMap(
                    "spring.application.name", processGuid));
            environment.getPropertySources().addBefore("systemProperties",
                    propertySource);
        }
    }
}
