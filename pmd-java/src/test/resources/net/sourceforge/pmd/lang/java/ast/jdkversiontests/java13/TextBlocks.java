/**
 * @see <a href="http://openjdk.java.net/jeps/355">JEP 355: Text Blocks (Preview)</a>
 */
public class TextBlocks {

    public static void main(String[] args) {
        String html = """
                <html>
                    <body>
                        <p>Hello, world</p>
                    </body>
                </html>
                """;

        System.out.println(html);

        String season = """
                winter""";    // the six characters w i n t e r

        String period = """
                        winter
                        """;          // the seven characters w i n t e r LF

        String greeting =
            """
            Hi, "Bob"
            """;        // the ten characters H i , SP " B o b " LF

        String salutation =
            """
            Hi,
             "Bob"
            """;        // the eleven characters H i , LF SP " B o b " LF

        String empty = """
                       """;      // the empty string (zero length)

        String quote = """
                       "
                       """;      // the two characters " LF

        String backslash = """
                           \\
                           """;  // the two characters \ LF

        String normalStringLiteral = "test";

        String code =
            """
            String text = \"""
                A text block inside a text block
            \""";
            """;
    }
}
