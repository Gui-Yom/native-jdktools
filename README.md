# javac-native

An experiment with generating a native-image for javac. A desirable performance metric for developer tools is speed,
however javac takes ages just to start. We aim to fix that by building a native-image with a javac wrapper.

*This was fucking hard*

javac need to read the jdk module files and requires the `jrt:` file system provider which is disabled by default. It
also requires the `jimage` native library to map the modules file in memory but for some reasons it doesn't work and end
up in a `UnsatisfiedLinkError` so we need to disallow using it.

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

To build the final executable, you need a jar with everyting that's under `src`. Though module-info isn't mandatory.
Set `GRAALVM_HOME` to your graalvm installation dir. At runtime, please set `JAVAC_HOME` to the home of a jdk that is
the same version as GraalVM (which can be GraalVM).

```shell
native-image -jar javac-native.jar javac-native
```

The final executable is 100% compatible with javac cli. You can also use additional properties for the native-image
runtime. You can still use javac-native to compile for versions lower than the one it was compiled with by using
the `--system`
option.

## Benchmarks

TODO but I went from 600ms to 70ms when compiling a couple files with it.
