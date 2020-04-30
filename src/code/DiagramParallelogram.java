package code;

import java.awt.*;

public class DiagramParallelogram extends AbstractDiagramNode {


    public DiagramParallelogram(double mX, double mY, String caption) {
        super(mX, mY, caption);
        getVertices().clear();
        getVertices().add(new Vertex(minX()+ scale(WIDTH / 10 * 2.5),  minY(),                     "bot-middle"));
        getVertices().add(new Vertex(minX()+ scale(WIDTH / 10 * 5),  minY(),                       "bot-left"));
        getVertices().add(new Vertex(minX()+ scale(WIDTH / 10 * 7.5),  minY(),                     "bot-right"));
        getVertices().add(new Vertex(minX()+ scale(WIDTH / 10 * 2.5), minY()  + scale(HEIGHT) , "top-left"));
        getVertices().add(new Vertex(minX()+ scale(WIDTH / 10 * 5), minY()  + scale(HEIGHT) ,   "top-middle"));
        getVertices().add(new Vertex(minX()+ scale(WIDTH / 10 * 7.5), minY()  + scale(HEIGHT) , "top-right"));
    }


    @Override
    protected void internalDraw() {

        double mX = getmX();
        double mY = getmY();

        int[] x = {scaleX (mX - WIDTH / 2),  scaleX (mX - 0.3 * WIDTH),scaleX (mX + WIDTH / 2), scaleX (mX + 0.3 * WIDTH)};
        int[] y = {scaleY (mY + HEIGHT / 2), scaleY (mY - HEIGHT / 2), scaleY (mY - HEIGHT / 2), scaleY (mY + HEIGHT / 2)};
        int[] x2 = {scaleX (mX - WIDTH / 2 )+1,  scaleX (mX - 0.3 * WIDTH)+1,scaleX (mX + WIDTH / 2), scaleX (mX + 0.3 * WIDTH)};
        int[] y2 = {scaleY (mY + HEIGHT / 2 ), scaleY (mY - HEIGHT / 2)+1, scaleY (mY - HEIGHT / 2)+1, scaleY (mY + HEIGHT / 2)};

        getVertices().clear();
        getVertices().add(new Vertex(minX()+ scale(WIDTH / 10 * 2.5),  minY(),                     "bot-middle"));
        getVertices().add(new Vertex(minX()+ scale(WIDTH / 10 * 5),  minY(),                       "bot-left"));
        getVertices().add(new Vertex(minX()+ scale(WIDTH / 10 * 7.5),  minY(),                     "bot-right"));
        getVertices().add(new Vertex(minX()+ scale(WIDTH / 10 * 2.5), minY()  + scale(HEIGHT) , "top-left"));
        getVertices().add(new Vertex(minX()+ scale(WIDTH / 10 * 5), minY()  + scale(HEIGHT) ,   "top-middle"));
        getVertices().add(new Vertex(minX()+ scale(WIDTH / 10 * 7.5), minY()  + scale(HEIGHT) , "top-right"));

        getCanvas().setFont(getCanvas().getFont().deriveFont((float) scale(FONTSIZEPT)));
        FontMetrics metrics = getCanvas().getFontMetrics();
        shift = metrics.stringWidth(getCaption());

        getCanvas().setColor(colorFill);
        getCanvas().fillPolygon(x, y, 4);
        getCanvas().setColor(colorBorder);
        getCanvas().drawPolygon(x, y, 4);
        getCanvas().setColor(colorFont);
        getCanvas().drawString(getCaption(), (int)(scaleX(mX)-shift/2) , scaleY(mY + 0.5 * (FONTSIZEPT)));

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
        return getVertices().get(4);
    }

    @Override
    protected Vertex getBotMiddleVertex(){
        return getVertices().get(1);
    }


    @Override
    protected Vertex getLeftVertex(){
        return getVertices().get(4);
    }

    @Override
    protected Vertex getRightVertex(){
        return getVertices().get(1);
    }

    @Override
    protected boolean internalGetHint(StringBuilder hintStr) {
        hintStr.append("Input/output: " + getCaption());
        return true;
    }

}