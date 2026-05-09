@echo off
REM Run HangmanFXPro with JavaFX
REM Replace C:\path\to\javafx-sdk with your actual JavaFX SDK path

set JAVAFX_PATH=C:\Users\karlp\Downloads\javafx-sdk-26.0.1\lib

java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.media -cp . HangmanFXPro