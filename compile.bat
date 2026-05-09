@echo off
REM Compile HangmanFXPro.java with JavaFX
REM Replace C:\path\to\javafx-sdk with your actual JavaFX SDK path

set JAVAFX_PATH=C:\Users\karlp\Downloads\javafx-sdk-26.0.1\lib

javac --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.media HangmanFXPro.java

echo Compilation complete. Run with: java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.media -cp . HangmanFXPro