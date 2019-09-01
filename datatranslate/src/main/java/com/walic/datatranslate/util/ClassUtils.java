package com.walic.datatranslate.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public final class ClassUtils {

	private ClassUtils() {
	}

	/**
	 * @description 获取类及其父类所有字段
	 * @param modelClass
	 * @return
	 */
	public static Field[] getModelField(Class<?> modelClass) {

		Field[] fields = modelClass.getDeclaredFields();
		fields = Arrays.stream(fields).filter(f -> !"serialVersionUID".equals(f.getName())).toArray(Field[]::new);
		Class<?> supperClass = modelClass.getSuperclass();
		while (supperClass != null) {
			Field[] parentFields = supperClass.getDeclaredFields();
			int fieldsLength = fields.length;
			int parentFLength = parentFields.length;
			fields = Arrays.copyOf(fields, fieldsLength + parentFLength);
			System.arraycopy(parentFields, 0, fields, fieldsLength, parentFLength);
			supperClass = supperClass.getSuperclass();
		}
		return fields;
	}

	/**
	 * @description 判断是否为基本数据类型
	 * @param obj
	 * @return
	 */
	public static boolean isBaseType(Object obj) {
		return obj instanceof Character || obj instanceof CharSequence || obj instanceof Number
				|| obj instanceof Boolean;
	}

	/**
	 * @description 判断是否为集合、映射、数组
	 * @param field
	 * @param fieldFrom
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static boolean isComposity(Object obj) {
		return obj instanceof Collection || obj instanceof Map || obj instanceof Object[];
	}
}
