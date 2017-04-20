
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Archiver {

    public static byte[] compressed(byte[] buffer) {
        byte[] compressed = new byte[buffer.length * 2];
        int count = 1;
        int iter = 0;
        for (int i = 0; i < buffer.length - 1; i++) {
            if (i < buffer.length && buffer[i] == buffer[i + 1]) {
                count++;
                if (count == 128) {
                    compressed[iter] = (byte) (count - 1);
                    iter++;
                    compressed[iter] = buffer[i];
                    iter++;
                    count = 1;
                }
            } else {
                if (count != 1) {
                    compressed[iter] = (byte) count;
                    iter++;
                }
                compressed[iter] = buffer[i];
                iter++;
                count = 1;
            }
        }
        if (count != 1) {
            compressed[iter] = (byte) count;
            iter++;
        }
        compressed[iter] = buffer[buffer.length - 1];
        iter++;

        byte [] finish = new byte[iter];
        System.arraycopy(compressed, 0, finish, 0 , iter);
        return finish;
    }

    public static byte[] deCompressed(byte[] buffer) {
        List<Byte> decompressed = new ArrayList<>();
        return null;
    }

    public static void main(String[] args) {
        String example = "aaaaaaaabbbb";
        byte[] compressed = compressed(example.getBytes());
        for (byte b :
                compressed) {
            System.out.print(b + ".");
        }
    }
}
