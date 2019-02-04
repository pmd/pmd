/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner;

import static net.sourceforge.pmd.util.fxdesigner.popups.SimplePopups.showLicensePopup;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.reactfx.Subscription;
import org.reactfx.value.Val;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.app.AbstractController;
import net.sourceforge.pmd.util.fxdesigner.app.DesignerRoot;
import net.sourceforge.pmd.util.fxdesigner.app.NodeSelectionSource;
import net.sourceforge.pmd.util.fxdesigner.model.XPathEvaluationException;
import net.sourceforge.pmd.util.fxdesigner.popups.EventLogController;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;
import net.sourceforge.pmd.util.fxdesigner.util.LimitedSizeStack;
import net.sourceforge.pmd.util.fxdesigner.util.SoftReferenceCache;
import net.sourceforge.pmd.util.fxdesigner.util.TextAwareNodeWrapper;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsPersistenceUtil;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsPersistenceUtil.PersistentProperty;

import javafx.fxml.FXML;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;


/**
 * Main controller of the app. Mediator for subdivisions of the UI.
 *
 * @author Clément Fournier
 * @see NodeInfoPanelController
 * @see SourceEditorController
 * @see EventLogController
 * @see XPathPanelController
 * @since 6.0.0
 */
@SuppressWarnings("PMD.UnusedPrivateField")
public class MainDesignerController extends AbstractController<AbstractController<?>> {


    /* Menu bar */
    @FXML
    private MenuItem setupAuxclasspathMenuItem;
    @FXML
    public MenuItem openEventLogMenuItem;
    @FXML
    private MenuItem openFileMenuItem;
    @FXML
    private MenuItem licenseMenuItem;
    @FXML
    private Menu openRecentMenu;
    @FXML
    private Menu fileMenu;
    /* Bottom panel */
    @FXML
    private TabPane bottomTabPane;
    @FXML
    private Tab xpathEditorTab;
    @FXML
    private SplitPane mainHorizontalSplitPane;


    /* Children */
    @FXML
    private NodeInfoPanelController nodeInfoPanelController;
    @FXML
    private XPathPanelController xpathPanelController;
    @FXML
    private SourceEditorController sourceEditorController;
    // we cache it but if it's not used the FXML is not created, etc
    private final SoftReferenceCache<EventLogController> eventLogController;

    // Other fields
    private final Stack<File> recentFiles = new LimitedSizeStack<>(5);


    public MainDesignerController(DesignerRoot owner) {
        super(owner, null);
        eventLogController = new SoftReferenceCache<>(() -> new EventLogController(this));
    }



    @Override
    protected void beforeParentInit() {
        try {
            SettingsPersistenceUtil.restoreProperties(this, DesignerUtil.getSettingsFile());
        } catch (Exception e) {
            // shouldn't prevent the app from opening
            // in case the file is corrupted, it will be overwritten on shutdown
            logInternalException(e);
        }

        licenseMenuItem.setOnAction(e -> showLicensePopup());
        openFileMenuItem.setOnAction(e -> onOpenFileClicked());
        openRecentMenu.setOnAction(e -> updateRecentFilesMenu());
        openRecentMenu.setOnShowing(e -> updateRecentFilesMenu());
        fileMenu.setOnShowing(e -> onFileMenuShowing());

        setupAuxclasspathMenuItem.setOnAction(e -> sourceEditorController.showAuxclasspathSetupPopup());

        openEventLogMenuItem.setOnAction(e -> {
            EventLogController wizard = eventLogController.getValue();
            Subscription parentToWizSubscription = wizard.errorNodesProperty().values().subscribe(sourceEditorController.currentErrorNodesProperty()::setValue);
            wizard.showPopup(parentToWizSubscription);
        });
        openEventLogMenuItem.textProperty().bind(
            getLogger().numNewLogEntriesProperty().map(i -> "Event log (" + (i > 0 ? i : "no") + " new)")
        );

    }


    @Override
    protected void afterChildrenInit() {
        updateRecentFilesMenu();
        refreshAST(); // initial refreshing

        sourceEditorController.currentRuleResultsProperty().bind(xpathPanelController.currentResultsProperty());
    }



    public void shutdown() {
        try {
            SettingsPersistenceUtil.persistProperties(this, DesignerUtil.getSettingsFile());
        } catch (IOException ioe) {
            // nevermind
            ioe.printStackTrace();
        }
    }


    /**
     * Attempts to refresh the AST with the up-to-date source,
     * also updating XPath results.
     */
    public void refreshAST() {
        Optional<Node> root = sourceEditorController.refreshAST();

        if (root.isPresent()) {
            xpathPanelController.evaluateXPath(root.get(), getLanguageVersion());
        } else {
            xpathPanelController.invalidateResults(true);
        }
    }

