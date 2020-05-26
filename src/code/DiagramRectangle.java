package code;

import java.awt.*;

/**
 * UML Use Case on a diagram.
 */
class DiagramRectangle extends AbstractDiagramNode {


    public DiagramRectangle(double i, double j, String string) {
        super(i, j, string);    getVertices().clear();

        addVertex("down", new Vertex(i+ (WIDTH / 2),  j));
        addVertex("up", new Vertex(i+ (WIDTH / 2), j  + (HEIGHT)));
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
        shift = metrics.stringWidth(getCaption());
        getCanvas().setColor(colorBorder);
        getCanvas().drawRect(scaleX(mX -WIDTH/2) ,scaleY(mY - HEIGHT / 2), scale(WIDTH), scale(HEIGHT));
        getCanvas().setColor(colorFill);
        getCanvas().fillRect(scaleX(getmX() -WIDTH/2)+1 , scaleY(getmY() - HEIGHT / 2)+1, scale(WIDTH)-1, scale(HEIGHT)-1);
        getCanvas().setColor(colorFont);
        getCanvas().drawString(getCaption(), (int)(scaleX(getmX())-shift/2) , scaleY(getmY() + 0.5 * (FONTSIZEPT)));
       // getCanvas().setPaintMode();

    }

    protected boolean internalTestHit(double x, double y) {
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
    protected double getMaxY() {
        return getmY() + HEIGHT / 2;
    }

    @Override
    protected boolean internalGetHint(StringBuilder hintStr) {
        hintStr.append("Operation: " + getCaption());
        return true;
    }

}