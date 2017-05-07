package org.gvozdetscky.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.gvozdetscky.exception.KeyException;
import org.gvozdetscky.logic.CryptographerOFB;

public class MyApp extends Application{

    private static final int WIDTH = 300;
    private static final int HEIGHT = 340;

    private CryptographerOFB cryptographerOFB;
    private Boolean isStatusArch = true;
    private Boolean isStatusBase64 = false;
    private Boolean isStatusWithKey = true;

    private Label lblStatus;
    private FileChooser fileChooser;
    private TextInputDialog dialogPassword;
    private TextField txtEncrypt;
    private TextField txtDecrypt;
    private Button btnEncrypt;
    private Button btnDecrypt;
    private CheckMenuItem arch;
    private CheckMenuItem base64;
    private CheckMenuItem withKey;
    private ToggleButton tgArch;
    private ToggleButton tgBase64;
    private ToggleButton tgWithKey;

    public static void launchMyApp(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage myStage) throws Exception {
        myStage.setTitle("OFB");

        BorderPane rootNode = new BorderPane();
        rootNode.setCenter(initialWindow(myStage));

        myStage.setScene(new Scene(rootNode, WIDTH, HEIGHT));
        myStage.setResizable(false);

        rootNode.setTop(getMenu());

        myStage.show();
    }

