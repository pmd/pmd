package net.sourceforge.pmd.build.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class ConfigUtil {
	
	private static final String BUNDLE_NAME = "config";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private ConfigUtil() {}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
