/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.popups;

import static net.sourceforge.pmd.properties.MultiValuePropertyDescriptor.DEFAULT_DELIMITER;
import static net.sourceforge.pmd.properties.MultiValuePropertyDescriptor.DEFAULT_NUMERIC_DELIMITER;
import static net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil.rewire;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.reactfx.util.Try;
import org.reactfx.value.Var;

import net.sourceforge.pmd.properties.PropertyTypeId;
import net.sourceforge.pmd.properties.ValueParser;
import net.sourceforge.pmd.properties.ValueParserConstants;
import net.sourceforge.pmd.util.fxdesigner.util.DesignerUtil;
import net.sourceforge.pmd.util.fxdesigner.util.PropertyDescriptorSpec;
import net.sourceforge.pmd.util.fxdesigner.util.controls.PropertyTableView;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 * Property edition dialog. Use {@link #bindToDescriptor(PropertyDescriptorSpec, ObservableList)} )}
 * to use this dialog to edit a descriptor spec. Typically owned by a {@link PropertyTableView}.
 * The controller must be instantiated by hand.
 *
 * @author Clément Fournier
 * @see PropertyDescriptorSpec
 * @since 6.0.0
 */
public class EditPropertyDialogController implements Initializable {

    private final Var<PropertyTypeId> typeId = Var.newSimpleVar(PropertyTypeId.STRING);
    private final Var<Runnable> commitHandler = Var.newSimpleVar(null);
    private Var<PropertyDescriptorSpec> backingDescriptor = Var.newSimpleVar(null);
    private Var<ObservableList<PropertyDescriptorSpec>> backingDescriptorList = Var.newSimpleVar(null);

    private ValidationSupport validationSupport = new ValidationSupport();
    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private ChoiceBox<PropertyTypeId> typeChoiceBox;
    @FXML
    private TextField valueField;
    @FXML
    private Button commitButton;


    public EditPropertyDialogController() {
        // default constructor
    }


    public EditPropertyDialogController(Runnable commitHandler) {
        this.commitHandler.setValue(commitHandler);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        commitButton.setOnAction(e -> {
            commitHandler.ifPresent(Runnable::run);
            getStage().close();
            this.free();
        });

        commitButton.disableProperty().bind(validationSupport.invalidProperty());

        Platform.runLater(() -> {
            typeId.bind(typeChoiceBox.getSelectionModel().selectedItemProperty());
            typeChoiceBox.setConverter(DesignerUtil.stringConverter(PropertyTypeId::getStringId,
                                                                    PropertyTypeId::lookupMnemonic));
            typeChoiceBox.getItems().addAll(PropertyTypeId.typeIdsToConstants().values());
            FXCollections.sort(typeChoiceBox.getItems());
        });

        Platform.runLater(this::registerBasicValidators);

        typeIdProperty().values()
                        .filter(Objects::nonNull)
                        .subscribe(this::registerTypeDependentValidators);

    }


    private Stage getStage() {
        return (Stage) commitButton.getScene().getWindow();
    }


    /** Unbinds this dialog from its backing properties. */
    public void free() {
        backingDescriptor.ifPresent(PropertyDescriptorSpec::unbind);
        backingDescriptor.setValue(null);
        backingDescriptorList.setValue(null);
        this.nameProperty().setValue(""); // necessary to get the validator to reevaluate each time
    }


    /**
     * Wires this dialog to the descriptor, so that the controls edit the descriptor.
     *
     * @param spec The descriptor
     */
    public void bindToDescriptor(PropertyDescriptorSpec spec, ObservableList<PropertyDescriptorSpec> allDescriptors) {
        backingDescriptor.setValue(spec);
        backingDescriptorList.setValue(allDescriptors);
        rewire(spec.nameProperty(), this.nameProperty(), this::setName);
        rewire(spec.typeIdProperty(), this.typeIdProperty(), this::setTypeId);
        rewire(spec.valueProperty(), this.valueProperty(), this::setValue);
        rewire(spec.descriptionProperty(), this.descriptionProperty(), this::setDescription);
    }


    // Validators for attributes common to all properties
    private void registerBasicValidators() {
        Validator<String> noWhitespaceName
                = Validator.createRegexValidator("Name cannot contain whitespace", "\\S*+", Severity.ERROR);
        Validator<String> emptyName = Validator.createEmptyValidator("Name required");
        Validator<String> uniqueName = (c, val) -> {
            long sameNameDescriptors = backingDescriptorList.getOrElse(FXCollections.emptyObservableList())
                                                            .stream()
                                                            .map(PropertyDescriptorSpec::getName)
                                                            .filter(getName()::equals)
                                                            .count();

            return new ValidationResult().addErrorIf(c, "The name must be unique", sameNameDescriptors > 1);
        };

        validationSupport.registerValidator(nameField, Validator.combine(noWhitespaceName, emptyName, uniqueName));

        Validator<String> noWhitespaceDescription
                = Validator.createRegexValidator("Message cannot be whitespace", "(\\s*+\\S.*)?", Severity.ERROR);
        Validator<String> emptyDescription = Validator.createEmptyValidator("Message required");
        validationSupport.registerValidator(descriptionField, Validator.combine(noWhitespaceDescription, emptyDescription));
    }


    private void registerTypeDependentValidators(PropertyTypeId typeId) {
        Validator<String> valueValidator = (c, val) ->
                ValidationResult.fromErrorIf(valueField, "The value couldn't be parsed",
                                             Try.tryGet(() -> getValueParser(typeId).valueOf(getValue())).isFailure());


        validationSupport.registerValidator(valueField, valueValidator);
    }


    private ValueParser<?> getValueParser(PropertyTypeId typeId) {
        ValueParser<?> parser = typeId.getValueParser();
        if (typeId.isPropertyMultivalue()) {
            char delimiter = typeId.isPropertyNumeric() ? DEFAULT_NUMERIC_DELIMITER : DEFAULT_DELIMITER;
            parser = ValueParserConstants.multi(parser, delimiter);
        }
        return parser;
    }


    public String getName() {
        return nameField.getText();
    }


    public void setName(String name) {
        nameField.setText(name);
    }


    public Property<String> nameProperty() {
        return nameField.textProperty();
    }


    public String getDescription() {
        return descriptionField.getText();
    }


    public void setDescription(String description) {
        descriptionField.setText(description);
    }


    public Property<String> descriptionProperty() {
        return descriptionField.textProperty();
    }


    public PropertyTypeId getTypeId() {
        return typeId.getValue();
    }


    public void setTypeId(PropertyTypeId typeId) {
        typeChoiceBox.getSelectionModel().select(typeId);
    }


    public Var<PropertyTypeId> typeIdProperty() {
        return typeId;
    }


    public String getValue() {
        return valueField.getText();
    }


    public void setValue(String value) {
        valueField.setText(value);
    }


    public Property<String> valueProperty() {
        return valueField.textProperty();
    }


}
