package com.walic.datatranslate.core;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.walic.datatranslate.translator.SensitiveTranslator;

public class TranslatorRegistry {

	private static ConcurrentMap<String, AbstractTranslator> data = new ConcurrentHashMap<>();
	static {
		registry(new SensitiveTranslator());
	}

	public static AbstractTranslator getTranslator(String name) {
		return data.get(name);
	}

	public static AbstractTranslator getTranslator(Class<? extends Annotation> annotationClass) {
		return data.get(annotationClass.getName());
	}

	public static void registry(AbstractTranslator member) {
		data.put(member.getName(), member);
	}

	public static void registryAll(Collection<AbstractTranslator> members) {
		members.forEach(member -> data.put(member.getName(), member));
	}

}
