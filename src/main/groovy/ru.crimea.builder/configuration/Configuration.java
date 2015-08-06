package ru.crimea.builder.configuration;

import ru.intertrust.cm.core.config.ConfigurationExplorerImpl;
import ru.intertrust.cm.core.config.ConfigurationSerializer;
import ru.intertrust.cm.core.config.converter.ConfigurationClassesCache;
import ru.intertrust.cm.core.config.module.ModuleConfiguration;
import ru.intertrust.cm.core.config.module.ModuleService;

/**
 * This class represents...
 * <p>
 * Created by Alexander Bogatyrenko on 05.08.2015.
 */
public class Configuration {

    private ConfigurationExplorerImpl configExplorer;

    public Configuration() throws Exception {
        ConfigurationClassesCache.getInstance().build();

        ConfigurationSerializer configurationSerializer = new ConfigurationSerializer();
        configurationSerializer.setModuleService(getModuleService());

        ru.intertrust.cm.core.config.base.Configuration config = configurationSerializer.deserializeConfiguration();
        this.configExplorer = new ConfigurationExplorerImpl(config);
    }

    private ModuleService getModuleService() {
        ModuleService service = new ModuleService();
        service.init();
//        service.getModuleList().remove(0);  remove core module
        for(ModuleConfiguration configuration : service.getModuleList()) {
            configuration.setConfigurationSchemaPath("config/configuration.xsd");
        }
        return service;
    }

    public ConfigurationExplorerImpl getConfigExplorer() {
        return configExplorer;
    }
}
