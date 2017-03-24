
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Yagorka
 */
public class MyApp extends Application{

    Ofb ofb;

    public static void launchMyApp(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage myStage) throws Exception {
        myStage.setTitle("OFB");
        FileChooser fileChooser = new FileChooser();

        Label label = new Label("MyApp");
        label.setTooltip(new Tooltip("Активный статус"));
        Label lblShifr = new Label("Шифрование");
        Label lblDefifr = new Label("Дешифрование");
        TextField text1 = new TextField();
        text1.setPromptText("Введите имя файла");
        text1.setTooltip(new Tooltip("Введите файл для шифрования"));
        TextField text2 = new TextField();
        text2.setPromptText("Введите имя файла");
        text2.setTooltip(new Tooltip("Введите файл для дешифрования"));

        Button btnCh1 = new Button("Open...");
        btnCh1.setOnAction((ActionEvent event) -> {
            FileChooserMethod(myStage, fileChooser, text1, "Открытие файла");
        });
        btnCh1.setTooltip(new Tooltip("Проводник выбора файлов"));

        Button btnCh2 = new Button();
        btnCh2.setText("Open...");
        btnCh2.setOnAction((ActionEvent event) -> {
            FileChooserMethod(myStage, fileChooser, text2, "Открыте зашифрованного файла");
        });
        btnCh2.setTooltip(new Tooltip("Проводник выбора файлов"));

        Button btnEnc = new Button();
        btnEnc.setText("Encrypt");
        btnEnc.setOnAction((ActionEvent event) -> {
            encrypt(text1, label);
        });
        btnEnc.setTooltip(new Tooltip("Run"));

        Button btnDec = new Button();
        btnDec.setText("Decrypt");
        btnDec.setOnAction((ActionEvent event) -> {
            decrypt(text2, label);
        });
        btnDec.setTooltip(new Tooltip("Run"));

        Separator separator1 = new Separator();
        Separator separator2 = new Separator();
        separator1.setPrefWidth(290);
        separator2.setPrefWidth(290);

        FlowPane root = new FlowPane(10, 10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(
                btnCh1,
                text1,
                btnEnc,
                lblShifr,
                separator1,
                btnCh2,
                text2,
                btnDec,
                lblDefifr,
                separator2,
                label);
        myStage.setScene(new Scene(root, 300, 180));
        myStage.setResizable(false);
        myStage.show();
    }

    private void FileChooserMethod(Stage myStage, FileChooser fileChooser, TextField text, String textmsg) {
        fileChooser.setTitle(textmsg);
        File file = fileChooser.showOpenDialog(myStage);
        if (file != null) {
            text.setText(file.getAbsolutePath());
        }
    }

    private void encrypt(TextField text, Label label) {
        if (text.getText().equals("")) {
            label.setText("Шиврование: Введите имя файла");
            return;
        }
        try {
            ofb.encrypt(text.getText());
            System.out.println("Done");
            label.setText("Шиврование: Готово");
        } catch(FileNotFoundException ex) {
            label.setText("Шиврование: файл не найден");
        } catch(Exception ex) {
            label.setText("Шиврование: что-то пошло не так");
            ex.printStackTrace();
        }
    }

    private void decrypt(TextField text, Label label) {
        if (text.getText().equals("")) {
            label.setText("Расшивровка: Введите имя файла");
            return;
        }
        try {
            ofb.decrypt(text.getText());
            System.out.println("Done");
            label.setText("Расшивровка: Готово");
        } catch (FileNotFoundException | KeyException ex) {
            label.setText("Расшивровка: " + ex.getMessage());
        } catch (Exception ex) {
            label.setText("Расшивровка: что-то пошло не так ");
        }
    }

    @Override
    public void init() throws Exception {
        super.init();
        ofb = new Ofb();
    }
}
