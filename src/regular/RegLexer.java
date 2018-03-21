package regular;

import tools.Utils;

/**
 * Created by baobaowang on 2018/3/21.
 * 正则表达式词法解析
 */
public class RegLexer {
    private static TokenType[] tokenMap = new TokenType[128];

    private String mCurReg = "";

    private int index = 0;

    boolean inQuoted = false;

    public enum TokenType {
        EOS,
        ANY,//.通配符
        START_BOL,//^开头匹配
        END_BOL,//$末尾匹配
        CCL_END,//]
        CCL_START,//[
        CLOSE_CURLY,//}
        OPEN_CURLY,//{
        CLOSE_PAREN,//)
        OPRN_PAREN,//(
        CLOSURE,//*
        DASH,//-
        END_OF_INPUT,//输入流结束
        L,  //字符常量
        OPTIONAL, //?
        OR, // |
        PLUS,// +
    }

    public static class Token {
        TokenType type;
        char value;
        String desc;

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (type == TokenType.L) {
                sb.append("普通字符:  ");
            } else {
                sb.append("特殊字符:  ");
            }
            if (desc != null && !desc.equals("")) {
                sb.append("---");
                sb.append(desc);
            } else {
                sb.append(value);
            }
            return sb.toString();
        }
    }

    static {
        for (int i = 0; i < tokenMap.length; i++) {
            tokenMap[i] = TokenType.L;
        }
        tokenMap['.'] = TokenType.ANY;
        tokenMap['^'] = TokenType.START_BOL;
        tokenMap['$'] = TokenType.END_BOL;
        tokenMap[']'] = TokenType.CCL_END;
        tokenMap['['] = TokenType.CCL_START;
        tokenMap['}'] = TokenType.CLOSE_CURLY;
        tokenMap['{'] = TokenType.OPEN_CURLY;
        tokenMap[')'] = TokenType.CLOSE_PAREN;
        tokenMap['('] = TokenType.OPRN_PAREN;
        tokenMap['*'] = TokenType.CLOSURE;
        tokenMap['-'] = TokenType.DASH;
        tokenMap['?'] = TokenType.OPTIONAL;
        tokenMap['|'] = TokenType.OR;
        tokenMap['+'] = TokenType.PLUS;
    }

    public void prepareLex(String regExpr) {
        mCurReg = regExpr;
        index = 0;
    }

    public boolean hasNext() {
        return index < mCurReg.length();
    }

    public Token nextToken() throws RegularMacroException {
        while (index < mCurReg.length()) {
            char c = mCurReg.charAt(index);
            if (c == '"') {
                index++;
                inQuoted = !inQuoted;
            } else if (c == '\\') {
                if (!inQuoted) {
                    if (index < mCurReg.length() - 1) {
                        index++;
                        return getNextEscToken(index);
                    } else {
                        throw new RegularMacroException("\\不能出现在表达式末尾:" + mCurReg);
                    }
                } else {
                    index++;
                    Token token = new Token();
                    token.type = tokenMap[c];
                    token.value = c;
                    return token;
                }
            } else {
                index++;
                Token token = new Token();
                token.type = tokenMap[c];
                token.value = c;
                return token;
            }
        }
        throw new RegularMacroException("非法表达式，没有找到正确的token:" + mCurReg);
    }

    /*当转移符 \ 存在时，它必须与跟在它后面的字符或字符串一起解读
     *我们处理的转义字符有以下几种形式
     * \b backspace
     * \f formfeed
     * \n newline
     * \r carriage return 回车
     * \s space 空格
     * \t tab
     * \e ASCII ESC ('\033')
     * \DDD 3位八进制数
     * \xDDD 3位十六进制数
     * \^C C是任何字符， 例如^A, ^B 在Ascii 表中都有对应的特殊含义
     * ASCII 字符表参见：
     * http://baike.baidu.com/pic/%E7%BE%8E%E5%9B%BD%E4%BF%A1%E6%81%AF%E4%BA%A4%E6%8D%A2%E6%A0%87%E5%87%86%E4%BB%A3%E7%A0%81/8950990/0/9213b07eca8065387d4c671896dda144ad348213?fr=lemma&ct=single#aid=0&pic=9213b07eca8065387d4c671896dda144ad348213
     */
    private Token getNextEscToken(int start) throws RegularMacroException {
        char c = mCurReg.charAt(start);
        String strC = String.valueOf(c).toUpperCase();
        char val = 0;
        char upC = strC.charAt(0);
        String desc = "";
        switch (upC) {
            case '\0':
                val = '\\';
                desc = "双斜杠";
                break;
            case 'B':
                val = '\b';
                desc = "退格";
                break;
            case 'F':
                val = '\f';
                desc = "formated";
                break;
            case 'N':
                val = '\n';
                desc = "newline";
                break;
            case 'S':
                val = ' ';
                desc = "空格";
                break;
            case 'R':
                val = '\r';
                desc = "回车";
                break;
            case 'T':
                val = '\t';
                desc = "制表";
                break;
            case 'X':
                index++;
                return getHexToken();
                default:
                {
                    return getOctToken();
                }
        }
        Token token = new Token();
        token.type = tokenMap[val];
        token.value = val;
        token.desc = desc;
        index++;
        return token;
    }

    /**
     * 8进制
     * @return
     */
    private Token getOctToken() throws RegularMacroException {
        int value = 0;
        for (int i = 0; i < 3; i++) {
            if (index >= mCurReg.length()) {
                break;
            }
            char c = mCurReg.charAt(index);
            if (Utils.isOct(c)) {
                value = (value << 3) | Utils.getIntValue(c);
                ++index;
            } else {
                break;
            }
        }
        if (value > 0) {
            Token token = new Token();
            token.type = TokenType.L;
            token.desc = "8进制转换数字:" + String.valueOf(value);
            return token;
        }
        throw new RegularMacroException("非法表达式，没有找到正确的 OctToken:" + mCurReg);
    }

    /**
     * 16进制数字
     * @return
     * @throws RegularMacroException
     */
    private Token getHexToken() throws RegularMacroException {
        int value = 0;
        for (int i = 0; i < 3; i++) {
            if (index >= mCurReg.length()) {
                break;
            }
            char c = mCurReg.charAt(index);
            if (Utils.isHex(c)) {
                value = (value << 4) | Utils.getIntValue(c);
                ++index;
            } else {
                break;
            }
        }
        if (value > 0) {
            Token token = new Token();
            token.type = TokenType.L;
            token.desc = "数字:" + String.valueOf(value);
            return token;
        }
        throw new RegularMacroException("非法表达式，没有找到正确的 HexToken:" + mCurReg);
    }
}
