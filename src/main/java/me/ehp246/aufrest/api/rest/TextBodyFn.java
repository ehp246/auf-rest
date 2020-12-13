package me.ehp246.aufrest.api.rest;

/**
 * @author Lei Yang
 * @since 2.1
 * @version 2.1
 */
public interface TextBodyFn extends BodyFn {
	Object fromText(String body, BodyReceiver receiver);

	String toText(BodySupplier supplier);
}
