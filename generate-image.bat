@echo off
%GRAALVM_HOME%\bin\java -agentlib:native-image-agent=config-merge-dir=src/META-INF/native-image -jar out/artifacts/javac-native.jar -source 11 -target 11 -encoding UTF-8 -d temp Test.java
%GRAALVM_HOME%\bin\native-image -dsa --no-fallback -jar out/artifacts/javac-native.jar out/native/javac-native
