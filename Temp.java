import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Temp {
    public static void main(String[] args) throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream("myFile"));

        int input = 5665;
        byte highByte = (byte)((input >> 8) & 0xFF);
        byte lowByte = (byte)(input & 0xFF);

        dos.writeByte(highByte);
        dos.writeByte(lowByte);
        dos.writeInt(0xcafebabe);
        dos.writeInt((byte)input);


        return;
    }
}
