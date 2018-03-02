package inputsystem;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * Created by baobaowang on 2018/2/28.
 */
public class InputUtils {

    public static void skipSpace(Input input) {
        do {
            byte c = input.ii_lookahead(1);
            if (Character.isSpaceChar(c) || c == '\n' || c == '\r') {
                input.ii_advance();
            } else {
                break;
            }
        } while (true);
    }

    public static String getWord(Input input) {
        StringBuilder sb = new StringBuilder();
        do {
            byte c = input.ii_lookahead(1);
            if (!Character.isSpaceChar(c) && c != '\n' && !Character.isSpaceChar(c) && c != '\r' && c != Input.EOF) {
                sb.append(getNextChar(input));
            } else {
                break;
            }
        } while (true);
        return sb.toString();
    }

    public static String getNextChar(Input input){
        byte[] data = new byte[1];
        data[0] = input.ii_advance();
        try {
            return new String(data, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }
}
