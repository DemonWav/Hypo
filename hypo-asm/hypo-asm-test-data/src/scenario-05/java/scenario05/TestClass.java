package scenario05;

import java.util.Random;
import java.util.function.Function;

// Compiled with JDK 21
public class TestClass {

    private long num = 0;

    public void test() {
        final String s3 = Integer.toString(new Random().nextInt());
        String s33 = Integer.toString(new Random().nextInt());
        StringBuilder sb = new StringBuilder();
        String str = sb.toString();
        final Runnable r = () -> {
            final String s4 = s3 + s3 + this.num;
            final String s44 = s33 + s33;
            str.length();
            final Runnable r1 = () -> {
                final String s5 = s4 + s4;
            };
            final Function<String, Object> f = TestClass::thing;
        };
    }

    public static Object thing(String in) {
        return null;
    }

    public static void testStatic() {
        final String s = Integer.toString(new Random().nextInt());
        final Runnable r = () -> {
            System.out.println(s);
        };
    }

    public void testFunction() {
        final String s = Integer.toString(new Random().nextInt());
        final Function<String, String> func = s1 -> s1 + s;
    }
}
