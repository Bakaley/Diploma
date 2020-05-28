package code;

import org.json.simple.JSONObject;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static code.DiagramPanel.getDiagramObject;

abstract class AbstractDiagramNode extends DiagramObject{


    private double mX;
    private double mY;

    protected Color colorFill = Color.cyan;
    protected Color colorFont = Color.black;
    protected Color colorBorder = Color.black;

    protected HashMap <String, Vertex> vertices = new HashMap<>();

    public HashMap<String, Vertex> getVertices (){
        return this.vertices;
    }

    public void addVertex (String string, Vertex vertex){
        vertices.put(string, vertex);
    }

    public void setColorFont (Color color) { this.colorFont = color;}
    public void setColorFill (Color color) { this.colorFill = color;}
    public void setColorBorder(Color color) {this.colorBorder = color;}


    public ArrayList<DiagramGeneralization> get_lines_in() {
        HashMap <Integer, DiagramObject> diagramObjects = ((Scheme)getDiagramObject()).diagramObjects;
        ArrayList <DiagramGeneralization> lines = new ArrayList<>();
        Iterator<DiagramObject> iterator = diagramObjects.values().iterator();
        while (iterator.hasNext()){
            DiagramObject object = iterator.next();
            if(object.getClass().getName().equals("code.DiagramGeneralization")) {
                DiagramGeneralization link = (DiagramGeneralization)object;
                if(link.nTo == this){
                    lines.add(link);
                }
            }
        }
    return lines;}

    public ArrayList<DiagramGeneralization> get_lines_out() {
        HashMap<Integer, DiagramObject> diagramObjects = ((Scheme) getDiagramObject()).diagramObjects;
        ArrayList<DiagramGeneralization> lines = new ArrayList<>();
        Iterator<DiagramObject> iterator = diagramObjects.values().iterator();
        while (iterator.hasNext()) {
            DiagramObject object = iterator.next();
            if (object.getClass().getName().equals("code.DiagramGeneralization")) {
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
        elementDetails.put("text", this.getCaption());
        elementDetails.put("shape", this.getClass().getName());

        return elementDetails;
    }


    AbstractDiagramNode(double mX, double mY, String caption) {
        this.mX = mX;
        this.mY = mY;
        this.caption = caption;

    }


    protected static double SIZE_SCALE = 1.5;
    protected static int FONTSIZEPT = 10;
    protected static double HEIGHT = SIZE_SCALE * 3 * FONTSIZEPT;
    protected static double WIDTH = SIZE_SCALE * 50;
    protected double shift;

    protected double getHEIGHT (){return HEIGHT;}
    protected double getWIDTH (){return WIDTH;}
    

    final double getmX() {
        return mX;
    }


    final double getmY() {
        return mY;
    }

    @Override
    protected String getCaption() {
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


    @Override
    protected boolean internalGetHint(StringBuilder hintStr) {
        hintStr.append(caption);
        return true;
    }



}