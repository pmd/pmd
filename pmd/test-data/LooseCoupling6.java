import java.util.*;
public class LooseCoupling1 {
 private HashSet fooSet = new HashSet(); // NOT OK

 public HashSet getFoo() { // NOT OK
  return fooSet;
 }
}
