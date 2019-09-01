package com.walic.datatranslate.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 用于翻译嵌套对象
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD })
public @interface ObjectField {

}
