package com.walic.datatranslate.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public abstract class AbstractTranslator implements TranslatorNameAware {

	/**
	 * @description 绑定翻译器所关联注解类
	 */
	protected abstract Class<? extends Annotation> bindAnnotationClass();

	@Override
	public String getName() {
		return bindAnnotationClass().getName();
	}

	/**
	 * @param fieldValue
	 *            该字段的对象值
	 * @param field
	 *            该字段
	 * @param obj
	 *            该字段所依附的对象
	 * @return
	 */
	public abstract Object translate(Object fieldValue, Field field, Object obj);

}
