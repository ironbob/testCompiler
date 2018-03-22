package lexer;

import regular.RegLexer;
import regular.RegularMacroException;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by baobaowang on 2018/3/22.
 */
public class ThomposonConstrutor {
    private RegLexer mRegLexer;

    public void prepare(String reg) {
        if (mRegLexer == null) {
            this.mRegLexer = new RegLexer();
        }
        mRegLexer.prepareLex(reg);
    }

    public NfaPaire term() throws RegularMacroException {
        NfaPaire paire = new NfaPaire();
        mRegLexer.advance();
        boolean handled = construtorSimpleChar(paire);
        if (!handled) {
            handled = construtorAnyChar(paire);
        }
        if (!handled) {
            handled = construtorCharSet(paire);
        }
        if (!handled) {
            throw new RegularMacroException("此表达式不能构造出状态机:" + mRegLexer.getReg());
        }
        return paire;
    }

    private boolean construtorSimpleChar(NfaPaire paire) throws RegularMacroException {
        if (!mRegLexer.matchToken(RegLexer.TokenType.L)) {
            return false;
        }
        paire.startNode = NFANodeManager.getInstance().getNFANode();
        paire.endNode = NFANodeManager.getInstance().getNFANode();
        paire.startNode.next = paire.endNode;
        Edge edge = new Edge();
        edge.type = Edge.CCL;
        edge.transitionSet.add(mRegLexer.getCurrentToken().value);
        paire.startNode.edge = edge;
        mRegLexer.advance();
        return true;
    }

    /**
     * .号
     *
     * @param paire
     * @return
     */
    private boolean construtorAnyChar(NfaPaire paire) throws RegularMacroException {
        if (!mRegLexer.matchToken(RegLexer.TokenType.ANY)) {
            return false;
        }
        paire.startNode = NFANodeManager.getInstance().getNFANode();
        paire.endNode = NFANodeManager.getInstance().getNFANode();
        paire.startNode.next = paire.endNode;
        Edge edge = new Edge();
        edge.type = Edge.CCL;
        edge.transitionSet.add((byte) '\r');
        edge.transitionSet.add((byte) '\n');
        edge.setComplete();
        paire.startNode.edge = edge;
        mRegLexer.advance();
        return true;
    }

    /**
     * [a-z]  ,  [^a-z]  ,  [a-zA-Z]等
     *
     * @param paire
     * @return
     */
    private boolean construtorCharSet(NfaPaire paire) throws RegularMacroException {
        if (!mRegLexer.matchToken(RegLexer.TokenType.CCL_START)) {
            return false;
        }
        paire.startNode = NFANodeManager.getInstance().getNFANode();
        paire.endNode = NFANodeManager.getInstance().getNFANode();
        paire.startNode.next = paire.endNode;
        boolean isNegative = false;
        mRegLexer.advance();
        if (mRegLexer.getCurrentToken().value == '^') {
            isNegative = true;
            mRegLexer.advance();
        }
        Set<Byte> findSet = getCharSetFromReg();

        Edge edge = new Edge();
        edge.type = Edge.CCL;
        edge.transitionSet.addAll(findSet);
        if (isNegative) {
            edge.transitionSet.add((byte) '\r');
            edge.transitionSet.add((byte) '\n');
            edge.setComplete();
        }
        paire.startNode.edge = edge;
        mRegLexer.advance();
        return true;
    }

    /**
     * 获取 [  ]中的set集
     *
     * @return
     */
    private Set<Byte> getCharSetFromReg() throws RegularMacroException {
        Set<Byte> set = new TreeSet<>();
        if (!mRegLexer.matchToken(RegLexer.TokenType.L)) {
            throw new RegularMacroException("错误的表达式，\'[\' 中第一个字符必须为常规字符:" + mRegLexer.getReg());
        }
        byte start = mRegLexer.getCurrentToken().value;
        while (true) {
            if (mRegLexer.matchToken(RegLexer.TokenType.CCL_END)) {
                return set;
            }
            if (mRegLexer.matchToken(RegLexer.TokenType.EOS)) {
                throw new RegularMacroException("没有找到]," + mRegLexer.getReg());
            }
            if (mRegLexer.getCurrentToken().value == '-') {
                mRegLexer.advance();
                if (!mRegLexer.matchToken(RegLexer.TokenType.L)) {
                    throw new RegularMacroException("错误的表达式，\'- \' 后面必须接一个常规字符:" + mRegLexer.getReg());
                }
                byte end = mRegLexer.getCurrentToken().value;
                if (start >= end) {
                    throw new RegularMacroException("\'- \' 前一个字符值必须小于后面 start =" + start + ",end=" + end);
                }
                for (byte b = start; b <= end; b++) {
                    set.add(b);
                }
                start = mRegLexer.advance().value;
            } else {
                set.add(start = mRegLexer.getCurrentToken().value);
                mRegLexer.advance();
            }
        }
    }
}
