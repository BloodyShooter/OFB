
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Yagorka
 */
public class MyApp extends Application{

    cryptographerOFB cryptographerOFB;

    private Label lblStatus;
    private FileChooser fileChooser;
    private TextInputDialog dialogPassword;
    private TextField txtEncrypt;
    private TextField txtDecrypt;

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

        Button btnEncrypt = new Button();
        btnEncrypt.setText("Encrypt");
        btnEncrypt.setOnAction((ActionEvent event) -> {
            encrypt(txtEncrypt);
        });
        btnEncrypt.setTooltip(new Tooltip("Run"));

        Button btnDecrypt = new Button();
        btnDecrypt.setText("Decrypt");
        btnDecrypt.setOnAction((ActionEvent event) -> {
            decrypt(txtDecrypt);
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
        myStage.setScene(new Scene(root, 300, 180));
        myStage.setResizable(false);
        myStage.show();
    }

    private void FileChooserMethod(Stage myStage, TextField text, String textMsg) {
        fileChooser.setTitle(textMsg);
        File file = fileChooser.showOpenDialog(myStage);
        if (file != null) {
            text.setText(file.getAbsolutePath());
        }
    }

    private void encrypt(TextField text) {
        if (text.getText().equals("")) {
            lblStatus.setText("Шиврование: Введите имя файла");
            return;
        }
        try {
            Optional<String> password = getPasswordFromDialog("Шифрование");
            cryptographerOFB.encrypt(text.getText(), password.get());
            lblStatus.setText("Шиврование: Готово");
        } catch(FileNotFoundException ex) {
            lblStatus.setText("Шиврование: файл не найден");
        } catch(Exception ex) {
            lblStatus.setText("Шиврование: что-то пошло не так");
            ex.printStackTrace();
        }
    }

    private void decrypt(TextField text) {
        if (text.getText().equals("")) {
            lblStatus.setText("Расшивровка: Введите имя файла");
            return;
        }
        try {
            Optional<String> password = getPasswordFromDialog("Расшифровка");
            cryptographerOFB.decrypt(text.getText(), password.get());
            lblStatus.setText("Расшивровка: Готово");
        } catch (FileNotFoundException | KeyException ex) {
            lblStatus.setText("Расшивровка: " + ex.getMessage());
        } catch (Exception ex) {
            lblStatus.setText("Расшивровка: что-то пошло не так ");
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
        cryptographerOFB = new cryptographerOFB();
    }
}
