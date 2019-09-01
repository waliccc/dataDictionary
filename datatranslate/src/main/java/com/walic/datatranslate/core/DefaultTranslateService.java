package com.walic.datatranslate.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.walic.datatranslate.annotation.ObjectField;
import com.walic.datatranslate.annotation.SuppressArray;
import com.walic.datatranslate.annotation.SuppressCollection;
import com.walic.datatranslate.annotation.SuppressMap;
import com.walic.datatranslate.util.ClassUtils;

public class DefaultTranslateService implements TranslateService {

	@Override
	public Object translate(Object object) {
		if (object == null) {
			return null;
		}
		if (ClassUtils.isBaseType(object)) {
			return object;
		}
		if (ClassUtils.isComposity(object)) {
			return translateComposityObject(object, null);
		}
		JSONObject json = null;
		try {
			json = (JSONObject) JSONObject.toJSON(object);
		} catch (Exception e) {
			return object;
		}
		Field[] fields = ClassUtils.getModelField(object.getClass());
		for (Field field : fields) {
			Object translateValue = translateField(field, object);
			if (translateValue != null) {
				json.put(field.getName(), translateValue);
			}
		}
		return json;
	}

	private Object translateField(Field field, Object obj) {
		AbstractTranslator translator = getTranslator(field);
		Object fieldValue = null;
		try {
			field.setAccessible(true);
			fieldValue = field.get(obj);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return null;
		}
		if (translator != null) {
			return translator.translate(fieldValue, field, obj);
		}
		if (ClassUtils.isBaseType(fieldValue)) {
			return null;
		}
		if (ClassUtils.isComposity(fieldValue)) {
			return translateComposityObject(fieldValue, field);
		}
		if (field.isAnnotationPresent(ObjectField.class)) {
			return translate(fieldValue);
		}
		return null;
	}

	private Object translateComposityObject(Object obj, Field thisField) {
		if (obj instanceof Object[] && (thisField == null || !thisField.isAnnotationPresent(SuppressArray.class))) {
			JSONArray values = new JSONArray();
			for (Object o : (Object[]) obj) {
				values.add(translate(o));
			}
			return values;
		}
		if (obj instanceof Collection
				&& (thisField == null || !thisField.isAnnotationPresent(SuppressCollection.class))) {
			JSONArray values = new JSONArray();
			for (Object o : (Collection<?>) obj) {
				values.add(translate(o));
			}
			return values;
		}
		if (obj instanceof Map && (thisField == null || !thisField.isAnnotationPresent(SuppressMap.class))) {
			JSONObject value = new JSONObject();
			Map<?, ?> map = (Map<?, ?>) obj;
			for (Entry<?, ?> set : map.entrySet()) {
				value.put(String.valueOf(set.getKey()), translate(set.getValue()));
			}
			return value;
		}
		return null;
	}

	private AbstractTranslator getTranslator(Field field) {
		Annotation[] annotations = field.getAnnotations();
		for (Annotation annotation : annotations) {
			AbstractTranslator translator = TranslatorRegistry.getTranslator(annotation.annotationType());
			if (translator != null) {
				return translator;
			}
		}
		return null;
	}

}
