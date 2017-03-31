
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author Yagorka
 */
public class Ofb {
    public void encrypt(String pathFile, String password) throws FileNotFoundException {
        File file = new File(pathFile);
        byte keyBytes[] = generateKey();
        int[] hashKey = Transfer.byteToInt(getHash(password));

        int[] key = Transfer.byteToInt(keyBytes);
        int[] shfrKey = encryptKey(key, hashKey);

        try (FileOutputStream writer = new FileOutputStream(pathFile + ".enc");
                FileInputStream reader = new FileInputStream(pathFile)) {
            writer.write(Transfer.intToByte(shfrKey));
            runCicle(writer, reader, file.length(), key);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void decrypt(String pathFile, String password) throws KeyException, FileNotFoundException {
        File file = new File(pathFile);
        int[] hashKey = Transfer.byteToInt(getHash(password));
        byte[] keyBytes = new byte[16];

        String s = file.getName().substring(0, file.getName().lastIndexOf("."));

        try (FileOutputStream writer = new FileOutputStream(file.getParent() + "\\" + s);
             FileInputStream reader = new FileInputStream(pathFile)) {
            FilesManager.readFile(new BufferedInputStream(reader, 16), keyBytes);
            int[] key = encryptKey(Transfer.byteToInt(keyBytes), hashKey);

            runCicle(writer, reader, file.length() - 16, key);
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

    private void runCicle(FileOutputStream writer, FileInputStream reader,
                          long sizeFile, int[] key) throws IOException {
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        System.out.println("Старт программы " + formatter.format(new Date()));
        int vector[] = initVector();

        optimazMethodBuff(writer, reader, key, vector, sizeFile/ 512, 512);
        optimazMethodBuff(writer, reader, key, vector, (sizeFile % 512) / 8, 8);
        lastBlog(writer, reader, sizeFile, vector, key);

        System.out.println("Зашифровали " + formatter.format(new Date()));
    }

    private void lastBlog(FileOutputStream writer, FileInputStream reader, long sizeFile, int[] vector, int[] key) throws IOException {
        if ((sizeFile % 8) != 0) {
            byte[] bufferValue = new byte[8];
            int sizeBlock = FilesManager.readFile(reader, bufferValue);
            int[] value = Transfer.byteToInt(bufferValue);
            encrypt(vector, key);
            shifr(value, vector);
            FilesManager.writeFile(writer, Transfer.intToByte(value), sizeBlock);
        }
    }

    private long optimazMethodBuff(FileOutputStream writer, FileInputStream reader,
                                   int[] key, int[] vector, long size,
                                   int sizeBuffer) throws IOException {
        byte[] bufferValue = new byte[sizeBuffer];
        BufferedInputStream bis = new BufferedInputStream(reader, sizeBuffer);
        BufferedOutputStream bos = new BufferedOutputStream(writer, sizeBuffer);
        int[] newValue = new int[2];

        for (long i = 0; i < size; i++) {
            FilesManager.readFile(bis, bufferValue);
            int[] value = Transfer.byteToInt(bufferValue);
            for (int j = 0; j < value.length; j += 2) {
                encrypt(vector, key);
                newValue[0] = value[j];
                newValue[1] = value[j + 1];
                shifr(newValue, vector);
                value[j] = newValue[0];
                value[j + 1] = newValue[1];
            }
            FilesManager.writeFile(bos, Transfer.intToByte(value));
        }

        return size / sizeBuffer;
    }

    private byte[] getHash(String password) {
        byte[] hashKey = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            hashKey = md.digest();
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Ошибка с хэшиком");
        }
        return hashKey;
    }

    private int[] encryptKey(int[] key, int[] hashKey) {
        int[] part1 = {key[0], key[1]};
        int[] part2 = {key[2], key[3]};
        int[] vector = initVector();

        encrypt(vector, hashKey);
        shifr(part1, vector);
        encrypt(vector, hashKey);
        shifr(part2, vector);

        return new int[]{part1[0], part1[1], part2[0], part2[1]};
    }

    private int[] initVector() {
        return new int[]{0x00_00_00_00, 0x00_00_00_00};
    }
}
