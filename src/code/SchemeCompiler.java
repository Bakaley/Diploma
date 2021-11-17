package code;

import java.util.*;

public class SchemeCompiler {

    enum DATA_TYPE {
        INT,
        BOOL,
        STRING,
        FLOAT,
        NULL
    }

    final static String OPERATOR_PLUS = "+";
    final static String OPERATOR_MINUS = "-";
    final static String OPERATOR_MULTIPLICATION = "*";
    final static String OPERATOR_DIVISION = "/";
    final static String OPERATOR_MODULE_DIVISION = "%";
    final static String OPERATOR_ASSIGNMENT = "=";
    final static String OPERATOR_EQUALS = "==";
    final static String OPERATOR_NOT_EQUALS = "!=";
    final static String OPERATOR_AND = "&&";
    final static String OPERATOR_OR = "||";
    final static String OPERATOR_MORE = ">";
    final static String OPERATOR_LESS = "<";
    final static String OPERATOR_MORE_OR_EQUALS = ">=";
    final static String OPERATOR_LESS_OR_EQUALS = "<=";
    final static String OPERATOR_NOT = "!";
    final static String OPERATOR_INCREMENT = "++";
    final static String OPERATOR_DECREMENT = "--";

    final static ArrayList<String> unaryOperators = new ArrayList<>();
    final static ArrayList<String> binaryOperators = new ArrayList<>();
    final static ArrayList<String> operatorsList = new ArrayList<>();

    final static String legitVarSymbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
    final static String digits = "0123456789";

    static HashMap<String, Integer> priorityMap = new HashMap<>();

    AbstractDiagramNode currentBlock;
    static boolean stringConcatFlag;
    static boolean coutFlag;
    static String currentString;

    SchemeCompiler() {
        unaryOperators.add(OPERATOR_NOT);
        unaryOperators.add(OPERATOR_INCREMENT);
        unaryOperators.add(OPERATOR_DECREMENT);

        binaryOperators.add(OPERATOR_PLUS);
        binaryOperators.add(OPERATOR_MINUS);
        binaryOperators.add(OPERATOR_MULTIPLICATION);
        binaryOperators.add(OPERATOR_DIVISION);
        binaryOperators.add(OPERATOR_MODULE_DIVISION);
        binaryOperators.add(OPERATOR_ASSIGNMENT);
        binaryOperators.add(OPERATOR_EQUALS);
        binaryOperators.add(OPERATOR_NOT_EQUALS);
        binaryOperators.add(OPERATOR_AND);
        binaryOperators.add(OPERATOR_OR);
        binaryOperators.add(OPERATOR_MORE);
        binaryOperators.add(OPERATOR_LESS);
        binaryOperators.add(OPERATOR_MORE_OR_EQUALS);
        binaryOperators.add(OPERATOR_LESS_OR_EQUALS);

        operatorsList.addAll(unaryOperators);
        operatorsList.addAll(binaryOperators);

        priorityMap.put(OPERATOR_INCREMENT, 1);
        priorityMap.put(OPERATOR_DECREMENT, 2);
        priorityMap.put(OPERATOR_NOT, 3);
        priorityMap.put(OPERATOR_MULTIPLICATION, 4);
        priorityMap.put(OPERATOR_DIVISION, 5);
        priorityMap.put(OPERATOR_PLUS, 6);
        priorityMap.put(OPERATOR_MINUS, 7);
        priorityMap.put(OPERATOR_LESS, 8);
        priorityMap.put(OPERATOR_LESS_OR_EQUALS, 9);
        priorityMap.put(OPERATOR_MORE, 10);
        priorityMap.put(OPERATOR_MORE_OR_EQUALS, 11);
        priorityMap.put(OPERATOR_EQUALS, 12);
        priorityMap.put(OPERATOR_NOT_EQUALS, 13);
        priorityMap.put(OPERATOR_AND, 14);
        priorityMap.put(OPERATOR_OR, 15);
        priorityMap.put(OPERATOR_ASSIGNMENT, 16);
    }

    enum TOKEN_TYPE {
        VARIABLE,
        OPERATOR,
        DECLARATION,
        INT_CONST,
        FLOAT_CONST,
        STRING_CONST,
        BOOL_CONST,
        LEFT_BRACKET,
        RIGHT_BRACKET,
        NULL
    }

