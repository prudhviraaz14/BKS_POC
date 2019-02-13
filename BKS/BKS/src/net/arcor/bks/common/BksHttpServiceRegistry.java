package net.arcor.bks.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class BksHttpServiceRegistry {
    protected final static Map<String, BksHttpServiceHandler> internalRegistry = new HashMap<String, BksHttpServiceHandler>();

	public static Map<String, BksHttpServiceHandler> getInternalregistry() {
		return internalRegistry;
	}

    public void setBksHttpServiceHandler(final Collection<BksHttpServiceHandler> serviceConfigs) {
        for (BksHttpServiceHandler serviceConfigToAdd : serviceConfigs) {
            String serviceName = serviceConfigToAdd.getSoapaction();
            internalRegistry.put(serviceName, serviceConfigToAdd);
        }
    }
}
