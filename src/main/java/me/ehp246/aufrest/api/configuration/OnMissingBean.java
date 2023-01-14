package me.ehp246.aufrest.api.configuration;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author Lei Yang
 *
 */
public class OnMissingBean implements Condition {

    @Override
    public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
        context.getBeanFactory().containsBeanDefinition("objectMapper");
        return false;
    }

}
