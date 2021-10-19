package code;

import org.json.simple.JSONObject;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

abstract class AbstractDiagramNode extends DiagramObject{


    private double mX;
    private double mY;

    protected Color colorFill = Color.cyan;
    protected Color colorFont = Color.black;
    protected Color colorBorder = Color.black;

    protected Color savedFill = Color.cyan;
    protected Color savedFont = Color.black;
    protected Color savedBorder = Color.black;

    protected HashMap <String, Vertex> vertices = new HashMap<>();

    public HashMap<String, Vertex> getVertices (){
        return this.vertices;
    }

    public void addVertex (String string, Vertex vertex){
        vertices.put(string, vertex);
    }

    public void setColorFont (Color color) {
        this.colorFont = color;
        savedFont = color;
    }
    public void setColorFill (Color color) {
        this.colorFill = color;
        savedFill = color;
    }
    public void setColorBorder(Color color) {
        this.colorBorder = color;
        savedBorder = color;
    }

    void drawText(){
        String[] parts = getCaption().split("\n");
        double yShift = parts.length%2 == 0 ? 1 : 0.5;
        double xShift;
        int n = - parts.length/2;

        for (int i = 0; i < parts.length; i++, n++){
            FontMetrics metrics = getCanvas().getFontMetrics();
            xShift = metrics.stringWidth(parts[i]);
            getCanvas().drawString(parts[i], (int)(scaleX(mX)-xShift/2), scaleY(mY + yShift * FONTSIZEPT + n * FONTSIZEPT));
        }
    }

    public ArrayList<DiagramGeneralization> get_lines_in() {
        HashMap <Integer, DiagramObject> diagramObjects = ((Scheme)(getParent())).diagramObjects;
        ArrayList <DiagramGeneralization> lines = new ArrayList<>();
        Iterator<DiagramObject> iterator = diagramObjects.values().iterator();
        while (iterator.hasNext()){
            DiagramObject object = iterator.next();
            if(object.getClass().equals(code.DiagramGeneralization.class)) {
                DiagramGeneralization link = (DiagramGeneralization)object;
                if(link.nTo == this){
                    lines.add(link);
                }
            }
        }
    return lines;}

    public ArrayList<DiagramGeneralization> get_lines_out() {
        HashMap<Integer, DiagramObject> diagramObjects = ((Scheme)(getParent())).diagramObjects;
        ArrayList<DiagramGeneralization> lines = new ArrayList<>();
        Iterator<DiagramObject> iterator = diagramObjects.values().iterator();
        while (iterator.hasNext()) {
            DiagramObject object = iterator.next();
            if (object.getClass().equals(code.DiagramGeneralization.class)) {
                DiagramGeneralization link = (DiagramGeneralization) object;
                if (link.nFrom == this) {
                    lines.add(link);
                }
            }
        }
        return lines;
    }

