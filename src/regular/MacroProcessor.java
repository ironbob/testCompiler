package regular;

import inputsystem.Input;
import inputsystem.InputUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by baobaowang on 2018/2/28.
 */
public class MacroProcessor {
    HashMap<String, String> mDefineMap = new HashMap<>();

    public MacroProcessor(String macroDefineFile) {
        Input input = new Input();
        input.ii_newFile(macroDefineFile);
        input.ii_advance();
        input.ii_pushback(1);
        preProcess(input);
    }

    private void preProcess(Input input) {
        while (true) {
            InputUtils.skipSpace(input);
            String macroName = InputUtils.getWord(input);
            if (macroName.equals("")) {
                break;
            }
            InputUtils.skipSpace(input);
            String define = InputUtils.getWord(input);
            mDefineMap.put(macroName, define);
        }
    }

    public ArrayList<String> processRegExp(String regExpFileName) throws RegularMacroException {
        ArrayList<String> list = new ArrayList<>();
        Input input = new Input();
        input.ii_newFile(regExpFileName);
        input.ii_advance();
        input.ii_pushback(1);
        InputUtils.skipSpace(input);
        boolean inQuot = false;
        StringBuilder sb = new StringBuilder();
        while (input.ii_lookahead(1) != Input.EOF) {
            if (input.ii_lookahead(1) == '{' && !inQuot) {
                String macro = getNextMacro(input);
                sb.append(expandMacro(macro));
            } else if (input.ii_lookahead(1) == '"') {
                inQuot = !inQuot;
                sb.append(InputUtils.getNextChar(input));
            } else if(input.ii_lookahead(1) == '\r' || input.ii_lookahead(1) == '\n'){
                list.add(sb.toString());
                InputUtils.skipSpace(input);
                sb.setLength(0);
            }else {
                sb.append(InputUtils.getNextChar(input));
            }
        }
        return list;
    }

    private String expandMacro(String macro) throws RegularMacroException {
        String content = macro;
        while (true) {
            if (!content.contains("{")) {
                return content;
            } else {
                String macroName = getFirstMacroName(content);
                if(macroName == null){
                    return content;
                }
                String define = mDefineMap.get(macroName);
                if (define == null) {
                    throw new RegularMacroException("没有发现'" + macro + "'的宏定义");
                }
                int index = content.indexOf(macroName);
                content = content.substring(0, index - 1) + "(" + define + ")" + content.substring(index + macroName.length() + 1);
            }
        }


    }

    private String getFirstMacroName(String macro) throws RegularMacroException {
        StringBuilder sb = new StringBuilder();
        boolean inQuot = false;
        for (int i = 0; i < macro.length(); i++) {
            char c = macro.charAt(i);
            if (c == '{' && !inQuot) {
                int j = i + 1;
                while (macro.charAt(j) != '}' && j <= macro.length()) {
                    sb.append(macro.charAt(j));
                    j++;
                }
                if (j == macro.length()) {
                    throw new RegularMacroException("错误的表达式,缺少 '}',macro = " + macro);
                }
                return sb.toString();
            } else if (c == '"') {
                inQuot = !inQuot;
            }
        }
        return null;
    }

    //返回 {xxx}
    private String getNextMacro(Input input) throws RegularMacroException {
        StringBuilder sb = new StringBuilder();
        while (input.ii_lookahead(1) != '}') {
            if (input.ii_lookahead(1) != '\n' && input.ii_lookahead(1) != Input.EOF) {
                sb.append(InputUtils.getNextChar(input));
            } else if (input.ii_lookahead(1) == Input.EOF) {
                throw new RegularMacroException("错误的表达式,缺少 '}',lineNum:," + input.ii_lineno());
            }
        }
        sb.append(InputUtils.getNextChar(input));
        return sb.toString();
    }

    public void printMacroDefineMap() {
        Set<Map.Entry<String, String>> set = mDefineMap.entrySet();
        for (Map.Entry<String, String> entry : set) {
            System.out.println("macroName = " + entry.getKey() + ", define = " + entry.getValue());
        }
    }
}
