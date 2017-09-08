package net.sourceforge.pmd.util.fxdesigner;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class DesignerWindowSettingsTest {

    @Test
    public void testKeysNamesAreUnique() {
        List<String> keyNames = Arrays.stream(DesignerWindowSettings.values())
                                      .map(DesignerWindowSettings::getKeyName)
                                      .collect(Collectors.toList());

        assertEquals(keyNames.size(), new HashSet<>(keyNames).size());

    }

}