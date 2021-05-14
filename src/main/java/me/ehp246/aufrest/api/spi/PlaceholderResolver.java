package me.ehp246.aufrest.api.spi;

/**
 * @author Lei Yang
 *
 */
@FunctionalInterface
public interface PlaceholderResolver {
    /**
     * Resolve ${...} placeholders in the given text, replacing them with
     * corresponding property values as resolved by {@link #getProperty}.
     * Unresolvable placeholders with no default value will cause an
     * IllegalArgumentException to be thrown.
     * 
     * @return the resolved String (never {@code null})
     * @throws IllegalArgumentException if given text is {@code null} or if any
     *                                  placeholders are unresolvable
     * @see org.springframework.util.SystemPropertyUtils#resolvePlaceholders(String,
     *      boolean)
     */
    String resolve(String text);
}
