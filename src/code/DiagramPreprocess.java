package code;

import org.json.simple.JSONObject;

import java.awt.*;

public class DiagramPreprocess extends AbstractDiagramNode{

    Scheme innerScheme;

    public DiagramPreprocess(double i, double j, String string) {
        super(i, j, string);    getVertices().clear();

        addVertex("down", new Vertex(i+ (WIDTH / 2),  j));
        addVertex("up", new Vertex(i+ (WIDTH / 2), j  + (HEIGHT)));

        innerScheme = new Scheme(true);
        innerScheme.caption = getName();
        innerScheme.parentScheme = (Scheme)DiagramPanel.getDiagramObject();
    }

    @Override
    protected JSONObject getJSON() {

        JSONObject elementDetails = new JSONObject();

        elementDetails.put("id", this.getId());
        elementDetails.put("x", this.getmX());
        elementDetails.put("y", this.getmY());
        elementDetails.put("text", this.getClearCaption());
        elementDetails.put("shape", this.getClass().getName());
        elementDetails.put("innerScheme", innerScheme.getJSON());
        elementDetails.put("colorFillR", this.colorFill.getRed());
        elementDetails.put("colorFillG", this.colorFill.getGreen());
        elementDetails.put("colorFillB", this.colorFill.getBlue());
        elementDetails.put("colorFontR", this.colorFont.getRed());
        elementDetails.put("colorFontG", this.colorFont.getGreen());
        elementDetails.put("colorFontB", this.colorFont.getBlue());
        elementDetails.put("colorBorderR", this.colorBorder.getRed());
        elementDetails.put("colorBorderG", this.colorBorder.getGreen());
        elementDetails.put("colorBorderB", this.colorBorder.getBlue());

        return elementDetails;
    }

    // @Override
    protected void internalDraw() {
        double mX = getmX();
        double mY = getmY();

        vertices.clear();
        addVertex("down", new Vertex( scaleX(mX),  scaleY(mY-HEIGHT/2)));
        addVertex("up", new Vertex( scaleX(mX),  scaleY(mY +HEIGHT/2)));

        getCanvas().setFont(getCanvas().getFont().deriveFont((float) scale(FONTSIZEPT)));
        FontMetrics metrics = getCanvas().getFontMetrics();
        getCanvas().setColor(colorBorder);
        getCanvas().drawRect(scaleX(mX -WIDTH/2) ,scaleY(mY - HEIGHT / 2), scale(WIDTH), scale(HEIGHT));
        getCanvas().setColor(colorFill);
        getCanvas().fillRect(scaleX(getmX() -WIDTH/2)+1 , scaleY(getmY() - HEIGHT / 2)+1, scale(WIDTH)-1, scale(HEIGHT)-1);
        getCanvas().setColor(colorFont);
        getCanvas().drawLine(scaleX(mX - WIDTH/7*3), scaleY(mY + HEIGHT/2), scaleX(mX - WIDTH/7*3), scaleY(mY - HEIGHT/2));
        getCanvas().drawLine(scaleX(mX + WIDTH/7*3), scaleY(mY + HEIGHT/2), scaleX(mX + WIDTH/7*3), scaleY(mY - HEIGHT/2));

        drawText();
        // getCanvas().setPaintMode();

    }

    @Override
    public boolean internalTestHit(double x, double y) {
        double dX = x - getmX();
        double dY = y - getmY();
        return dY > -HEIGHT / 2 && dY < HEIGHT / 2 && dX > -WIDTH/2 && dX < WIDTH/2 ;
    }

    @Override
    protected double getMinX() { return getmX() -  WIDTH/2; }

    @Override
    protected double getMinY() {
        return getmY() - HEIGHT / 2;
    }

    @Override
    protected double getMaxX() {
        return getmX() +  WIDTH/2 ;
    }

    @Override
    protected double getMaxY() {
        return getmY() + HEIGHT / 2;
    }

    @Override
    protected Vertex getTopMiddleVertex(){
        return vertices.get("up");
    }

    @Override
    protected Vertex getBotMiddleVertex(){
        return vertices.get("down");
    }

    @Override
    protected Vertex getLeftVertex(){
        return null;
    }

    @Override
    protected Vertex getRightVertex(){
        return null;
    }

    @Override
    protected boolean internalGetHint(StringBuilder hintStr) {
        hintStr.append("Operation: " + getCaption());
        return true;
    }

    @Override
    public void generateCode(SchemeCompiler.CodeGenerator codeGenerator) {
        codeGenerator.add(caption);
    }

}
