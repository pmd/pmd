/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;

import javafx.scene.control.RadioMenuItem;

/**
 * Enumerates the settings that are persisted from session to session.
 *
 * <p>To persist one more setting, just add an enum constant.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
enum DesignerWindowSettings {

    /**
     * Language version.
     */
    LANGUAGE_VERSION("langVersion") {
        @Override
        public String getValueFrom(DesignerWindowPresenter presenter) {
            return presenter.model.getLanguageVersion().getTerseName();
        }


        @Override
        void setValueIn(DesignerWindowPresenter presenter, String value) {
            LanguageVersion version = LanguageRegistry.findLanguageVersionByTerseName(value);
            presenter.languageVersionToggleGroup.getToggles()
                                                .stream()
                                                .filter(toggle -> toggle.getUserData().equals(version))
                                                .findAny()
                                                .orElse(new RadioMenuItem()) // discard
                                                .setSelected(true);
        }
    },

    /**
     * Code from the main editor area.
     */
    SOURCE_CODE("code") {
        @Override
        public String getValueFrom(DesignerWindowPresenter presenter) {
            return presenter.model.getSourceCode();
        }


        @Override
        void setValueIn(DesignerWindowPresenter presenter, String value) {
            presenter.view.getCodeEditorArea().replaceText(value);
        }
    },

    /**
     * Version of the XPath parser.
     */
    XPATH_VERSION("xpathVersion") {
        @Override
        public String getValueFrom(DesignerWindowPresenter presenter) {
            return presenter.model.getXPathVersion();
        }


        @Override
        void setValueIn(DesignerWindowPresenter presenter, String value) {
            presenter.view.getXpathVersionToggleGroup()
                          .getToggles()
                          .stream()
                          .filter(toggle -> toggle.getUserData().equals(value))
                          .findFirst()
                          .orElse(new RadioMenuItem()) // discard
                          .setSelected(true);
        }
    },

    /**
     * Code of the XPath expression.
     */
    XPATH_CODE("xpathCode") {
        @Override
        public String getValueFrom(DesignerWindowPresenter presenter) {
            return presenter.view.getXpathExpressionArea().getText();
        }


        @Override
        void setValueIn(DesignerWindowPresenter presenter, String value) {
            presenter.view.getXpathExpressionArea().replaceText(value);
        }
    },

    /**
     * Whether to refresh XPath results when updating the AST.
     */
    IS_REFRESH_XPATH("isRefreshXPath") {
        @Override
        String getValueFrom(DesignerWindowPresenter presenter) {
            return Boolean.toString(presenter.view.getRefreshXPathToggle().isSelected());
        }


        @Override
        void setValueIn(DesignerWindowPresenter presenter, String value) {
            boolean b = Boolean.parseBoolean(value);
            presenter.view.getRefreshXPathToggle().setSelected(b);
        }
    };

    public final String keyName;


    DesignerWindowSettings(String keyName) {
        this.keyName = keyName;
    }


    /**
     * Gets the value of the setting to save it.
     *
     * @param presenter The presenter
     *
     * @return The value obtained from the presenter
     */
    abstract String getValueFrom(DesignerWindowPresenter presenter);


    /**
     * Restores the value into the presenter.
     *
     * @param presenter The presenter
     * @param value     The value of the setting
     */
    abstract void setValueIn(DesignerWindowPresenter presenter, String value);

}
