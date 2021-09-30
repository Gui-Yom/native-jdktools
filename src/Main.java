import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {

        if ("printenv".equals(args[0])) {
            var props = new ArrayList<Entry>();
            for (Map.Entry<Object, Object> e : System.getProperties().entrySet()) {
                props.add(new Entry(e.getKey().toString(), e.getValue().toString()));
            }
            props.stream().sorted(Comparator.comparing(e -> e.key)).forEach(e -> System.out.println(e.key + ": " + e.value));
            System.exit(0);
        }

        // Fast start options
        // -Xshare:on -XX:TieredStopAtLevel=1 -XX:+UseParallelGC -Xverify:none

        String jdkHome = System.getenv("GRAALVM_HOME");
        if (jdkHome == null || jdkHome.trim().isEmpty()) {
            System.err.println("Error: Environment variable GRAALVM_HOME is missing");
            System.exit(1);
        }

        // We must set this property ourselves since we won't run in the JVM but as a native-image
        System.setProperty("java.home", Path.of(jdkHome).toString());

        com.sun.tools.javac.Main.main(args);
    }

    static class Entry {
        String key;
        String value;

        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
