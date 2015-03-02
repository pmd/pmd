/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Enumeration;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;

/**
 * Defines a datatype with a set of preset values of any type as held within a
 * pair of maps. While the values are not serialized out, the labels are and
 * serve as keys to obtain the values. The choices() method provides the ordered
 * selections to be used in an editor widget.
 * 
 * @author Brian Remedios
 * @param <E>
 */
public class EnumeratedProperty<E> extends AbstractEnumeratedProperty<E, Object> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<EnumeratedProperty>(
            Enumeration.class) {

        public EnumeratedProperty createWith(Map<String, String> valuesById) {

            return new EnumeratedProperty(nameIn(valuesById), descriptionIn(valuesById), labelsIn(valuesById),
                    choicesIn(valuesById), indexIn(valuesById), 0f);
        }
    };

    /**
     * Constructor for EnumeratedProperty.
     * 
     * @param theName String
     * @param theDescription String
     * @param theLabels String[]
     * @param theChoices E[]
     * @param defaultIndex int
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public EnumeratedProperty(String theName, String theDescription, String[] theLabels, E[] theChoices,
            int defaultIndex, float theUIOrder) {
        super(theName, theDescription, theLabels, theChoices, new int[] { defaultIndex }, theUIOrder, false);
    }

    /**
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<Object> type() {
        return Object.class;
    }

    /**
     * @param value Object
     * @return String
     * @see net.sourceforge.pmd.PropertyDescriptor#errorFor(Object)
     */
    @Override
    public String errorFor(Object value) {
        return labelsByChoice.containsKey(value) ? null : nonLegalValueMsgFor(value);
    }

    /**
     * @param value String
     * @return Object
     * @throws IllegalArgumentException
     * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
     */
    public Object valueFrom(String value) throws IllegalArgumentException {
        return choiceFrom(value);
    }

    /**
     *
     * @param value Object
     * @return String
     * @see net.sourceforge.pmd.PropertyDescriptor#asDelimitedString(Object)
     */
    @Override
    public String asDelimitedString(Object value) {
        return labelsByChoice.get(value);
    }
}
