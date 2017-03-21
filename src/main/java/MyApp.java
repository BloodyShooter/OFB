
import java.io.File;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author Yagorka
 */
public class MyApp extends Application{

    public static void launchMyApp(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage myStage) throws Exception {
        myStage.setTitle("OFB");
        //Image ico = new Image("resources\\images\\iconLogo.png");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открытие файла");
        
        Label label = new Label("MyApp");
        Label lblShifr = new Label("Шифрование");
        Label lblDefifr = new Label("Дешифрование");
        TextField text1 = new TextField();
        text1.setPromptText("Введите имя файла");
        TextField text2 = new TextField();
        text2.setPromptText("Введите имя файла");
        
        Button btnCh1 = new Button("Open...");
        btnCh1.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                FileChooserMethod(myStage, fileChooser, text1);
            }
        });
               
        Button btnCh2 = new Button();
        btnCh2.setText("Open...");
        btnCh2.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                FileChooserMethod(myStage, fileChooser, text2);
            }
        });
        
        Button btnEnc = new Button();
        btnEnc.setText("Encrypt");
        btnEnc.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                encrypt(text1, label);
            }
        });
        
        Button btnDec = new Button();
        btnDec.setText("Decrypt");
        btnDec.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                decrypt(text2, label);
            }
        });
        
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

    private void FileChooserMethod(Stage myStage, FileChooser fileChooser, TextField text) {
        File file = fileChooser.showOpenDialog(myStage);
        if (file != null) {
            text.setText(file.getAbsolutePath());
        }
    }

    private void encrypt(TextField text, Label label) {
        if (text.getText().equals("")) {
            label.setText("Введите имя файла");
            return;
        }
        try {
            new Ofb().encrypt(text.getText());
            System.out.println("Done");
            label.setText("Шиврование: Готово");
        } catch(Exception ex) {
            label.setText("Шиврование: что-то пошло не так");
        }
    }

    private void decrypt(TextField text, Label label) {
        if (text.getText().equals("")) {
            label.setText("Введите имя файла");
            return;
        }
        try {
            new Ofb().decrypt(text.getText());
            System.out.println("Done");
            label.setText("Расшивровка: Готово");
        } catch (KeyNotFound ex) {
            label.setText("Расшивровка: Не найден ключ");
        } catch (Exception ex) {
            label.setText("Расшивровка: что-то пошло не так");
        }
    }
}
