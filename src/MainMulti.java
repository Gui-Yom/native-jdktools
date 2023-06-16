import org.graalvm.nativeimage.IsolateThread;
import org.graalvm.nativeimage.UnmanagedMemory;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CCharPointerPointer;
import org.graalvm.nativeimage.c.type.CConst;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.word.WordFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.spi.ToolProvider;

public class MainMulti {
    public static void main(String[] args) {

        fixupEnv();

//        System.out.println(Arrays.toString(args));
//        ServiceLoader<ToolProvider> sl = ServiceLoader.load(ToolProvider.class);
//        for (ToolProvider tp : sl) {
//            System.out.println(tp.getClass() + ": " + tp.name());
//        }


        if (args.length == 0) {
            printHelp();
            System.exit(-1);
        }

        // Busybox style launcher
        var subArgs = Arrays.copyOfRange(args, 1, args.length);
        int code = 0;
        switch (args[0]) {
            case "help", "--help", "-h" -> printHelp();
            case "version", "-version", "--version", "-v" -> System.out.println("Built with JDK 17");
            case "javac" -> {
                // This should be set at image built time, but I set it at runtime too for consistency
                System.setProperty("jdk.image.use.jvm.map", "false");
                // Launch javac main
                code = ToolProvider.findFirst("javac").get().run(System.out, System.err, subArgs);
            }
            case "javadoc" -> code = ToolProvider.findFirst("javadoc").get().run(System.out, System.err, subArgs);
            case "jar" -> code = ToolProvider.findFirst("jar").get().run(System.out, System.err, subArgs);
            case "jdeps" -> code = ToolProvider.findFirst("jdeps").get().run(System.out, System.err, subArgs);
            case "javap" -> code = ToolProvider.findFirst("javap").get().run(System.out, System.err, subArgs);
            case "jmod" -> code = ToolProvider.findFirst("jmod").get().run(System.out, System.err, subArgs);
            case "jlink" -> code = ToolProvider.findFirst("jlink").get().run(System.out, System.err, subArgs);
            case "jpackage" -> code = ToolProvider.findFirst("jpackage").get().run(System.out, System.err, subArgs);
            default -> {
                System.out.println("No subcommand named '" + args[0] + "' exists.");
                code = -1;
            }
        }
        System.exit(code);
    }

    static void printHelp() {
        System.out.println("Please specify a subcommand.");
        System.out.println("Available subcommands : javac, javadoc, jar, jdeps, javap, jmod, jlink, jpackage");
    }

    static void fixupEnv() {
        // JAVAC_HOME should point to a jdk with the version used to build the current javac.
        // If you want to build for a jvm < current, you can pass the --system option to javac
        if (System.getProperty("java.home") == null) {
            String jdkHome = System.getenv("JDKTOOLS_HOME");
            if (jdkHome == null || jdkHome.trim().isEmpty()) {
                System.err.println("Error: Environment variable JDKTOOLS_HOME is missing (or set jvm property java.home)");
                System.exit(1);
            }
            // We must set this property ourselves since we won't run in the JVM but as a native-image
            System.setProperty("java.home", jdkHome);
        }
    }

    /**
     * The resulting ptr is owned by the caller.
     *
     * @param isolate
     * @param toolptr
     * @param argsptr
     * @param len
     * @return
     */
    @CEntryPoint
    static CCharPointer run(IsolateThread isolate, @CConst CCharPointer toolptr, @CConst CCharPointerPointer argsptr, int len) {
        fixupEnv();
        String tool = CTypeConversion.utf8ToJavaString(toolptr);
        String[] args = new String[len];
        for (int i = 0; i < len; ++i) {
            args[i] = CTypeConversion.utf8ToJavaString(argsptr.read(i));
        }
        var out = new ByteArrayOutputStream(8192);
        var err = new ByteArrayOutputStream(8192);
        ToolProvider.findFirst(tool).get().run(new PrintStream(out), new PrintStream(err), args);
        var output = out.toString(StandardCharsets.UTF_8) + err.toString(StandardCharsets.UTF_8);
        var bytesize = CTypeConversion.toCString(output, StandardCharsets.UTF_8, WordFactory.nullPointer(), WordFactory.zero());
        var ptr = (CCharPointer) UnmanagedMemory.malloc(bytesize.add(1));
        CTypeConversion.toCString(output, StandardCharsets.UTF_8, ptr, bytesize);
        return ptr;
    }
}
