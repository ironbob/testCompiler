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
    private Term term = new Term();
    private Factor factor = new Factor();

    public void prepare(String reg) {
        if (mRegLexer == null) {
            this.mRegLexer = new RegLexer();
        }
        mRegLexer.prepareLex(reg);
    }

    public NfaPaire construtor() throws RegularMacroException {
        NfaPaire paire = term.term(mRegLexer);
        paire = factor.factor(paire,mRegLexer);
        return paire;
    }


}
