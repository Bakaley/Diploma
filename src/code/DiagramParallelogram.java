package code;

import java.awt.*;

public class DiagramParallelogram extends AbstractDiagramNode {


    public DiagramParallelogram(double mX, double mY, String caption) {
        super(mX, mY, caption);

        addVertex("down", new Vertex(mX+ (WIDTH / 2),  mY));
        addVertex("up", new Vertex(mX+ (WIDTH / 2), mY  + (HEIGHT)));
    }


    @Override
    protected void internalDraw() {

        double mX = getmX();
        double mY = getmY();

        vertices.clear();
        addVertex("down", new Vertex( scaleX(mX),  scaleY(mY-HEIGHT/2)));
        addVertex("up", new Vertex( scaleX(mX),  scaleY(mY +HEIGHT/2)));


        int[] x = {scaleX (mX - WIDTH / 2),  scaleX (mX - 0.3 * WIDTH),scaleX (mX + WIDTH / 2), scaleX (mX + 0.3 * WIDTH)};
        int[] y = {scaleY (mY + HEIGHT / 2), scaleY (mY - HEIGHT / 2), scaleY (mY - HEIGHT / 2), scaleY (mY + HEIGHT / 2)};
        int[] x2 = {scaleX (mX - WIDTH / 2 )+1,  scaleX (mX - 0.3 * WIDTH)+1,scaleX (mX + WIDTH / 2), scaleX (mX + 0.3 * WIDTH)};
        int[] y2 = {scaleY (mY + HEIGHT / 2 ), scaleY (mY - HEIGHT / 2)+1, scaleY (mY - HEIGHT / 2)+1, scaleY (mY + HEIGHT / 2)};

        getCanvas().setFont(getCanvas().getFont().deriveFont((float) scale(FONTSIZEPT)));
        FontMetrics metrics = getCanvas().getFontMetrics();

        getCanvas().setColor(colorFill);
        getCanvas().fillPolygon(x, y, 4);
        getCanvas().setColor(colorBorder);
        getCanvas().drawPolygon(x, y, 4);
        getCanvas().setColor(colorFont);

        drawText();
    }

    @Override
    protected boolean internalTestHit(double x, double y) {
        double dX = x - getmX();
        double dY = y - getmY();
        return dY > -HEIGHT / 2 && dY < HEIGHT / 2 && dX > -WIDTH / 2 && dX < WIDTH / 2;
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
        hintStr.append("Input/output: " + getCaption());
        return true;
    }

}