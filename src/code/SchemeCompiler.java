package code;

import javax.swing.*;
import java.lang.reflect.Array;
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

    static String legitVarSymbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
    static String digits = "0123456789";

    static HashMap<String, Integer> priorityMap = new HashMap<>();

    static AbstractDiagramNode currentBlock;

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

    ArrayList<Variable> variables = new ArrayList<>();
    ArrayList<String> loops = new ArrayList<>();

    public void compile(ArrayList<AbstractDiagramNode> blocks){
        variables = new ArrayList<>();
        loops = new ArrayList<>();
        ArrayList<DiagramDiamond> loopBlocks = new ArrayList<>();
        for (AbstractDiagramNode block : blocks) {
            currentBlock = block;
            if(DiagramTerminator.class.isAssignableFrom(block.getClass())) continue;
            if(block.getClearCaption().isEmpty()){
                if(block.getClass().equals(DiagramRhombus.class)) throw new SchemeCompilationException("В блоке условия должно находиться BOOL выражение");
                else if(block.getClass().equals(DiagramDiamond.class)) throw new SchemeCompilationException("Блоки циклов должны содержать имя своего цикла в верхней строке. Если блок начинает цикл, то во второй строке также должно находиться BOOL условие");
                else continue;
            }
            if(block.getClass().equals(DiagramDiamond.class)) loopBlocks.add((DiagramDiamond)block);
            String[] strings = block.getClearCaption().split("\r\n");
            if(block.getClass().equals(DiagramRhombus.class) && strings.length != 1) throw new SchemeCompilationException("В блоке условия должно находиться BOOL выражение");
            if(block.getClass().equals(DiagramDiamond.class)){
                if(block.getClearCaption().isEmpty())throw new SchemeCompilationException("Блоки циклов должны содержать имя своего цикла в верхней строке. Если блок начинает цикл, то во второй строке также должно находиться BOOL условие");
                ArrayList<Lexeme> lexemes1 = lexemesParse(strings[0]);
                switch (strings.length){
                    case 1:
                        DATA_TYPE result = solve(lexemes1, operatorsList(lexemes1), new ArrayList<>());
                        if(result == DATA_TYPE.STRING && lexemes1.size() == 1 && lexemes1.get(lexemes1.size()-1).type == TOKEN_TYPE.STRING_CONST){
                            if(loops.contains(strings[0])){
                                if(loops.get(loops.size()-1).equals(strings[0])){
                                    loops.remove(strings[0]);
                                    ((DiagramDiamond)currentBlock).cycleName = strings[0];
                                    ((DiagramDiamond)currentBlock).isOpening = false;
                                } else throw new SchemeCompilationException("Циклы должны закрываться в последовательнности, обратной той, как открывались");
                            } else throw new SchemeCompilationException("Неизвестное имя закрывающегося цикла. Если вы хотите объявить новый цикл, во второй строке должно находиться BOOL условие");
                        } else throw new SchemeCompilationException("В первой строке блока цикла должно находиться имя цикла в формате STRING");
                        break;
                    case 2:

                        DATA_TYPE result2 = solve(lexemes1, operatorsList(lexemes1), new ArrayList<>());
                        if(result2 == DATA_TYPE.STRING && lexemes1.size() == 1 && lexemes1.get(lexemes1.size()-1).type == TOKEN_TYPE.STRING_CONST){
                            if(loops.contains(strings[0])) throw new SchemeCompilationException("Нельзя внутри цикла объявить цикл с таким же именем");
                            else{
                                loops.add(strings[0]);
                                ((DiagramDiamond)currentBlock).cycleName = strings[0];
                                ((DiagramDiamond)currentBlock).isOpening = true;
                            }

                            ArrayList<Lexeme> lexemes2 = lexemesParse(strings[1]);
                            result2 = solve(lexemes2, operatorsList(lexemes2), new ArrayList<>());
                            if(result2 != DATA_TYPE.BOOL) throw new SchemeCompilationException("Во второй строке открывающегося цикла должно находиться условие");
                        } else throw new SchemeCompilationException("В первой строке блока цикла должно находиться имя цикла в формате STRING");
                        break;
                    default:
                        throw new SchemeCompilationException("Блоки циклов должны содержать имя своего цикла в верхней строке. Если блок начинает цикл, то во второй строке также должно находиться BOOL условие");
                }
                //if(loops.size() == 0)
            }
            for (String string : strings) {
                if(string.isEmpty()) continue;
                ArrayList<Lexeme> lexemes = lexemesParse(string);
                ArrayList<Operator> operators = operatorsList(lexemes);
                DATA_TYPE result = solve(lexemes, operators, variables);
               if(block.getClass().equals(DiagramRhombus.class) && !result.equals(DATA_TYPE.BOOL)) throw new SchemeCompilationException("В блоке условия должно находиться BOOL выражение");
            }
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
                if(chainedNodes.contains(((Scheme)DiagramPanel.getDiagramObject()).getEndTerm())) throw new SchemeCompiler.SchemeCompilationException("Нельзя выходить из цикла, не пройдя через его закрывающий блок");
            }
            else{
                ArrayList<AbstractDiagramNode> chainedNodes = loop.getBlocksInLoop();
                if(chainedNodes.contains(((Scheme)DiagramPanel.getDiagramObject()).getStartTerm())) throw new SchemeCompiler.SchemeCompilationException("Нельзя входить в середину цикла, не пройдя через его открывающий блок");
            }
        }

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
                    } else throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c);
                } else currentToken += c;
            } else if (c == ' ') {
                if (token_type == TOKEN_TYPE.OPERATOR) {
                    if (operatorsList.contains(currentToken)) tokens.add(currentToken);
                    else throw new SchemeCompilationException("Неизвестная лексема " + currentToken);
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
                    } else throw new SchemeCompilationException("Неизвестная лексема " + currentToken);
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
                    } else throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c);
                } else {
                    throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c);
                }

            } else if (c == '"') {
                if (token_type == TOKEN_TYPE.OPERATOR && operatorsList.contains(currentToken)) {
                    tokens.add(currentToken);
                    currentToken = "" + c;
                    token_type = TOKEN_TYPE.STRING_CONST;
                } else if (currentToken.length() == 0) {
                    currentToken = "" + c;
                    token_type = TOKEN_TYPE.STRING_CONST;
                } else throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c);
            } else if (c == '.') {
                if (token_type == TOKEN_TYPE.INT_CONST) {
                    currentToken += c;
                    token_type = TOKEN_TYPE.FLOAT_CONST;
                } else {
                    throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c);
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
                    if (currentToken.length() >= 3) throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c);
                    else if (!operatorsList.contains(currentToken))
                        throw new SchemeCompilationException("Неизвестная лексема " + currentToken);
                } else throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c);
            } else if (legitVarSymbols.indexOf(c) > -1) {
                if (token_type == TOKEN_TYPE.OPERATOR) {
                    if (operatorsList.contains(currentToken)) {
                        tokens.add(currentToken);
                        currentToken = "";
                        token_type = TOKEN_TYPE.VARIABLE;
                    } else throw new SchemeCompilationException("Неизвестная лексема " + currentToken);
                }
                if (token_type == TOKEN_TYPE.INT_CONST || token_type == TOKEN_TYPE.FLOAT_CONST)
                    throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c);
                currentToken = currentToken + c;
            } else if (legitVarSymbols.indexOf(c) < 0 && (token_type == TOKEN_TYPE.INT_CONST || token_type == TOKEN_TYPE.FLOAT_CONST || token_type == TOKEN_TYPE.VARIABLE)) {
                if (currentToken.length() != 0) tokens.add(currentToken);
                currentToken = "" + c;
                token_type = TOKEN_TYPE.OPERATOR;
            } else if (legitVarSymbols.indexOf(c) < 0 && token_type == TOKEN_TYPE.OPERATOR) {
                currentToken = currentToken + c;
                if (currentToken.length() >= 3) throw new SchemeCompilationException("Неизвестная лексема " + currentToken + c);
                else if (!operatorsList.contains(currentToken))
                    throw new SchemeCompilationException("Неизвестная лексема " + currentToken);
            } else throw new SchemeCompilationException(currentToken);
        }
        if (token_type == TOKEN_TYPE.STRING_CONST) {
            if (currentToken.length() == 1) throw new SchemeCompilationException("Неизвестная лексема " + currentToken);
            else if (currentToken.toCharArray()[currentToken.length() - 1] == '"') tokens.add(currentToken);
            else throw new SchemeCompilationException("Неизвестная лексема " + currentToken);
        } else if (token_type == TOKEN_TYPE.OPERATOR) {
            if (operatorsList.contains(currentToken)) tokens.add(currentToken);
            else throw new SchemeCompilationException("Неизвестная лексема " + currentToken);
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
                else throw new SchemeCompilationException("Brackets");
            } else if (parseTokenType(tokens.get(i)) == TOKEN_TYPE.VARIABLE) {
                lexes.add(new Variable(tokens.get(i), DATA_TYPE.NULL));
            } else if (parseTokenType(tokens.get(i)) != TOKEN_TYPE.NULL) {
                lexes.add(new Lexeme(tokens.get(i), parseTokenType(tokens.get(i))));
            } else throw new SchemeCompilationException("Unknown lexeme " + tokens.get(i));
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

    private ArrayList<Variable> variablesList(ArrayList<Lexeme> lexes){
        ArrayList<Variable> variables = new ArrayList<>();
        for (int i = 0; i < lexes.size(); i++) {
            if (lexes.get(i).type == TOKEN_TYPE.DECLARATION) {
                switch (lexes.get(i).sign) {
                    case "int":
                        variables.add(new Variable(lexes.get(i + 1).sign, DATA_TYPE.INT));
                        break;
                    case "float":
                        variables.add(new Variable(lexes.get(i + 1).sign, DATA_TYPE.FLOAT));
                        break;
                    case "string":
                        variables.add(new Variable(lexes.get(i + 1).sign, DATA_TYPE.STRING));
                        break;
                    case "bool":
                        variables.add(new Variable(lexes.get(i + 1).sign, DATA_TYPE.BOOL));
                        break;
                }
            }
        }
        return variables;
    }

    private DATA_TYPE solve(ArrayList<Lexeme> lexemes, ArrayList<Operator> operators, ArrayList<Variable> vars) {
        ArrayList<Object> convertedData = new ArrayList<>();
        String lastLex = "";
        for (Lexeme lexeme : lexemes) {
            if (lexemes.size() == 1 && !currentBlock.getClass().equals(DiagramParallelogram.class) && !currentBlock.getClass().equals(DiagramDiamond.class)) throw new SchemeCompilationException("Выражение " + lexeme.sign + " не является операцией");
            switch (lexeme.type) {
                case VARIABLE:
                    if (variableExists(lexeme.sign)){
                        if(currentBlock.getClass().equals(DiagramParallelogram.class) && lexemes.size() == 1){
                            convertedData.add(getVariableByName(lexeme.sign).dataType);
                        }
                        else if (lexemes.get(1) == lexeme && lexemes.get(0).type == TOKEN_TYPE.DECLARATION) {
                            convertedData.add(getVariableByName(lexeme.sign).dataType);
                        }
                        else if (isVarDeclared(currentBlock, lexeme.sign)) convertedData.add(getVariableByName(lexeme.sign).dataType);
                        else throw new SchemeCompilationException("Неизвестная переменная " + lexeme.sign);
                    } else {
                        throw new SchemeCompilationException("Неизвестная переменная " + lexeme.sign);
                    }
                    if (lastLex.length() != 0)
                        throw new SchemeCompilationException("Выражение " + lastLex + " " + lexeme.sign + " не является операцией");
                    else lastLex = lexeme.sign;
                    break;
                case STRING_CONST:
                    convertedData.add(DATA_TYPE.STRING);
                    if (lastLex.length() != 0)
                        throw new SchemeCompilationException("Выражение " + lastLex + " " + lexeme.sign + " не является операцией");
                    else lastLex = lexeme.sign;
                    break;
                case INT_CONST:
                    convertedData.add(DATA_TYPE.INT);
                    if (lastLex.length() != 0)
                        throw new SchemeCompilationException("Выражение " + lastLex + " " + lexeme.sign + " не является операцией");
                    else lastLex = lexeme.sign;
                    break;
                case FLOAT_CONST:
                    convertedData.add(DATA_TYPE.FLOAT);
                    if (lastLex.length() != 0)
                        throw new SchemeCompilationException("Выражение " + lastLex + " " + lexeme.sign + " не является операцией");
                    else lastLex = lexeme.sign;
                    break;
                case BOOL_CONST:
                    convertedData.add(DATA_TYPE.BOOL);
                    if (lastLex.length() != 0)
                        throw new SchemeCompilationException("Выражение " + lastLex + " " + lexeme.sign + " не является операцией");
                    else lastLex = lexeme.sign;
                    break;
                case OPERATOR:
                    convertedData.add(lexeme);
                    lastLex = "";
                    break;
                case DECLARATION:
                    if (lexemes.get(0) == lexeme) {
                        if (lexemes.get(1).type == TOKEN_TYPE.VARIABLE) {
                            if (!variableExists(lexemes.get(1).sign)) {
                                switch (lexeme.sign) {
                                    case "int":
                                        vars.add(new Variable(lexemes.get(1).sign, DATA_TYPE.INT));
                                        break;
                                    case "float":
                                        vars.add(new Variable(lexemes.get(1).sign, DATA_TYPE.FLOAT));
                                        break;
                                    case "string":
                                        vars.add(new Variable(lexemes.get(1).sign, DATA_TYPE.STRING));
                                        break;
                                    case "bool":
                                        vars.add(new Variable(lexemes.get(1).sign, DATA_TYPE.BOOL));
                                        break;
                                    default: throw new SchemeCompilationException("Неизвестный тип данных " + lexeme.sign);
                                }
                            } else
                                throw new SchemeCompilationException("Переменная с таким именем " + lexemes.get(1).sign + " уже сущетсвует");
                        } else
                            throw new SchemeCompilationException("За объявленным типом " + lexeme.sign + " должна следовать переменная");
                    } else
                        throw new SchemeCompilationException("Объявление переменной " + lexeme.sign + " допустимо только в начале строки");
                    break;
            }
        }

        String data = "";

        for (Object obj : convertedData) {
            if (obj.getClass().equals(DATA_TYPE.class)) data += obj;
            else data += ((Operator) obj).sign;
        }
        System.out.println(data);
        for (Operator op : operators) {
            if (binaryOperators.contains(((Operator) op).sign)) {
                int n = convertedData.indexOf(op);
                DATA_TYPE data_type1;
                DATA_TYPE data_type2;
                try {
                    data_type1 = (DATA_TYPE) convertedData.get(n - 1);
                } catch (ClassCastException e) {
                    throw new SchemeCompilationException("Оператор " + ((Operator) op).sign + " неприменим к " + convertedData.get(n - 1));
                } catch (IndexOutOfBoundsException e) {
                    throw new SchemeCompilationException("Оператор " + ((Operator) op).sign + " применяется к двум операндам");
                }
                try {
                    data_type2 = (DATA_TYPE) convertedData.get(convertedData.indexOf(op) + 1);
                } catch (ClassCastException e) {
                    throw new SchemeCompilationException("Оператор " + ((Operator) op).sign + " неприменим к " + convertedData.get(n + 1));
                } catch (IndexOutOfBoundsException e) {
                    throw new SchemeCompilationException("Оператор " + ((Operator) op).sign + " применяется к двум операндам");
                }
                if (op.sign.equals("=")) {
                    if (lexemes.get(0).type == TOKEN_TYPE.DECLARATION && lexemes.get(1).type == TOKEN_TYPE.VARIABLE && lexemes.get(2) == op) {
                        data_type1 = getVariableByName(lexemes.get(1).sign).dataType;
                    } else if (lexemes.get(0).type == TOKEN_TYPE.VARIABLE && lexemes.get(1) == op) {
                        data_type1 = getVariableByName(lexemes.get(0).sign).dataType;
                    } else {
                        throw new SchemeCompilationException("Некорректное присваивание значения переменной");
                    }
                }
                if (binaryOperationCheck(op, data_type1, data_type2) != DATA_TYPE.NULL) {
                    convertedData.remove(n - 1);
                    convertedData.remove(n - 1);
                    convertedData.remove(n - 1);
                    convertedData.add(n - 1, binaryOperationCheck(op, data_type1, data_type2));

                    data = "";
                    for (Object obj : convertedData) {
                        if (obj.getClass().equals(DATA_TYPE.class)) data += obj;
                        else data += ((Operator) obj).sign;
                    }
                    System.out.println(data);
                } else
                    if(op.sign.equals(OPERATOR_ASSIGNMENT)){
                        Lexeme var;
                        if(lexemes.get(1).type == TOKEN_TYPE.VARIABLE) var = lexemes.get(1);
                        else var = lexemes.get(0);
                        throw new SchemeCompilationException("Переменной " + var.sign + " типа " + getVariableByName(var.sign).dataType + " не может быть присвоено значение " + convertedData.get(convertedData.size()-1));
                    }
                    else throw new SchemeCompilationException("Оператор " + ((Operator) op).sign + " неприменим к " + convertedData.get(convertedData.indexOf(op) - 1) + " и " + convertedData.get(convertedData.indexOf(op) + 1));
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
                        System.out.println(data);
                    } else
                        throw new SchemeCompilationException("Оператор " + ((Operator) op).sign + " неприменим к " + convertedData.get(operandNumber));
                } catch (ClassCastException e) {
                    throw new SchemeCompilationException("Оператор " + ((Operator) op).sign + " неприменим к " + convertedData.get(operandNumber));
                } catch (IndexOutOfBoundsException e) {
                    throw new SchemeCompilationException("Оператор " + ((Operator) op).sign + " не имеет операнда");
                }
            }
        }
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

    private boolean variableExists (String name){
        for (Variable variable : variables) {
            if (variable.sign.equals(name)) return true;
        }
        return false;
    }

    private Variable getVariableByName(String name){
        for (Variable variable : variables) {
            if (variable.sign.equals(name)) return variable;
        }
        return  null;
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

    private boolean isVarDeclared(AbstractDiagramNode node, String variable){
        ArrayList<AbstractDiagramNode> chainedBlocksUp = node.getChainedBlocksUp();
        chainedBlocksUp.add(node);
        ArrayList<Variable> declaredVars = new ArrayList<>();
        for (AbstractDiagramNode block : chainedBlocksUp) {
            if(!DiagramTerminator.class.isAssignableFrom(block.getClass())){
                if(!block.getClearCaption().isEmpty()){
                    String[] strings = block.getClearCaption().split("\r\n");
                    for (String string : strings) {
                        ArrayList<Lexeme> lex = lexemesParse(string);
                        ArrayList<Variable> c = variablesList(lex);
                        declaredVars.addAll(c);
                        System.out.println(declaredVars.toString());
                        for (Variable v :
                                declaredVars) {
                            if(v.sign.equals(variable)) return true;
                        }
                    }
                }
            }
        }
        System.out.println(declaredVars.toString());
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
                if (types.contains(DATA_TYPE.STRING)) return DATA_TYPE.STRING;
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
        public AbstractDiagramNode invalidBlock;
        public SchemeCompilationException(String message) {
            super(message);
            invalidBlock = currentBlock;
        }
    }
}