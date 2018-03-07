package main;

import inputsystem.Input;
import regular.MacroProcessor;
import regular.RegularMacroException;

import java.io.File;

/**
 * Created by baobaowang on 2018/2/28.
 */
public class Test {
    public static void main(String[] args) {
        MacroProcessor processor = new MacroProcessor("F:\\compile\\MyCompiler\\macrodefine.txt");
        processor.printMacroDefineMap();
        try {
            String regExp = processor.processRegExp("F:\\compile\\MyCompiler\\regExp.txt");
            System.out.println(regExp);
        } catch (RegularMacroException e) {
            e.printStackTrace();
        }
    }
}
