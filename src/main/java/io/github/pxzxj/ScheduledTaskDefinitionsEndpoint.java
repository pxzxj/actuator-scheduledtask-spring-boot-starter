package io.github.pxzxj;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.scheduling.config.ScheduledTaskHolder;

import java.util.Collection;
import java.util.List;

@Endpoint
public class ScheduledTaskDefinitionsEndpoint {

    private final Collection<ScheduledTaskHolder> scheduledTaskHolders;

    public ScheduledTaskDefinitionsEndpoint(Collection<ScheduledTaskHolder> scheduledTaskHolders) {
        this.scheduledTaskHolders = scheduledTaskHolders;
    }

    @ReadOperation
    public List<ScheduledTaskDefinition> page() {
        return null;
    }
}
