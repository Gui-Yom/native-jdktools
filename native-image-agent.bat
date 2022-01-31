@echo off
@rem Launch native-image-image for every configuration of every tool

set TOOL=%GRAALVM_HOME%\bin\java -agentlib:native-image-agent=config-merge-dir=src/META-INF/native-image -cp out/production/native-jdktools MainMulti

echo javac
rmdir /S /Q test\javac
%TOOL% javac -version > nul
%TOOL% javac --help > nul
%TOOL% javac -d test/javac test/Test.java > nul

echo javadoc
rmdir /S /Q test\javadoc
%TOOL% javadoc --help > nul
%TOOL% javadoc -d test/javadoc test/Test.java > nul 2> nul

echo jar
rmdir /S /Q test\jar
mkdir test\jar
%TOOL% jar --version > nul
%TOOL% jar --help > nul
%TOOL% jar --create --file test/jar/test.jar -e Test test/javac/Test.class > nul

echo jdeps
%TOOL% jdeps --version > nul
%TOOL% jdeps --help > nul
%TOOL% jdeps test/javac/Test.class > nul

echo javap
%TOOL% javap -version > nul
%TOOL% javap --help > nul
%TOOL% javap -c test/javac/Test.class > nul

echo jmod
%TOOL% jmod --version > nul
%TOOL% jmod --help > nul

echo jlink
rmdir /S /Q test\jlink
%TOOL% jlink --version > nul
%TOOL% jlink --help > nul
%TOOL% jlink --add-modules java.base --no-header-files --no-man-pages --strip-debug --output test/jlink > nul

set PATH=D:\apps\WixToolset;%PATH%

echo jpackage
rmdir /S /Q test\jpackage
mkdir test\jpackage
%TOOL% jpackage --version > nul
%TOOL% jpackage --help > nul
%TOOL% jpackage --main-class Test -t exe --main-jar test.jar -i test/jar --runtime-image test/jlink -d test/jpackage > nul
