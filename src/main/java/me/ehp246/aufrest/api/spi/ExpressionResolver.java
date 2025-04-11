package me.ehp246.aufrest.api.spi;

/**
 * The abstraction of the functionality to resolve Spring property placeholders
 * and SpEL expressions.
 * 
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface ExpressionResolver {
    String resolve(String expression);
}
