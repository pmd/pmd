import java.io.*;
public class UnusedLocal1 {
 public foo() {
  try {
   FileReader fr = new FileReader("/dev/null");
   } catch (Exception e) {}
 }
}
