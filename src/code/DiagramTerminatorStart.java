package code;

public class DiagramTerminatorStart extends DiagramTerminator {

    public DiagramTerminatorStart(double i, double j, String string) {
        super(i, j, string);
       this.setCaption("Начало");
    }

    @Override
    public void generateCode(SchemeCompiler.CodeGenerator codeGenerator) {
        codeGenerator.addMain(this);
    }
}
