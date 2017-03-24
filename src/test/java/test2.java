import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Егор on 24.03.2017.
 */
public class test2 {

    public static void main(String[] args) throws Exception {
        byte[] buffer = new byte[8];
        File file = new File("E:\\Егор\\Idea\\OFB\\src\\test\\resourse\\text.txt");
        try (FileOutputStream writer = new FileOutputStream("E:\\Егор\\Idea\\OFB\\src\\test\\resourse\\text2.txt");
             FileInputStream reader = new FileInputStream(file)) {
            for (int i = 0, pos = 0; i < file.length(); i += 8) {
                int size = readFile(reader, buffer, pos);
                System.out.println(size);
                System.out.println(new String(buffer));
                writeFile(writer, buffer, size);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private static int readFile(FileInputStream fis, byte[] buffer, int pos) throws IOException {
        fis.skip(pos);
        int size = 0;
        int tempByte;
        for (int i = 0; i < 8; i++) {
            tempByte = fis.read();
            if (tempByte != -1) {
                buffer[i] = (byte) tempByte;
                size++;
            } else buffer[i] = 0;
        }
        return size;
    }

    private static void writeFile(FileOutputStream fos, byte[] buffer, int pos) throws IOException {
        fos.write(buffer, 0, pos);
    }
}

