package code;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * UML Use Case on a diagram.
 */
class DiagramTerminator extends AbstractDiagramNode {

    protected static double HEIGHT = 2 * SIZE_SCALE *  FONTSIZEPT;

    public DiagramTerminator(double i, double j, String string) {
        super(i, j, string);

        addVertex("down", new Vertex(i+ (WIDTH / 2),  j));
        addVertex("up", new Vertex(i+ (WIDTH / 2), j  + (HEIGHT)));
    }




    @Override
    protected void internalDraw() {
        double mX = getmX();
        double mY = getmY();


        vertices.clear();
        addVertex("down", new Vertex( scaleX(mX),  scaleY(mY-HEIGHT/2)));
        addVertex("up", new Vertex( scaleX(mX),  scaleY(mY +HEIGHT/2)));



        getCanvas().setColor(colorFill);
        getCanvas().fillRoundRect(scaleX(mX - WIDTH/2)+1,
                scaleY(mY - HEIGHT / 2)+1,
                scale(WIDTH),
                scale(HEIGHT),
                scale(20),
                scale(90));

        getCanvas().setColor(colorBorder);
        getCanvas().drawRoundRect(scaleX(mX - WIDTH/2) ,
                scaleY(mY - HEIGHT / 2),
                scale(WIDTH),
                scale(HEIGHT),
                scale(20),
                scale(90));

        getCanvas().setFont(getCanvas().getFont().deriveFont((float) scale(FONTSIZEPT)));
        FontMetrics metrics = getCanvas().getFontMetrics();
        shift = metrics.stringWidth(getCaption());
        getCanvas().setColor(colorFont);
        getCanvas().drawString(getCaption(), (int)(scaleX(mX)-shift/2) , scaleY(mY + 0.5 * (FONTSIZEPT)));

    }


    protected boolean internalTestHit(double x, double y) {
        double dX = x - getmX();
        double dY = y - getmY();
        return dY > -HEIGHT / 2 && dY < HEIGHT / 2 && dX > -WIDTH/2  && dX < WIDTH/2;
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
    protected double getHEIGHT (){return HEIGHT;}

    @Override
    protected double getWIDTH () {return  WIDTH;}

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
        hintStr.append(getCaption()+ " of scheme");
        return true;
    }



}