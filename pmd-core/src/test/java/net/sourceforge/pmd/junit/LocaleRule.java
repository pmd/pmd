/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.junit;

import java.util.Locale;
import java.util.Objects;

import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

/**
 *
 * Based on <a href="https://gist.github.com/digulla/5884162">digulla/DefaultLocaleRule.java</a>.
 *
 */
public class LocaleRule extends TestWatcher {

    private Locale localeForTest;
    private Locale originalDefault;

    private LocaleRule(Locale localeForTest) {
        this.localeForTest = Objects.requireNonNull(localeForTest);
    }

    @Override
    protected void starting(Description description) {
        originalDefault = Locale.getDefault();
        Locale.setDefault(localeForTest);
    }

    @Override
    protected void finished(Description description) {
        Locale.setDefault(originalDefault);
    }

    public void setDefault(Locale newLocale) {
        Locale.setDefault(Objects.requireNonNull(newLocale));
    }

    public static LocaleRule en() {
        return new LocaleRule(Locale.ENGLISH);
    }

    public static LocaleRule de() {
        return new LocaleRule(Locale.GERMAN);
    }
}
