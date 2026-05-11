@echo off
REM Compile HangmanFXPro.java with JavaFX

set JAVAFX_PATH=..\.\emeryll-HANGMAN-OOP-PROJECT--5eb4e52\javafx-sdk\javafx-sdk-26.0.1\lib

javac --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.media HangmanFXPro.java

echo Compilation complete. Run with: java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.media -cp . HangmanFXPro