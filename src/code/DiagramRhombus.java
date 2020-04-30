package code;

import java.awt.*;

/**
 * UML Actor on a diagram.
 */
class DiagramRhombus extends AbstractDiagramNode {



    public DiagramRhombus(double mX, double mY, String caption) {
        super(mX, mY, caption);
        getVertices().clear();
        getVertices().add(new Vertex(minX()+ scale(WIDTH / 2),  minY(), "bot-miiddle"));
        getVertices().add(new Vertex(minX()+ scale(WIDTH / 2), minY()  + scale(HEIGHT) , "top-miiddle"));
        getVertices().add(new Vertex(minX(),  minY() + scale(HEIGHT / 2), "left-miiddle"));
        getVertices().add(new Vertex(minX() + scale(WIDTH),  minY() + scale(HEIGHT / 2), "right-miiddle"));
    }





    @Override
    protected void internalDraw() {

        double mX = getmX();
        double mY = getmY();

        getCanvas().setFont(getCanvas().getFont().deriveFont((float) scale(FONTSIZEPT)));
        FontMetrics metrics = getCanvas().getFontMetrics();
        shift = metrics.stringWidth(getCaption());

        int[] x = {scaleX(mX),  scaleX(mX +  WIDTH / 2),scaleX(mX ), scaleX(mX - WIDTH / 2)};
        int[] y = {scaleY(mY- HEIGHT/2), scaleY(mY ),  scaleY(mY +  HEIGHT/2), scaleY(mY)};
        int[] x2 = {scaleX(mX)+1,                 scaleX(mX +  WIDTH / 2)-1,    scaleX(mX )-1,                  scaleX(mX - WIDTH / 2)+1};
        int[] y2 = {scaleY(mY- HEIGHT/2)+1, scaleY(mY),                      scaleY(mY +  HEIGHT/2)-1, scaleY(mY)};


        getCanvas().setColor(colorFill);
        getCanvas().fillPolygon(x, y, 4);
        getCanvas().setColor(colorBorder);
        getCanvas().drawPolygon(x, y, 4);
        getCanvas().setColor(colorFont);
        getCanvas().drawString(getCaption(), (int)(scaleX(mX)-shift/2) , scaleY(mY + 0.5 * (FONTSIZEPT)));

        getVertices().clear();
        getVertices().add(new Vertex(minX()+ scale(WIDTH / 2),  minY(), "bot-miiddle"));
        getVertices().add(new Vertex(minX()+ scale(WIDTH / 2), minY()  + scale(HEIGHT) , "top-miiddle"));
        getVertices().add(new Vertex(   minX(),  minY() + scale(HEIGHT / 2), "left-miiddle"));
        getVertices().add(new Vertex(minX() + scale(WIDTH),  minY() + scale(HEIGHT / 2), "right-miiddle"));


    }

    @Override
    protected boolean internalTestHit(double x, double y) {
        double dX = x - getmX();
        double dY = y - getmY();
        return dY > -HEIGHT/2 && dY < HEIGHT/2 && dX > -WIDTH/2 && dX < WIDTH/2;
    }


    @Override
    protected double getMinX() {
        return getmX() - WIDTH / 2;
    }

    @Override
    protected double getMinY() {
        return getmY() - HEIGHT / 2;
    }

    @Override
    protected double getMaxX() {
        return getmX() + WIDTH / 2;
    }

    @Override
    protected double getMaxY() {
        return getmY() + HEIGHT / 2;
    }

    @Override
    protected Vertex getTopMiddleVertex(){
        return getVertices().get(1);
    }


    @Override
    protected Vertex getBotMiddleVertex(){
        return getVertices().get(0);
    }


    @Override
    protected Vertex getLeftVertex(){
        return getVertices().get(2);
    }


    @Override
    protected Vertex getRightVertex(){
        return getVertices().get(3);
    }

    @Override
    protected boolean internalGetHint(StringBuilder hintStr) {
        hintStr.append("Condtition: " + getCaption());
        return true;
    }

}