package lexer;

import regular.RegLexer;
import regular.RegularMacroException;

/**
 * Created by baobaowang on 2018/3/22.
 */
public class Factor {
    private RegLexer mRegLexer;

    public NfaPaire factor(NfaPaire paire, RegLexer lexer) throws RegularMacroException {
        mRegLexer = lexer;
        boolean handled = construtorClosuarePlus(paire);
        if(!handled){
            handled = construtorClosuareStare(paire);
        }
        if(!handled){
            handled = construtorClosuareOptional(paire);
        }
        return paire;
    }

    /**
     *  +号闭包
     * @param paire
     * @return
     * @throws RegularMacroException
     */
    private boolean construtorClosuarePlus(NfaPaire paire) throws RegularMacroException {
        if(!mRegLexer.matchToken(RegLexer.TokenType.PLUS)){
            return false;
        }
        NFA originStartNode = paire.startNode;
        NFA originEndNode = paire.endNode;
        NFA newStartNode = NFANodeManager.getInstance().getNFANode();
        NFA newEndNode = NFANodeManager.getInstance().getNFANode();
        newStartNode.next = originStartNode;
        newStartNode.edge = Edge.createEpsilonEdge();
        originEndNode.next = newEndNode;
        originEndNode.edge = Edge.createEpsilonEdge();
        originEndNode.next2 =originStartNode;
        paire.startNode = newStartNode;
        paire.endNode = newEndNode;
        mRegLexer.advance();
        return true;
    }

    /**
     *   *星号闭包
     * @param paire
     * @return
     * @throws RegularMacroException
     */
    private boolean construtorClosuareStare(NfaPaire paire) throws RegularMacroException {
        if(!mRegLexer.matchToken(RegLexer.TokenType.CLOSURE)){
            return false;
        }
        NFA originStartNode = paire.startNode;
        NFA originEndNode = paire.endNode;
        NFA newStartNode = NFANodeManager.getInstance().getNFANode();
        NFA newEndNode = NFANodeManager.getInstance().getNFANode();
        newStartNode.next = originStartNode;
        newStartNode.edge = Edge.createEpsilonEdge();
        originEndNode.next = newEndNode;
        originEndNode.edge = Edge.createEpsilonEdge();
        originEndNode.next2 =originStartNode;
        paire.startNode = newStartNode;
        paire.endNode = newEndNode;
        paire.startNode.next2 = paire.endNode;
        mRegLexer.advance();
        return true;
    }

    /**
     *   ?问号闭包
     * @param paire
     * @return
     * @throws RegularMacroException
     */
    private boolean construtorClosuareOptional(NfaPaire paire) throws RegularMacroException {
        if(!mRegLexer.matchToken(RegLexer.TokenType.OPTIONAL)){
            return false;
        }
        NFA originStartNode = paire.startNode;
        NFA originEndNode = paire.endNode;
        NFA newStartNode = NFANodeManager.getInstance().getNFANode();
        NFA newEndNode = NFANodeManager.getInstance().getNFANode();
        newStartNode.next = originStartNode;
        newStartNode.edge = Edge.createEpsilonEdge();
        originEndNode.next = newEndNode;
        originEndNode.edge = Edge.createEpsilonEdge();
//        originEndNode.next2 =originStartNode;
        paire.startNode = newStartNode;
        paire.endNode = newEndNode;
        paire.startNode.next2 = paire.endNode;
        mRegLexer.advance();
        return true;
    }
}
