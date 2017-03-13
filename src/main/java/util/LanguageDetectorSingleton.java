package util;

import java.util.List;

import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;

public class LanguageDetectorSingleton {
	private static LanguageDetectorSingleton instance = null;
	LanguageDetector languageDetector;

	public LanguageDetectorSingleton() throws Exception {
		List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
		languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard()).withProfiles(languageProfiles)
				.build();
	}

	public static LanguageDetectorSingleton getInstance() throws Exception {
		if (instance == null) {
			instance = new LanguageDetectorSingleton();
		}
		return instance;
	}

	public String detect(String text) {
		String lang = "";
		try {
			lang = languageDetector.getProbabilities(text(text)).get(0).getLocale().getLanguage();
		} catch (Exception e) {
		}
		return lang;
	}

	private CharSequence text(CharSequence text) {
		return CommonTextObjectFactories.forDetectingShortCleanText().forText(text);
	}
}
