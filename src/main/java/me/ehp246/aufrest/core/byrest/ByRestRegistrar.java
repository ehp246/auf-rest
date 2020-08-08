package me.ehp246.aufrest.core.byrest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import me.ehp246.aufrest.api.annotation.ByRest;
import me.ehp246.aufrest.api.annotation.EnableByRest;

public class ByRestRegistrar implements ImportBeanDefinitionRegistrar {
	private final static Logger LOGGER = LoggerFactory.getLogger(ByRestRegistrar.class);

	@Override
	public void registerBeanDefinitions(final AnnotationMetadata metadata, final BeanDefinitionRegistry registry) {

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

}
