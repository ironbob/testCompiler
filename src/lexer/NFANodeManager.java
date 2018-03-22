package lexer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by baobaowang on 2018/3/22.
 */
public class NFANodeManager {
    private AtomicInteger ati = new AtomicInteger(0);
    private static NFANodeManager instance;

    private NFANodeManager(){

    }

    public synchronized static NFANodeManager getInstance(){
        if(instance == null){
            instance = new NFANodeManager();
        }
        return instance;
    }

    public NFA getNFANode(){
        NFA nfa = new NFA();
        int num = ati.incrementAndGet();
        nfa.stateNum = num;
        return nfa;
    }
}
