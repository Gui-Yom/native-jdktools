# native-jdktools

An experiment about generating a GraalVM native image for every JDK tools. A desirable performance metric for a
developer tools is speed, however a tool like javac takes ages just to start. This is a problem inherent to the JVM and
literally the reason GraalVM was created.

Narrator : *This wasn't easy*

Turns out, the native-image-agent doesn't detect everything that's needed. We also need special flags (*hidden flags*)
at build time that are related to how javac and other tools work (reading jdk modules).

## native-image options

The final options are :

| option                        | justification                                                                         |
|-------------------------------|---------------------------------------------------------------------------------------|
| -dsa                          | Disabling system assertions can only improve speed right ?                            |
| --no-fallback                 | Do what I said and shut up                                                            |
| --gc=epsilon                  | The no-GC GC, don't spend time cleaning up if we finish in a second                   |
| -H:+AllowJRTFileSystem        | The most important of all to allow it to work. Allows including `jdk.internal.jrtfs`. |
| -Djdk.image.use.jvm.map=false | Can probably be replaced to use the `jimage` native library but I don't know how.     |

More options could probably be used at build time and at runtime but this is a good start. `--gc=epsilon` should be
removed if the final goal is to compile a über codebase.

## Compiling

To build the final executable, you need a jar with everything that's under `src` compiled to a jar. Set `GRAALVM_HOME`
to your graalvm installation dir.

```shell
native-image -jar native-jdktools.jar native-jdktools
```

The resulting image weights 44 MB on my machine and took about 75 secs to compile.

At runtime, please set `JDKTOOLS_HOME` to the home of a jdk. This JDK should be the same version as the one used to
build the native image.

The individual tools' cli are accessible under the subcommand with their name.

## Benchmarks

TODO but I went from 600ms to 70ms when compiling a couple files with it. This entirely removes the jvm start and stop
time so that's already a huge gain.
