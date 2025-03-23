/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.velocity;

import org.junit.jupiter.api.Test;

/**
 * Unit test for VM parsing.
 */
class VtlParserTest {

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
    void testParser() {
        VtlParsingHelper.DEFAULT.parse(VM_SRC);
    }

    @Test
    void testParser2() {
        VtlParsingHelper.DEFAULT.parse(SRC2);
    }

    @Test
    void testParser3() {
        VtlParsingHelper.DEFAULT.parse(SRC3);
    }

}
