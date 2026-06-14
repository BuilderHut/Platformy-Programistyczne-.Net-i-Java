package pl.pwr;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main extends Application {

    private ImageView originalImageView;
    private ImageView resultImageView;

    private BufferedImage originalBufferedImage;
    private BufferedImage currentBufferedImage;

    private Button saveButton;
    private Button executeButton;
    private Button rotateLeftButton;
    private Button rotateRightButton;
    private Button scaleButton;

    private ComboBox<String> operationComboBox;

    private boolean operationPerformed = false;

    @Override
    public void start(Stage stage) {
        Label titleLabel = new Label("Image Editor - Laboratorium 6");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        InputStream logoStream = getClass().getResourceAsStream("/logo2.png");

        ImageView logoImageView = new ImageView();

        if (logoStream != null) {
            Image logoImage = new Image(logoStream);
            logoImageView.setImage(logoImage);
        } else {
            System.out.println("Nie znaleziono pliku logo2.png w resources!");
        }

        logoImageView.setFitWidth(140);
        logoImageView.setFitHeight(140);
        logoImageView.setPreserveRatio(true);
        logoImageView.setSmooth(true);

        StackPane logoPane = new StackPane(logoImageView);
        logoPane.setPrefSize(160, 160);
        logoPane.setMaxSize(160, 160);
        logoPane.setMinSize(160, 160);

        Label logoLabel = new Label("Politechnika Wrocławska");
        logoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox logoBox = new VBox(5, logoPane, logoLabel);
        logoBox.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Witaj w aplikacji do obróbki obrazów.");
        welcomeLabel.setStyle("-fx-font-size: 14px;");

        Button loadButton = new Button("Wczytaj obraz .jpg");
        saveButton = new Button("Zapisz obraz");
        executeButton = new Button("Wykonaj");

        rotateLeftButton = new Button("↶");
        rotateRightButton = new Button("↷");
        scaleButton = new Button("Skaluj");

        saveButton.setDisable(true);
        executeButton.setDisable(true);
        rotateLeftButton.setDisable(true);
        rotateRightButton.setDisable(true);
        scaleButton.setDisable(true);

        operationComboBox = new ComboBox<>();
        operationComboBox.setPromptText("Wybierz operację");
        operationComboBox.getItems().addAll("Negatyw", "Progowanie", "Konturowanie");
        operationComboBox.setValue(null);

        loadButton.setOnAction(e -> loadImage(stage));
        saveButton.setOnAction(e -> showSaveDialog());

        rotateLeftButton.setOnAction(e -> rotateImageLeft());
        rotateRightButton.setOnAction(e -> rotateImageRight());
        scaleButton.setOnAction(e -> showScaleDialog());

        executeButton.setOnAction(e -> {
            String operation = operationComboBox.getValue();

            if (operation == null) {
                showAlert(Alert.AlertType.WARNING, "Nie wybrano operacji do wykonania");
                return;
            }

            switch (operation) {
                case "Negatyw":
                    applyNegative();
                    break;
                case "Progowanie":
                    showThresholdDialog();
                    break;
                case "Konturowanie":
                    applyEdgeDetection();
                    break;
                default:
                    showAlert(Alert.AlertType.WARNING, "Nieznana operacja");
            }
        });

        HBox mainControls = new HBox(
                10,
                loadButton,
                saveButton,
                operationComboBox,
                executeButton
        );
        mainControls.setAlignment(Pos.CENTER);

        HBox extraControls = new HBox(
                10,
                rotateLeftButton,
                rotateRightButton,
                scaleButton
        );
        extraControls.setAlignment(Pos.CENTER);

        originalImageView = new ImageView();
        resultImageView = new ImageView();

        setupImageView(originalImageView);
        setupImageView(resultImageView);

        VBox originalBox = new VBox(10, new Label("Obraz oryginalny"), originalImageView);
        originalBox.setAlignment(Pos.CENTER);

        VBox resultBox = new VBox(10, new Label("Obraz po zmianach"), resultImageView);
        resultBox.setAlignment(Pos.CENTER);

        HBox imageContainer = new HBox(20, originalBox, resultBox);
        imageContainer.setAlignment(Pos.CENTER);

        Label footer = new Label("Autor: Adam Jachimowicz");
        footer.setStyle("-fx-font-size: 12px;");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox root = new VBox(
                12,
                titleLabel,
                logoBox,
                welcomeLabel,
                mainControls,
                extraControls,
                imageContainer,
                spacer,
                footer
        );

        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 1050, 800);

        stage.setTitle("Image Editor - PWr");
        stage.setScene(scene);
        stage.show();
    }

    private void setupImageView(ImageView imageView) {
        imageView.setFitWidth(400);
        imageView.setFitHeight(350);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-border-color: gray; -fx-border-width: 1px;");
    }
    
    private void loadImage(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz obraz JPG");

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Pliki JPG", "*.jpg")
        );

        File file = fileChooser.showOpenDialog(stage);

        if (file == null) {
            return;
        }

        if (!file.getName().toLowerCase().endsWith(".jpg")) {
            showAlert(Alert.AlertType.ERROR, "Niedozwolony format pliku");
            return;
        }

        try {
            originalBufferedImage = ImageIO.read(file);

            if (originalBufferedImage == null) {
                showAlert(Alert.AlertType.ERROR, "Nie udało się załadować pliku");
                return;
            }

            currentBufferedImage = deepCopy(originalBufferedImage);
            operationPerformed = false;

            originalImageView.setImage(SwingFXUtils.toFXImage(originalBufferedImage, null));
            resultImageView.setImage(SwingFXUtils.toFXImage(currentBufferedImage, null));

            saveButton.setDisable(false);
            executeButton.setDisable(false);
            rotateLeftButton.setDisable(false);
            rotateRightButton.setDisable(false);
            scaleButton.setDisable(false);

            operationComboBox.setValue(null);

            showAlert(Alert.AlertType.INFORMATION, "Pomyślnie załadowano plik");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Nie udało się załadować pliku");
        }
    }

    private void applyNegative() {
        try {
            BufferedImage result = deepCopy(currentBufferedImage);

            for (int y = 0; y < result.getHeight(); y++) {
                for (int x = 0; x < result.getWidth(); x++) {
                    int rgba = result.getRGB(x, y);

                    int alpha = (rgba >> 24) & 0xff;
                    int red = (rgba >> 16) & 0xff;
                    int green = (rgba >> 8) & 0xff;
                    int blue = rgba & 0xff;

                    red = 255 - red;
                    green = 255 - green;
                    blue = 255 - blue;

                    int negativePixel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                    result.setRGB(x, y, negativePixel);
                }
            }

            currentBufferedImage = result;
            operationPerformed = true;
            updateResultPreview();

            showAlert(Alert.AlertType.INFORMATION, "Negatyw został wygenerowany pomyślnie!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Nie udało się wykonać negatywu.");
        }
    }

    private void showThresholdDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Progowanie");

        Label title = new Label("Podaj próg z zakresu 0-255:");

        TextField thresholdField = new TextField();
        thresholdField.setPromptText("Próg");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button executeThresholdButton = new Button("Wykonaj progowanie");
        Button cancelButton = new Button("Anuluj");

        thresholdField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                thresholdField.setText(oldValue);
            }
        });

        cancelButton.setOnAction(e -> dialog.close());

        executeThresholdButton.setOnAction(e -> {
            String text = thresholdField.getText();

            if (text.isBlank()) {
                errorLabel.setText("Pole jest wymagane");
                return;
            }

            int threshold = Integer.parseInt(text);

            if (threshold < 0 || threshold > 255) {
                errorLabel.setText("Wartość musi być z zakresu 0-255");
                return;
            }

            applyThreshold(threshold);
            dialog.close();
        });

        HBox buttons = new HBox(10, executeThresholdButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, title, thresholdField, errorLabel, buttons);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        dialog.setScene(new Scene(root, 350, 200));
        dialog.showAndWait();
    }

    private void applyThreshold(int threshold) {
        try {
            BufferedImage result = deepCopy(currentBufferedImage);

            for (int y = 0; y < result.getHeight(); y++) {
                for (int x = 0; x < result.getWidth(); x++) {
                    int rgba = result.getRGB(x, y);

                    int alpha = (rgba >> 24) & 0xff;
                    int red = (rgba >> 16) & 0xff;
                    int green = (rgba >> 8) & 0xff;
                    int blue = rgba & 0xff;

                    int gray = (red + green + blue) / 3;
                    int newValue = gray >= threshold ? 255 : 0;

                    int thresholdPixel = (alpha << 24) | (newValue << 16) | (newValue << 8) | newValue;
                    result.setRGB(x, y, thresholdPixel);
                }
            }

            currentBufferedImage = result;
            operationPerformed = true;
            updateResultPreview();

            showAlert(Alert.AlertType.INFORMATION, "Progowanie zostało przeprowadzone pomyślnie!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Nie udało się wykonać progowania.");
        }
    }

    private void applyEdgeDetection() {
        try {
            BufferedImage source = currentBufferedImage;
            BufferedImage result = new BufferedImage(
                    source.getWidth(),
                    source.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            int[][] kernelX = {
                    {-1, 0, 1},
                    {-2, 0, 2},
                    {-1, 0, 1}
            };

            int[][] kernelY = {
                    {-1, -2, -1},
                    {0, 0, 0},
                    {1, 2, 1}
            };

            for (int y = 1; y < source.getHeight() - 1; y++) {
                for (int x = 1; x < source.getWidth() - 1; x++) {
                    int gx = 0;
                    int gy = 0;

                    for (int ky = -1; ky <= 1; ky++) {
                        for (int kx = -1; kx <= 1; kx++) {
                            int rgb = source.getRGB(x + kx, y + ky);

                            int red = (rgb >> 16) & 0xff;
                            int green = (rgb >> 8) & 0xff;
                            int blue = rgb & 0xff;

                            int gray = (red + green + blue) / 3;

                            gx += gray * kernelX[ky + 1][kx + 1];
                            gy += gray * kernelY[ky + 1][kx + 1];
                        }
                    }

                    int magnitude = Math.min(255, Math.abs(gx) + Math.abs(gy));
                    int edgePixel = (magnitude << 16) | (magnitude << 8) | magnitude;

                    result.setRGB(x, y, edgePixel);
                }
            }

            currentBufferedImage = result;
            operationPerformed = true;
            updateResultPreview();

            showAlert(Alert.AlertType.INFORMATION, "Konturowanie zostało przeprowadzone pomyślnie!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Nie udało się wykonać konturowania.");
        }
    }

    private void rotateImageLeft() {
        try {
            currentBufferedImage = rotateImage(currentBufferedImage, false);
            operationPerformed = true;
            updateResultPreview();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Nie udało się obrócić obrazu.");
        }
    }

    private void rotateImageRight() {
        try {
            currentBufferedImage = rotateImage(currentBufferedImage, true);
            operationPerformed = true;
            updateResultPreview();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Nie udało się obrócić obrazu.");
        }
    }

    private BufferedImage rotateImage(BufferedImage source, boolean right) {
        int width = source.getWidth();
        int height = source.getHeight();

        BufferedImage result = new BufferedImage(height, width, source.getType());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (right) {
                    result.setRGB(height - 1 - y, x, source.getRGB(x, y));
                } else {
                    result.setRGB(y, width - 1 - x, source.getRGB(x, y));
                }
            }
        }

        return result;
    }

    private void showScaleDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Skalowanie obrazu");

        Label title = new Label("Podaj nową szerokość i wysokość obrazu:");

        TextField widthField = new TextField();
        widthField.setPromptText("Szerokość");

        TextField heightField = new TextField();
        heightField.setPromptText("Wysokość");

        Label widthErrorLabel = new Label();
        widthErrorLabel.setStyle("-fx-text-fill: red;");

        Label heightErrorLabel = new Label();
        heightErrorLabel.setStyle("-fx-text-fill: red;");

        setupNumericField(widthField);
        setupNumericField(heightField);

        Button scaleDialogButton = new Button("Zmień rozmiar");
        Button restoreOriginalSizeButton = new Button("Przywróć oryginalne wymiary");
        Button cancelButton = new Button("Anuluj");

        cancelButton.setOnAction(e -> {
            widthField.clear();
            heightField.clear();
            dialog.close();
        });

        restoreOriginalSizeButton.setOnAction(e -> {
            int originalWidth = originalBufferedImage.getWidth();
            int originalHeight = originalBufferedImage.getHeight();

            currentBufferedImage = resizeImage(currentBufferedImage, originalWidth, originalHeight);
            operationPerformed = true;
            updateResultPreview();

            dialog.close();
        });

        scaleDialogButton.setOnAction(e -> {
            widthErrorLabel.setText("");
            heightErrorLabel.setText("");

            String widthText = widthField.getText();
            String heightText = heightField.getText();

            boolean valid = true;

            if (widthText.isBlank()) {
                widthErrorLabel.setText("Pole jest wymagane");
                valid = false;
            }

            if (heightText.isBlank()) {
                heightErrorLabel.setText("Pole jest wymagane");
                valid = false;
            }

            if (!valid) {
                return;
            }

            int width = Integer.parseInt(widthText);
            int height = Integer.parseInt(heightText);

            if (width <= 0 || width > 3000) {
                widthErrorLabel.setText("Wartość musi być z zakresu 1-3000");
                valid = false;
            }

            if (height <= 0 || height > 3000) {
                heightErrorLabel.setText("Wartość musi być z zakresu 1-3000");
                valid = false;
            }

            if (!valid) {
                return;
            }

            currentBufferedImage = resizeImage(currentBufferedImage, width, height);
            operationPerformed = true;
            updateResultPreview();

            dialog.close();
        });

        HBox fields = new HBox(10, widthField, heightField);
        fields.setAlignment(Pos.CENTER);

        HBox errors = new HBox(10, widthErrorLabel, heightErrorLabel);
        errors.setAlignment(Pos.CENTER);

        HBox buttons = new HBox(10, scaleDialogButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, title, fields, errors, restoreOriginalSizeButton, buttons);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        dialog.setScene(new Scene(root, 520, 230));
        dialog.showAndWait();
    }

    private void setupNumericField(TextField textField) {
        textField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(oldValue);
            }
        });
    }

    private BufferedImage resizeImage(BufferedImage source, int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics2D graphics = result.createGraphics();
        graphics.drawImage(source, 0, 0, width, height, null);
        graphics.dispose();

        return result;
    }

    private void updateResultPreview() {
        resultImageView.setImage(SwingFXUtils.toFXImage(currentBufferedImage, null));
    }

    private void showSaveDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Zapisz obraz");

        Label infoLabel = new Label("Na pliku nie zostały wykonane żadne operacje!");
        infoLabel.setStyle("-fx-text-fill: orange;");
        infoLabel.setVisible(!operationPerformed);

        TextField fileNameField = new TextField();
        fileNameField.setPromptText("Nazwa pliku");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button saveDialogButton = new Button("Zapisz");
        Button cancelButton = new Button("Anuluj");

        fileNameField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.length() > 100) {
                fileNameField.setText(oldValue);
            }
        });

        cancelButton.setOnAction(e -> {
            fileNameField.clear();
            dialog.close();
        });

        saveDialogButton.setOnAction(e -> {
            String fileName = fileNameField.getText();

            if (fileName.length() < 3) {
                errorLabel.setText("Wpisz co najmniej 3 znaki");
                return;
            }

            saveImage(fileName, dialog);
        });

        HBox buttons = new HBox(10, saveDialogButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);

        VBox root = new VBox(10, infoLabel, fileNameField, errorLabel, buttons);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        dialog.setScene(new Scene(root, 380, 210));
        dialog.showAndWait();
    }

    private void saveImage(String fileName, Stage dialog) {
        String finalFileName = fileName.toLowerCase().endsWith(".jpg")
                ? fileName
                : fileName + ".jpg";

        Path picturesPath = Path.of(
                System.getProperty("user.home"),
                "Pictures",
                finalFileName
        );

        if (Files.exists(picturesPath)) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Plik " + finalFileName + " już istnieje w systemie. Podaj inną nazwę pliku!"
            );
            return;
        }

        try {
            BufferedImage imageToSave = new BufferedImage(
                    currentBufferedImage.getWidth(),
                    currentBufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            Graphics2D graphics = imageToSave.createGraphics();
            graphics.drawImage(currentBufferedImage, 0, 0, null);
            graphics.dispose();

            ImageIO.write(imageToSave, "jpg", picturesPath.toFile());

            showAlert(Alert.AlertType.INFORMATION, "Zapisano obraz w pliku " + finalFileName);
            dialog.close();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Nie udało się zapisać pliku " + finalFileName);
        }
    }

    private BufferedImage deepCopy(BufferedImage source) {
        BufferedImage copy = new BufferedImage(
                source.getWidth(),
                source.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D graphics = copy.createGraphics();
        graphics.drawImage(source, 0, 0, null);
        graphics.dispose();

        return copy;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Komunikat");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}