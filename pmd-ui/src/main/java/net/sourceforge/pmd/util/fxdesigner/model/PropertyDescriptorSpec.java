/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;


import java.util.HashMap;
import java.util.Map;

import org.reactfx.value.Val;
import org.reactfx.value.Var;

import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyDescriptorField;
import net.sourceforge.pmd.properties.PropertyTypeId;
import net.sourceforge.pmd.properties.builders.PropertyDescriptorExternalBuilder;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsOwner;
import net.sourceforge.pmd.util.fxdesigner.util.beans.SettingsPersistenceUtil.PersistentProperty;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;


/**
 * Stores enough data to build a property descriptor, can be displayed within table views.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class PropertyDescriptorSpec implements SettingsOwner {

    private static final String DEFAULT_STRING = "TODO";

    private final Val<Boolean> isNumerical;
    private final Val<Boolean> isPackaged;
    private final Val<Boolean> isMultivalue;

    private final Var<PropertyTypeId> typeId = Var.newSimpleVar(PropertyTypeId.STRING);
    private final Var<String> name = Var.newSimpleVar(DEFAULT_STRING);
    private final Var<String> value = Var.newSimpleVar(DEFAULT_STRING);
    private final Var<String> description = Var.newSimpleVar(DEFAULT_STRING);


    public PropertyDescriptorSpec() {
        isNumerical = typeId.map(PropertyTypeId::isPropertyNumeric);
        isPackaged = typeId.map(PropertyTypeId::isPropertyPackaged);
        isMultivalue = typeId.map(PropertyTypeId::isPropertyMultivalue);
    }


    public Boolean getIsNumerical() {
        return isNumerical.getValue();
    }


    public Val<Boolean> isNumericalProperty() {
        return isNumerical;
    }


    public Boolean getIsPackaged() {
        return isPackaged.getValue();
    }


    public Val<Boolean> isPackagedProperty() {
        return isPackaged;
    }


    public Boolean getIsMultivalue() {
        return isMultivalue.getValue();
    }


    public Val<Boolean> isMultivalueProperty() {
        return isMultivalue;
    }


    @PersistentProperty
    public String getDescription() {
        return description.getValue();
    }


    public void setDescription(String description) {
        this.description.setValue(description);
    }


    public Var<String> descriptionProperty() {
        return description;
    }


    @PersistentProperty
    public PropertyTypeId getTypeId() {
        return typeId.getValue();
    }


    public void setTypeId(PropertyTypeId typeId) {
        this.typeId.setValue(typeId);
    }


    public Var<PropertyTypeId> typeIdProperty() {
        return typeId;
    }


    @PersistentProperty
    public String getName() {
        return name.getValue();
    }


    public void setName(String name) {
        this.name.setValue(name);
    }


    public Var<String> nameProperty() {
        return name;
    }


    @PersistentProperty
    public String getValue() {
        return value.getValue();
    }


    public void setValue(String value) {
        this.value.setValue(value);
    }


    public Var<String> valueProperty() {
        return value;
    }


    /**
     * Returns an xml string of this property definition.
     *
     * @return An xml string
     */
    public String toXml() {
        return String.format("<property name=\"%s\" type=\"%s\" value=\"%s\" />",
                             getName(), getTypeId().getStringId(), getValue());
    }


    @Override
    public String toString() {
        return toXml();
    }


    /**
     * Builds the descriptor. May throw IllegalArgumentException.
     *
     * @return the descriptor if it can be built
     */
    public PropertyDescriptor<?> build() {
        PropertyDescriptorExternalBuilder<?> externalBuilder = getTypeId().getFactory();
        Map<PropertyDescriptorField, String> values = new HashMap<>();
        values.put(PropertyDescriptorField.NAME, getName());
        values.put(PropertyDescriptorField.DEFAULT_VALUE, getValue());
        values.put(PropertyDescriptorField.DESCRIPTION, getDescription());
        values.put(PropertyDescriptorField.MIN, "-2000000");
        values.put(PropertyDescriptorField.MAX, "+2000000");

        return externalBuilder.build(values);
    }


    /**
     * Removes bindings from this property spec.
     */
    public void unbind() {
        typeIdProperty().unbind();
        nameProperty().unbind();
        descriptionProperty().unbind();
        valueProperty().unbind();
    }


    /** Extractor for observable lists. */
    public static Callback<PropertyDescriptorSpec, Observable[]> extractor() {
        return spec -> new Observable[]{spec.nameProperty(), spec.typeIdProperty(), spec.valueProperty()};
    }


    public static ObservableList<PropertyDescriptorSpec> observableList() {
        return FXCollections.observableArrayList(extractor());
    }
}