    public String compile(Scheme scheme){
        currentBlock = scheme.getStartTerm();
        ArrayList<AbstractDiagramNode> blocks = currentBlock.getChainedBlocksDown();
        for (AbstractDiagramNode block : blocks) {
            block.compilingReset();
        }
        Stack<List<Variable>> varStack = new Stack<>();
        ArrayList<String> loops = new ArrayList<>();
        ArrayList<DiagramDiamond> loopBlocks = new ArrayList<>();
        ArrayList<AbstractDiagramNode> scanned = new ArrayList<>();
        Stack <AbstractDiagramNode> scopeOpeners = new Stack<>();
        Stack <DiagramGeneralization> stack = new Stack<>();
        CodeGenerator codeGenerator = new CodeGenerator();

        currentBlock.generateCode(codeGenerator);
        currentBlock.get_lines_out().get(0).passed = true;
        stack.push(currentBlock.get_lines_out().get(0));
        varStack.push(new ArrayList<>());
        currentBlock = stack.lastElement().getnTo();

        while (!currentBlock.getClass().equals(DiagramTerminatorEnd.class)) {
            if (currentBlock.getClearCaption().isEmpty()) {
                if (currentBlock.getClass().equals(DiagramRhombus.class))
                    throw new SchemeCompilationException("В блоке условия должно находиться BOOL выражение", currentBlock);
                else if (currentBlock.getClass().equals(DiagramDiamond.class))
                    throw new SchemeCompilationException("Блоки циклов должны содержать имя своего цикла в верхней строке. Если блок начинает цикл, то во второй строке также должно находиться BOOL условие", currentBlock);
            }
            currentBlock.resetCodeString();
            String[] strings = currentBlock.getClearCaption().split("\r\n");
            if(scanned.contains(currentBlock)){
                if(!currentBlock.getClass().equals(DiagramRhombus.class)) throw new SchemeCompilationException("Возвращение в ранее пройденную точку программы хоть и возможно, но является плохим тоном. Вместо этого используйте циклы.", currentBlock);
            }
            else{
                scanned.add(currentBlock);
            }
            if (currentBlock.getClass().equals(DiagramRhombus.class)) {
                DiagramRhombus rhombus = (DiagramRhombus) currentBlock;
                if (strings.length != 1)
                    throw new SchemeCompilationException("В блоке условия может находиться только одна строка", rhombus);
                ArrayList<Lexeme> lexemes = lexemesParse(strings[0]);
                ArrayList<Operator> operators = operatorsList(lexemes);
                DATA_TYPE result = solve(lexemes, operators, varStack);
                if (currentBlock.getClass().equals(DiagramRhombus.class) && !result.equals(DATA_TYPE.BOOL))
                    throw new SchemeCompilationException("В блоке условия должно находиться BOOL выражение");
                varStack.push(new ArrayList<>());
                if (!rhombus.getTrueBranch().passed) {
                    rhombus.getTrueBranch().passed = true;
                    stack.add(rhombus.getTrueBranch());
                    codeGenerator.addIf(rhombus.caption);
                    scopeOpeners.push(rhombus);
                } else if (!rhombus.getFalseBranch().passed) {
                    rhombus.getFalseBranch().passed = true;
                    stack.add(rhombus.getFalseBranch());
                    codeGenerator.addElse();
                    scopeOpeners.push(rhombus);
                } else {
                    AbstractDiagramLink link = stack.pop();
                    while (!link.nFrom.getClass().equals(DiagramRhombus.class)) {
                        link = stack.pop();
                    }
                    currentBlock = link.nFrom;
                }
            } else if (currentBlock.getClass().equals(DiagramPreprocess.class)) {
                DiagramPreprocess pp = (DiagramPreprocess) currentBlock;
                if(strings.length == 0 || pp.getClearCaption().isEmpty()) throw new SchemeCompilationException("Все функции должны быть проименованы");
                else if(strings.length > 1) throw new SchemeCompilationException("В одном блоке подпроцесса может быть только одна функция");
                else{
                    String str = pp.getClearCaption();
                    if(str.charAt(pp.getClearCaption().length()-1) != ')' || str.charAt(pp.getClearCaption().length()-2) != '(')
                        throw new SchemeCompilationException("В конце имени функции должны стоять скобки без параметров \"()\"");
                    str = str.substring(0, str.length() - 1);
                    str = str.substring(0, str.length() - 1);
                    if(str.length() == 0) throw new SchemeCompilationException("Все функции должны быть проименованы");
                    for (char c : str.toCharArray()) {
                        if(legitVarSymbols.indexOf(c) < 0 && digits.indexOf(c) < 0) throw new SchemeCompilationException("В имени функции допустимы только латинские буквы и цифры");
                    }
                    if(digits.indexOf(str.charAt(0)) > -1) throw new SchemeCompilationException("Имя функции не может начинаться с цифры");
                }
                try{
                    DiagramPanel.generatedCode = DiagramPanel.compileDiagram(pp.innerScheme) + "\n" + DiagramPanel.generatedCode;
                }
                catch (SchemeCompilationException e){
                    currentBlock = pp;
                    throw new SchemeCompilationException("Следующий предопределенный процесс содержит внутренние ошибки:\n" + pp.getName(), pp);
                }
                pp.generateCode(codeGenerator);
                currentBlock.get_lines_out().get(0).passed = true;
                stack.add(currentBlock.get_lines_out().get(0));
            } else if (currentBlock.getClass().equals(DiagramDiamond.class)) {
                loopBlocks.add((DiagramDiamond) currentBlock);
                if (currentBlock.getClearCaption().isEmpty())
                    throw new SchemeCompilationException("Блоки циклов должны содержать имя своего цикла в верхней строке. Если блок начинает цикл, то во второй строке также должно находиться BOOL условие", currentBlock);
                ArrayList<Lexeme> loopLexemes = lexemesParse(strings[0]);
                switch (strings.length) {
                    case 1:
                        DATA_TYPE result = solve(loopLexemes, operatorsList(loopLexemes), varStack);
                        if (result == DATA_TYPE.STRING && loopLexemes.size() == 1 && loopLexemes.get(loopLexemes.size() - 1).type == TOKEN_TYPE.STRING_CONST) {
                            if (loops.contains(strings[0])) {
                                if (loops.get(loops.size() - 1).equals(strings[0])) {
                                    loops.remove(strings[0]);
                                    ((DiagramDiamond) currentBlock).cycleName = strings[0];
                                    ((DiagramDiamond) currentBlock).isOpening = false;
                                    currentBlock.generateCode(codeGenerator);
                                    currentBlock.get_lines_out().get(0).passed = true;
                                    stack.add(currentBlock.get_lines_out().get(0));
                                    varStack.pop();
                                } else
                                    throw new SchemeCompilationException("Циклы должны закрываться в последовательнности, обратной той, как открывались", currentBlock);
                            } else
                                throw new SchemeCompilationException("Неизвестное имя закрывающегося цикла. Если вы хотите объявить новый цикл, во второй строке должно находиться BOOL условие", currentBlock);
                        } else
                            throw new SchemeCompilationException("В первой строке блока цикла должно находиться имя цикла в формате STRING", currentBlock);
                        break;
                    case 2:
                        DATA_TYPE result2 = solve(loopLexemes, operatorsList(loopLexemes), varStack);
                        if (result2 == DATA_TYPE.STRING && loopLexemes.size() == 1 && loopLexemes.get(loopLexemes.size() - 1).type == TOKEN_TYPE.STRING_CONST) {
                            if (loops.contains(strings[0]))
                                throw new SchemeCompilationException("Нельзя внутри цикла объявить цикл с таким же именем", currentBlock);
                            else {
                                loops.add(strings[0]);
                                ((DiagramDiamond) currentBlock).cycleName = strings[0];
                                ((DiagramDiamond) currentBlock).isOpening = true;
                            }
                            ArrayList<Lexeme> lexemes2 = lexemesParse(strings[1]);
                            result2 = solve(lexemes2, operatorsList(lexemes2), varStack);
                            if (result2 != DATA_TYPE.BOOL)
                                throw new SchemeCompilationException("Во второй строке открывающегося цикла должно находиться BOOL условие", currentBlock);
                            else{
                                currentBlock.generateCode(codeGenerator);
                                currentBlock.get_lines_out().get(0).passed = true;
                                stack.add(currentBlock.get_lines_out().get(0));
                                varStack.push(new ArrayList<>());
                            }
                        } else
                            throw new SchemeCompilationException("В первой строке блока цикла должно находиться имя цикла в формате STRING", currentBlock);
                        break;
                    default:
                        throw new SchemeCompilationException("Блоки циклов должны содержать имя своего цикла в верхней строке. Если блок начинает цикл, то во второй строке также должно находиться BOOL условие", currentBlock);
                }
            } else if (currentBlock.getClass().equals(DiagramRectangle.class)) {
                for (String string : strings) {
                    if (string.isEmpty()) continue;
                    currentString = string;
                    ArrayList<Lexeme> lexemes = lexemesParse(string);
                    ArrayList<Operator> operators = operatorsList(lexemes);
                    DATA_TYPE result = solve(lexemes, operators, varStack);
                    if (stringConcatFlag) stringConcatFlag = false;
                    else currentBlock.addCodeString(currentString);
                }
                currentBlock.generateCode(codeGenerator);
                currentBlock.get_lines_out().get(0).passed = true;
                stack.add(currentBlock.get_lines_out().get(0));
            } else if (currentBlock.getClass().equals(DiagramParallelogram.class)) {
                for (String string : strings) {
                    if (string.isEmpty()) continue;
                    currentString = string;
                    ArrayList<Lexeme> lexemes = lexemesParse(string);
                    ArrayList<Operator> operators = operatorsList(lexemes);
                    DATA_TYPE result = solve(lexemes, operators, varStack);
                     if (coutFlag) coutFlag = false;
                    else currentBlock.addCodeString("cout << (" + currentString + ") << endl");
                }
                currentBlock.generateCode(codeGenerator);
                currentBlock.get_lines_out().get(0).passed = true;
                stack.add(currentBlock.get_lines_out().get(0));
            }
            AbstractDiagramNode next = stack.lastElement().nTo;
            ArrayList<DiagramGeneralization> linesIn = next.get_lines_in();
            if(linesIn.size() != 1){
                int notPassed = 0;
                for (DiagramGeneralization link : linesIn) {
                    if(!link.passed) notPassed++;
                }
                while(scopeOpeners.size() !=0 && next.scopeOpeners.contains(scopeOpeners.lastElement())){
                    next.scopeOpeners.remove(scopeOpeners.pop());
                    codeGenerator.closeBranch();
                    varStack.pop();
                }
                if(scopeOpeners.size() != 0 && notPassed != 0) next.scopeOpeners.add(scopeOpeners.pop());

                if(notPassed != 0){
                    AbstractDiagramLink link = stack.pop();
                    while(!link.nFrom.getClass().equals(DiagramRhombus.class)){
                        try {
                            link = stack.pop();
                        } catch (EmptyStackException e){
                            throw new SchemeCompilationException("Возвращение в ранее пройденную точку программы хоть и возможно, но является плохим тоном. Вместо этого используйте циклы.", currentBlock.get_lines_out().get(0).nTo);
                        }
                    }
                    currentBlock = link.nFrom;
                    codeGenerator.closeBranch();
                    varStack.pop();
                }
                else{
                    if(next.scopeOpeners.size() != 0) throw new SchemeCompilationException("Управление не может быть передано команде в другой контекстной области", next);
                    currentBlock = linesIn.get(0).nTo;
                }
            }
            else currentBlock = linesIn.get(0).nTo;
        }
        if(loops.size() != 0){
            String err = "Циклы со следующими именами должны быть закрыты:";
            for (String str :
                    loops) {
                err += "\n" + str;
            }
            throw new SchemeCompilationException(err);
        }

        for (DiagramDiamond loop : loopBlocks) {
            currentBlock = loop;
            if(loop.isOpening){
                ArrayList<AbstractDiagramNode> chainedNodes = loop.getBlocksInLoop();
                if(chainedNodes.contains(scheme.getEndTerm())) throw new SchemeCompiler.SchemeCompilationException("Нельзя выходить из цикла, не пройдя через его закрывающий блок", loop);
            }
            else{
                ArrayList<AbstractDiagramNode> chainedNodes = loop.getBlocksInLoop();
                if(chainedNodes.contains(scheme.getStartTerm())) throw new SchemeCompiler.SchemeCompilationException("Нельзя входить в середину цикла, не пройдя через его открывающий блок", loop);
            }
        }

        codeGenerator.closeBranch();
        varStack.pop();

        return codeGenerator.getGeneratedCode();
    }


