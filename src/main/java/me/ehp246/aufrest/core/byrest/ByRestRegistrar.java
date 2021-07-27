package me.ehp246.aufrest.core.byrest;

import java.util.Arrays;
import java.util.List;
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
import me.ehp246.aufrest.api.annotation.Default;
import me.ehp246.aufrest.api.annotation.EnableByRest;
import me.ehp246.aufrest.api.rest.AuthScheme;
import me.ehp246.aufrest.api.rest.ByRestProxyConfig;

public final class ByRestRegistrar implements ImportBeanDefinitionRegistrar {
    private final static Logger LOGGER = LogManager.getLogger(ByRestRegistrar.class);

    @Override
    public void registerBeanDefinitions(final AnnotationMetadata metadata, final BeanDefinitionRegistry registry) {
        LOGGER.debug("Scanning for {}", ByRest.class.getCanonicalName());

        new ByRestScanner(EnableByRest.class, ByRest.class, metadata).perform().forEach(beanDefinition -> {
            LOGGER.trace("Registering {}", beanDefinition.getBeanClassName());

            final Class<?> byRestInterface;
            try {
                byRestInterface = Class.forName(beanDefinition.getBeanClassName());
            } catch (final ClassNotFoundException ignored) {
                // Class scanning started this. Should not happen.
                throw new RuntimeException("Class scanning started this. Should not happen.");
            }

            final var name = byRestInterface.getAnnotation(ByRest.class).name();
            registry.registerBeanDefinition(name.equals("") ? byRestInterface.getSimpleName() : name,
                    this.getProxyBeanDefinition(
                    metadata.getAnnotationAttributes(EnableByRest.class.getCanonicalName()), byRestInterface));
        });
    }

    private BeanDefinition getProxyBeanDefinition(Map<String, Object> map, final Class<?> byRestInterface) {

        final var byRest = byRestInterface.getAnnotation(ByRest.class);
        final var globalErrorType = (Class<?>) map.get("errorType");

        final var args = new ConstructorArgumentValues();
        args.addGenericArgumentValue(byRestInterface);
        args.addGenericArgumentValue(new ByRestProxyConfig() {
            private final Auth auth = new Auth() {

                @Override
                public List<String> value() {
                    return Arrays.asList(byRest.auth().value());
                }

                @Override
                public AuthScheme scheme() {
                    return AuthScheme.valueOf(byRest.auth().scheme().name());
                }

            };

            @Override
            public String uri() {
                return byRest.value();
            }

            @Override
            public String timeout() {
                return byRest.timeout();
            }

            @Override
            public String contentType() {
                return byRest.contentType();
            }

            @Override
            public boolean acceptGZip() {
                return byRest.acceptGZip();
            }

            @Override
            public String accept() {
                return byRest.accept();
            }

            @Override
            public Class<?> errorType() {
                return byRest.errorType() == Default.class ? globalErrorType : byRest.errorType();
            }

            @Override
            public Auth auth() {
                return auth;
            }

        });

        final var beanDef = new GenericBeanDefinition();
        beanDef.setBeanClass(byRestInterface);
        beanDef.setConstructorArgumentValues(args);
        beanDef.setFactoryBeanName(ByRestFactory.class.getName());
        beanDef.setFactoryMethodName("newInstance");

        return beanDef;
    }
}
