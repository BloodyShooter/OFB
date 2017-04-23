import java.util.ArrayList;
import java.util.List;

public class Archiver {

    private static byte[] compressed(byte[] buffer) {
        List<Byte> compressed = new ArrayList<>();
        List<Byte> target = new ArrayList<>();
        int count = 1;
        int anotherCount = 0;
        for (int i = 0; i < buffer.length - 1; i++) {
            if (i < buffer.length && buffer[i] == buffer[i + 1]) {
                count++;
                if (count == 128) {
                    compressed.add((byte) (count -1));
                    compressed.add(buffer[i]);
                    count = 1;
                }
                if (anotherCount > 1) {
                    compressed.add((byte) ((anotherCount - 1) * -1));
                    compressed.addAll(target);
                }
                anotherCount = 0;
                target.clear();
            } else if (i < buffer.length && buffer[i] != buffer[i + 1]) {
                anotherCount++;
                if (anotherCount != 1)
                    target.add(buffer[i]);
                if (anotherCount == 128) {
                    compressed.add((byte) (anotherCount -1));
                    compressed.add(buffer[i]);
                }
                if (count != 1) {
                    compressed.add((byte) (count));
                    compressed.add(buffer[i]);
                }
                count = 1;
            }
        }
        if (count != 1) {
            compressed.add((byte) (count));
            compressed.add(buffer[buffer.length - 1]);
        }
        if (anotherCount > 1) {
            compressed.add((byte) (anotherCount * -1));
            compressed.addAll(target);
            compressed.add(buffer[buffer.length - 1]);
        }

        return toArrayFromList(compressed);
    }

    private static byte[] deCompressed(byte[] buffer) {
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
        String example = "aaaaaaaazxcvbnmmnbvbbbb11112345678911111ooo123456789";
        byte[] compressed = compressed(example.getBytes());
        for (byte b :
                compressed) {
            System.out.print(b + ".");
        }
        System.out.println();
        System.out.println(new String(deCompressed(compressed)));
    }

    private static byte[] toArrayFromList(List<Byte> list) {
        byte[] bytes = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            bytes[i] = list.get(i);
        }
        return bytes;
    }
}