    private ArrayList<String> parseInTokens(String str) {
        String currentToken = "";
        TOKEN_TYPE token_type = TOKEN_TYPE.VARIABLE;

        ArrayList<String> tokens = new ArrayList<>();
        for (char c : str.toCharArray()) {
            if (token_type == TOKEN_TYPE.STRING_CONST) {
                if (currentToken.length() != 1 && currentToken.toCharArray()[currentToken.length() - 1] == '"') {
                    if (c == ' ') {
                        tokens.add(currentToken);
                        currentToken = "";
                        token_type = TOKEN_TYPE.VARIABLE;
                    } else if (c == '+') {
                        tokens.add(currentToken);
                        currentToken = "+";
                        token_type = TOKEN_TYPE.OPERATOR;
                    } else if (c == ')') {
                        tokens.add(currentToken);
                        tokens.add(c + "");
                        currentToken = "";
                        token_type = TOKEN_TYPE.VARIABLE;
                    } else throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c, currentBlock);
                } else currentToken += c;
            } else if (c == ' ') {
                if (token_type == TOKEN_TYPE.OPERATOR) {
                    if (operatorsList.contains(currentToken)) tokens.add(currentToken);
                    else throw new SchemeCompilationException("Неизвестная лексема " + currentToken, currentBlock);
                } else if (currentToken.length() != 0) tokens.add(currentToken);
                currentToken = "";
                token_type = TOKEN_TYPE.VARIABLE;

            } else if (c == '(' || c == ')') {
                if (token_type == TOKEN_TYPE.OPERATOR) {
                    if (operatorsList.contains(currentToken)) {
                        tokens.add(currentToken);
                        tokens.add(c + "");
                        currentToken = "";
                        token_type = TOKEN_TYPE.VARIABLE;
                    } else throw new SchemeCompilationException("Неизвестная лексема " + currentToken, currentBlock);
                } else if (token_type == TOKEN_TYPE.INT_CONST || token_type == TOKEN_TYPE.FLOAT_CONST) {
                    tokens.add(currentToken);
                    tokens.add(c + "");
                    token_type = TOKEN_TYPE.VARIABLE;
                    currentToken = "";
                } else if (currentToken.length() != 0) {
                    tokens.add(currentToken);
                    tokens.add(c + "");
                    currentToken = "";
                    token_type = TOKEN_TYPE.VARIABLE;
                } else tokens.add(c + "");
            } else if (digits.indexOf(c) > -1) {
                if (currentToken.length() == 0) {
                    currentToken = "" + c;
                    token_type = TOKEN_TYPE.INT_CONST;
                } else if (token_type == TOKEN_TYPE.INT_CONST || token_type == TOKEN_TYPE.FLOAT_CONST || token_type == TOKEN_TYPE.VARIABLE)
                    currentToken += c;
                else if (token_type == TOKEN_TYPE.OPERATOR) {
                    if (operatorsList.contains(currentToken)) {
                        tokens.add(currentToken);
                        currentToken = "" + c;
                        token_type = TOKEN_TYPE.INT_CONST;
                    } else throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c, currentBlock);
                } else {
                    throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c, currentBlock);
                }

            } else if (c == '"') {
                if (token_type == TOKEN_TYPE.OPERATOR && operatorsList.contains(currentToken)) {
                    tokens.add(currentToken);
                    currentToken = "" + c;
                    token_type = TOKEN_TYPE.STRING_CONST;
                } else if (currentToken.length() == 0) {
                    currentToken = "" + c;
                    token_type = TOKEN_TYPE.STRING_CONST;
                } else throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c, currentBlock);
            } else if (c == '.') {
                if (token_type == TOKEN_TYPE.INT_CONST) {
                    currentToken += c;
                    token_type = TOKEN_TYPE.FLOAT_CONST;
                } else {
                    throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c, currentBlock);
                }
            } else if (c == '-') {
                if (currentToken.length() == 0) {
                    if (tokens.size() == 0 || operatorsList.contains(tokens.get(tokens.size() - 1))) {
                        currentToken += c;
                        token_type = TOKEN_TYPE.INT_CONST;
                    } else {
                        currentToken += c;
                        token_type = TOKEN_TYPE.OPERATOR;
                    }
                } else if (token_type == TOKEN_TYPE.INT_CONST || token_type == TOKEN_TYPE.FLOAT_CONST || token_type == TOKEN_TYPE.VARIABLE) {
                    tokens.add(currentToken);
                    currentToken = "" + c;
                    token_type = TOKEN_TYPE.OPERATOR;
                } else if (token_type == TOKEN_TYPE.OPERATOR) {
                    currentToken = currentToken + c;
                    if (currentToken.length() >= 3) throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c, currentBlock);
                    else if (!operatorsList.contains(currentToken))
                        throw new SchemeCompilationException("Неизвестная лексема " + currentToken, currentBlock);
                } else throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c, currentBlock);
            } else if (legitVarSymbols.indexOf(c) > -1) {
                if (token_type == TOKEN_TYPE.OPERATOR) {
                    if (operatorsList.contains(currentToken)) {
                        tokens.add(currentToken);
                        currentToken = "";
                        token_type = TOKEN_TYPE.VARIABLE;
                    } else throw new SchemeCompilationException("Неизвестная лексема " + currentToken, currentBlock);
                }
                if (token_type == TOKEN_TYPE.INT_CONST || token_type == TOKEN_TYPE.FLOAT_CONST)
                    throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c, currentBlock);
                currentToken = currentToken + c;
            } else if (legitVarSymbols.indexOf(c) < 0 && (token_type == TOKEN_TYPE.INT_CONST || token_type == TOKEN_TYPE.FLOAT_CONST || token_type == TOKEN_TYPE.VARIABLE)) {
                if (currentToken.length() != 0) tokens.add(currentToken);
                currentToken = "" + c;
                token_type = TOKEN_TYPE.OPERATOR;
            } else if (legitVarSymbols.indexOf(c) < 0 && token_type == TOKEN_TYPE.OPERATOR) {
                currentToken = currentToken + c;
                if (currentToken.length() >= 3) throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c, currentBlock);
                else if (!operatorsList.contains(currentToken))
                    throw new SchemeCompilationException("Неизвестная лексема " + currentToken, currentBlock);
            } else throw new SchemeCompilationException(currentToken, currentBlock);
        }
        if (token_type == TOKEN_TYPE.STRING_CONST) {
            if (currentToken.length() == 1) throw new SchemeCompilationException("Неизвестная лексема " + currentToken, currentBlock);
            else if (currentToken.toCharArray()[currentToken.length() - 1] == '"') tokens.add(currentToken);
            else throw new SchemeCompilationException("Неизвестная лексема " + currentToken, currentBlock);
        } else if (token_type == TOKEN_TYPE.OPERATOR) {
            if (operatorsList.contains(currentToken)) tokens.add(currentToken);
            else throw new SchemeCompilationException("Неизвестная лексема " + currentToken, currentBlock);
        } else if (token_type == TOKEN_TYPE.INT_CONST || token_type == TOKEN_TYPE.FLOAT_CONST) {
            tokens.add(currentToken);
        } else if (currentToken.length() != 0) tokens.add(currentToken);
        return tokens;
    }

    private ArrayList<Lexeme> lexemesParse(String str) {
        ArrayList<String> tokens = parseInTokens(str);
        ArrayList<Lexeme> lexes = new ArrayList<>();
        for (int i = 0; i < tokens.size(); i++) {
            if (parseTokenType(tokens.get(i)) == TOKEN_TYPE.OPERATOR) {
                int leftBracketsCount = 0;
                int rightBracketsCount = 0;
                for (int j = 0; j < i; j++) {
                    if (tokens.get(j).equals("(")) leftBracketsCount++;
                    if (tokens.get(j).equals(")")) leftBracketsCount--;
                }
                for (int j = i; j < tokens.size(); j++) {
                    if (tokens.get(j).equals("(")) rightBracketsCount--;
                    if (tokens.get(j).equals(")")) rightBracketsCount++;
                }
                if (leftBracketsCount == rightBracketsCount)
                    lexes.add(new Operator(tokens.get(i), leftBracketsCount, i));
                else throw new SchemeCompilationException("Количества открывающих и закрывающих скобок отличаются", currentBlock);
            } else if (parseTokenType(tokens.get(i)) == TOKEN_TYPE.VARIABLE) {
                lexes.add(new Variable(tokens.get(i), DATA_TYPE.NULL));
            } else if (parseTokenType(tokens.get(i)) != TOKEN_TYPE.NULL) {
                lexes.add(new Lexeme(tokens.get(i), parseTokenType(tokens.get(i))));
            } else throw new SchemeCompilationException("Unknown lexeme " + tokens.get(i), currentBlock);
        }
        return lexes;
    }

    private ArrayList<Operator> operatorsList(ArrayList<Lexeme> lexes) {
        ArrayList<Operator> operators = new ArrayList<>();
        for (int i = 0; i < lexes.size(); i++) {
            if (lexes.get(i).type == TOKEN_TYPE.OPERATOR) {
                operators.add((Operator) lexes.get(i));
            }
        }
        Collections.sort(operators, new OperatorPriorityComparator());
        return operators;
    }


    private DATA_TYPE solve(ArrayList<Lexeme> lexemes, ArrayList<Operator> operators, Stack<List<Variable>> vars) {

        ArrayList<Object> convertedData = new ArrayList<>();
        String lastLex = "";
        for (Lexeme lexeme : lexemes) {
            if (lexemes.size() == 1 && !currentBlock.getClass().equals(DiagramRhombus.class) && !currentBlock.getClass().equals(DiagramParallelogram.class) && !currentBlock.getClass().equals(DiagramDiamond.class)) throw new SchemeCompilationException("Выражение " + lexeme.sign + " не является операцией", currentBlock);
            switch (lexeme.type) {
                case VARIABLE:
                    if (isVarDeclared(vars, lexeme.sign)){
                        if(currentBlock.getClass().equals(DiagramParallelogram.class) && lexemes.size() == 1){
                            convertedData.add(getVariableByName(vars, lexeme.sign).dataType);
                            currentBlock.addCodeString("cout << " + lexeme.sign + " << endl");
                            coutFlag = true;
                        }
                        else if(currentBlock.getClass().equals(DiagramRhombus.class) && lexemes.size() == 1){
                            if(isVarDeclared(vars, lexeme.sign)){
                                convertedData.add(getVariableByName(vars,lexeme.sign).dataType);
                            }
                            else throw new SchemeCompilationException("Неизвестная переменная " + lexeme.sign, currentBlock);
                        }
                        else if (lexemes.get(1) == lexeme && lexemes.get(0).type == TOKEN_TYPE.DECLARATION) {
                            if(currentBlock.getClass().equals(DiagramRectangle.class) && lexemes.size() == 2){
                                throw new SchemeCompilationException("Все объявленные переменные либо должны иметь значение по умолчанию, либо находиться в блоке пользовательского ввода:\n" + currentString, currentBlock);
                            }
                            else if(currentBlock.getClass().equals(DiagramParallelogram.class) && lexemes.size() >= 2){
                                convertedData.add(getVariableByName(vars, lexeme.sign).dataType);
                                currentBlock.addCodeString(currentString);
                                currentBlock.addCodeString("cin >> " + lexemes.get(1).sign);
                                coutFlag = true;
                            }
                            else if (currentBlock.getClass().equals(DiagramRectangle.class) && lexemes.size() > 2){
                                convertedData.add(getVariableByName(vars, lexeme.sign).dataType);
                            }
                            else{
                                throw new SchemeCompilationException("Объявление переменных допустимо только в блоке операций или ввода:\n" + currentString, currentBlock);
                            }
                        }
                        else if(isVarDeclared(vars, lexeme.sign)){
                            convertedData.add(getVariableByName(vars, lexeme.sign).dataType);
                        }
                        else throw new SchemeCompilationException("Неизвестная переменная " + lexeme.sign, currentBlock);
                    } else {
                        throw new SchemeCompilationException("Неизвестная переменная " + lexeme.sign, currentBlock);
                    }
                    if (lastLex.length() != 0)
                        throw new SchemeCompilationException("Выражение " + lastLex + " " + lexeme.sign + " не является операцией", currentBlock);
                    else lastLex = lexeme.sign;
                    break;
                case STRING_CONST:
                    convertedData.add(DATA_TYPE.STRING);
                    if (lastLex.length() != 0)
                        throw new SchemeCompilationException("Выражение " + lastLex + " " + lexeme.sign + " не является операцией", currentBlock);
                    else lastLex = lexeme.sign;
                    break;
                case INT_CONST:
                    convertedData.add(DATA_TYPE.INT);
                    if (lastLex.length() != 0)
                        throw new SchemeCompilationException("Выражение " + lastLex + " " + lexeme.sign + " не является операцией", currentBlock);
                    else lastLex = lexeme.sign;
                    break;
                case FLOAT_CONST:
                    convertedData.add(DATA_TYPE.FLOAT);
                    if (lastLex.length() != 0)
                        throw new SchemeCompilationException("Выражение " + lastLex + " " + lexeme.sign + " не является операцией", currentBlock);
                    else lastLex = lexeme.sign;
                    break;
                case BOOL_CONST:
                    convertedData.add(DATA_TYPE.BOOL);
                    if (lastLex.length() != 0)
                        throw new SchemeCompilationException("Выражение " + lastLex + " " + lexeme.sign + " не является операцией", currentBlock);
                    else lastLex = lexeme.sign;
                    break;
                case OPERATOR:
                    convertedData.add(lexeme);
                    lastLex = "";
                    break;
                case DECLARATION:
                    if (lexemes.get(0) == lexeme) {
                        if (lexemes.get(1).type == TOKEN_TYPE.VARIABLE) {
                            if (!isVarDeclared(vars, lexemes.get(1).sign)) {
                                switch (lexeme.sign) {
                                    case "int":
                                        vars.lastElement().add(new Variable(lexemes.get(1).sign, DATA_TYPE.INT));
                                        break;
                                    case "float":
                                        vars.lastElement().add(new Variable(lexemes.get(1).sign, DATA_TYPE.FLOAT));
                                        break;
                                    case "string":
                                        vars.lastElement().add(new Variable(lexemes.get(1).sign, DATA_TYPE.STRING));
                                        break;
                                    case "bool":
                                        vars.lastElement().add(new Variable(lexemes.get(1).sign, DATA_TYPE.BOOL));
                                        break;
                                    default: throw new SchemeCompilationException("Неизвестный тип данных " + lexeme.sign, currentBlock);
                                }
                            } else
                                throw new SchemeCompilationException("Переменная с таким именем " + lexemes.get(1).sign + " уже существует в данной области", currentBlock);
                        } else
                            throw new SchemeCompilationException("За объявленным типом " + lexeme.sign + " должна следовать переменная", currentBlock);
                    } else
                        throw new SchemeCompilationException("Объявление переменной " + lexeme.sign + " допустимо только в начале строки", currentBlock);
                    break;
            }
        }

        String data = "";
        boolean assignementFlag = false;

        for (Object obj : convertedData) {
            if (obj.getClass().equals(DATA_TYPE.class)) data += obj;
            else data += ((Operator) obj).sign;
        }
        for (Operator op : operators) {
            if (binaryOperators.contains(((Operator) op).sign)) {
                int n = convertedData.indexOf(op);
                DATA_TYPE data_type1;
                DATA_TYPE data_type2;
                try {
                    data_type1 = (DATA_TYPE) convertedData.get(n - 1);
                } catch (ClassCastException e) {
                    throw new SchemeCompilationException("Оператор " + ((Operator) op).sign + " неприменим к " + convertedData.get(n - 1), currentBlock);
                } catch (IndexOutOfBoundsException e) {
                    throw new SchemeCompilationException("Оператор " + ((Operator) op).sign + " применяется к двум операндам", currentBlock);
                }
                try {
                    data_type2 = (DATA_TYPE) convertedData.get(convertedData.indexOf(op) + 1);
                } catch (ClassCastException e) {
                    throw new SchemeCompilationException("Оператор " + ((Operator) op).sign + " неприменим к " + convertedData.get(n + 1), currentBlock);
                } catch (IndexOutOfBoundsException e) {
                    throw new SchemeCompilationException("Оператор " + ((Operator) op).sign + " применяется к двум операндам", currentBlock);
                }
                if (op.sign.equals("=")) {
                    if (lexemes.get(0).type == TOKEN_TYPE.DECLARATION && lexemes.get(1).type == TOKEN_TYPE.VARIABLE && lexemes.get(2) == op) {
                        data_type1 = getVariableByName(vars, lexemes.get(1).sign).dataType;
                    } else if (lexemes.get(0).type == TOKEN_TYPE.VARIABLE && lexemes.get(1) == op) {
                        data_type1 = getVariableByName(vars, lexemes.get(0).sign).dataType;
                    } else {
                        throw new SchemeCompilationException("Некорректное присваивание значения переменной", currentBlock);
                    }
                }
                DATA_TYPE dt = binaryOperationCheck(op, data_type1, data_type2);
                if (dt != DATA_TYPE.NULL) {

                    convertedData.remove(n - 1);
                    convertedData.remove(n - 1);
                    convertedData.remove(n - 1);
                    convertedData.add(n - 1, dt);

                    data = "";
                    for (Object obj : convertedData) {
                        if (obj.getClass().equals(DATA_TYPE.class)) data += obj;
                        else data += ((Operator) obj).sign;
                    }
                    if(op.sign.equals(OPERATOR_ASSIGNMENT))
                        assignementFlag = true;
                    } else
                    if(op.sign.equals(OPERATOR_ASSIGNMENT)){
                        Lexeme var;
                        if(lexemes.get(1).type == TOKEN_TYPE.VARIABLE) var = lexemes.get(1);
                        else var = lexemes.get(0);
                        throw new SchemeCompilationException("Переменной " + var.sign + " типа " + getVariableByName(vars, var.sign).dataType + " не может быть присвоено значение " + convertedData.get(convertedData.size()-1), currentBlock);
                    }
                    else throw new SchemeCompilationException("Оператор " + ((Operator) op).sign + " неприменим к " + convertedData.get(convertedData.indexOf(op) - 1) + " и " + convertedData.get(convertedData.indexOf(op) + 1), currentBlock);
            } else if (unaryOperators.contains(((Operator) op).sign)) {
                int n = convertedData.indexOf(op);
                int operandNumber = n;
                DATA_TYPE data_type;
                try {
                    switch (((Operator) op).sign) {
                        case OPERATOR_NOT:
                            operandNumber = n + 1;
                            break;
                        case OPERATOR_INCREMENT:
                        case OPERATOR_DECREMENT:
                            assignementFlag = true;
                            operandNumber = n - 1;
                            break;
                    }
                    data_type = (DATA_TYPE) convertedData.get(operandNumber);
                    if (unaryOperationCheck(op, data_type) != DATA_TYPE.NULL) {
                        convertedData.remove(operandNumber);
                        n = convertedData.indexOf(op);
                        convertedData.remove(op);
                        convertedData.add(n, unaryOperationCheck(op, data_type));
                        data = "";
                        for (Object obj : convertedData) {
                            if (obj.getClass().equals(DATA_TYPE.class)) data += obj;
                            else data += ((Operator) obj).sign;
                        }
                    } else
                        throw new SchemeCompilationException("Оператор " + ((Operator) op).sign + " неприменим к " + convertedData.get(operandNumber), currentBlock);
                } catch (ClassCastException e) {
                    throw new SchemeCompilationException("Оператор " + ((Operator) op).sign + " неприменим к " + convertedData.get(operandNumber), currentBlock);
                } catch (IndexOutOfBoundsException e) {
                    throw new SchemeCompilationException("Оператор " + ((Operator) op).sign + " не имеет операнда", currentBlock);
                }
            }
        }
        if(!assignementFlag && currentBlock.getClass().equals(DiagramRectangle.class))
            throw new SchemeCompilationException("Операция " + currentString + " не имеет смысла, так как её результат нигде не хранится и не используется", currentBlock);
        return (DATA_TYPE)convertedData.get(0);
    }


    private TOKEN_TYPE parseTokenType(String str){
        try{
            int n = Integer.parseInt(str);
            return TOKEN_TYPE.INT_CONST;
        }
        catch (NumberFormatException numberFormatExceptionInt) {

        }
        try {
            float f = Float.parseFloat(str);
            return TOKEN_TYPE.FLOAT_CONST;
        }
        catch (NumberFormatException numberFormatExceptionFloat) {
        }
        if(str.equals("true") || str.equals("false")) return TOKEN_TYPE.BOOL_CONST;
        if(str.equals("int") || str.equals("bool") || str.equals("float") || str.equals("string")) return TOKEN_TYPE.DECLARATION;
        if(str.toCharArray()[0] == '"' && str.toCharArray()[str.length()-1]== '"') return TOKEN_TYPE.STRING_CONST;
        if(str.equals("(")) return TOKEN_TYPE.LEFT_BRACKET;
        if(str.equals(")")) return TOKEN_TYPE.RIGHT_BRACKET;
        if(operatorsList.contains(str)) return TOKEN_TYPE.OPERATOR;
        return TOKEN_TYPE.VARIABLE;
    }

    private Variable getVariableByName(Stack<List<Variable>> varStack, String name){
        for (List<Variable> list :
                varStack) {
            for (Variable var :
                    list) {
                if (var.sign.equals(name)) return var;
            }
        }
        return null;
    }

    private class Lexeme {
        String sign;
        TOKEN_TYPE type;

        Lexeme(){

        }

        private Lexeme(String tSign, TOKEN_TYPE tType){
            sign = tSign;
            type = tType;
        }
    }

    private class Variable extends Lexeme {
        DATA_TYPE dataType;

        private Variable(String varName, DATA_TYPE varData){
            type = TOKEN_TYPE.VARIABLE;
            dataType = varData;
            sign = varName;
        }
    }

    private class Operator extends Lexeme {
        int layer;
        int place;

        Operator(String opSign, int opLayer, int opPlace){
            type = TOKEN_TYPE.OPERATOR;
            sign = opSign;
            layer = opLayer;
            place = opPlace;
        }
    }

    private boolean isVarDeclared(Stack<List<Variable>> varStack, String variable){
        for (List<Variable> list :
                varStack) {
            for (Variable var :
                    list) {
                if(var.sign.equals(variable)) return true;
            }
        }
        return false;
    }

    private class OperatorPriorityComparator implements Comparator<Operator> {
        @Override
        public int compare(Operator o1, Operator o2) {
            int n = o2.layer - o1.layer;
            if(n == 0) return priorityMap.get(o1.sign) - priorityMap.get(o2.sign);
            else return n;
        }
    }

    private DATA_TYPE unaryOperationCheck (Operator operator, DATA_TYPE var1){
        switch (operator.sign) {
            case OPERATOR_NOT:
                if(var1 == DATA_TYPE.BOOL) return DATA_TYPE.BOOL;
                    break;
            case OPERATOR_INCREMENT:
            case OPERATOR_DECREMENT:
                if(var1 == DATA_TYPE.INT) return DATA_TYPE.INT;
                if(var1 == DATA_TYPE.FLOAT) return DATA_TYPE.FLOAT;
                break;
        }
        return DATA_TYPE.NULL;
    }

    private DATA_TYPE binaryOperationCheck (Operator operator, DATA_TYPE var1, DATA_TYPE var2) {
        ArrayList<DATA_TYPE> types = new ArrayList<>();
        types.add(var1);
        types.add(var2);
        switch (operator.sign) {
            case OPERATOR_PLUS:
                ArrayList<Lexeme> lexes = lexemesParse(currentString);
                String str = "";
                if (types.contains(DATA_TYPE.STRING)){
                    if(!currentBlock.getClass().equals(DiagramRectangle.class))
                        throw new SchemeCompilationException("Объединять строки можно только в блоке операций", currentBlock);
                    if(lexes.size() != 5)
                        throw new SchemeCompilationException("Объединять строки можно только отдельной операцией, и только по две за раз", currentBlock);
                    if(var1 == DATA_TYPE.STRING && var2 == DATA_TYPE.STRING) return DATA_TYPE.STRING;
                    else if(var1 == DATA_TYPE.STRING && var2 != DATA_TYPE.STRING){
                        lexes.set(4, new Lexeme("to_string(" + lexes.get(4).sign + ")", TOKEN_TYPE.STRING_CONST));
                    }
                    else if(var1 != DATA_TYPE.STRING && var2 == DATA_TYPE.STRING){
                        lexes.set(2, new Lexeme("to_string(" + lexes.get(2).sign + ")", TOKEN_TYPE.STRING_CONST));
                    }
                    for (int i = 0; i < lexes.size(); i++) {
                        str += lexes.get(i).sign;
                        if(i < lexes.size() - 1) str += " ";
                    }
                    currentBlock.addCodeString(str);
                    stringConcatFlag = true;
                    return DATA_TYPE.STRING;
                }
                if (types.contains(DATA_TYPE.FLOAT) && !types.contains(DATA_TYPE.BOOL)) return DATA_TYPE.FLOAT;
                if (types.get(0) == types.get(1) && types.get(0) == DATA_TYPE.INT) return DATA_TYPE.INT;
                break;
            case OPERATOR_MINUS:
            case OPERATOR_MULTIPLICATION:
            case OPERATOR_DIVISION:
                if (types.contains(DATA_TYPE.STRING) || types.contains(DATA_TYPE.BOOL)) return DATA_TYPE.NULL;
                if (types.get(0) == types.get(1) && types.get(0) == DATA_TYPE.INT) return DATA_TYPE.INT;
                if (types.contains(DATA_TYPE.FLOAT)) return DATA_TYPE.FLOAT;
                break;
            case OPERATOR_MODULE_DIVISION:
                if (types.get(0) == types.get(1) && types.get(0) == DATA_TYPE.INT) return DATA_TYPE.INT;
                break;
            case OPERATOR_AND:
            case OPERATOR_OR:
                if (types.get(0) == types.get(1) && types.get(0) == DATA_TYPE.BOOL) return DATA_TYPE.BOOL;
                break;
            case OPERATOR_EQUALS:
            case OPERATOR_NOT_EQUALS:
                if (types.get(0) == types.get(1)) return DATA_TYPE.BOOL;
                if (types.contains(DATA_TYPE.INT) && types.contains(DATA_TYPE.FLOAT)) return DATA_TYPE.BOOL;
                break;
            case OPERATOR_LESS:
            case OPERATOR_LESS_OR_EQUALS:
            case OPERATOR_MORE:
            case OPERATOR_MORE_OR_EQUALS:
                if (types.get(0) == types.get(1) && types.get(0) == DATA_TYPE.INT) return DATA_TYPE.BOOL;
                if (types.get(0) == types.get(1) && types.get(0) == DATA_TYPE.FLOAT) return DATA_TYPE.BOOL;
                if (types.contains(DATA_TYPE.INT) && types.contains(DATA_TYPE.FLOAT)) return DATA_TYPE.BOOL;
                break;
            case OPERATOR_ASSIGNMENT:
                if(var1.equals(var2)) return var1;
        }
        return DATA_TYPE.NULL;
    }

    public static class SchemeCompilationException extends RuntimeException {
        public SchemeCompilationException(String message) {
            super(message);
        }
        public SchemeCompilationException(String message, AbstractDiagramNode block) {
            super(message);
            block.errorPaint();
            DiagramPanel.canvasRepaint();
        }
        public SchemeCompilationException(String message, List<AbstractDiagramNode> blocks) {
            super(message);
            for (AbstractDiagramNode block : blocks) {
                block.errorPaint();
            }
            DiagramPanel.canvasRepaint();
        }
    }

    public static class CodeGenerator{
        int indents = 0;
        String code = "";
        void addMain(AbstractDiagramNode currentBlock){
            if(((Scheme)(currentBlock.getParent())).parentScheme != null){
                code += "void " + currentBlock.getParent().caption + "{\n";
            }
            else {
                code += "int main(){\n";
            }
            indents++;
        }
        void add (String str){
            for (int i = 0; i < indents; i++) {
                code += "\t";
            }
            code += str + ";\n";
        }
        void addIf(String str){
            for (int i = 0; i < indents; i++) {
                code += "\t";
            }
            code += "if(" + str + "){\n";
            indents ++;
        }
        void addElse(){
            for (int i = 0; i < indents; i++) {
                code += "\t";
            }
            code += "else {\n";
            indents ++;
        }
        void closeBranch(){
            indents--;
            for (int i = 0; i < indents; i++) {
                code += "\t";
            }
            code += "}\n";
        }
        void addWhile(String str){
            for (int i = 0; i < indents; i++) {
                code += "\t";
            }
            code += "while(" + str + "){\n";
            indents ++;
        }
        String getGeneratedCode(){
            return code;
        }
    }
}