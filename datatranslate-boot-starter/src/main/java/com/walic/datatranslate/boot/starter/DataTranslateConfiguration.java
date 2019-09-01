package com.walic.datatranslate.boot.starter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.walic.datatranslate.core.AbstractTranslator;
import com.walic.datatranslate.core.TranslatorRegistry;

@Configuration
@ComponentScan(basePackages = "com.walic.datatranslate.boot.starter")
public class DataTranslateConfiguration {

	@Autowired
	public DataTranslateConfiguration(List<AbstractTranslator> list) {
		TranslatorRegistry.registryAll(list);
	}

}
