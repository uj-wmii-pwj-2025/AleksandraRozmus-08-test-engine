package uj.wmii.pwj.anns;

public class MyBeautifulTestSuite {

    @MyTest
    public void testSoemthing() {
        System.out.println("I'm testing something!");
    }

    @MyTest(params = {"a param", "b param", "c param. Long long C param."})
    public void testWithParam(String param) {
        System.out.printf("I was invoked with parameter: %s\n", param);
    }

    public void notATest() {
        System.out.println("I'm not a test.");
    }

    @MyTest
    public void imFailue() {
        System.out.println("I AM EVIL.");
        throw new NullPointerException();
    }


    @MyTest(params = {"2,3", "10,20", "5,7"}, expected = {"5", "30", "12"})
    public String testsum(String aStr, String bStr) {
        int a = Integer.parseInt(aStr);
        int b = Integer.parseInt(bStr);
        return Integer.toString(a + b);
    }

    @MyTest(params = {"1,2,3", "4,5,6"}, expected = {"0", "15"})
    public String testSumThree(String aStr, String bStr, String cStr) {
        int a = Integer.parseInt(aStr);
        int b = Integer.parseInt(bStr);
        int c = Integer.parseInt(cStr);
        return Integer.toString(a + b + c);
    }

    @MyTest(expected = {"Hello World"})
    public String testNoParams() {
        return "Hello World";
    }

    @MyTest(expected = {"10"})
    public String wrongReturn() {
        return "11";
    }

    @MyTest
    public int divisionByZero() {
        return 10 / 0;
    }

    @MyTest(params = {"Hello"}, expected = {"Hello"})
    public String hello(String input) {
        return input;
    }

    @MyTest(params = {"Hello", "World"}, expected = {"Hello", "World"})
    public String helloWorld(String input) {
        return input;
    }

    @MyTest(params = {"1,2,3"})
    public String wrongArgs(String a, String b) {
        return a + b;
    }

    @MyTest
    public void throwException() {
        throw new RuntimeException("Exception!");
    }
}
/* ./gradlew run --args="uj.wmii.pwj.anns.MyBeautifulTestSuite" */