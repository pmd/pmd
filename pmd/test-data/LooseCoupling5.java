import java.util.*;
public class LooseCoupling5 {
 private HashSet fooSet = new HashSet(); // NOT OK

 public Set getFoo() {
  return fooSet;
 }
}
