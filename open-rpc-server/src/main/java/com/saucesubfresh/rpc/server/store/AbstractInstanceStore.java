package com.saucesubfresh.rpc.server.store;

import com.saucesubfresh.rpc.core.enums.ClientStatus;
import com.saucesubfresh.rpc.core.information.ClientInformation;

import java.util.List;

/**
 * @author lijunping on 2022/2/18
 */
public abstract class AbstractInstanceStore implements InstanceStore{


    protected void buildClient(List<ClientInformation> instances, ClientStatus clientStatus, long currentTime){
        instances.forEach(instance->{
            instance.setStatus(clientStatus);
            instance.setOnlineTime(currentTime);
        });
    }
}
