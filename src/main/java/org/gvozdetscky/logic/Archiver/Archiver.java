package org.gvozdetscky.logic.Archiver;

import org.gvozdetscky.utils.FilesManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Archiver {

    public static byte[] compressed(byte[] buffer) {
        return compressedRLE(buffer);
    }

    public static byte[] deCompressed(byte[] buffer) {
        return deCompressedRLE(buffer);
    }

    private static byte[] compressedRLE(byte[] buffer) {
        List<Byte> compressed = new ArrayList<>();
        List<Byte> unequalSequence = new ArrayList<>();
        int countEqualValues = 1;
        int countUnequalValues = 1;
        for (int i = 0; i < buffer.length - 1; i++) {
            if (i < buffer.length && buffer[i] == buffer[i + 1]) {
                countEqualValues++;
                if (countUnequalValues > 1) {
                    compressed.add((byte) ((countUnequalValues - 1) * -1));
                    compressed.addAll(unequalSequence);
                }
                countUnequalValues = 0;
                if (countEqualValues == 130) {
                    compressed.add((byte) (countEqualValues - 3));
                    compressed.add(buffer[i]);
                    countEqualValues = 1;
                    countUnequalValues = 1;
                }
                unequalSequence.clear();
            } else if (i < buffer.length && buffer[i] != buffer[i + 1]) {
                countUnequalValues++;
                if (countUnequalValues != 1)
                    unequalSequence.add(buffer[i]);
                if (countUnequalValues == 129) {
                    compressed.add((byte) ((countUnequalValues - 1) * -1));
                    compressed.addAll(unequalSequence);
                    unequalSequence.clear();
                    countUnequalValues = 1;
                }
                if (countEqualValues != 1) {
                    compressed.add((byte) (countEqualValues - 2));
                    compressed.add(buffer[i]);
                }
                countEqualValues = 1;
            }
        }
        if (countEqualValues != 1) {
            compressed.add((byte) (countEqualValues - 2));
            compressed.add(buffer[buffer.length - 1]);
        }
        if (countUnequalValues > 0) {
            compressed.add((byte) (countUnequalValues * -1));
            compressed.addAll(unequalSequence);
            compressed.add(buffer[buffer.length - 1]);
        }

        return toArrayFromList(compressed);
    }

    private static byte[] deCompressedRLE(byte[] buffer) {
        List<Byte> decompressed = new ArrayList<>();
        int iter = 0;
        while(iter < buffer.length) {
            Byte aByte = buffer[iter];
            iter++;
            if (iter == buffer.length) {
                decompressed.add(aByte);
                continue;
            }
            if (aByte >= 0) {
                int finish = aByte + 2;
                for (int i = 0; i < finish; i++) {
                    decompressed.add(buffer[iter]);
                }
                iter++;
            }
            if (aByte < 0) {
                for (int i = iter; i < iter + (aByte * -1); i++) {
                    decompressed.add(buffer[i]);
                }
                iter += (aByte * -1);
            }

        }

        return toArrayFromList(decompressed);
    }

    public static void main(String[] args) {
        byte[] example = null;
        try {
            example = FilesManager.readFile(new File("D:\\test\\test.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
            return;
        }
        byte[] compressed = compressed(example);
        String temp = new String(compressed);
        System.out.println("Размер начального: " + example.length + " Размер сжатого: " + temp.length());
        FilesManager.writeFile("D:\\test\\testCom.ZIP.txt", compressed);
        for (byte b: compressed) {
            System.out.print(b + ":");
        }
        System.out.println();
        byte[] resultByte = deCompressed(compressed);
        String result = new String(resultByte);
        System.out.println(result);
        FilesManager.writeFile("D:\\test\\text.unZIP.txt", resultByte);
        if (new String(example).equals(result)) {
            System.out.println("Получилсоь");
        } else {
            System.out.println("Не вышло");
        }
    }

    private static byte[] toArrayFromList(List<Byte> list) {
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }
}