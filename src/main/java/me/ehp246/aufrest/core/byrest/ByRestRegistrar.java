package me.ehp246.aufrest.core.byrest;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.configuration.EnableByRest;

public final class ByRestRegistrar implements ImportBeanDefinitionRegistrar {
    private final static Logger LOGGER = LogManager.getLogger(ByRestRegistrar.class);

    @Override
    public void registerBeanDefinitions(final AnnotationMetadata metadata, final BeanDefinitionRegistry registry) {
        LOGGER.atDebug().log("Scanning for {}", ByRest.class::getCanonicalName);

        new ByRestScanner(EnableByRest.class, ByRest.class, metadata).perform().forEach(beanDefinition -> {
            LOGGER.atTrace().log("Registering {}", beanDefinition::getBeanClassName);

            final Class<?> byRestInterface;
            try {
                byRestInterface = Class.forName(beanDefinition.getBeanClassName());
            } catch (final ClassNotFoundException ignored) {
                // Class scanning started this. Should not happen.
                throw new RuntimeException("Class scanning started this. Should not happen.");
            }

            final var name = byRestInterface.getAnnotation(ByRest.class).name();

            registry.registerBeanDefinition(name.isBlank() ? byRestInterface.getName() : name,
                    this.getProxyBeanDefinition(metadata.getAnnotationAttributes(EnableByRest.class.getCanonicalName()),
                            byRestInterface));
        });
    }

    private BeanDefinition getProxyBeanDefinition(Map<String, Object> map, final Class<?> byRestInterface) {
        final var args = new ConstructorArgumentValues();

        args.addGenericArgumentValue(byRestInterface);

        final var beanDef = new GenericBeanDefinition();
        beanDef.setBeanClass(byRestInterface);
        beanDef.setConstructorArgumentValues(args);
        beanDef.setFactoryBeanName(ByRestProxyFactory.class.getName());
        beanDef.setFactoryMethodName("newInstance");

        return beanDef;
    }
}
