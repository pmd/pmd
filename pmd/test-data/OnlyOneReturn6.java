public class OnlyOneReturn6 {
 public int foo() {
  FileFilter f = new FileFilter() {
   public boolean accept(File file) {
    return false;
   }
  };
  return 2;
 }
}
