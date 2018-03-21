package main;

import inputsystem.Input;
import regular.MacroProcessor;
import regular.RegLexer;
import regular.RegularMacroException;
import tools.LogUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by baobaowang on 2018/2/28.
 */
public class Test {
    public static void main(String[] args) {
        MacroProcessor processor = new MacroProcessor("F:\\compile\\MyCompiler\\macrodefine.txt");
        LogUtil.printBound("正则表达式宏定义");
        processor.printMacroDefineMap();
        try {
            ArrayList<String> regList = processor.processRegExp("F:\\compile\\MyCompiler\\regExp.txt");
            LogUtil.printBound("正则表达式宏展开结果");
            for (String reg :
                    regList) {
                System.out.println(reg);
            }
            LogUtil.printBound("正则表达式标记流");
            RegLexer regLexer = new RegLexer();
            regLexer.prepareLex("[\\b\\r\\n]");
            regLexer.prepareLex("[\"+*?\"]");
            regLexer.prepareLex("[\\x1F]");
            regLexer.prepareLex("[\\71]");
            while(regLexer.hasNext()){
                RegLexer.Token token = regLexer.nextToken();
                System.out.println(token.toString());
            }
        } catch (RegularMacroException e) {
            e.printStackTrace();
        }
    }
}
