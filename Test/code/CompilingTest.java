package code;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.Assert.*;

public class CompilingTest {

    JSONParser jsonParser = new JSONParser();
    String filePath = "C:\\Users\\L\\OneDrive - Одеський національний політехнічний університет\\Документы\\tests\\";
    DiagramPanel diagramPanel = new DiagramPanel();

    Scheme parseScheme (String path) throws IOException, ParseException{
        Object obj = jsonParser.parse(new FileReader(filePath + path));
        JSONObject jsonObject =  (JSONObject) obj;
        Scheme newScheme = diagramPanel.parse(jsonObject);
        return newScheme;
    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void noStartBlock() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("На схеме отсутствует блок начала");
        DiagramPanel.compileDiagram(parseScheme("test_start.json"));
    }

    @Test
    public void noEndBlock() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("На схеме отсутствует блок конца");
        DiagramPanel.compileDiagram(parseScheme("test_end.json"));
    }

    @Test
    public void outOfReachFromStart() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("В следующие блоки невозможно попасть из блока начала:");
        DiagramPanel.compileDiagram(parseScheme("test_outOfReach_start.json"));
    }

    @Test
    public void outOfReachFromEnd() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Из следующих блоков невозможность попасть к блоку конца");
        DiagramPanel.compileDiagram(parseScheme("test_outOfReach_end.json"));
    }

    @Test
    public void controlTransferInAnotherContext() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Управление не может быть передано команде в другой контекстной области");
        DiagramPanel.compileDiagram(parseScheme("test_controlTransfer1.json"));
    }

    @Test
    public void controlTransferInBefore() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Возвращение в ранее пройденную точку программы хоть и возможно, но является плохим тоном. Вместо этого используйте циклы");
        DiagramPanel.compileDiagram(parseScheme("test_controlTransfer2.json"));
    }

    @Test
    public void controlTransferInCondition() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Возвращение в ранее пройденную точку программы хоть и возможно, но является плохим тоном. Вместо этого используйте циклы");
        DiagramPanel.compileDiagram(parseScheme("test_controlTransfer3.json"));
    }

    @Test
    public void controlTransferLoopOut() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Нельзя выходить из цикла, не пройдя через его закрывающий блок");
        DiagramPanel.compileDiagram(parseScheme("test_controlTransfer4.json"));
    }

    @Test
    public void controlTransferLoopIn() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Нельзя входить в середину цикла, не пройдя через его открывающий блок");
        DiagramPanel.compileDiagram(parseScheme("test_controlTransfer5.json"));
    }

    @Test
    public void conditionNotEnoughLinesOut() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Блоки условий должны иметь ровно два выхода");
        DiagramPanel.compileDiagram(parseScheme("test_conditionBlock.json"));
    }

    @Test
    public void notClosedLoops() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Циклы со следующими именами должны быть закрыты");
        DiagramPanel.compileDiagram(parseScheme("test_notClosedLoops.json"));
    }

    @Test
    public void loopsClosingOrder() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Циклы должны закрываться в последовательнности, обратной той, как открывались");
        DiagramPanel.compileDiagram(parseScheme("test_loopClosingOrder.json"));
    }

    @Test
    public void loopClosing() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Неизвестное имя закрывающегося цикла. Если вы хотите объявить новый цикл, во второй строке должно находиться BOOL условие");
        DiagramPanel.compileDiagram(parseScheme("test_loopClosing.json"));
    }

    @Test
    public void syntax1() throws IOException, ParseException {
        //25q
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Неизвестная лексема 25q");
        DiagramPanel.compileDiagram(parseScheme("test_syntax1.json"));
    }

    @Test
    public void syntax2() throws IOException, ParseException {
        // "25""ss80"
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Неизвестная лексема \"25\"\"");
        DiagramPanel.compileDiagram(parseScheme("test_syntax2.json"));
    }

    @Test
    public void syntax3() throws IOException, ParseException {
        //truel
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Выражение truel не является операцией");
        DiagramPanel.compileDiagram(parseScheme("test_syntax3.json"));
    }

    @Test
    public void syntax4() throws IOException, ParseException {
        //25.5.5
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Неизвестная лексема 25.5.");
        DiagramPanel.compileDiagram(parseScheme("test_syntax4.json"));
    }

    @Test
    public void syntax5() throws IOException, ParseException {
        //int a = 2(56 * 4)
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Выражение 2 56 не является операцией");
        DiagramPanel.compileDiagram(parseScheme("test_syntax5.json"));
    }

    @Test
    public void syntax6() throws IOException, ParseException {
        //int a = 2*(56 * (4)
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Количества открывающих и закрывающих скобок отличаются");
        DiagramPanel.compileDiagram(parseScheme("test_syntax6.json"));
    }

    @Test
    public void syntax7() throws IOException, ParseException {
        //int a = 2*+56 * 4
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Неизвестная лексема *+");
        DiagramPanel.compileDiagram(parseScheme("test_syntax7.json"));
    }

    @Test
    public void syntax8() throws IOException, ParseException {
        //int (a = 2 * 56 - 4)
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("За объявленным типом int должна следовать переменная");
        DiagramPanel.compileDiagram(parseScheme("test_syntax8.json"));
    }

    @Test
    public void syntax9() throws IOException, ParseException {
        //int bool = 8
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("За объявленным типом int должна следовать переменная");
        DiagramPanel.compileDiagram(parseScheme("test_syntax9.json"));
    }

    @Test
    public void semantic1() throws IOException, ParseException {
        //int c = a - 8
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Неизвестная переменная a");
        DiagramPanel.compileDiagram(parseScheme("test_semantic1.json"));
    }

    @Test
    public void semantic2() throws IOException, ParseException {
        //bool b = true; int c = b - 8
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Оператор - неприменим к BOOL и INT");
        DiagramPanel.compileDiagram(parseScheme("test_semantic2.json"));
    }

    @Test
    public void semantic3() throws IOException, ParseException {
        //int c = 5; int c = 15
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Переменная с таким именем c уже существует в данной области");
        DiagramPanel.compileDiagram(parseScheme("test_semantic3.json"));
    }

    @Test
    public void semantic4() throws IOException, ParseException {
        //использование переменной, объявленной в другом контексте
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Неизвестная переменная c");
        DiagramPanel.compileDiagram(parseScheme("test_semantic4.json"));
    }

    @Test
    public void semantic5() throws IOException, ParseException {
        //int c
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Все объявленные переменные либо должны иметь значение по умолчанию, либо находиться в блоке пользовательского ввода");
        DiagramPanel.compileDiagram(parseScheme("test_semantic5.json"));
    }

    @Test
    public void semantic6() throws IOException, ParseException {
        //int c = 8 в блоке условий
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Объявление переменных допустимо только в блоке операций или ввода");
        DiagramPanel.compileDiagram(parseScheme("test_semantic6.json"));
    }


    @Test
    public void semantic9() throws IOException, ParseException {
        //int с = 8; c = "8"
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Переменной c типа INT не может быть присвоено значение STRING");
        DiagramPanel.compileDiagram(parseScheme("test_semantic9.json"));
    }

    @Test
    public void semantic10() throws IOException, ParseException {
        //bool + int
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Оператор + неприменим к BOOL и INT");
        DiagramPanel.compileDiagram(parseScheme("test_semantic10.json"));
    }

    @Test
    public void semantic11() throws IOException, ParseException {
        //string - string
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Оператор - неприменим к STRING и STRING");
        DiagramPanel.compileDiagram(parseScheme("test_semantic11.json"));
    }

    @Test
    public void semantic12() throws IOException, ParseException {
        //int * bool
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Оператор * неприменим к INT и BOOL");
        DiagramPanel.compileDiagram(parseScheme("test_semantic12.json"));
    }

    @Test
    public void semantic13() throws IOException, ParseException {
        //string / float
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Оператор / неприменим к STRING и FLOAT");
        DiagramPanel.compileDiagram(parseScheme("test_semantic13.json"));
    }

    @Test
    public void semantic14() throws IOException, ParseException {
        //!int
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Оператор ! неприменим к INT");
        DiagramPanel.compileDiagram(parseScheme("test_semantic14.json"));
    }

    @Test
    public void semantic15() throws IOException, ParseException {
        //string || string
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Оператор || неприменим к STRING и STRING");
        DiagramPanel.compileDiagram(parseScheme("test_semantic15.json"));
    }

    @Test
    public void semantic16() throws IOException, ParseException {
        //bool && int
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Оператор && неприменим к BOOL и INT");
        DiagramPanel.compileDiagram(parseScheme("test_semantic16.json"));
    }

    @Test
    public void semantic17() throws IOException, ParseException {
        //string < float
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Оператор < неприменим к STRING и FLOAT");
        DiagramPanel.compileDiagram(parseScheme("test_semantic17.json"));
    }

    @Test
    public void semantic18() throws IOException, ParseException {
        //string++
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Оператор ++ неприменим к STRING");
        DiagramPanel.compileDiagram(parseScheme("test_semantic18.json"));
    }

    @Test
    public void semantic19() throws IOException, ParseException {
        //bool --
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Оператор -- неприменим к BOOL");
        DiagramPanel.compileDiagram(parseScheme("test_semantic19.json"));
    }

    @Test
    public void logic1() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("В блоке условия должно находиться BOOL выражение");
        DiagramPanel.compileDiagram(parseScheme("test_logic1.json"));
    }

    @Test
    public void logic2() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Блоки циклов должны содержать имя своего цикла в верхней строке. Если блок начинает цикл, то во второй строке также должно находиться BOOL условие");
        DiagramPanel.compileDiagram(parseScheme("test_logic2.json"));
    }

    @Test
    public void logic3() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("В блоке условия может находиться только одна строка");
        DiagramPanel.compileDiagram(parseScheme("test_logic3.json"));
    }

    @Test
    public void logic4() throws IOException, ParseException {
        //5 + 15
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Операция 5 + 15 не имеет смысла, так как её результат нигде не хранится и не используется");
        DiagramPanel.compileDiagram(parseScheme("test_logic4.json"));
    }

    @Test
    public void logic5() throws IOException, ParseException {
        //функция с пустым именем
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Все функции должны быть проименованы");
        DiagramPanel.compileDiagram(parseScheme("test_logic5.json"));
    }

    @Test
    public void logic6() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("В одном блоке подпроцесса может быть только одна функция");
        DiagramPanel.compileDiagram(parseScheme("test_logic6.json"));
    }

    @Test
    public void logic7() throws IOException, ParseException {
        //func
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("В конце имени функции должны стоять скобки без параметров \"()\"");
        DiagramPanel.compileDiagram(parseScheme("test_logic7.json"));
    }

    @Test
    public void logic8() throws IOException, ParseException {
        //функция()
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("В имени функции допустимы только латинские буквы и цифры");
        DiagramPanel.compileDiagram(parseScheme("test_logic8.json"));
    }

    @Test
    public void logic9() throws IOException, ParseException {
        //3fucntion()
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Имя функции не может начинаться с цифры");
        DiagramPanel.compileDiagram(parseScheme("test_logic9.json"));
    }

    @Test
    public void logic10() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("В первой строке блока цикла должно находиться имя цикла в формате STRING");
        DiagramPanel.compileDiagram(parseScheme("test_logic10.json"));
    }

    @Test
    public void logic11() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Нельзя внутри цикла объявить цикл с таким же именем");
        DiagramPanel.compileDiagram(parseScheme("test_logic11.json"));
    }

    @Test
    public void logic12() throws IOException, ParseException {
        exceptionRule.expect(SchemeCompiler.SchemeCompilationException.class);
        exceptionRule.expectMessage("Во второй строке открывающегося цикла должно находиться BOOL условие");
        DiagramPanel.compileDiagram(parseScheme("test_logic12.json"));
    }

    @Test
    public void regularScheme1() throws IOException, ParseException {
        DiagramPanel.generatedCode = "";
        String str = DiagramPanel.compileDiagram(parseScheme("regular1.json"));
        DiagramPanel.generatedCode = DiagramPanel.generatedCode + "\n" + str;
        DiagramPanel.generatedCode = "#include <iostream>\n" +
                "using namespace std;\n" + DiagramPanel.generatedCode;
        System.out.println(DiagramPanel.generatedCode);
    }

    @Test
    public void regularScheme2() throws IOException, ParseException {
        DiagramPanel.generatedCode = "";
        String str = DiagramPanel.compileDiagram(parseScheme("regular2.json"));
        DiagramPanel.generatedCode = DiagramPanel.generatedCode + "\n" + str;
        DiagramPanel.generatedCode = "#include <iostream>\n" +
                "using namespace std;\n" + DiagramPanel.generatedCode;
        System.out.println(DiagramPanel.generatedCode);    }

    @Test
    public void regularScheme3() throws IOException, ParseException {
        DiagramPanel.generatedCode = "";
        String str = DiagramPanel.compileDiagram(parseScheme("regular3.json"));
        DiagramPanel.generatedCode = DiagramPanel.generatedCode + "\n" + str;
        DiagramPanel.generatedCode = "#include <iostream>\n" +
                "using namespace std;\n" + DiagramPanel.generatedCode;
        System.out.println(DiagramPanel.generatedCode);    }
}