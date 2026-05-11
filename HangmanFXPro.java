import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class HangmanFXPro extends Application {

    private Map<String, String[]> categories = new HashMap<>();
    private String word;
    private char[] guessed;
    private int lives = 6;

    private HBox wordBox;
    private Label livesLabel;
    private Label currentPlayerLabel;
    private Label[] playerScoreLabels;

    private Pane drawingPane;
    private GridPane lettersPane;
    private List<javafx.scene.Node> parts = new ArrayList<>();

    private Stage primaryStage;
    private List<String> playerNames = new ArrayList<>();
    private String selectedCategory = "Programming";

    private int currentPlayerIndex = 0;
    private int[] playerScores;

    private AudioClip correctSound;
    private AudioClip wrongSound;
    private AudioClip winSound;
    private AudioClip loseSound;

    private static final String BG       = "#1a1a2e";
    private static final String SURFACE  = "#16213e";
    private static final String ACCENT   = "#e94560";
    private static final String ACCENT_D = "#c73652";
    private static final String BTN_DARK = "#2a2a45";
    private static final String BTN_HOVER= "#38385e";

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
        this.primaryStage = stage;

        correctSound = loadAudio("correct.wav");
        wrongSound = loadAudio("wrong.wav");
        winSound = loadAudio("win.wav");
        loseSound = loadAudio("lose.wav");

        categories.put("Programming", new String[]{"JAVA", "CLASS", "OBJECT"});
        categories.put("Animals", new String[]{"DOG", "CAT", "ELEPHANT"});
        categories.put("Fruits", new String[]{"APPLE", "MANGO", "BANANA"});

        stage.setTitle("Hangman Pro");
        showMainMenu();
        stage.show();
    }

    // ─── MAIN MENU ───────────────────────────────────────────────────────────

    private void showMainMenu() {
        Label titleLabel = new Label("HANGMAN");
        titleLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 60));
        titleLabel.setTextFill(Color.WHITE);

        Label subtitleLabel = new Label("— The Word Guessing Game —");
        subtitleLabel.setFont(Font.font("Arial", 15));
        subtitleLabel.setTextFill(Color.web("#888888"));

        Pane decoHangman = makeDecoHangman();

        Button startBtn = new Button("START");
        startBtn.setPrefWidth(180);
        startBtn.setPrefHeight(48);
        startBtn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        startBtn.setStyle(accentBtn());
        startBtn.setOnMouseEntered(e -> startBtn.setStyle(accentBtnHover()));
        startBtn.setOnMouseExited(e -> startBtn.setStyle(accentBtn()));
        startBtn.setOnAction(e -> showCategorySelection());

        VBox content = new VBox(16, decoHangman, titleLabel, subtitleLabel, startBtn);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));

        StackPane root = new StackPane(content);
        root.setStyle("-fx-background-color: " + BG + ";");

        primaryStage.setScene(new Scene(root, 700, 500));
    }

    private Pane makeDecoHangman() {
        Pane pane = new Pane();
        pane.setPrefSize(130, 100);

        Line base = new Line(10, 95, 120, 95);
        Line pole = new Line(35, 95, 35, 5);
        Line top  = new Line(35, 5, 90, 5);
        Line rope = new Line(90, 5, 90, 25);

        for (Line l : new Line[]{base, pole, top, rope}) {
            l.setStroke(Color.web(ACCENT));
            l.setStrokeWidth(2.5);
        }

        Circle head = new Circle(90, 40, 15);
        head.setStroke(Color.web("#cccccc"));
        head.setStrokeWidth(2.5);
        head.setFill(Color.TRANSPARENT);

        Line body     = new Line(90, 55, 90, 75);
        Line leftArm  = new Line(90, 60, 65, 72);
        Line rightArm = new Line(90, 60, 115, 72);
        Line leftLeg  = new Line(90, 75, 65, 92);
        Line rightLeg = new Line(90, 75, 115, 92);

        for (Line l : new Line[]{body, leftArm, rightArm, leftLeg, rightLeg}) {
            l.setStroke(Color.web("#cccccc"));
            l.setStrokeWidth(2.5);
        }

        pane.getChildren().addAll(base, pole, top, rope, head, body, leftArm, rightArm, leftLeg, rightLeg);
        return pane;
    }

    // ─── CATEGORY SELECTION ──────────────────────────────────────────────────

    private void showCategorySelection() {
        Label titleLabel = new Label("Choose a Category");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.WHITE);

        Label hint = new Label("Select a word category to play");
        hint.setFont(Font.font("Arial", 13));
        hint.setTextFill(Color.web("#888888"));

        ComboBox<String> categoryDropdown = new ComboBox<>();
        categoryDropdown.getItems().addAll("Programming", "Animals", "Fruits");
        categoryDropdown.setValue("Programming");
        categoryDropdown.setPrefWidth(260);
        categoryDropdown.setPrefHeight(42);
        categoryDropdown.setStyle(
            "-fx-font-size: 14;" +
            "-fx-font-family: Arial;" +
            "-fx-background-color: " + SURFACE + ";" +
            "-fx-text-fill: white;" +
            "-fx-border-color: " + ACCENT + ";" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;"
        );

        Button nextBtn = new Button("Next  →");
        nextBtn.setPrefWidth(180);
        nextBtn.setPrefHeight(44);
        nextBtn.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        nextBtn.setStyle(accentBtn());
        nextBtn.setOnMouseEntered(e -> nextBtn.setStyle(accentBtnHover()));
        nextBtn.setOnMouseExited(e -> nextBtn.setStyle(accentBtn()));
        nextBtn.setOnAction(e -> {
            selectedCategory = categoryDropdown.getValue();
            showPlayerSetup();
        });

        Button backBtn = makeBackButton();
        backBtn.setOnAction(e -> showMainMenu());

        VBox card = new VBox(18, titleLabel, hint, categoryDropdown, nextBtn, backBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36));
        card.setMaxWidth(380);
        card.setStyle(
            "-fx-background-color: " + SURFACE + ";" +
            "-fx-background-radius: 12;"
        );

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: " + BG + ";");

        primaryStage.setScene(new Scene(root, 700, 420));
    }

    // ─── PLAYER SETUP ────────────────────────────────────────────────────────

    private void showPlayerSetup() {
        playerNames.clear();

        Label titleLabel = new Label("Player Setup");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.WHITE);

        Label categoryLabel = new Label("Category: " + selectedCategory);
        categoryLabel.setFont(Font.font("Arial", 13));
        categoryLabel.setTextFill(Color.web("#888888"));

        TextField nameField = new TextField();
        nameField.setPromptText("Enter player name...");
        nameField.setPrefWidth(210);
        nameField.setPrefHeight(40);
        nameField.setStyle(
            "-fx-background-color: " + BG + ";" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: #555;" +
            "-fx-border-color: " + ACCENT + ";" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-font-size: 13;"
        );

        Button addBtn = new Button("Add");
        addBtn.setPrefHeight(40);
        addBtn.setPrefWidth(72);
        addBtn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        addBtn.setStyle(accentBtn());
        addBtn.setOnMouseEntered(e -> addBtn.setStyle(accentBtnHover()));
        addBtn.setOnMouseExited(e -> addBtn.setStyle(accentBtn()));

        HBox inputRow = new HBox(8, nameField, addBtn);
        inputRow.setAlignment(Pos.CENTER);

        VBox playerList = new VBox(6);
        playerList.setAlignment(Pos.CENTER);
        playerList.setPrefHeight(60);

        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.web("#ff7070"));
        errorLabel.setFont(Font.font("Arial", 12));

        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                errorLabel.setText("Please enter a name.");
                return;
            }
            if (playerNames.size() >= 2) {
                errorLabel.setText("Maximum 2 players allowed.");
                return;
            }
            if (playerNames.contains(name)) {
                errorLabel.setText("That name is already added.");
                return;
            }
            playerNames.add(name);
            nameField.clear();
            errorLabel.setText("");

            Label tag = new Label("P" + playerNames.size() + "  " + name);
            tag.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            tag.setTextFill(Color.web("#66cc88"));
            playerList.getChildren().add(tag);
        });

        nameField.setOnAction(e -> addBtn.fire());

        Button proceedBtn = new Button("PROCEED");
        proceedBtn.setPrefWidth(180);
        proceedBtn.setPrefHeight(46);
        proceedBtn.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        proceedBtn.setStyle(accentBtn());
        proceedBtn.setOnMouseEntered(e -> proceedBtn.setStyle(accentBtnHover()));
        proceedBtn.setOnMouseExited(e -> proceedBtn.setStyle(accentBtn()));
        proceedBtn.setOnAction(e -> {
            if (playerNames.isEmpty()) {
                errorLabel.setText("Add at least one player to proceed.");
                return;
            }
            launchGame();
        });

        Button backBtn = makeBackButton();
        backBtn.setOnAction(e -> showCategorySelection());

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: #333355;");

        VBox card = new VBox(14, titleLabel, categoryLabel, inputRow, errorLabel, playerList, sep, proceedBtn, backBtn);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(36));
        card.setMaxWidth(400);
        card.setStyle(
            "-fx-background-color: " + SURFACE + ";" +
            "-fx-background-radius: 12;"
        );

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color: " + BG + ";");

        primaryStage.setScene(new Scene(root, 700, 520));
    }

    // ─── GAME SCREEN ─────────────────────────────────────────────────────────

    private void launchGame() {
        currentPlayerIndex = 0;
        playerScores = new int[playerNames.size()];

        currentPlayerLabel = new Label();
        currentPlayerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        currentPlayerLabel.setTextFill(Color.web(ACCENT));

        wordBox = new HBox(10);
        wordBox.setAlignment(Pos.CENTER);
        wordBox.setPadding(new Insets(6, 0, 6, 0));

        livesLabel = new Label();
        livesLabel.setFont(Font.font("Arial", 15));
        livesLabel.setTextFill(Color.web("#cccccc"));

        Label categoryLabel = new Label("Category: " + selectedCategory);
        categoryLabel.setFont(Font.font("Arial", 12));
        categoryLabel.setTextFill(Color.web("#666688"));

        VBox topBox = new VBox(4, categoryLabel, currentPlayerLabel, wordBox, livesLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(12, 10, 8, 10));
        topBox.setStyle("-fx-background-color: " + SURFACE + ";");

        playerScoreLabels = new Label[playerNames.size()];
        VBox scoresPanel = buildScoresPanel();

        drawingPane = new Pane();
        drawingPane.setPrefSize(200, 250);
        drawingPane.setStyle("-fx-background-color: transparent;");

        lettersPane = new GridPane();
        lettersPane.setHgap(6);
        lettersPane.setVgap(6);
        lettersPane.setAlignment(Pos.CENTER);
        lettersPane.setPadding(new Insets(10));

        Button resetBtn = new Button("Restart");
        resetBtn.setPrefWidth(110);
        resetBtn.setPrefHeight(36);
        resetBtn.setFont(Font.font("Arial", 13));
        resetBtn.setStyle(ghostBtn());
        resetBtn.setOnMouseEntered(e -> resetBtn.setStyle(ghostBtnHover()));
        resetBtn.setOnMouseExited(e -> resetBtn.setStyle(ghostBtn()));
        resetBtn.setOnAction(e -> {
            currentPlayerIndex = 0;
            playerScores = new int[playerNames.size()];
            startGame();
        });

        Button menuBtn = new Button("Main Menu");
        menuBtn.setPrefWidth(110);
        menuBtn.setPrefHeight(36);
        menuBtn.setFont(Font.font("Arial", 13));
        menuBtn.setStyle(ghostBtn());
        menuBtn.setOnMouseEntered(e -> menuBtn.setStyle(ghostBtnHover()));
        menuBtn.setOnMouseExited(e -> menuBtn.setStyle(ghostBtn()));
        menuBtn.setOnAction(e -> showMainMenu());

        HBox bottomBar = new HBox(10, resetBtn, menuBtn);
        bottomBar.setAlignment(Pos.CENTER);
        bottomBar.setPadding(new Insets(8));
        bottomBar.setStyle("-fx-background-color: " + SURFACE + ";");

        BorderPane root = new BorderPane();
        root.setTop(topBox);
        root.setLeft(scoresPanel);
        root.setCenter(lettersPane);
        root.setRight(drawingPane);
        root.setBottom(bottomBar);
        root.setStyle("-fx-background-color: " + BG + ";");

        startGame();

        primaryStage.setScene(new Scene(root, 780, 500));
    }

    private VBox buildScoresPanel() {
        Label title = new Label("SCORES");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        title.setTextFill(Color.web("#666688"));

        VBox panel = new VBox(12, title);
        panel.setPadding(new Insets(14, 16, 14, 16));
        panel.setAlignment(Pos.TOP_CENTER);
        panel.setStyle("-fx-background-color: " + SURFACE + ";");
        panel.setPrefWidth(130);

        for (int i = 0; i < playerNames.size(); i++) {
            playerScoreLabels[i] = new Label();
            playerScoreLabels[i].setWrapText(true);
            playerScoreLabels[i].setFont(Font.font("Arial", 13));
            panel.getChildren().add(playerScoreLabels[i]);
        }

        return panel;
    }

    private void refreshScoresPanel() {
        for (int i = 0; i < playerNames.size(); i++) {
            playerScoreLabels[i].setText("P" + (i + 1) + " " + playerNames.get(i) + "\n" + playerScores[i] + " pts");
            if (i == currentPlayerIndex) {
                playerScoreLabels[i].setStyle("-fx-font-weight: bold; -fx-text-fill: " + ACCENT + ";");
            } else {
                playerScoreLabels[i].setStyle("-fx-text-fill: #555577;");
            }
        }
    }

    private void startGame() {
        lettersPane.getChildren().clear();
        drawingPane.getChildren().clear();
        parts.clear();

        drawGallows();

        lives = 6;
        livesLabel.setText(heartsDisplay(lives));

        String currentName = playerNames.get(currentPlayerIndex);
        currentPlayerLabel.setText("► " + currentName + "'s Turn");

        refreshScoresPanel();

        String[] words = categories.get(selectedCategory);
        word = words[new Random().nextInt(words.length)];

        guessed = new char[word.length()];
        Arrays.fill(guessed, '_');

        updateWord();

        int row = 0, col = 0;
        for (char c = 'A'; c <= 'Z'; c++) {
            Button btn = new Button(String.valueOf(c));
            btn.setPrefWidth(42);
            btn.setPrefHeight(36);
            btn.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            btn.setStyle(letterBtn());
            btn.setOnMouseEntered(e -> { if (!btn.isDisable()) btn.setStyle(letterBtnHover()); });
            btn.setOnMouseExited(e ->  { if (!btn.isDisable()) btn.setStyle(letterBtn()); });

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
            btn.setStyle("-fx-background-color: #2d6a3f; -fx-text-fill: #aaffbb; -fx-background-radius: 6;");
            if (correctSound != null) correctSound.play();
        } else {
            btn.setStyle("-fx-background-color: #6a2d2d; -fx-text-fill: #ffaaaa; -fx-background-radius: 6;");
            lives--;
            livesLabel.setText(heartsDisplay(lives));
            if (wrongSound != null) wrongSound.play();
            drawNextPart();
        }

        btn.setDisable(true);
        updateWord();
        checkGame();
    }

    private void updateWord() {
        wordBox.getChildren().clear();
        for (char c : guessed) {
            wordBox.getChildren().add(makeTile(c));
        }
    }

    private StackPane makeTile(char c) {
        boolean revealed = (c != '_');

        Label letter = new Label(revealed ? String.valueOf(c) : "");
        letter.setFont(Font.font("Courier New", FontWeight.BOLD, 30));
        letter.setTextFill(Color.WHITE);

        StackPane tile = new StackPane(letter);
        tile.setPrefWidth(46);
        tile.setPrefHeight(52);
        tile.setAlignment(Pos.BOTTOM_CENTER);

        String lineColor = revealed ? "#e94560" : "#555577";
        tile.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: transparent transparent " + lineColor + " transparent;" +
            "-fx-border-width: 0 0 2 0;"
        );

        return tile;
    }

    private void checkGame() {
        String currentName = playerNames.get(currentPlayerIndex);

        if (String.valueOf(guessed).equals(word)) {
            int earned = lives * 10;
            playerScores[currentPlayerIndex] += earned;
            if (winSound != null) winSound.play();
            showAlert(currentName + " guessed it! +" + earned + " points");
            currentPlayerIndex = (currentPlayerIndex + 1) % playerNames.size();
            startGame();
        } else if (lives == 0) {
            if (loseSound != null) loseSound.play();
            showAlert(currentName + " ran out of lives! Word was: " + word);
            currentPlayerIndex = (currentPlayerIndex + 1) % playerNames.size();
            startGame();
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    // ─── DRAWING + ANIMATION ─────────────────────────────────────────────────

    private void drawGallows() {
        Line base  = new Line(20, 230, 180, 230);
        Line pole  = new Line(50, 230, 50, 20);
        Line cross = new Line(50, 20, 130, 20);
        Line rope  = new Line(130, 20, 130, 50);

        for (Line l : new Line[]{base, pole, cross, rope}) {
            l.setStroke(Color.web("#888899"));
            l.setStrokeWidth(2.5);
        }

        drawingPane.getChildren().addAll(base, pole, cross, rope);
    }

    private void drawNextPart() {
        javafx.scene.Node part = null;

        switch (parts.size()) {
            case 0:
                Circle head = new Circle(130, 70, 20);
                head.setFill(Color.TRANSPARENT);
                head.setStroke(Color.web("#dddddd"));
                head.setStrokeWidth(2.5);
                part = head;
                break;
            case 1: part = styledLine(130, 90, 130, 150); break;
            case 2: part = styledLine(130, 110, 100, 130); break;
            case 3: part = styledLine(130, 110, 160, 130); break;
            case 4: part = styledLine(130, 150, 100, 190); break;
            case 5: part = styledLine(130, 150, 160, 190); break;
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

    private Line styledLine(double x1, double y1, double x2, double y2) {
        Line l = new Line(x1, y1, x2, y2);
        l.setStroke(Color.web("#dddddd"));
        l.setStrokeWidth(2.5);
        return l;
    }

    // ─── STYLE HELPERS ───────────────────────────────────────────────────────

    private String accentBtn() {
        return "-fx-background-color: " + ACCENT + "; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;";
    }

    private String accentBtnHover() {
        return "-fx-background-color: " + ACCENT_D + "; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;";
    }

    private String ghostBtn() {
        return "-fx-background-color: transparent; -fx-text-fill: #aaaaaa; -fx-border-color: #444466; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;";
    }

    private String ghostBtnHover() {
        return "-fx-background-color: #2a2a45; -fx-text-fill: white; -fx-border-color: #666688; -fx-border-radius: 6; -fx-background-radius: 6; -fx-cursor: hand;";
    }

    private String letterBtn() {
        return "-fx-background-color: " + BTN_DARK + "; -fx-text-fill: #dddddd; -fx-background-radius: 6; -fx-cursor: hand;";
    }

    private String letterBtnHover() {
        return "-fx-background-color: " + BTN_HOVER + "; -fx-text-fill: white; -fx-background-radius: 6; -fx-cursor: hand;";
    }

    private String heartsDisplay(int remaining) {
        String filled = "♥ ".repeat(remaining).trim();
        String empty  = "♡ ".repeat(6 - remaining).trim();
        return (filled + (remaining > 0 && remaining < 6 ? " " : "") + empty).trim();
    }

    private Button makeBackButton() {
        Button backBtn = new Button("← Back");
        backBtn.setPrefWidth(180);
        backBtn.setPrefHeight(38);
        backBtn.setFont(Font.font("Arial", 13));
        backBtn.setStyle(ghostBtn());
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(ghostBtnHover()));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(ghostBtn()));
        return backBtn;
    }

    public static void main(String[] args) {
        launch();
    }
}
