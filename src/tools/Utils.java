package tools;

/**
 * Created by baobaowang on 2018/3/21.
 */
public class Utils {
    //16进制
    public static boolean isHex(char c) {
        return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    public static boolean isDigit(char c) {
        return (c >= '0' && c <= '9');
    }

    public static boolean isHexDigit(char c) {
        return (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    public static int getIntValue(char c) {
        if (isDigit(c)) {
            return c - '0';
        } else if (isHexDigit(c)) {
            if (c <= 'F') {
                return c - 'A' + 10;
            } else {
                return c - 'a' + 10;
            }
        }
        throw new IllegalArgumentException(c + "不能转换为数字");
    }

    //8进制
    public static boolean isOct(char c) {
        return (c >= '0' && c <= '7');
    }
}
