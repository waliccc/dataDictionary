package com.walic.datatranslate.translator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.walic.datatranslate.annotation.SensitiveField;
import com.walic.datatranslate.core.AbstractTranslator;

public class SensitiveTranslator extends AbstractTranslator {

	@Override
	protected Class<? extends Annotation> bindAnnotationClass() {
		return SensitiveField.class;
	}

	@Override
	public Object translate(Object fieldValue, Field field, Object obj) {
		if (fieldValue == null) {
			return null;
		}
		SensitiveField annotation = field.getAnnotation(SensitiveField.class);
		int prefixLength = annotation.prefix();
		if (prefixLength < 0) {
			return "";
		}
		int replaceLength = annotation.replaceLength();
		if (replaceLength < 0) {
			return replaceWords(String.valueOf(fieldValue), prefixLength, annotation.suffix(),
					annotation.replaceWith());
		}
		return replaceWords(String.valueOf(fieldValue), prefixLength, annotation.suffix(), annotation.replaceWith(),
				replaceLength);
	}

	private static String replaceWords(String sensitiveStr, int prefixLength, int suffixLength, char replaceWith,
			int replaceLength) {
		int l = sensitiveStr.length();
		char[] c = sensitiveStr.toCharArray();
		int newLength = prefixLength + suffixLength >= l ? l + replaceLength
				: prefixLength + suffixLength + replaceLength;
		char[] rc = new char[newLength];
		for (int i = l - 1; i >= 0 && i > l - suffixLength - 1; i--) {
			rc[--newLength] = c[i];
		}
		for (int i = 1; i <= replaceLength; i++) {
			rc[--newLength] = replaceWith;
		}
		while (--newLength >= 0) {
			rc[newLength] = c[newLength];
		}

		return new String(rc);
	}

	private static String replaceWords(String sensitiveStr, int prefixLength, int suffixLength, char replaceWith) {
		int l = sensitiveStr.length();
		char[] c = sensitiveStr.toCharArray();
		int end = l - suffixLength - 1;
		for (int i = prefixLength; i < l && i <= end; i++) {
			c[i] = replaceWith;
		}
		return new String(c);
	}

}
