package com.walic.datatranslate.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 敏感字段注解
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD })
public @interface SensitiveField {

	int prefix() default -1;

	int suffix() default 0;

	char replaceWith() default '*';

	int replaceLength() default -1;

}
