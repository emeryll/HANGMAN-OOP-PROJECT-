@echo off
REM Run HangmanFXPro with JavaFX

set JAVAFX_PATH=..\.\emeryll-HANGMAN-OOP-PROJECT--5eb4e52\javafx-sdk\javafx-sdk-26.0.1\lib

java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.media -cp . HangmanFXPro