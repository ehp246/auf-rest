package me.ehp246.aufrest.core.rest;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionOverrideException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.EnableByRest;

public final class ByRestRegistrar implements ImportBeanDefinitionRegistrar {
    private final static Logger LOGGER = LogManager.getLogger(ByRestRegistrar.class);

    @Override
    public void registerBeanDefinitions(final AnnotationMetadata metadata, final BeanDefinitionRegistry registry) {
        LOGGER.atDebug().log("Scanning for {}", ByRest.class::getCanonicalName);

        for (final var found : new ByRestScanner(EnableByRest.class, ByRest.class, metadata).perform()
                .collect(Collectors.toList())) {
            LOGGER.atTrace().log("Registering {}", found::getBeanClassName);

            final Class<?> byRestInterface;
            try {
                byRestInterface = Class.forName(found.getBeanClassName());
            } catch (final ClassNotFoundException ignored) {
                // Class scanning started this. Should not happen.
                throw new RuntimeException("Class scanning started this. Should not happen.");
            }

            final var beanName = beanName(byRestInterface);
            final var proxyBeanDefinition = this.getProxyBeanDefinition(
                    metadata.getAnnotationAttributes(EnableByRest.class.getCanonicalName()), byRestInterface);

            if (registry.containsBeanDefinition(beanName)) {
                throw new BeanDefinitionOverrideException(beanName, proxyBeanDefinition,
                        registry.getBeanDefinition(beanName));
            }

            registry.registerBeanDefinition(beanName, proxyBeanDefinition);
        }
    }

    private String beanName(final Class<?> byRestInterface) {
        final var name = byRestInterface.getAnnotation(ByRest.class).name();
        if (!name.isBlank()) {
            return name;
        }

        final char c[] = byRestInterface.getSimpleName().toCharArray();
        c[0] = Character.toLowerCase(c[0]);

        return new String(c);
    }

    private BeanDefinition getProxyBeanDefinition(final Map<String, Object> map, final Class<?> byRestInterface) {
        final var args = new ConstructorArgumentValues();

        args.addGenericArgumentValue(byRestInterface);

        final var beanDef = new GenericBeanDefinition();
        beanDef.setBeanClass(byRestInterface);
        beanDef.setConstructorArgumentValues(args);
        beanDef.setFactoryBeanName(ByRestProxyFactory.class.getName());
        beanDef.setFactoryMethodName("newInstance");
        beanDef.setResourceDescription(byRestInterface.getCanonicalName());

        return beanDef;
    }
}