        protected JSONObject getJSON(){
        JSONObject elementDetails = new JSONObject();

            elementDetails.put("id", this.getId());
            elementDetails.put("x", this.getmX());
            elementDetails.put("y", this.getmY());
            elementDetails.put("text", this.getClearCaption());
            elementDetails.put("shape", this.getClass().getName());
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


    AbstractDiagramNode(double mX, double mY, String caption) {
        this.mX = mX;
        this.mY = mY;
        this.caption = caption;

    }

    ArrayList <AbstractDiagramNode> getChainedBlocksDown(){
        Scheme dobj = (Scheme)getParent();
        ArrayList<DiagramObject> schemeNodes = new ArrayList<>(dobj.diagramObjects.values());
        for (DiagramObject obj: dobj.diagramObjects.values()) {
            if(obj.getClass().equals(DiagramGeneralization.class)){
                schemeNodes.remove(obj);
            }
        }
        schemeNodes.remove(this);
        ArrayList<AbstractDiagramNode> nodesToCheck = new ArrayList<>();
        ArrayList<AbstractDiagramNode> checkedNodes = new ArrayList<>();

        for (AbstractDiagramLink link : get_lines_out()) {
            nodesToCheck.add(link.nTo);
        }
        checkedNodes.add(this);

        while (nodesToCheck.size() != 0){
            for (AbstractDiagramLink link : nodesToCheck.get(0).get_lines_out()) {
                if(!checkedNodes.contains(link.nTo) && !nodesToCheck.contains(link.nTo)) nodesToCheck.add(link.nTo);
            }
            checkedNodes.add(nodesToCheck.get(0));
            nodesToCheck.remove(nodesToCheck.get(0));
        }
        return checkedNodes;
    }

    ArrayList <AbstractDiagramNode> getChainedBlocksUp(){
        Scheme dobj = (Scheme)getParent();

        ArrayList<DiagramObject> schemeNodes = new ArrayList<>(dobj.diagramObjects.values());
        for (DiagramObject obj: dobj.diagramObjects.values()) {
            if(obj.getClass().equals(DiagramGeneralization.class)){
                schemeNodes.remove(obj);
            }
        }
        schemeNodes.remove(this);
        ArrayList<AbstractDiagramNode> nodesToCheck = new ArrayList<>();
        ArrayList<AbstractDiagramNode> checkedNodes = new ArrayList<>();

        for (AbstractDiagramLink link : get_lines_in()) {
            nodesToCheck.add(link.nFrom);
        }
        checkedNodes.add(this);

        while (nodesToCheck.size() != 0){
            for (AbstractDiagramLink link : nodesToCheck.get(0).get_lines_in()) {
                if(!checkedNodes.contains(link.nFrom) && !nodesToCheck.contains(link.nFrom)) nodesToCheck.add(link.nFrom);
            }
            checkedNodes.add(nodesToCheck.get(0));
            nodesToCheck.remove(nodesToCheck.get(0));
        }
        checkedNodes.remove(this);
        return checkedNodes;
    }


    protected static double SIZE_SCALE = 1.5;
    protected static int FONTSIZEPT = 10;
    protected static double HEIGHT = SIZE_SCALE * 4 * FONTSIZEPT;
    protected static double WIDTH = SIZE_SCALE * 75;
    protected double getHEIGHT (){return HEIGHT;}
    protected double getWIDTH (){return WIDTH;}
    

    final double getmX() {
        return mX;
    }

    private boolean errorPainted;

    final double getmY() {
        return mY;
    }

    @Override
    protected String getCaption() {
        if(caption.isEmpty()) return "<Пусто>";
        return caption;
    }

    public String getClearCaption(){
        return caption;
    }

    @Override
    protected final void internalDrop(double dX, double dY) {
        mX += dX;
        mY += dY;
    }

    protected void setCaption (String caption) { this.caption = caption;}

    protected final void move (double newMx, double newMy){
        mX = newMx;
        mY = newMy;
    }

    protected abstract Vertex getTopMiddleVertex();
    protected abstract Vertex getBotMiddleVertex();
    protected abstract Vertex getLeftVertex();
    protected abstract Vertex getRightVertex();

    public void errorPaint() {
        if(!errorPainted){
            errorPainted = true;
            savedFill = colorFill;
            savedBorder = colorBorder;
            savedFont = colorFont;
            colorFill = Color.red;
            colorFont = Color.white;
        }
    }

    public void errorPaintReset(){
        errorPainted = false;
        colorFill = savedFill;
        colorBorder = savedBorder;
        colorFont = savedFont;
    }

    @Override
    protected boolean internalGetHint(StringBuilder hintStr) {
        hintStr.append(caption);
        return true;
    }

    public void generateCode(SchemeCompiler.CodeGenerator codeGenerator){

    }

    String code = "";
    public void resetCodeString() {
        for (DiagramGeneralization link : get_lines_in()) {  link.passed = false; }
        for (DiagramGeneralization link : get_lines_out()) {  link.passed = false; }

        code = "";
    }
    public void addCodeString(String str){
        code += str + "\n";
    }
}