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
                if (countEqualValues == 128) {
                    compressed.add((byte) (countEqualValues -1));
                    compressed.add(buffer[i]);
                    countEqualValues = 1;
                }
                if (countUnequalValues > 1) {
                    compressed.add((byte) ((countUnequalValues - 1) * -1));
                    compressed.addAll(unequalSequence);
                }
                countUnequalValues = 0;
                unequalSequence.clear();
            } else if (i < buffer.length && buffer[i] != buffer[i + 1]) {
                countUnequalValues++;
                if (countUnequalValues != 1)
                    unequalSequence.add(buffer[i]);
                if (countUnequalValues == 128) {
                    compressed.add((byte) ((countUnequalValues - 1) * -1));
                    compressed.addAll(unequalSequence);
                    unequalSequence.clear();
                    countUnequalValues = 1;
                }
                if (countEqualValues != 1) {
                    compressed.add((byte) (countEqualValues));
                    compressed.add(buffer[i]);
                }
                countEqualValues = 1;
            }
        }
        if (countEqualValues != 1) {
            compressed.add((byte) (countEqualValues));
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
            if (aByte > 0) {
                for (int i = 0; i < aByte; i++) {
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
        String example = "01234567890123456789012345678901234567890123456789" +
                "01234567890123456789012345678901234567890123456789" +
                "0123456789012345678901234567" +
                "aa1aaaaaazxcvbnmmnbvbbbb11112345678911111ooo123456789LL8";
        byte[] compressed = compressed(example.getBytes());
        for (byte b :
                compressed) {
            System.out.print(b + ".");
        }
        System.out.println();
        String result = new String(deCompressed(compressed));
        System.out.println(result);
        if (example.equals(result)) {
            System.out.println("Получилось");
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
