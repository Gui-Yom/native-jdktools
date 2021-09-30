package javacnative;

public class Main {

    public static void main(String[] args) throws Exception {
        String jdkHome = System.getenv("GRAALVM_HOME");
        if (jdkHome == null || jdkHome.trim().isEmpty()) {
            System.err.println("Error: Environment variable GRAALVM_HOME is missing");
            System.exit(1);
        }

        // We must set this property ourselves since we won't run in the JVM but as a native-image
        System.setProperty("java.home", jdkHome);
        //System.setProperty("java.library.path", Path.of(jdkHome, "bin") + ";" + System.getProperty("java.library.path"));
        System.setProperty("jdk.image.use.jvm.map", "false");

        /*
        if ("printenv".equals(args[0])) {
            var props = new ArrayList<Entry>();
            for (Map.Entry<Object, Object> e : System.getProperties().entrySet()) {
                props.add(new Entry(e.getKey().toString(), e.getValue().toString()));
            }
            props.stream().sorted(Comparator.comparing(e -> e.key)).forEach(e -> System.out.println(e.key + ": " + e.value));
            System.exit(0);
        }*/

        //FileSystemProvider.installedProviders().forEach(p -> System.out.println(p.getScheme() + " / " + p.getClass().getName()));

        com.sun.tools.javac.Main.main(args);
    }

    /*
    static class Entry {
        String key;
        String value;

        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }*/
}
