
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author Yagorka
 */
public class Ofb {
    public void encrypt(String pathFile) throws FileNotFoundException {
        File file = new File(pathFile);
        byte keyBytes[] = generateKey();

        FilesManager.writeFile(file.getParent() + "\\GEA_KEY", keyBytes);

        try (FileOutputStream writer = new FileOutputStream(pathFile + ".enc");
                FileInputStream reader = new FileInputStream(pathFile)) {
            runCicle(file, keyBytes, writer, reader);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void decrypt(String pathFile) throws KeyException, FileNotFoundException {
        File file = new File(pathFile);

        byte[] keyBytes;
        try {
            keyBytes = FilesManager.readFile(new File(file.getParent() + "\\GEA_KEY"));
        } catch (FileNotFoundException ex) {
            throw new KeyException("ключ не найден");
        }

        if (keyBytes.length != 16) throw new KeyException("ключ равен не 16 байт");

        String s = file.getName().substring(0, file.getName().lastIndexOf("."));

        try (FileOutputStream writer = new FileOutputStream(file.getParent() + "\\" + s);
             FileInputStream reader = new FileInputStream(pathFile)) {
            runCicle(file, keyBytes, writer, reader);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void encrypt(int[] data, int[] key) {
        int delta = 0x9e3779b9;
        int sum , n, v1, v2;
        n = 32;
        sum = 0;
        v1 = data[0];
        v2 = data[1];
        while (n-- > 0) {
            sum += delta;
            v1 += ((v2 << 4) + key[0] ^ v2) + (sum ^ (v2 >>> 5)) + key[1];
            v2 += ((v1 << 4) + key[2] ^ v1) + (sum ^ (v1 >>> 5)) + key[3];
        }
        data[0] = v1;
        data[1] = v2;
    }

    private void shifr(int[] data, int[] buf2) {
        data[0] ^= buf2[0];
        data[1] ^= buf2[1];
    }

    private byte[] generateKey() {
        Random r = new Random();
        byte[] bytes = new byte[16];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) r.nextInt(0xFF);
        }
        return bytes;
    }

    private void runCicle(File file, byte[] keyBytes, FileOutputStream writer, FileInputStream reader) throws IOException {
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        System.out.println("Старт программы " + formatter.format(new Date()));

        int size = (int)file.length()/8;
        int vector[] = initVector();
        byte[] bufferValue = new byte[8];
        int[] key = Transfer.byteToInt(keyBytes);
        BufferedInputStream bis = new BufferedInputStream(reader);

        for (int i = 0; i < size; i++) {
            FilesManager.readFile(bis, bufferValue);
            int[] value = Transfer.byteToInt(bufferValue);
            encrypt(vector, key);
            shifr(value, vector);
            FilesManager.writeFile(writer, Transfer.intToByte(value));
        }
        if (file.length()%8 != 0) {
            int sizeBlock = FilesManager.readFile(reader, bufferValue);
            int[] value = Transfer.byteToInt(bufferValue);
            FilesManager.writeFile(writer, Transfer.intToByte(value), sizeBlock);
        }

        System.out.println("Зашифровали " + formatter.format(new Date()));
    }

    private int[] initVector() {
        return new int[]{0x00_00_00_00, 0x00_00_00_00};
    }
}
