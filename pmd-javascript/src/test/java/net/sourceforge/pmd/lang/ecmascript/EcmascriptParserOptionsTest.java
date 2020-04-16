/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import static net.sourceforge.pmd.lang.ParserOptionsTest.verifyOptionsEqualsHashcode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptParserOptions.Version;
import net.sourceforge.pmd.lang.ecmascript.rule.AbstractEcmascriptRule;
import net.sourceforge.pmd.properties.BooleanProperty;

public class EcmascriptParserOptionsTest {

    @Test
    public void testDefaults() throws Exception {
        EcmascriptParserOptions parserOptions = new EcmascriptParserOptions();
        assertTrue(parserOptions.isRecordingComments());
        assertTrue(parserOptions.isRecordingLocalJsDocComments());
        assertEquals(EcmascriptParserOptions.Version.VERSION_ES6, parserOptions.getRhinoLanguageVersion());

        MyRule rule = new MyRule();
        parserOptions = (EcmascriptParserOptions) rule.getParserOptions();
        assertTrue(parserOptions.isRecordingComments());
        assertTrue(parserOptions.isRecordingLocalJsDocComments());
        assertEquals(EcmascriptParserOptions.Version.VERSION_ES6, parserOptions.getRhinoLanguageVersion());
    }

    @Test
    public void testConstructor() throws Exception {
        MyRule rule = new MyRule();

        rule.setProperty(EcmascriptParserOptions.RECORDING_COMMENTS_DESCRIPTOR, true);
        assertTrue(((EcmascriptParserOptions) rule.getParserOptions()).isRecordingComments());
        rule.setProperty(EcmascriptParserOptions.RECORDING_COMMENTS_DESCRIPTOR, false);
        assertFalse(((EcmascriptParserOptions) rule.getParserOptions()).isRecordingComments());

        rule.setProperty(EcmascriptParserOptions.RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR, true);
        assertTrue(((EcmascriptParserOptions) rule.getParserOptions()).isRecordingLocalJsDocComments());
        rule.setProperty(EcmascriptParserOptions.RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR, false);
        assertFalse(((EcmascriptParserOptions) rule.getParserOptions()).isRecordingLocalJsDocComments());

        rule.setProperty(EcmascriptParserOptions.RHINO_LANGUAGE_VERSION, Version.VERSION_DEFAULT);
        assertEquals(EcmascriptParserOptions.Version.VERSION_DEFAULT,
                ((EcmascriptParserOptions) rule.getParserOptions()).getRhinoLanguageVersion());
        rule.setProperty(EcmascriptParserOptions.RHINO_LANGUAGE_VERSION, Version.VERSION_1_8);
        assertEquals(EcmascriptParserOptions.Version.VERSION_1_8,
                ((EcmascriptParserOptions) rule.getParserOptions()).getRhinoLanguageVersion());
    }

    @Test
    public void testSetters() {
        EcmascriptParserOptions options = new EcmascriptParserOptions();

        options.setSuppressMarker("foo");
        assertEquals("foo", options.getSuppressMarker());
        options.setSuppressMarker(null);
        assertNull(options.getSuppressMarker());
    }

    @Test
    public void testEqualsHashcode() throws Exception {
        BooleanProperty[] properties = {EcmascriptParserOptions.RECORDING_COMMENTS_DESCRIPTOR,
                                        EcmascriptParserOptions.RECORDING_LOCAL_JSDOC_COMMENTS_DESCRIPTOR, };

        for (int i = 0; i < properties.length; i++) {
            BooleanProperty property = properties[i];

            MyRule rule = new MyRule();
            rule.setProperty(property, true);
            ParserOptions options1 = rule.getParserOptions();
            rule.setProperty(property, false);
            ParserOptions options2 = rule.getParserOptions();
            rule.setProperty(property, true);
            ParserOptions options3 = rule.getParserOptions();
            rule.setProperty(property, false);
            ParserOptions options4 = rule.getParserOptions();
            verifyOptionsEqualsHashcode(options1, options2, options3, options4);
        }

        EcmascriptParserOptions options1 = new EcmascriptParserOptions();
        options1.setSuppressMarker("foo");
        EcmascriptParserOptions options2 = new EcmascriptParserOptions();
        options2.setSuppressMarker("bar");
        EcmascriptParserOptions options3 = new EcmascriptParserOptions();
        options3.setSuppressMarker("foo");
        EcmascriptParserOptions options4 = new EcmascriptParserOptions();
        options4.setSuppressMarker("bar");
        verifyOptionsEqualsHashcode(options1, options2, options3, options4);

        options1 = new EcmascriptParserOptions();
        options1.setRhinoLanguageVersion(EcmascriptParserOptions.Version.VERSION_DEFAULT);
        options2 = new EcmascriptParserOptions();
        options2.setRhinoLanguageVersion(EcmascriptParserOptions.Version.VERSION_1_8);
        options3 = new EcmascriptParserOptions();
        options3.setRhinoLanguageVersion(EcmascriptParserOptions.Version.VERSION_DEFAULT);
        options4 = new EcmascriptParserOptions();
        options4.setRhinoLanguageVersion(EcmascriptParserOptions.Version.VERSION_1_8);
        verifyOptionsEqualsHashcode(options1, options2, options3, options4);
    }

    private static final class MyRule extends AbstractEcmascriptRule {
    }
}
