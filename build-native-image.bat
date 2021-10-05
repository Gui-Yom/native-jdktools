@echo off

%GRAALVM_HOME%\bin\native-image -jar out\artifacts\native-jdktools.jar out/native/native-jdktools