    private Node initialWindow(Stage myStage) {
        fileChooser = new FileChooser();
        dialogPassword = new TextInputDialog("password");

        lblStatus = new Label("org.gvozdetscky.view.MyApp");
        lblStatus.setTooltip(new Tooltip("Активный статус"));
        Label lblEncrypt = new Label("Шифрование");
        Label lblDecrypt = new Label("Дешифрование");
        txtEncrypt = new TextField();
        txtEncrypt.setPromptText("Введите имя файла");
        txtEncrypt.setTooltip(new Tooltip("Введите файл для шифрования"));
        txtDecrypt = new TextField();
        txtDecrypt.setPromptText("Введите имя файла");
        txtDecrypt.setTooltip(new Tooltip("Введите файл для дешифрования"));

        Button btnChooserEncFile = new Button("Open...");
        btnChooserEncFile.setOnAction((ActionEvent event) ->
                FileChooserMethod(myStage, txtEncrypt, "Открытие файла"));
        btnChooserEncFile.setTooltip(new Tooltip("Проводник выбора файлов"));

        Button btnChooserDecFile = new Button();
        btnChooserDecFile.setText("Open...");
        btnChooserDecFile.setOnAction((ActionEvent event) ->
                FileChooserMethod(myStage, txtDecrypt, "Открыте зашифрованного файла"));
        btnChooserDecFile.setTooltip(new Tooltip("Проводник выбора файлов"));

        btnEncrypt = new Button();
        btnEncrypt.setText("Encrypt");
        btnEncrypt.setOnAction((ActionEvent event) -> encrypt());
        btnEncrypt.setTooltip(new Tooltip("Run"));

        btnDecrypt = new Button();
        btnDecrypt.setText("Decrypt");
        btnDecrypt.setOnAction((ActionEvent event) -> decrypt());
        btnDecrypt.setTooltip(new Tooltip("Run"));

        tgArch = new RadioButton("Архивация");
        tgArch.setSelected(isStatusArch);
        tgArch.setOnAction(event -> {
            isStatusArch = !isStatusArch;
            arch.setSelected(isStatusArch);
        });
        tgArch.setTooltip(new Tooltip("Использовать архивацию при работе программы"));
        tgBase64 = new RadioButton("Тр. кодирование");
        tgBase64.setSelected(isStatusBase64);
        tgBase64.setOnAction(event -> {
            isStatusBase64 = !isStatusBase64;
            base64.setSelected(isStatusBase64);
        });
        tgBase64.setTooltip(new Tooltip("Использовать транспортное кодирование при работе программы"));
        tgWithKey = new RadioButton("Ипользовать ключ сеанса");
        tgWithKey.setSelected(isStatusWithKey);
        tgWithKey.setOnAction(event -> {
            isStatusWithKey = !isStatusWithKey;
            withKey.setSelected(isStatusWithKey);
        });
        tgWithKey.setTooltip(new Tooltip("Ипользовать ключ сеанса"));

        Separator separator1 = new Separator();
        Separator separator2 = new Separator();
        Separator separator3 = new Separator();
        separator1.setPrefWidth(290);
        separator2.setPrefWidth(290);
        separator3.setPrefWidth(290);

        VBox lvl3 = new VBox(10);
        lvl3.setAlignment(Pos.BASELINE_LEFT);
        lvl3.getChildren().addAll(separator2, tgArch, tgBase64, tgWithKey);

        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(lvl3, new Label("Настройки"));

        FlowPane root = new FlowPane(10, 10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(
                btnChooserEncFile,
                txtEncrypt,
                btnEncrypt,
                lblEncrypt,
                separator1,
                btnChooserDecFile,
                txtDecrypt,
                btnDecrypt,
                lblDecrypt,
                container,
                separator3,
                lblStatus);
        return root;
    }

    private Node getMenu() {
        MenuBar menuBar = new MenuBar();

        Menu file = new Menu("File");

        MenuItem exit = new MenuItem("Выход");
        exit.setOnAction(event -> Platform.exit());

        arch = new CheckMenuItem("Архивация");
        base64 = new CheckMenuItem("Трансп кодирование");
        withKey = new CheckMenuItem("Ключ сеанса");
        arch.setSelected(isStatusArch);
        base64.setSelected(isStatusBase64);
        withKey.setSelected(isStatusWithKey);
        arch.setOnAction(event -> {
            isStatusArch = !isStatusArch;
            tgArch.setSelected(isStatusArch);
        });
        base64.setOnAction(event -> {
            isStatusBase64 = !isStatusBase64;
            tgBase64.setSelected(isStatusBase64);
        });
        withKey.setOnAction(event -> {
            isStatusWithKey = !isStatusWithKey;
            tgWithKey.setSelected(isStatusWithKey);
        });

        file.getItems().addAll(arch, base64, withKey, new SeparatorMenuItem(), exit);

        menuBar.getMenus().add(file);

        return menuBar;
    }

    private void FileChooserMethod(Stage myStage, TextField text, String textMsg) {
        fileChooser.setTitle(textMsg);
        File file = fileChooser.showOpenDialog(myStage);
        if (file != null) {
            fileChooser.setInitialDirectory(file.getParentFile());
            text.setText(file.getAbsolutePath());
        }
    }

    private void encrypt() {
        if (txtEncrypt.getText().equals("")) {
            lblStatus.setText("Шиврование: Введите имя файла");
            return;
        }
        runEncrypt();
    }

    private void runEncrypt() {
        if (isStatusWithKey) {
            lblStatus.setText("Шиврование: в процессе");
            lockActionButton();
            Task newTask = new Task() {
                @Override
                protected Object call() throws Exception {
                    try {
                        cryptographerOFB.encryptInParts(txtEncrypt.getText());
                    } catch (FileNotFoundException e) {
                        setStatusText("Шифрование: файл не найден");
                    } catch (Exception e) {
                        setStatusText("Шифрование: что то пошло не так");
                    }
                    unlockActionButton();
                    setStatusText("Шифрование: готова");
                    return null;
                }
            };
            new Thread(newTask).start();
        } else {
            Optional<String> password = getPasswordFromDialog("Шифрование");
            lblStatus.setText("Шиврование: в процессе");
            lockActionButton();
            Task newTask = new Task() {
                @Override
                protected Object call() throws Exception {
                    try {
                        if (password.isPresent()) {
                            cryptographerOFB.encrypt(txtEncrypt.getText(), password.get(), isStatusArch, isStatusBase64);
                        }
                    } catch (FileNotFoundException e) {
                        setStatusText("Шифрование: файл не найден");
                    } catch (Exception e) {
                        setStatusText("Шифрование: что то пошло не так");
                    }
                    unlockActionButton();
                    setStatusText("Шифрование: готова");
                    return null;
                }
            };
            new Thread(newTask).start();
        }
    }

    private void decrypt() {
        if (txtDecrypt.getText().equals("")) {
            lblStatus.setText("Расшивровка: Введите имя файла");
            return;
        }
        runDecrypt();
    }

    private void runDecrypt() {
        if (isStatusWithKey) {
            lblStatus.setText("Расшифровка: в процессе");
            lockActionButton();
            Task newTask = new Task() {
                @Override
                protected Object call() throws Exception {
                    try {
                        cryptographerOFB.decryptInParts(txtDecrypt.getText());
                    } catch (FileNotFoundException | KeyException ex) {
                        setStatusText("Расшифровка: файл не найден");
                    } catch (Exception e) {
                        setStatusText("Расшифровка: что то пошло не так");
                    }
                    unlockActionButton();
                    setStatusText("Расшифровка: готова");
                    return null;
                }
            };
            new Thread(newTask).start();
        } else {
            Optional<String> password = getPasswordFromDialog("Расшифровка");
            lblStatus.setText("Расшифровка: в процессе");
            lockActionButton();
            Task newTask = new Task() {
                @Override
                protected Object call() throws Exception {
                    try {
                        if (password.isPresent()) {
                            cryptographerOFB.decrypt(txtDecrypt.getText(), password.get(), isStatusArch, isStatusBase64);
                        }
                    } catch (FileNotFoundException | KeyException ex) {
                        setStatusText("Расшифровка: файл не найден");
                    } catch (Exception e) {
                        setStatusText("Расшифровка: что то пошло не так");
                    }
                    unlockActionButton();
                    setStatusText("Расшифровка: готова");
                    return null;
                }
            };
            new Thread(newTask).start();
        }
    }

    private Optional<String> getPasswordFromDialog(String text) {
        dialogPassword.setTitle(text);
        dialogPassword.setHeaderText("Не говорите никому свой пароль");
        dialogPassword.setContentText("Введите пароль:");
        return dialogPassword.showAndWait();
    }

    @Override
    public void init() throws Exception {
        super.init();
        cryptographerOFB = new CryptographerOFB();
    }

    private void lockActionButton() {
        btnEncrypt.setDisable(true);
        btnDecrypt.setDisable(true);
    }

    private void unlockActionButton() {
        btnEncrypt.setDisable(false);
        btnDecrypt.setDisable(false);
    }

    private void setStatusText(String text) {
        Platform.runLater(() -> lblStatus.setText(text));
    }
}
