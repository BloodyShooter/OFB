package org.gvozdetscky.logic;

import org.gvozdetscky.exception.KeyException;
import org.gvozdetscky.logic.Archiver.Archiver;
import org.gvozdetscky.logic.base64.Base64;
import org.gvozdetscky.utils.FilesManager;
import org.gvozdetscky.utils.Transfer;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CryptographerOFB {

    private static final int SIZE_BIG_BUFFER = 1024;
    private static final int SIZE_SMALL_BUFFER = 8;
    private static final int SIZE_FILE_WITH_KEY = 16;

    public void encrypt(String pathFile, String password,
                        boolean isStatusArch, boolean isStatusBase64) throws FileNotFoundException {
        if (isStatusArch || isStatusBase64) {
            encryptWithArchiver(pathFile, password, isStatusArch, isStatusBase64);
        } else {
            encryptInParts(pathFile, password);
        }
    }

    public void decrypt(String pathFile, String password,
                        boolean isStatusArch, boolean isStatusBase64) throws KeyException, FileNotFoundException {
        if (isStatusArch || isStatusBase64) {
            decryptWithArchiver(pathFile, password, isStatusArch, isStatusBase64);
        } else {
            decryptInParts(pathFile, password);
        }
    }

    private void encryptInParts(String pathFile, String password) throws FileNotFoundException {
        int[] hashKey;
        int[] cipheredKey = null;
        File file = new File(pathFile);

        byte keyBytes[] = generateKey();
        if (password != null) {
            hashKey = Transfer.byteToInt(getHashMD5(password));
            cipheredKey = encryptKeyOFB(Transfer.byteToInt(keyBytes), hashKey);
        } else {
            FilesManager.writeFile(file.getParent() + "\\key", keyBytes);
        }

        int[] key = Transfer.byteToInt(keyBytes);

        try (FileOutputStream writer = new FileOutputStream(pathFile + ".enc");
                FileInputStream reader = new FileInputStream(pathFile)) {
            if (password != null) writer.write(Transfer.intToByte(cipheredKey));
            runEncryptOFB(writer, reader, file.length(), key);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void encryptWithArchiver(String pathFile, String password,
                                     boolean isStatusArch, boolean isStatusBase64) throws FileNotFoundException {
        File file = new File(pathFile);
        byte keyBytes[] = generateKey();
        int[] cipheredKey = null;
        int[] key = Transfer.byteToInt(keyBytes);

        if (password != null) {
            int[] hashKey = Transfer.byteToInt(getHashMD5(password));
            cipheredKey = encryptKeyOFB(key, hashKey);
        } else {
            FilesManager.writeFile(file.getParent() + "\\key", keyBytes);
        }

        String fileName = pathFile;

        if (isStatusArch) fileName += ".rle";
        fileName += ".enc";
        if (isStatusBase64) fileName += ".bs64";

        try (FileOutputStream writer = new FileOutputStream(fileName);
             FileInputStream reader = new FileInputStream(pathFile)) {
            if (password != null) {
                writer.write(Transfer.intToByte(cipheredKey));
            }

            BufferedInputStream bufferedReader = new BufferedInputStream(reader);
            byte[] buffer = new byte[(int) file.length()];
            FilesManager.readFile(bufferedReader, buffer);

            if (isStatusArch) {
                buffer = Archiver.compressed(buffer);
            }
            int fileSize = buffer.length;

            int[] value = Transfer.byteToInt(buffer);
            int vector[] = initVector();
            value = goAllElementShifr(value, key, vector);

            if (isStatusBase64) {
                byte[] temp = new byte[fileSize];
                System.arraycopy(Transfer.intToByte(value), 0, temp, 0, temp.length);

                byte[] byteBase64 = Base64.encode(temp);
                FilesManager.writeFile(writer, byteBase64, byteBase64.length);
            } else {
                FilesManager.writeFile(writer, Transfer.intToByte(value), fileSize);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void decryptInParts(String pathFile, String password) throws KeyException, FileNotFoundException {
        byte[] keyBytes = new byte[SIZE_FILE_WITH_KEY];
        int[] hashKey = null;
        File file = new File(pathFile);

        if (password != null) {
            hashKey = Transfer.byteToInt(getHashMD5(password));
        }

        String s = file.getName().substring(0, file.getName().lastIndexOf("."));

        try (FileOutputStream writer = new FileOutputStream(file.getParent() + "\\" + s);
             FileInputStream reader = new FileInputStream(pathFile)) {
            if (password != null) {
                FilesManager.readFile(new BufferedInputStream(reader, SIZE_FILE_WITH_KEY), keyBytes);
                int[] key = encryptKeyOFB(Transfer.byteToInt(keyBytes), hashKey);
                runEncryptOFB(writer, reader, file.length() - SIZE_FILE_WITH_KEY, key);
            } else {
                keyBytes = FilesManager.readFile(new File(file.getParent() + "\\key"));
                int[] key = Transfer.byteToInt(keyBytes);
                runEncryptOFB(writer, reader, file.length(), key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void decryptWithArchiver(String pathFile, String password,
                                     boolean isStatusArch, boolean isStatusBase64) throws KeyException, FileNotFoundException {
        File file = new File(pathFile);
        byte[] keyBytes = new byte[SIZE_FILE_WITH_KEY];
        int[] key;

        //String s = file.getName().substring(0, file.getName().lastIndexOf("."));
        String[] tokens = file.getName().split("\\.");
        String s = tokens[0] + "." + tokens[1];

        try (FileOutputStream writer = new FileOutputStream(file.getParent() + "\\" + s);
             FileInputStream reader = new FileInputStream(pathFile)) {
            if (password != null) {
                FilesManager.readFile(new BufferedInputStream(reader, SIZE_FILE_WITH_KEY), keyBytes);
                int[] hashKey = Transfer.byteToInt(getHashMD5(password));
                key = encryptKeyOFB(Transfer.byteToInt(keyBytes), hashKey);
            } else {
                key = Transfer.byteToInt(FilesManager.readFile(new File(file.getParent() + "\\key")));
            }

            BufferedInputStream bufferedReader = new BufferedInputStream(reader);
            byte[] buffer;
            if (password != null) {
                buffer = new byte[(int) file.length() - SIZE_FILE_WITH_KEY];
            } else {
                buffer = new byte[(int) file.length()];
            }
            FilesManager.readFile(bufferedReader, buffer);

            if (isStatusBase64) {
                buffer = Base64.decode(buffer);
            }

            int[] value = Transfer.byteToInt(buffer);
            int vector[] = initVector();
            value = goAllElementShifr(value, key, vector);

            System.arraycopy(Transfer.intToByte(value), 0, buffer, 0, buffer.length);
            if (isStatusArch) {
                byte[] bufferArch = Archiver.deCompressed(buffer);
                FilesManager.writeFile(writer, bufferArch, bufferArch.length);
            } else {
                FilesManager.writeFile(writer, buffer, buffer.length);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private void encryptTEA(int[] data, int[] key) {
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

    private void encXOR(int[] data, int[] buf2) {
        data[0] ^= buf2[0];
        data[1] ^= buf2[1];
    }

    private byte[] generateKey() {
        Random r = new Random();
        byte[] bytes = new byte[SIZE_FILE_WITH_KEY];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) r.nextInt(0xFF);
        }
        return bytes;
    }

    private void runEncryptOFB(FileOutputStream writer, FileInputStream reader,
                               long sizeFile, int[] key) throws IOException {
        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        System.out.println("Старт программы " + formatter.format(new Date()));
        int vector[] = initVector();

        sizeFile -= encryptWithBuffer(writer, reader, key, vector,
                sizeFile, SIZE_BIG_BUFFER);
        sizeFile -= encryptWithBuffer(writer, reader, key, vector,
                sizeFile, SIZE_SMALL_BUFFER);
        encryptLastBlog(writer, reader, sizeFile, vector, key);

        System.out.println("Зашифровали " + formatter.format(new Date()));
    }

    private void encryptLastBlog(FileOutputStream writer, FileInputStream reader,
                                 long sizeFile, int[] vector, int[] key) throws IOException {
        if (sizeFile != 0) {
            byte[] bufferValue = new byte[SIZE_SMALL_BUFFER];
            int sizeBlock = FilesManager.readFile(reader, bufferValue);
            int[] value = Transfer.byteToInt(bufferValue);
            encryptTEA(vector, key);
            encXOR(value, vector);
            FilesManager.writeFile(writer, Transfer.intToByte(value), sizeBlock);
        }
    }

    private long encryptWithBuffer(FileOutputStream writer, FileInputStream reader,
                                   int[] key, int[] vector, long size,
                                   int sizeBuffer) throws IOException {
        size /= sizeBuffer;
        byte[] bufferValue = new byte[sizeBuffer];
        BufferedInputStream bufferReader = new BufferedInputStream(reader, sizeBuffer);
        BufferedOutputStream bufferWriter = new BufferedOutputStream(writer, sizeBuffer);

        for (long i = 0; i < size; i++) {
            FilesManager.readFile(bufferReader, bufferValue);
            int[] value = Transfer.byteToInt(bufferValue);
            goAllElementShifr(value, key, vector);
            bufferValue = Transfer.intToByte(value);
            FilesManager.writeFile(bufferWriter, bufferValue);
        }

        return size * sizeBuffer;
    }

    private byte[] getHashMD5(String password) {
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

    private int[] encryptKeyOFB(int[] key, int[] hashKey) {
        int[] vector = initVector();
        int[] part1 = {key[0], key[1]};
        int[] part2 = {key[2], key[3]};

        encryptTEA(vector, hashKey);
        encXOR(part1, vector);
        encryptTEA(vector, hashKey);
        encXOR(part2, vector);

        return new int[]{part1[0], part1[1], part2[0], part2[1]};
    }

    private int[] goAllElementShifr(int[] value, int[] key, int[] vector) {
        int[] newValue = new int[2];
        for (int j = 0; j < value.length; j += 2) {
            encryptTEA(vector, key);
            newValue[0] = value[j];
            newValue[1] = value[j + 1];
            encXOR(newValue, vector);
            value[j] = newValue[0];
            value[j + 1] = newValue[1];
        }

        return value;
    }

    private int[] initVector() {
        return new int[]{0x00_00_00_00, 0x00_00_00_00};
    }
}
