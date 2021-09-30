@echo off
%GRAALVM_HOME%\bin\java -agentlib:native-image-agent=config-merge-dir=src/META-INF/native-image -jar out/artifacts/javac-native.jar -encoding UTF-8 -d temp Test.java
%GRAALVM_HOME%\bin\native-image -jar out/artifacts/javac-native.jar out/native/javac-native
