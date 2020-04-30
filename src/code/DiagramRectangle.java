package code;

import java.awt.*;

/**
 * UML Use Case on a diagram.
 */
class DiagramRectangle extends AbstractDiagramNode {


    public DiagramRectangle(double i, double j, String string) {
        super(i, j, string);    getVertices().clear();
        getVertices().add(new Vertex((i+ scale(WIDTH / 2)),    scale(j), "bot-middle"));
        getVertices().add(new Vertex((i+ scale(WIDTH / 4)),    scale(j), "bot-left"));
        getVertices().add(new Vertex((i+ scale(WIDTH / 4 * 3)),scale(j), "bot-right"));
        getVertices().add(new Vertex((i+ scale(WIDTH / 2)),    scale(j + HEIGHT), "top-middle"));
        getVertices().add(new Vertex((i+ scale(WIDTH / 4)),    scale(j + HEIGHT), "top-left"));
        getVertices().add(new Vertex((i+ scale(WIDTH / 4 * 3)),scale(j + HEIGHT), "top-right"));
        getVertices().add(new Vertex((i),             scale(j + HEIGHT / 3), "left-bot"));
        getVertices().add(new Vertex((i),             scale(j + HEIGHT / 3 * 2), "left-top"));
        getVertices().add(new Vertex((i+ scale(WIDTH)),        scale(j +HEIGHT / 3), "right-bot"));
        getVertices().add(new Vertex((i+ scale(WIDTH)),        scale(j + HEIGHT / 3 * 2), "right-top"));
    }




    // @Override
    protected void internalDraw() {
        double mX = getmX();
        double mY = getmY();

        getVertices().clear();
        getVertices().add(new Vertex((minX()+ scale(WIDTH / 2)),    (minY()), "bot-middle"));
        getVertices().add(new Vertex((minX()+ scale(WIDTH / 4)),    (minY()), "bot-left"));
        getVertices().add(new Vertex((minX()+ scale(WIDTH / 4 * 3)),(minY()), "bot-right"));
        getVertices().add(new Vertex((minX()+ scale(WIDTH / 2)),    (minY() + scale(HEIGHT)), "top-middle"));
        getVertices().add(new Vertex((minX()+ scale(WIDTH / 4)),    (minY() + scale(HEIGHT)), "top-left"));
        getVertices().add(new Vertex((minX()+ scale(WIDTH / 4) * 3),(minY() + scale(HEIGHT)), "top-right"));
        getVertices().add(new Vertex((minX()),                            (minY() + scale(HEIGHT / 3)), "left-bot"));
        getVertices().add(new Vertex((minX()),                            (minY() + scale(HEIGHT / 3 )* 2), "left-top"));
        getVertices().add(new Vertex((minX()+ scale(WIDTH)),              (minY() +scale(HEIGHT / 3)), "right-bot"));
        getVertices().add(new Vertex((minX()+ scale(WIDTH)),              (minY() + scale(HEIGHT / 3 * 2)), "right-top"));



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
        return getVertices().get(3);
    }

    @Override
    protected Vertex getBotMiddleVertex(){
        return getVertices().get(0);
    }

    @Override
    protected Vertex getLeftVertex(){
        return getVertices().get(7);
    }

    @Override
    protected Vertex getRightVertex(){
        return getVertices().get(9);
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