package me.ehp246.aufrest.core.byrest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.api.configuration.ByRestConfiguration;
import me.ehp246.aufrest.api.rest.EnableByRestConfig;

public final class EnableByRestRegistrar implements ImportBeanDefinitionRegistrar {
    private final static Logger LOGGER = LogManager.getLogger(EnableByRestRegistrar.class);

    @Override
    public void registerBeanDefinitions(final AnnotationMetadata metadata, final BeanDefinitionRegistry registry) {

        // Register the enable config.
        registry.registerBeanDefinition(EnableByRestConfig.class.getCanonicalName(),
                getEnableConfigBeanDefinition(
                        (Class<?>) metadata.getAnnotationAttributes(EnableByRest.class.getCanonicalName())
                                .get("errorType")));

        LOGGER.debug("Scanning for {}", ByRest.class.getCanonicalName());

        new ByRestScanner(EnableByRest.class, ByRest.class, metadata).perform().forEach(beanDefinition -> {
            registry.registerBeanDefinition(beanDefinition.getBeanClassName(),
                    this.getProxyBeanDefinition(beanDefinition));
        });
    }

    private BeanDefinition getProxyBeanDefinition(final BeanDefinition beanDefinition) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(beanDefinition.getBeanClassName());
        } catch (final ClassNotFoundException ignored) {
            // Class scanning started this. Should not happen.
            throw new RuntimeException("Class scanning started this. Should not happen.");
        }

        LOGGER.trace("Defining {}", beanDefinition.getBeanClassName());

        final var proxyBeanDefinition = new GenericBeanDefinition();
        proxyBeanDefinition.setBeanClass(clazz);

        final var args = new ConstructorArgumentValues();
        args.addGenericArgumentValue(clazz);

        proxyBeanDefinition.setConstructorArgumentValues(args);

        proxyBeanDefinition.setFactoryBeanName(ByRestFactory.class.getName());

        proxyBeanDefinition.setFactoryMethodName("newInstance");

        return proxyBeanDefinition;
    }

    private BeanDefinition getEnableConfigBeanDefinition(final Class<?> errorType) {
        final var proxyBeanDefinition = new GenericBeanDefinition();
        proxyBeanDefinition.setBeanClass(EnableByRestConfig.class);

        final var args = new ConstructorArgumentValues();
        args.addGenericArgumentValue(errorType);

        proxyBeanDefinition.setConstructorArgumentValues(args);

        proxyBeanDefinition.setFactoryBeanName(ByRestConfiguration.class.getName());

        proxyBeanDefinition.setFactoryMethodName("newEnableByRestConfig");

        return proxyBeanDefinition;
    }
}