    /**
     * Refreshes the XPath results if the compilation unit is valid.
     * Otherwise does nothing.
     */
    public void refreshXPathResults() {
        sourceEditorController.getCompilationUnit().ifPresent(root -> xpathPanelController.evaluateXPath(root, getLanguageVersion()));
    }


    /**
     * Returns a wrapper around the given node that gives access
     * to its textual representation in the editor area.
     */
    public TextAwareNodeWrapper wrapNode(Node node) {
        return sourceEditorController.wrapNode(node);
    }


    /**
     * Runs an XPath (2.0) query on the current AST.
     * Performs no side effects.
     *
     * @param query the query
     * @return the matched nodes
     * @throws XPathEvaluationException if the query fails
     */
    public List<Node> runXPathQuery(String query) throws XPathEvaluationException {
        return sourceEditorController.getCompilationUnit()
                                     .map(n -> xpathPanelController.runXPathQuery(n, getLanguageVersion(), query))
                                     .orElseGet(Collections::emptyList);
    }



    private void onFileMenuShowing() {
        openRecentMenu.setDisable(recentFiles.isEmpty());
    }


    private void onOpenFileClicked() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load source from file");
        File file = chooser.showOpenDialog(getMainStage());
        loadSourceFromFile(file);
    }

    private void loadSourceFromFile(File file) {
        if (file != null) {
            try {
                String source = IOUtils.toString(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8);
                sourceEditorController.setText(source);
                LanguageVersion guess = DesignerUtil.getLanguageVersionFromExtension(file.getName());
                if (guess != null) { // guess the language from the extension
                    sourceEditorController.setLanguageVersion(guess);
                    refreshAST();
                }

                recentFiles.push(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void updateRecentFilesMenu() {
        List<MenuItem> items = new ArrayList<>();
        List<File> filesToClear = new ArrayList<>();

        for (final File f : recentFiles) {
            if (f.exists() && f.isFile()) {
                CustomMenuItem item = new CustomMenuItem(new Label(f.getName()));
                item.setOnAction(e -> loadSourceFromFile(f));
                item.setMnemonicParsing(false);
                Tooltip.install(item.getContent(), new Tooltip(f.getAbsolutePath()));
                items.add(item);
            } else {
                filesToClear.add(f);
            }
        }
        recentFiles.removeAll(filesToClear);

        if (items.isEmpty()) {
            openRecentMenu.setDisable(true);
            return;
        }

        Collections.reverse(items);

        items.add(new SeparatorMenuItem());
        MenuItem clearItem = new MenuItem();
        clearItem.setText("Clear menu");
        clearItem.setOnAction(e -> {
            recentFiles.clear();
            openRecentMenu.setDisable(true);
        });
        items.add(clearItem);

        openRecentMenu.getItems().setAll(items);
    }


    /**
     * Called when the AST is updated to update all parts of the UI.
     */
    public void invalidateAst() {
        nodeInfoPanelController.setFocusNode(null);
        xpathPanelController.invalidateResults(false);
        NodeSelectionSource.CHANNEL.pushEvent(this, null);
    }


    public LanguageVersion getLanguageVersion() {
        return sourceEditorController.getLanguageVersion();
    }


    public void setLanguageVersion(LanguageVersion version) {
        sourceEditorController.setLanguageVersion(version);
    }


    public Val<LanguageVersion> languageVersionProperty() {
        return sourceEditorController.languageVersionProperty();
    }


    @PersistentProperty
    public String getRecentFiles() {
        return recentFiles.stream().map(File::getAbsolutePath).collect(Collectors.joining(File.pathSeparator));
    }


    public void setRecentFiles(String files) {
        Arrays.stream(files.split(File.pathSeparator)).map(File::new).forEach(recentFiles::push);
    }


    @PersistentProperty
    public boolean isMaximized() {
        return getMainStage().isMaximized();
    }


    public void setMaximized(boolean b) {
        getMainStage().setMaximized(!b); // trigger change listener anyway
        getMainStage().setMaximized(b);
    }


    @PersistentProperty
    public int getBottomTabIndex() {
        return bottomTabPane.getSelectionModel().getSelectedIndex();
    }


    public void setBottomTabIndex(int i) {
        if (i >= 0 && i < bottomTabPane.getTabs().size()) {
            bottomTabPane.getSelectionModel().select(i);
        }
    }


    @Override
    public List<AbstractController<MainDesignerController>> getChildren() {
        return Arrays.asList(xpathPanelController, sourceEditorController, nodeInfoPanelController);
    }


    @Override
    public String getDebugName() {
        return "MAIN";
    }

}
