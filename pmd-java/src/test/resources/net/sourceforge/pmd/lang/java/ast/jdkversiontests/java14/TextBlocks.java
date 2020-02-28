import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @see <a href="https://openjdk.java.net/jeps/368">JEP 368: Text Blocks (Second Preview)</a>
 */
public class TextBlocks {


    public static void main(String[] args) throws Exception {
        // note: there is trailing whitespace!!
        String html = """
                      <html>   
                          <body>
                              <p>Hello, world</p>    
                          </body> 
                      </html>   
                      """;
        System.out.println(html);

        String query = """
                       SELECT `EMP_ID`, `LAST_NAME` FROM `EMPLOYEE_TB`
                       WHERE `CITY` = 'INDIANAPOLIS'
                       ORDER BY `EMP_ID`, `LAST_NAME`;
                       """;
        System.out.println(query);

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        Object obj = engine.eval("""
                                 function hello() {
                                     print('"Hello, world"');
                                 }
                                 
                                 hello();
                                 """);

        // Escape sequences
        String htmlWithEscapes = """
                      <html>\r
                          <body>\r
                              <p>Hello, world</p>\r
                          </body>\r
                      </html>\r
                      """;
        System.out.println(htmlWithEscapes);

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

        // new escape sequences
        String text = """
                      Lorem ipsum dolor sit amet, consectetur adipiscing \
                      elit, sed do eiusmod tempor incididunt ut labore \
                      et dolore magna aliqua.\
                      """;
        System.out.println(text);

        String colors = """
                        red  \s
                        green\s
                        blue \s
                        """;
        System.out.println(colors);

        // empty new line as first content
        String emptyLine = """

test
""";
        System.out.println(emptyLine.replaceAll("\n", "<LF>"));

        // backslash escapes
        String bs = """
                \\test
                """;
        System.out.println(bs.replaceAll("\n", "<LF>"));
    }
}
