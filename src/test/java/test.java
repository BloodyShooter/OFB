import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Егор on 24.03.2017.
 */
public class test {
    public static void main(String[] args) {
        byte[] bytes = new byte[8];
        try (FileInputStream fis = new FileInputStream("E:\\Егор\\Idea\\OFB\\src\\test\\resourse\\text.txt")) {
            fis.skip(28);
            int size = 0;
            int tempbyte = 0;
            for (int i = 0; i < 8; i++) {
                tempbyte = fis.read();
                if (tempbyte != -1) {
                    bytes[i] = (byte) tempbyte;
                    size++;
                } else break;
            }
            System.out.println(size);
            System.out.println(new String(bytes));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
