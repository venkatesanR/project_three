package com.addval.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

public final class CacheAnnotations {
	private CacheAnnotations() {
	}

	/**
	 * Set the "Max-age" Cache header.
	 */

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface CacheMaxAge {
		long time();

		TimeUnit unit();
	}

	/**
	 * Sets the cache header to the value "no cache"
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface NoCache {

	}
}
