package com.walic.datatranslate;

import com.walic.datatranslate.core.DefaultTranslateService;
import com.walic.datatranslate.core.TranslateService;

public class TranslateExecutor {

	private static TranslateService translateService = new DefaultTranslateService();

	public static Object translate(Object object) {
		return translateService.translate(object);
	}

}
