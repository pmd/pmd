public class OnlyOneReturn5 {
 public int foo(int x) {
 	try {
 		x += 2;
 		return x;
 	} finally {
 		System.err.println("WunderBuggy!");
 	}
 }
}
