
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
        byte[] readFile = readFile(file);
        int size = readFile.length;

        int buf2[] = {0x00_00_00_00, 0x00_00_00_00};

        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        System.out.println("Старт программы " + formatter.format(new Date()));

        byte keyBytes[] = generateKey();
        writeFile(file.getParent() + "\\GEA_KEY", keyBytes);
        int[] text = byteToInt(readFile);
        int[] key = byteToInt(keyBytes);

        System.out.println("Перекинулы в инт " + formatter.format(new Date()));

        for (int i = 0; i < text.length; i += 2) {
            encrypt(buf2, key);
            shifr(text, buf2, i);
        }

        System.out.println("Зашифровали " + formatter.format(new Date()));

        byte[] encText = intToByte(text);
        byte[] finishBytes = new byte[size];
        System.arraycopy(encText, 0, finishBytes, 0, size);
        writeFile(file + ".enc", finishBytes);

        System.out.println("Записали в файл " + formatter.format(new Date()));
    }

    public void decrypt(String pathFile) throws KeyException, FileNotFoundException {
        File file = new File(pathFile);
        byte[] readFile = readFile(file);
        int size = readFile.length;
        int buf2[] = {0x00_00_00_00, 0x00_00_00_00};

        byte[] keyBytes = null;
        try {
            keyBytes = readFile(new File(file.getParent() + "\\GEA_KEY"));
        } catch (FileNotFoundException ex) {
            throw new KeyException("ключ не найден");
        }

        if (keyBytes.length != 16) throw new KeyException("ключ равен не 16 байт");

        int[] text = byteToInt(readFile);
        int[] key = byteToInt(keyBytes);

        for (int i = 0; i < text.length; i += 2) {
            encrypt(buf2, key);
            shifr(text, buf2, i);
        }

        byte[] encText = intToByte(text);
        String s = file.getName().substring(0, file.getName().lastIndexOf("."));
        byte[] finishBytes = new byte[size];
        System.arraycopy(encText, 0, finishBytes, 0, size);

        writeFile(file.getParent() + "\\" + s, finishBytes);
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

    private void shifr(int[] data, int[] buf2, int index) {
        data[index] ^= buf2[0];
        data[index + 1] ^= buf2[1];
    }

    private byte[] generateKey() {
        Random r = new Random();
        byte[] bytes = new byte[16];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) r.nextInt(0xFF);
        }
        return bytes;
    }

    private byte[] readFile(File f) throws FileNotFoundException {
        byte[] buffer = new byte[(int) f.length()];
        try (BufferedInputStream reader = new BufferedInputStream(new FileInputStream(f))) {
            reader.read(buffer);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("файл не найден");
        } catch (IOException e) {
        }
        return buffer;
    }

    private void writeFile(String pathFile, byte[] data) {
        try (BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(pathFile))) {//запись файла побайтово
            writer.write(data);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            System.out.println("Ошибка ввода\\вывода");
        }
    }

    private int[] byteToInt(byte[] data) {
        int paddedSize = ((data.length/8) + (((data.length%8)==0)?0:1)) * 2;
        int[] result = new int[paddedSize];
        for (int i = 0, j = 0, shift = 24; i < data.length; i++) {
            result[j] |= ((data[i] & 0xFF) << shift);
            if (shift == 0) {
                shift = 24;
                j++;
                if (j<result.length) result[j] = 0;
            } else {
                shift -=8;
            }
        }
        return result;
    }

    private byte[] intToByte(int[] data) {
        byte[] result = new byte[data.length * 4];
        for (int j = 0, i = 0, count = 0; j < result.length; j++) {
            result[j] = (byte) ((data[i] >> (24 - (8*count))) & 0xFF);
            count++;
            if (count ==4) {
                count = 0;
                i++;
            }
        }
        return result;
    }
}
