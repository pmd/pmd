/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.settings;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class AppSetting {

    private final String keyName;
    private final Supplier<String> getValueFunction;
    private final Consumer<String> setValueFunction;


    public AppSetting(String keyName, Supplier<String> getValueFunction,
               Consumer<String> setValueFunction) {
        this.keyName = keyName;
        this.getValueFunction = getValueFunction;
        this.setValueFunction = setValueFunction;
    }


    public String getValue() {
        return getValueFunction.get();
    }


    public void setValue(String value) {
        setValueFunction.accept(value);
    }


    public String getKeyName() {
        return keyName;
    }
}
