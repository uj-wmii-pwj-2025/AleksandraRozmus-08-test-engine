package uj.wmii.pwj.anns;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Objects;

public class MyTestEngine {
    private final String className;
    private final TestLogger logger = new TestLogger();

    public MyTestEngine(String className) {
        this.className = className;
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please specify test class name");
            System.exit(-1);
        }
        String className = args[0].trim();
        MyTestEngine engine = new MyTestEngine(className);

        engine.logger.printStartInfo(className);
        engine.runTests();
    }

    public void runTests() {
        final Object unit = getObject(className);
        List<Method> testMethods = getTestMethods(unit);
        int passCount = 0, failCount = 0, errorCount = 0;
        int testNum = 1;

        for (Method m : testMethods) {
            logger.printTestStartHeader(testNum++, testMethods.size(), m.getName());
            TestResult result = launchSingleMethod(m, unit);

            switch (result) {
                case PASS -> passCount++;
                case FAIL -> failCount++;
                case ERROR -> errorCount++;
            }
        }
        logger.printSummary(testMethods.size(), passCount, failCount, errorCount);
    }

    private TestResult launchSingleMethod(Method m, Object unit) {
        MyTest annotation = m.getAnnotation(MyTest.class);
        Object[][] paramsArray = prepareParamsArray(annotation, m.getParameterCount(), m.getName());
        String[] expectedArray = prepareExpectedArray(annotation);

        if (paramsArray.length == 0)
            return runNoParamMethod(m, unit, expectedArray);
        else
            return runParamMethod(m, unit, paramsArray, expectedArray);
    }

    private Object[][] prepareParamsArray(MyTest annotation, int paramCount, String methodName) {
        String[] params = annotation.params();

        if (params.length == 0)
            return new Object[0][];

        Object[][] prepared = new Object[params.length][];

        for (int i = 0; i < params.length; i++) {
            String[] split = params[i].split(",");

            if (split.length != paramCount) {
                logger.printResult(methodName + " (" + params[i] + ")", TestResult.ERROR,"Wrong number of arguments. Expected " + paramCount + ", got " + split.length);
                prepared[i] = null;
            } else
                prepared[i] = split;
        }
        return prepared;
    }

    private String[] prepareExpectedArray(MyTest annotation) {
        String[] expected = annotation.expected();
        if (expected.length == 0)
            return new String[]{""};

        return expected;
    }

    private TestResult runNoParamMethod(Method m, Object unit, String[] expectedArray) {
        TestResult result;
        try {
            Object value = m.invoke(unit);
            result = checkResult(value, expectedArray[0]);
            logger.logTestResult(m.getName(), result, value, expectedArray[0]);
        } catch (ReflectiveOperationException e) {
            result = TestResult.ERROR;
            logger.logTestResult(m.getName(), result, extractErrorMessage(e), "");
        }
        return result;
    }

    private TestResult runParamMethod(Method m, Object unit, Object[][] paramsArray, String[] expectedArray) {
        TestResult overall = TestResult.PASS;

        for (int i = 0; i < paramsArray.length; i++) {
            Object[] args = paramsArray[i];

            if (args == null) {
                overall = TestResult.ERROR;
                logger.printResult(m.getName(), TestResult.ERROR, "Invalid number of arguments");
                continue;
            }

            String expected = (expectedArray.length > i) ? expectedArray[i] : "";
            try {
                Object result = m.invoke(unit, args);
                TestResult testResult = checkResult(result, expected);
                logger.logTestResult(m.getName() + " (" + Arrays.toString(args) + ")", testResult, result, expected);

                if (overall != TestResult.ERROR && testResult == TestResult.FAIL)
                    overall = TestResult.FAIL;

            } catch (ReflectiveOperationException e) {
                overall = TestResult.ERROR;
                logger.logTestResult(m.getName() + " (" + Arrays.toString(args) + ")", overall, extractErrorMessage(e), "");
            }
        }

        if (paramsArray.length > 1) {
            System.out.println();
            logger.printResult("[overall]  " + m.getName(), overall, "");
        }
        return overall;
    }

    private TestResult checkResult(Object obj, String expected) {
        if (expected.isEmpty())
            return TestResult.PASS;

        if (obj != null && obj.toString().equals(expected))
            return TestResult.PASS;

        return TestResult.FAIL;
    }

    private String extractErrorMessage(Throwable e) {
        Throwable cause = e.getCause();
        Throwable actualCause  = Objects.requireNonNullElse(cause, e);

        String msg = actualCause .getMessage();
        if (msg == null || msg.isEmpty())
            msg = "";

        return actualCause.getClass().getSimpleName() + ": " + msg;
    }

    private static List<Method> getTestMethods(Object unit) {
        Method[] methods = unit.getClass().getDeclaredMethods();
        return Arrays.stream(methods)
                .filter(m -> m.getAnnotation(MyTest.class) != null)
                .sorted(Comparator.comparing(Method::getName))
                .collect(Collectors.toList());
    }

    private static Object getObject(String className) {
        try {
            Class<?> unitClass = Class.forName(className);
            return unitClass.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return new Object();
        }
    }
}
