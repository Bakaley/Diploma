package code;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class DiagramDiamond extends AbstractDiagramNode{

    public DiagramDiamond(double i, double j, String string) {
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

        int[] x = {scaleX(mX - WIDTH/2),  scaleX(mX + HEIGHT/2- WIDTH/2), scaleX(mX + WIDTH/2 - HEIGHT/2), scaleX(mX + WIDTH/2), scaleX(mX + WIDTH/2 - HEIGHT/2), scaleX(mX + HEIGHT/2 - WIDTH/2)};
        int[] y = {scaleY(mY), scaleY(mY + HEIGHT/2), scaleY(mY+ HEIGHT/2),  scaleY(mY), scaleY(mY -HEIGHT/2), scaleY(mY -HEIGHT/2), scaleY(mY)};


        getCanvas().setColor(colorFill);
        getCanvas().fillPolygon(x, y, 6);
        getCanvas().setColor(colorBorder);
        getCanvas().drawPolygon(x, y, 6);
        getCanvas().setColor(colorFont);

        getCanvas().setFont(getCanvas().getFont().deriveFont((float) scale(FONTSIZEPT)));

        drawText();

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
        hintStr.append("Цикл: " + getCaption());
        return true;
    }

    public String cycleName;
    public boolean isOpening;

    public ArrayList<AbstractDiagramNode> getBlocksInLoop() {
        if (isOpening) {
            ArrayList<DiagramObject> schemeNodes = new ArrayList<>(((Scheme) getParent()).diagramObjects.values());
            for (DiagramObject obj : ((Scheme) getParent()).diagramObjects.values()) {
                if (obj.getClass().equals(DiagramGeneralization.class)) {
                    schemeNodes.remove(obj);
                }
            }
            schemeNodes.remove(this);
            ArrayList<AbstractDiagramNode> nodesToCheck = new ArrayList<>();
            ArrayList<AbstractDiagramNode> checkedNodes = new ArrayList<>();

            for (AbstractDiagramLink link : get_lines_out()) {
                nodesToCheck.add(link.nTo);
            }
            DiagramDiamond loopEnd = null;
            while (nodesToCheck.size() != 0) {
                for (AbstractDiagramLink link : nodesToCheck.get(0).get_lines_out()) {
                    if (!checkedNodes.contains(link.nTo) && !nodesToCheck.contains(link.nTo)) {
                        if (link.nTo.getClass().equals(DiagramDiamond.class)) {
                            DiagramDiamond loop = (DiagramDiamond) link.nTo;
                            if (loop.cycleName.equals(cycleName) && loop.isOpening)
                                throw new SchemeCompiler.SchemeCompilationException("Нельзя выходить из цикла, не закрыв его", loop);
                            if (loop.cycleName.equals(cycleName) && !loop.isOpening) {
                                loopEnd = loop;
                                checkedNodes.add(loop);
                                continue;
                            }
                        }
                        nodesToCheck.add(link.nTo);
                    }
                }
                checkedNodes.add(nodesToCheck.get(0));
                nodesToCheck.remove(nodesToCheck.get(0));
            }
            checkedNodes.remove(this);
            checkedNodes.remove(loopEnd);
            return checkedNodes;
        }
        else{
            ArrayList<DiagramObject> schemeNodes = new ArrayList<>(((Scheme) getParent()).diagramObjects.values());
            for (DiagramObject obj : ((Scheme) getParent()).diagramObjects.values()) {
                if (obj.getClass().equals(DiagramGeneralization.class)) {
                    schemeNodes.remove(obj);
                }
            }
            schemeNodes.remove(this);
            ArrayList<AbstractDiagramNode> nodesToCheck = new ArrayList<>();
            ArrayList<AbstractDiagramNode> checkedNodes = new ArrayList<>();

            for (AbstractDiagramLink link : get_lines_in()) {
                nodesToCheck.add(link.nFrom);
            }

            DiagramDiamond loopStart = null;

            while (nodesToCheck.size() != 0) {
                for (AbstractDiagramLink link : nodesToCheck.get(0).get_lines_in()) {
                    if (!checkedNodes.contains(link.nFrom) && !nodesToCheck.contains(link.nFrom)) {
                        if (link.nFrom.getClass().equals(DiagramDiamond.class)) {
                            DiagramDiamond loop = (DiagramDiamond) link.nFrom;
                            if (loop.cycleName.equals(cycleName) && !loop.isOpening)
                                throw new SchemeCompiler.SchemeCompilationException("Нельзя выходить из цикла, не закрыв его", loop);
                            if (loop.cycleName.equals(cycleName) && loop.isOpening) {
                                loopStart = loop;
                                checkedNodes.add(loop);
                                continue;
                            }
                        }
                        nodesToCheck.add(link.nFrom);
                    }
                }
                checkedNodes.add(nodesToCheck.get(0));
                nodesToCheck.remove(nodesToCheck.get(0));
            }
            checkedNodes.remove(this);
            checkedNodes.remove(loopStart);
            return checkedNodes;
        }
    }

    @Override
    public void generateCode(SchemeCompiler.CodeGenerator codeGenerator) {
        String[] strings = getClearCaption().split("\r\n");
        if(isOpening){
            codeGenerator.addWhile(strings[1]);
        }
        else codeGenerator.closeBranch();
    }
}
