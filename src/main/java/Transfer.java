/**
 * Created by Егор on 30.03.2017.
 */
public class Transfer {

    public static int[] byteToInt(byte[] data) {
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

    public static byte[] intToByte(int[] data) {
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
