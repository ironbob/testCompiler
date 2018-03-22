package lexer;

/**
 * Created by baobaowang on 2018/3/22.
 */
public class NfaPaire {
    NFA startNode;
    NFA endNode;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("状态").append(startNode.stateNum).append("--->").append(endNode.stateNum).append("   ");
        sb.append(startNode.edge.toString());
        return sb.toString();
    }
}
