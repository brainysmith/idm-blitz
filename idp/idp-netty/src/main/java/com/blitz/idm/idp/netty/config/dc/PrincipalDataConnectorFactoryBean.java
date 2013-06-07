package com.blitz.idm.idp.netty.config.dc;

import com.blitz.idm.idp.netty.dc.PrincipalDataConnector;
import edu.internet2.middleware.shibboleth.common.config.attribute.resolver.dataConnector.BaseDataConnectorFactoryBean;
import org.opensaml.util.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class PrincipalDataConnectorFactoryBean extends BaseDataConnectorFactoryBean implements ApplicationContextAware {

    private static Logger log = LoggerFactory.getLogger(PrincipalDataConnectorFactoryBean.class);

    private ApplicationContext rootCtx = null;

    private String storageService;

    public void setStorageService(String storage){
        storageService = storage;
    }

    public Class getObjectType()
    {
        return PrincipalDataConnector.class;
    }
    @SuppressWarnings("unchecked")
    protected Object createInstance() throws Exception
    {
        PrincipalDataConnector connector = new PrincipalDataConnector();
        populateDataConnector(connector);
        connector.setStorageService((StorageService) rootCtx.getBean(storageService));
        connector.initialize();
        log.debug("An instance of PrincipalDataConnector has been created {}", connector);
        return connector;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        rootCtx = applicationContext;
        while(rootCtx.getParent() != null)
            rootCtx = rootCtx.getParent();
        log.debug("The root context gained");
    }

}
