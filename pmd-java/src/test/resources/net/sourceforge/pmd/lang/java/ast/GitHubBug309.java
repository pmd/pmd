import java.util.*;

public class GitHubBug309 {
    public static void main(String[] args) {
        Runnable r11 = Main::<String>new;
        IntFunction<int[]> r13 = int[]::<String>new; // produces the same results
    }
}
