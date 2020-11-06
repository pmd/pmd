/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm;

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Unit test for VM parsing.
 *
 */
public class VmParserTest {

    private static final String VM_SRC = "<HTML><BODY>Hello $customer.Name <table> "
            + "#foreach($mud in $mudsOnSpecial)" + "  #if ( $customer.hasPurchased($mud) )" + "     <tr>" + "      <td>"
            + "       $flogger.getPromo( $mud )" + "    </td>" + "  </tr>" + " #elseif ($customer.broke) do stuff #end"
            + "\n " + "#end " + "</table>";

    private static final String SRC2 = "#macro(tablerows $color $values ) " + "#foreach( $value in $values ) "
            + "<tr><td bgcolor=$color>$value</td></tr> " + "#end " + "#end "
            + "#set( $greatlakes = [\"Superior\",\"Michigan\",\"Huron\",\"Erie\",\"Ontario\"] ) "
            + "#set( $color = \"blue\" ) " + "<table> " + " #tablerows( $color $greatlakes ) " + "</table>";

    private static final String SRC3 = "#if ( $c1 ) #if ( $c2)#end #end";

    // private static final String VM_SRC = "#if( $mud == 1 ) blah #if ($dirt ==
    // 2) stuff #end #end";

    @Test
    public void testParser() {
        final Node node = parse(VM_SRC);
        Assert.assertNotNull(node);
    }

    @Test
    public void testParser2() {
        final Node node = parse(SRC2);
        Assert.assertNotNull(node);
    }

    @Test
    public void testParser3() {
        final Node node = parse(SRC3);
        Assert.assertNotNull(node);
    }

    private Node parse(final String code) {
        final LanguageVersionHandler vmLang = LanguageRegistry.getLanguage(VmLanguageModule.NAME).getDefaultVersion()
                .getLanguageVersionHandler();
        final Parser parser = vmLang.getParser(vmLang.getDefaultParserOptions());
        final Node node = parser.parse(null, new StringReader(code));
        return node;
    }
}
