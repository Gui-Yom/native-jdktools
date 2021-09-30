package javacnative;

public class Main {
    public static void main(String[] args) throws Exception {

        // This is the home of a jdk with the version used to build the current javac.
        // If you want to build for a jvm < current, you can pass the --system option to javac
        String jdkHome = System.getenv("JAVAC_HOME");
        if (jdkHome == null || jdkHome.trim().isEmpty()) {
            System.err.println("Error: Environment variable JAVAC_HOME is missing");
            System.exit(1);
        }

        // We must set this property ourselves since we won't run in the JVM but as a native-image
        System.setProperty("java.home", jdkHome);
        // This should be set at image built time, but I set it at runtime too for consistency
        System.setProperty("jdk.image.use.jvm.map", "false");

        // Launch javac main
        com.sun.tools.javac.Main.main(args);
    }
}
