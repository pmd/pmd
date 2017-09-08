/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Enumerates the settings that are persisted from session to session.
 *
 * <p>To persist one more setting, just add an enum constant.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
enum DesignerWindowSettings {

    /** Language version. */
    LANGUAGE_VERSION("langVersion",
                     DesignerWindowPresenter::getLanguageVersionTerseName,
                     DesignerWindowPresenter::setLanguageVersionFromTerseName),

    /** Code from the main editor area. */
    SOURCE_CODE("code",
                DesignerWindowPresenter::getSourceCode,
                DesignerWindowPresenter::setSourceCode),
    /** Version of the XPath parser. */
    XPATH_VERSION("xpathVersion",
                  DesignerWindowPresenter::getXPathVersion,
                  DesignerWindowPresenter::setXPathVersion),

    /** Code of the XPath expression. */
    XPATH_CODE("xpathCode",
               DesignerWindowPresenter::getXPathCode,
               DesignerWindowPresenter::setXPathCode),

    /** Whether the xpath bottom pane is expanded. */
    IS_XPATH_PANEL_EXPANDED("isXPathPanelExpanded",
                            DesignerWindowPresenter::isXPathPanelExpanded,
                            DesignerWindowPresenter::setIsXPathPanelExpanded),

    /** Position of the divider for the left toolbar (information). */
    LEFT_TOOLBAR_DIVIDER_POSITION("leftToolBarDividerPosition",
                                  DesignerWindowPresenter::getLeftToolbarDividerPosition,
                                  DesignerWindowPresenter::setLeftToolbarDividerPosition);


    private final String keyName;
    private final Function<DesignerWindowPresenter, String> getValueFunction;
    private final PresenterSettingSetter setValueFunction;


    DesignerWindowSettings(String keyName, Function<DesignerWindowPresenter, String> getValueFunction,
                           PresenterSettingSetter setValueFunction) {
        this.keyName = keyName;
        this.getValueFunction = getValueFunction;
        this.setValueFunction = setValueFunction;
    }


    /**
     * Gets the value of the setting to save it.
     *
     * @param presenter The presenter
     *
     * @return The value obtained from the presenter
     */
    String getValueFrom(DesignerWindowPresenter presenter) {
        return getValueFunction.apply(presenter);
    }


    /**
     * Restores the value into the presenter.
     *
     * @param presenter The presenter
     * @param value     The value of the setting
     */
    void setValueIn(DesignerWindowPresenter presenter, String value) {
        setValueFunction.set(presenter, value);
    }


    public String getKeyName() {
        return keyName;
    }


    /** Get the setting from the name of its key. */
    static DesignerWindowSettings ofKeyName(String key) {
        return Arrays.stream(DesignerWindowSettings.values())
                     .filter(cst -> cst.keyName.equals(key))
                     .findAny()
                     .get();
    }


    @FunctionalInterface
    private interface PresenterSettingSetter {

        void set(DesignerWindowPresenter presenter, String value);
    }

}
