# native-jdktools

An experiment with generating a GraalVM native-image for every jdk tool. A desirable performance metric for a developer
tools is speed, however something like javac takes ages just to start.

*This was fucking hard*

A tool like javac need to read the jdk module files and requires the `jrt:` file system provider which is disabled by
default. It also requires the `jimage` native library to map the modules file in memory but for some reasons it doesn't
work and end up in a `UnsatisfiedLinkError` so we need to disallow using it.

## native-image options

The final options used are :

option                       |justification
-----------------------------|-------------
-dsa                         |Disabling system assertions can only improve speed right ?
--no-fallback                |Do what I said and shut up
--gc=epsilon                 |The no-GC GC, don't spend time cleaning up if we finish in a second
-H:+AllowJRTFileSystem       |The most important of all to allow it to work. Allows including `jdk.internal.jrtfs`.
-Djdk.image.use.jvm.map=false|Can probably be remplaced to use the `jimage` native library but I don't know how.

More options could probably be used at build time and at runtime but this is a good start.

To build the final executable, you need a jar with everyting that's under `src` compiled. Set `GRAALVM_HOME` to your
graalvm installation dir.

```shell
native-image -jar native-jdktools.jar native-jdktools
```

The resulting image weights 46 MB on my machine and took about 70 secs to compile.

At runtime, please set `JDKTOOLS_HOME` to the home of a jdk. This JDK should be the same version as the one used to
build the native image.

The individual tools cli are accessible under the subcommand with their name.

## Benchmarks

TODO but I went from 600ms to 70ms when compiling a couple files with it. This entirely removes the jvm start and stop
time so that's already a huge gain.
