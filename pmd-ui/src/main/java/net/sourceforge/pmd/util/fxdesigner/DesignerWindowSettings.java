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
                     DesignerWindowController::getLanguageVersionTerseName,
                     DesignerWindowController::setLanguageVersionFromTerseName),

    /** Code from the main editor area. */
    SOURCE_CODE("code",
                DesignerWindowController::getSourceCode,
                DesignerWindowController::setSourceCode),

    /** Version of the XPath parser. */
    XPATH_VERSION("xpathVersion",
                  DesignerWindowController::getXPathVersion,
                  DesignerWindowController::setXPathVersion),

    /** Code of the XPath expression. */
    XPATH_CODE("xpathCode",
               DesignerWindowController::getXPathCode,
               DesignerWindowController::setXPathCode),

    /** Which tab of the bottom area is currently visible. */
    BOTTOM_EXPANDED_TAB("bottomExpandedTab",
                        DesignerWindowController::getBottomExpandedTab,
                        DesignerWindowController::setBottomExpandedTab),

    /** Whether the window is maximized. */
    IS_MAXIMIZED("isMaximized",
                 DesignerWindowController::isMaximized,
                 DesignerWindowController::setIsMaximized),

    /** List of recent files. */
    RECENT_FILES("recentFiles",
                 DesignerWindowController::getRecentFiles,
                 DesignerWindowController::setRecentFiles),

    AST_PANE_WIDTH("astPaneWidth",
                   DesignerWindowController::getASTPaneWidth,
                   DesignerWindowController::setAstPaneWidth),

    IS_SYNTAX_HIGHLIGHTING_ENABLED("isSyntaxHighlightingEnabled",
                                   DesignerWindowController::isSyntaxHighlightingEnabled,
                                   DesignerWindowController::setIsSyntaxHighlightingEnabled);

    private final String keyName;
    private final Function<DesignerWindowController, String> getValueFunction;
    private final PresenterSettingSetter setValueFunction;


    DesignerWindowSettings(String keyName, Function<DesignerWindowController, String> getValueFunction,
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
    String getValueFrom(DesignerWindowController presenter) {
        return getValueFunction.apply(presenter);
    }


    /**
     * Restores the value into the presenter. The string passed is the same as that returned by {@link
     * #getValueFrom(DesignerWindowController)} when the settings were last saved.
     *
     * @param presenter The presenter
     * @param value     The value of the setting
     */
    void setValueIn(DesignerWindowController presenter, String value) {
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

        void set(DesignerWindowController presenter, String value);
    }

}
