import java.util.Arrays;
import java.util.spi.ToolProvider;

public class MainMulti {
    public static void main(String[] args) {

        // JAVAC_HOME should point to a jdk with the version used to build the current javac.
        // If you want to build for a jvm < current, you can pass the --system option to javac
        if (System.getProperty("java.home") == null) {
            String jdkHome = System.getenv("JAVAC_HOME");
            if (jdkHome == null || jdkHome.trim().isEmpty()) {
                System.err.println("Error: Environment variable JAVAC_HOME is missing (or set property java.home)");
                System.exit(1);
            }
            // We must set this property ourselves since we won't run in the JVM but as a native-image
            System.setProperty("java.home", jdkHome);
        }

        /*System.out.println(Arrays.toString(args));
        ServiceLoader<ToolProvider> sl = ServiceLoader.load(ToolProvider.class);
        for (ToolProvider tp : sl) {
            System.out.println(tp.getClass());
        }*/

        if (args.length <= 0) {
            printHelp();
            System.exit(-1);
        }

        var subArgs = Arrays.copyOfRange(args, 1, args.length);
        switch (args[0]) {
            case "help", "--help", "-h" -> {
                printHelp();
            }
            case "version", "-version", "--version", "-v" -> {
                System.out.println("Built with JDK 17");
            }
            case "javac" -> {
                // This should be set at image built time, but I set it at runtime too for consistency
                System.setProperty("jdk.image.use.jvm.map", "false");
                // Launch javac main
                ToolProvider.findFirst("javac").get().run(System.out, System.err, subArgs);
            }
            case "javadoc" -> {
                ToolProvider.findFirst("javadoc").get().run(System.out, System.err, subArgs);
            }
            case "jar" -> {
                ToolProvider.findFirst("jar").get().run(System.out, System.err, subArgs);
            }
            case "jdeps" -> {
                ToolProvider.findFirst("jdeps").get().run(System.out, System.err, subArgs);
            }
            case "javap" -> {
                ToolProvider.findFirst("javap").get().run(System.out, System.err, subArgs);
            }
            case "jmod" -> {
                ToolProvider.findFirst("jmod").get().run(System.out, System.err, subArgs);
            }
            case "jlink" -> {
                ToolProvider.findFirst("jlink").get().run(System.out, System.err, subArgs);
            }
            case "jpackage" -> {
                ToolProvider.findFirst("jpackage").get().run(System.out, System.err, subArgs);
            }
            default -> {
                System.out.println("No subcommand named '" + args[0] + "' exists.");
                System.exit(-1);
            }
        }
    }

    static void printHelp() {
        System.out.println("Please specify a subcommand.");
        System.out.println("Available subcommands : javac, javadoc, jar, jdeps, javap, jmod, jlink, jpackage");
    }
}
