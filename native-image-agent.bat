@echo off
@rem Launch native-image-image for every configuration of every tool

set TOOL=%GRAALVM_HOME%\bin\java -agentlib:native-image-agent=config-merge-dir=src/META-INF/native-image -cp out/production/native-jdktools MainMulti

echo javac
%TOOL% javac -version > nul 2> nul
%TOOL% javac --help > nul 2> nul
%TOOL% javac -d test/build test/Test.java > nul 2> nul

echo javadoc
%TOOL% javadoc --help > nul 2> nul
%TOOL% javadoc -d test/javadoc test/Test.java > nul 2> nul

echo jar
%TOOL% jar --version > nul 2> nul
%TOOL% jar --help > nul 2> nul
%TOOL% jar --create --file test/jar/test.jar -e Test test/build/Test.class > nul 2> nul

echo jdeps
%TOOL% jdeps --version > nul 2> nul
%TOOL% jdeps --help > nul 2> nul
%TOOL% jdeps test/build/Test.class > nul 2> nul

echo javap
%TOOL% javap -version > nul 2> nul
%TOOL% javap --help > nul 2> nul
%TOOL% javap -c test/build/Test.class > nul 2> nul

echo jmod
%TOOL% jmod --version > nul 2> nul
%TOOL% jmod --help > nul 2> nul

echo jlink
%TOOL% jlink --version > nul 2> nul
%TOOL% jlink --help > nul 2> nul
%TOOL% jlink --add-modules java.base --no-header-files --no-man-pages --strip-debug --output test/jlink > nul 2> nul

set PATH=D:\apps\WixToolset;%PATH%

echo jpackage
%TOOL% jpackage --version > nul 2> nul
%TOOL% jpackage --help > nul 2> nul
%TOOL% jpackage --main-class Test -t exe --main-jar test.jar -i test/jar --runtime-image test/jlink > nul 2> nul
