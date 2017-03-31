import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * Created by Егор on 31.03.2017.
 */
public class test3 {

    public static void main(String[] args) {
        byte[] hashKeyBytes = getHash("123");
        int[] hashKey = Transfer.byteToInt(hashKeyBytes);
        byte[] keyBytes = generateKey();
        int[] key = Transfer.byteToInt(keyBytes);
        for (int i: key) {
            System.out.print(i + ":");
        }
        System.out.println();
        key = encryptKey(key, hashKey);
        for (int i: key) {
            System.out.print(i + ":");
        }
        System.out.println();
        key = encryptKey(key, hashKey);
        for (int i: key) {
            System.out.print(i + ":");
        }
        System.out.println();
    }

    private static byte[] getHash(String password) {
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

    private static int[] encryptKey(int[] key, int[] hashKey) {
        int[] part1 = {key[0], key[1]};
        int[] part2 = {key[2], key[3]};
        int[] vector = initVector();

        encrypt(vector, hashKey);
        shifr(part1, vector);
        encrypt(vector, hashKey);
        shifr(part2, vector);

        return new int[]{part1[0], part1[1], part2[0], part2[1]};
    }

    private static void encrypt(int[] data, int[] key) {
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

    private static void shifr(int[] data, int[] buf2) {
        data[0] ^= buf2[0];
        data[1] ^= buf2[1];
    }

    private static byte[] generateKey() {
        Random r = new Random();
        byte[] bytes = new byte[16];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) r.nextInt(0xFF);
        }
        return bytes;
    }

    private static int[] initVector() {
        return new int[]{0x00_00_00_00, 0x00_00_00_00};
    }
}
