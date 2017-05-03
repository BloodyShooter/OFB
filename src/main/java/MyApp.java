
import java.io.Externalizable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Yagorka
 */
public class MyApp extends Application{

    private static final int WIDTH = 300;
    private static final int HEIGHT = 210;

    CryptographerOFB cryptographerOFB;

    private Label lblStatus;
    private FileChooser fileChooser;
    private TextInputDialog dialogPassword;
    private TextField txtEncrypt;
    private TextField txtDecrypt;
    private Button btnEncrypt;
    private Button btnDecrypt;
    private MenuBar menuBar;
    private CheckMenuItem arch;
    private CheckMenuItem base64;

    public static void launchMyApp(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage myStage) throws Exception {
        myStage.setTitle("OFB");
        fileChooser = new FileChooser();
        dialogPassword = new TextInputDialog("password");

        lblStatus = new Label("MyApp");
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
        btnChooserEncFile.setOnAction((ActionEvent event) -> {
            FileChooserMethod(myStage, txtEncrypt, "Открытие файла");
        });
        btnChooserEncFile.setTooltip(new Tooltip("Проводник выбора файлов"));

        Button btnChooserDecFile = new Button();
        btnChooserDecFile.setText("Open...");
        btnChooserDecFile.setOnAction((ActionEvent event) -> {
            FileChooserMethod(myStage, txtDecrypt, "Открыте зашифрованного файла");
        });
        btnChooserDecFile.setTooltip(new Tooltip("Проводник выбора файлов"));

        btnEncrypt = new Button();
        btnEncrypt.setText("Encrypt");
        btnEncrypt.setOnAction((ActionEvent event) -> {
            encrypt();
        });
        btnEncrypt.setTooltip(new Tooltip("Run"));

        btnDecrypt = new Button();
        btnDecrypt.setText("Decrypt");
        btnDecrypt.setOnAction((ActionEvent event) -> {
            decrypt();
        });
        btnDecrypt.setTooltip(new Tooltip("Run"));

        Separator separator1 = new Separator();
        Separator separator2 = new Separator();
        separator1.setPrefWidth(290);
        separator2.setPrefWidth(290);

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
                separator2,
                lblStatus);

        BorderPane rootNode = new BorderPane();
        rootNode.setCenter(root);

        myStage.setScene(new Scene(rootNode, WIDTH, HEIGHT));
        myStage.setResizable(false);

        rootNode.setTop(getMenu());

        myStage.show();
    }

    private Node getMenu() {
        menuBar = new MenuBar();

        Menu file = new Menu("File");

        MenuItem exit = new MenuItem("Выход");
        exit.setOnAction(event -> Platform.exit());

        arch = new CheckMenuItem("Архивация");
        base64 = new CheckMenuItem("Трансп кодирование");
        arch.setSelected(true);
        base64.setSelected(true);

        file.getItems().addAll(arch, base64, new SeparatorMenuItem(), exit);

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
        Optional<String> password = getPasswordFromDialog("Шифрование");
        lblStatus.setText("Шиврование: в процессе");
        lockActionButton();
        Task newTask = new Task() {
            @Override
            protected Object call() throws Exception {
                try {
                    if (password.isPresent()) {
                        cryptographerOFB.encrypt(txtEncrypt.getText(), password.get(), arch.isSelected(), base64.isSelected());
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

    private void decrypt() {
        if (txtDecrypt.getText().equals("")) {
            lblStatus.setText("Расшивровка: Введите имя файла");
            return;
        }
        runDecrypt();
    }

    private void runDecrypt() {
        Optional<String> password = getPasswordFromDialog("Расшифровка");
        lblStatus.setText("Расшифровка: в процессе");
        lockActionButton();
        Task newTask = new Task() {
            @Override
            protected Object call() throws Exception {
                try {
                    if (password.isPresent()) {
                        cryptographerOFB.decrypt(txtDecrypt.getText(), password.get(), arch.isSelected(), base64.isSelected());
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
