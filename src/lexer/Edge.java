package lexer;

import tools.Utils;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by baobaowang on 2018/3/22.
 */
public class Edge {
    public static final int EPSILON = -1; //边对应的是ε
    public static final int CCL = -2; //边对应的是字符集
    public static final int EMPTY = -3; //

    int type = EMPTY;
    public Set<Byte> transitionSet = new TreeSet<>();


    /**
     * 求补
     */
    public void setComplete() {
        Set<Byte> newSet = new TreeSet<>();
        for (byte b = 0; b < 127; b++) {
            if (!transitionSet.contains(b)) {
                newSet.add(b);
            }
        }
        transitionSet = newSet;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("type:");
        if (type == CCL) {
            sb.append("字符集( ");
            for (Byte b :
                    transitionSet) {
                sb.append(Utils.getPrintableByte(b)).append(" ");
            }
            sb.append(")");
        }else{
            sb.append("epsinal");
        }
        return sb.toString();
    }
}
