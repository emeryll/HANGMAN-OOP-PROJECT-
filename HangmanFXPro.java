import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class HangmanFXPro extends Application {

    private Map<String, String[]> categories = new HashMap<>();
    private String word;
    private char[] guessed;
    private int lives = 6;
    private int score = 0;

    private Label wordLabel;
    private Label livesLabel;
    private Label scoreLabel;
    private ComboBox<String> categoryBox;

    private Pane drawingPane;
    private GridPane lettersPane;
    private List<javafx.scene.Node> parts = new ArrayList<>();

    // 🔊 SOUND (use your own .wav files)
    private AudioClip correctSound;
    private AudioClip wrongSound;
    private AudioClip winSound;
    private AudioClip loseSound;

    private AudioClip loadAudio(String fileName) {
        try {
            return new AudioClip("file:" + fileName);
        } catch (Exception e) {
            System.out.println("Audio file " + fileName + " not found, sound disabled.");
            return null;
        }
    }

    @Override
    public void start(Stage stage) {
        // Load audio files (optional)
        correctSound = loadAudio("correct.wav");
        wrongSound = loadAudio("wrong.wav");
        winSound = loadAudio("win.wav");
        loseSound = loadAudio("lose.wav");

        // Categories
        categories.put("Programming", new String[]{"JAVA", "CLASS", "OBJECT"});
        categories.put("Animals", new String[]{"DOG", "CAT", "ELEPHANT"});
        categories.put("Fruits", new String[]{"APPLE", "MANGO", "BANANA"});

        wordLabel = new Label();
        wordLabel.setFont(new Font(30));

        livesLabel = new Label();
        scoreLabel = new Label("Score: 0");

        categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll(categories.keySet());
        categoryBox.setValue("Programming");
        categoryBox.setOnAction(e -> startGame());

        drawingPane = new Pane();
        drawingPane.setPrefSize(200, 250);

        lettersPane = new GridPane();
        lettersPane.setHgap(5);
        lettersPane.setVgap(5);
        lettersPane.setAlignment(Pos.CENTER);

        Button resetBtn = new Button("Restart");
        resetBtn.setOnAction(e -> startGame());

        VBox top = new VBox(10, wordLabel, livesLabel, scoreLabel, categoryBox);
        top.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setTop(top);
        root.setCenter(lettersPane);
        root.setRight(drawingPane);
        root.setBottom(resetBtn);

        startGame();

        stage.setScene(new Scene(root, 700, 450));
        stage.setTitle("Hangman Pro");
        stage.show();
    }

    private void startGame() {
        lettersPane.getChildren().clear();
        drawingPane.getChildren().clear();
        parts.clear();

        drawGallows();

        lives = 6;
        livesLabel.setText("Lives: " + lives);

        String category = categoryBox.getValue();
        String[] words = categories.get(category);

        word = words[new Random().nextInt(words.length)];

        guessed = new char[word.length()];
        Arrays.fill(guessed, '_');

        updateWord();

        int row = 0, col = 0;
        for (char c = 'A'; c <= 'Z'; c++) {
            Button btn = new Button(String.valueOf(c));
            btn.setPrefWidth(40);

            char letter = c;
            btn.setOnAction(e -> handleGuess(letter, btn));

            lettersPane.add(btn, col, row);
            col++;
            if (col == 7) { col = 0; row++; }
        }
    }

    private void handleGuess(char letter, Button btn) {
        boolean found = false;

        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == letter) {
                guessed[i] = letter;
                found = true;
            }
        }

        if (found) {
            btn.setStyle("-fx-background-color: lightgreen;");
            if (correctSound != null) correctSound.play();
        } else {
            btn.setStyle("-fx-background-color: red;");
            lives--;
            livesLabel.setText("Lives: " + lives);
            if (wrongSound != null) wrongSound.play();
            drawNextPart();
        }

        btn.setDisable(true);
        updateWord();
        checkGame();
    }

    private void updateWord() {
        StringBuilder sb = new StringBuilder();
        for (char c : guessed) sb.append(c).append(" ");
        wordLabel.setText(sb.toString());
    }

    private void checkGame() {
        if (String.valueOf(guessed).equals(word)) {
            score += lives * 10;
            scoreLabel.setText("Score: " + score);
            if (winSound != null) winSound.play();
            showAlert("You Win!");
            startGame();
        } else if (lives == 0) {
            if (loseSound != null) loseSound.play();
            showAlert("You Lose! Word: " + word);
            startGame();
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // 🎨 DRAWING + ANIMATION

    private void drawGallows() {
        drawingPane.getChildren().addAll(
                new Line(20, 230, 180, 230),
                new Line(50, 230, 50, 20),
                new Line(50, 20, 130, 20),
                new Line(130, 20, 130, 50)
        );
    }

    private void drawNextPart() {
        javafx.scene.Node part = null;

        switch (parts.size()) {
            case 0: part = new Circle(130, 70, 20); break;
            case 1: part = new Line(130, 90, 130, 150); break;
            case 2: part = new Line(130, 110, 100, 130); break;
            case 3: part = new Line(130, 110, 160, 130); break;
            case 4: part = new Line(130, 150, 100, 190); break;
            case 5: part = new Line(130, 150, 160, 190); break;
        }

        if (part != null) {
            part.setOpacity(0);

            FadeTransition ft = new FadeTransition(Duration.seconds(0.5), part);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.play();

            parts.add(part);
            drawingPane.getChildren().add(part);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}