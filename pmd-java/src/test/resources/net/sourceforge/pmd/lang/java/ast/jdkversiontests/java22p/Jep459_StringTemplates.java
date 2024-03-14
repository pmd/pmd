/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import static java.lang.StringTemplate.RAW;
import static java.util.FormatProcessor.FMT;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;

/**
 * @see <a href="https://openjdk.org/jeps/430">JEP 430: String Templates (Preview)</a> (Java 21)
 * @see <a href="https://openjdk.org/jeps/459">JEP 459: String Templates (Second Preview)</a> (Java 22)
 */
class Jep459_StringTemplates {
    record Request(String date, String time, String ipAddress) {}

    static void STRTemplateProcessor() {
        // Embedded expressions can be strings
        String firstName = "Bill";
        String lastName  = "Duck";
        String fullName  = STR."\{firstName} \{lastName}";
        // | "Bill Duck"
        String sortName  = STR."\{lastName}, \{firstName}";
        // | "Duck, Bill"

        // Embedded expressions can perform arithmetic
        int x = 10, y = 20;
        String s1 = STR."\{x} + \{y} = \{x + y}";
        // | "10 + 20 = 30"

        // Embedded expressions can invoke methods and access fields
        String s2 = STR."You have a \{getOfferType()} waiting for you!";
        // | "You have a gift waiting for you!"
        Request req = new Request("2022-03-25", "15:34", "8.8.8.8");
        String t = STR."Access at \{req.date} \{req.time} from \{req.ipAddress}";
        //| "Access at 2022-03-25 15:34 from 8.8.8.8"

        String filePath = "tmp.dat";
        File   file     = new File(filePath);
        String old = "The file " + filePath + " " + (file.exists() ? "does" : "does not") + " exist";
        String msg = STR."The file \{filePath} \{file.exists() ? "does" : "does not"} exist";
        // | "The file tmp.dat does exist" or "The file tmp.dat does not exist"

        // spread over multiple lines
        String time = STR."The time is \{
            // The java.time.format package is very useful
            DateTimeFormatter
                    .ofPattern("HH:mm:ss")
                    .format(LocalTime.now())
        } right now";
        // | "The time is 12:34:56 right now"

        // Left to right
        // Embedded expressions can be postfix increment expressions
        int index = 0;
        String data = STR."\{index++}, \{index++}, \{index++}, \{index++}";
        // | "0, 1, 2, 3"

        // Embedded expression is a (nested) template expression
        String[] fruit = { "apples", "oranges", "peaches" };
        String s3 = STR."\{fruit[0]}, \{STR."\{fruit[1]}, \{fruit[2]}"}";
        // | "apples, oranges, peaches"
        String s4 = STR."\{fruit[0]}, \{
            STR."\{fruit[1]}, \{fruit[2]}"
        }";
    }

    static String getOfferType() { return "_getOfferType_"; }

    static void multilineTemplateExpressions() {
        String title = "My Web Page";
        String text  = "Hello, world";
        String html = STR."""
        <html>
          <head>
            <title>\{title}</title>
          </head>
          <body>
            <p>\{text}</p>
          </body>
        </html>
        """;
        /*
        | """
        | <html>
        |   <head>
        |     <title>My Web Page</title>
        |   </head>
        |   <body>
        |     <p>Hello, world</p>
        |   </body>
        | </html>
        | """
         */

        String name    = "Joan Smith";
        String phone   = "555-123-4567";
        String address = "1 Maple Drive, Anytown";
        String json = STR."""
            {
                "name":    "\{name}",
                "phone":   "\{phone}",
                "address": "\{address}"
            }
            """;
        /*
        | """
        | {
        |     "name":    "Joan Smith",
        |     "phone":   "555-123-4567",
        |     "address": "1 Maple Drive, Anytown"
        | }
        | """
         */

        record Rectangle(String name, double width, double height) {
            double area() {
                return width * height;
            }
        }
        Rectangle[] zone = new Rectangle[] {
            new Rectangle("Alfa", 17.8, 31.4),
            new Rectangle("Bravo", 9.6, 12.4),
            new Rectangle("Charlie", 7.1, 11.23),
        };
        String table = STR."""
            Description  Width  Height  Area
            \{zone[0].name}  \{zone[0].width}  \{zone[0].height}     \{zone[0].area()}
            \{zone[1].name}  \{zone[1].width}  \{zone[1].height}     \{zone[1].area()}
            \{zone[2].name}  \{zone[2].width}  \{zone[2].height}     \{zone[2].area()}
            Total \{zone[0].area() + zone[1].area() + zone[2].area()}
            """;
        /*
        | """
        | Description  Width  Height  Area
        | Alfa  17.8  31.4     558.92
        | Bravo  9.6  12.4     119.03999999999999
        | Charlie  7.1  11.23     79.733
        | Total 757.693
        | """
         */
    }

    static void FMTTemplateProcessor() {
        record Rectangle(String name, double width, double height) {
            double area() {
                return width * height;
            }
        };
        Rectangle[] zone = new Rectangle[] {
            new Rectangle("Alfa", 17.8, 31.4),
            new Rectangle("Bravo", 9.6, 12.4),
            new Rectangle("Charlie", 7.1, 11.23),
        };
        String table = FMT."""
            Description     Width    Height     Area
            %-12s\{zone[0].name}  %7.2f\{zone[0].width}  %7.2f\{zone[0].height}     %7.2f\{zone[0].area()}
            %-12s\{zone[1].name}  %7.2f\{zone[1].width}  %7.2f\{zone[1].height}     %7.2f\{zone[1].area()}
            %-12s\{zone[2].name}  %7.2f\{zone[2].width}  %7.2f\{zone[2].height}     %7.2f\{zone[2].area()}
            \{" ".repeat(28)} Total %7.2f\{zone[0].area() + zone[1].area() + zone[2].area()}
            """;
        /*
        | """
        | Description     Width    Height     Area
        | Alfa            17.80    31.40      558.92
        | Bravo            9.60    12.40      119.04
        | Charlie          7.10    11.23       79.73
        |                              Total  757.69
        | """
         */
    }

    static void ensuringSafety() {
        String name = "Joan";
        StringTemplate st = RAW."My name is \{name}";
        String info = STR.process(st);
    }

    record User(String firstName, int accountNumber) {}

    static void literalsInsideTemplateExpressions() {
        String s1 = STR."Welcome to your account";
        // | "Welcome to your account"
        User user = new User("Lisa", 12345);
        String s2 = STR."Welcome, \{user.firstName()}, to your account \{user.accountNumber()}";
        // | "Welcome, Lisa, to your account 12345"
    }

    static void emptyEmbeddedExpression() {
        String s1=STR."Test \{ }";
    }
}
