package code;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

/**
 * Actor-Actor generalization arrow.
 */
class DiagramGeneralization extends AbstractDiagramLink implements LabelParent {

    private double ldX;
    private double ldY;

    public AbstractDiagramNode getnFrom() {
        return nFrom;
    }
    public AbstractDiagramNode getnTo() {
        return nTo;
    }

    public DiagramGeneralization(AbstractDiagramNode nFrom, AbstractDiagramNode nTo) {
        this.nFrom = nFrom;
        this.nTo = nTo;
        /*nFrom.get_lines_in().add(this);
        nTo.get_lines_in().add(this);*/
        this.caption = "";
        label = new DiagramLabel(this);

        if (nFrom.getClass().equals(DiagramRhombus.class)) {
            final ArrayList<DiagramGeneralization> lines_out = nFrom.get_lines_out();
            if(lines_out.size() > 0){
                DiagramLabel label1 = lines_out.get(0).label;
                System.out.println(label1.caption);
                if(lines_out.get(0).label.caption.equals("true")) label.caption = "false";
                else label.caption = "true";
            } else label.caption = "true";
            addToQueue(label);
            //Set<Integer> keys = ((Scheme) DiagramPanel.getDiagramObject()).diagramObjects.keySet();
            //label.setId(Collections.max(keys) + 1);

            //((Scheme)(DiagramPanel.getDiagramObject())).diagramObjects.put(getId(), label);
        }

    }

    @Override
    public String getText() {
        return caption;
    }

    @Override
    public double getLabelX() {
        return (nTo.getmX() + nFrom.getmX()) / 2 + ldX;
    }

    @Override
    public double getLabelY() {
        return (nTo.getmY() + nFrom.getmY()) / 2 + ldY - 2;
    }

    @Override
    public void setLabelX(double newX) {
        ldX = newX - (nTo.getmX() + nFrom.getmX()) / 2;
    }

    @Override
    public void setLabelY(double newY) {
        ldY = newY - (nTo.getmY() + nFrom.getmY()) / 2 + 2;
    }

    @Override
    protected void internalDraw() {

        getCanvas().setColor(Color.BLACK);
        getCanvas().setPaintMode();


        double x1 = nFrom.getTopMiddleVertex().getX();
        double y1 = nFrom.getTopMiddleVertex().getY();
        double x2 = nTo.getBotMiddleVertex().getX();
        double y2 = nTo.getBotMiddleVertex().getY();

        int[] abscissae = new int[3];
        int[] ordinates = new int[3];

        if ((nFrom.getBotMiddleVertex().getX() == nTo.getBotMiddleVertex().getX()) && (nFrom.getBotMiddleVertex().getY() < nTo.getBotMiddleVertex().getY())) {
            getCanvas().drawLine((int)(x1), (int)(y1), (int)(x2), (int)(y2));

        } else if ((nFrom.getBotMiddleVertex().getY() + scale(80) < nTo.getBotMiddleVertex().getY())) {

            getCanvas().drawLine(
                    (int)nFrom.getTopMiddleVertex().getX(),
                    (int)nFrom.getTopMiddleVertex().getY(),
                    (int)nFrom.getTopMiddleVertex().getX(),
                    (int)nFrom.getTopMiddleVertex().getY() + (int) length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nFrom.getTopMiddleVertex().getX(), nTo.getBotMiddleVertex().getY()) / 2);

            getCanvas().drawLine(
                    (int)nFrom.getTopMiddleVertex().getX(),
                    (int)nFrom.getTopMiddleVertex().getY() + (int) length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nFrom.getTopMiddleVertex().getX(), nTo.getBotMiddleVertex().getY()) / 2,
                    (int)nTo.getTopMiddleVertex().getX(),
                    (int)nFrom.getTopMiddleVertex().getY() + (int) length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nFrom.getTopMiddleVertex().getX(), nTo.getBotMiddleVertex().getY()) / 2);

            getArrowPoints(new Point2D.Double((int)nFrom.getTopMiddleVertex().getX(),
                    (int)nFrom.getTopMiddleVertex().getY() + (int) length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nFrom.getTopMiddleVertex().getX(), nTo.getBotMiddleVertex().getY()) / 2),
                    new Point2D.Double((nFrom.getTopMiddleVertex().getX() + nTo.getTopMiddleVertex().getX())/2,
                            (int)nFrom.getTopMiddleVertex().getY() + (int) length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nFrom.getTopMiddleVertex().getX(), nTo.getBotMiddleVertex().getY()) / 2),
                    abscissae, ordinates);

            if(Math.abs(nFrom.getTopMiddleVertex().getX() - nTo.getTopMiddleVertex().getX()) >= scale(30)){
                getCanvas().fillPolygon(abscissae, ordinates, 3);
                getCanvas().drawPolygon(abscissae, ordinates, 1);
            }

            getCanvas().drawLine(
                    (int) nTo.getTopMiddleVertex().getX(),
                    (int) nFrom.getTopMiddleVertex().getY() + (int) length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nFrom.getTopMiddleVertex().getX(), nTo.getBotMiddleVertex().getY()) / 2,
                    (int) nTo.getBotMiddleVertex().getX(),
                    (int) nTo.getBotMiddleVertex().getY());

        } else if ((nFrom.getBotMiddleVertex().getY() + scale(80) >= nTo.getBotMiddleVertex().getY()) && (Math.abs(nFrom.getBotMiddleVertex().getX() - nTo.getBotMiddleVertex().getX()) > scale(130))) {

            getCanvas().drawLine(
                    (int) nFrom.getTopMiddleVertex().getX(),
                    (int) nFrom.getTopMiddleVertex().getY(),
                    (int) nFrom.getTopMiddleVertex().getX(),
                    (int) nFrom.getTopMiddleVertex().getY() + scale(20));

            if (nFrom.getTopMiddleVertex().getX() > nTo.getBotMiddleVertex().getX()) {

                getCanvas().drawLine(
                        (int)   nFrom.getTopMiddleVertex().getX(),
                        (int)   nFrom.getTopMiddleVertex().getY() + scale(20),
                        (int)   nFrom.getTopMiddleVertex().getX() - (int) length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nTo.getTopMiddleVertex().getX(), nFrom.getBotMiddleVertex().getY()) / 2,
                        (int)   nFrom.getTopMiddleVertex().getY() + scale(20));

                getCanvas().drawLine(
                        (int)   nFrom.getTopMiddleVertex().getX() - (int) length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nTo.getTopMiddleVertex().getX(), nFrom.getBotMiddleVertex().getY()) / 2,
                        (int)   nFrom.getTopMiddleVertex().getY() + scale(20),
                        (int)   nFrom.getTopMiddleVertex().getX() - (int) length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nTo.getTopMiddleVertex().getX(), nFrom.getBotMiddleVertex().getY()) / 2,
                        (int)   nTo.getBotMiddleVertex().getY() - scale(20));

                getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().getX() - length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nTo.getTopMiddleVertex().getX(), nFrom.getBotMiddleVertex().getY()) / 2,
                                nFrom.getTopMiddleVertex().getY() + scale(20)),
                        new Point2D.Double(nFrom.getTopMiddleVertex().getX() - length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nTo.getTopMiddleVertex().getX(), nFrom.getBotMiddleVertex().getY()) / 2,
                                (nTo.getBotMiddleVertex().getY() - scale(20) + nFrom.getTopMiddleVertex().getY() + scale(20)) / 2),
                        abscissae, ordinates);
                getCanvas().fillPolygon(abscissae, ordinates, 3);
                getCanvas().drawPolygon(abscissae, ordinates, 1);


                getCanvas().drawLine(
                        (int)   nFrom.getTopMiddleVertex().getX() - (int) length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nTo.getTopMiddleVertex().getX(), nFrom.getBotMiddleVertex().getY()) / 2,
                        (int)   nTo.getBotMiddleVertex().getY() - scale(20),
                        (int)   nTo.getTopMiddleVertex().getX(),
                        (int)   nTo.getBotMiddleVertex().getY() - scale(20));
                getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().getX() + (int) length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nTo.getTopMiddleVertex().getX(), nFrom.getBotMiddleVertex().getY()) / 2,
                                nTo.getBotMiddleVertex().getY() - scale(20)),
                        new Point2D.Double(nTo.getTopMiddleVertex().getX(),
                                nTo.getBotMiddleVertex().getY() - scale(20)), abscissae, ordinates);
                getCanvas().fillPolygon(abscissae, ordinates, 3);
                getCanvas().drawPolygon(abscissae, ordinates, 1);

            } else {
                getCanvas().drawLine(
                        (int)    nFrom.getTopMiddleVertex().getX(),
                        (int)    nFrom.getTopMiddleVertex().getY() + scale(20),
                        (int)    nFrom.getTopMiddleVertex().getX() + (int) length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nTo.getTopMiddleVertex().getX(), nFrom.getBotMiddleVertex().getY()) / 2,
                        (int)    nFrom.getTopMiddleVertex().getY() + scale(20));
                getCanvas().drawLine(
                        (int)   nFrom.getTopMiddleVertex().getX() + (int) length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nTo.getTopMiddleVertex().getX(), nFrom.getBotMiddleVertex().getY()) / 2,
                        (int)   nFrom.getTopMiddleVertex().getY() + scale(20),
                        (int)   nFrom.getTopMiddleVertex().getX() + (int) length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nTo.getTopMiddleVertex().getX(), nFrom.getBotMiddleVertex().getY()) / 2,
                        (int)   nTo.getBotMiddleVertex().getY() - scale(20));
                getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().getX() + (int) length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nTo.getTopMiddleVertex().getX(), nFrom.getBotMiddleVertex().getY()) / 2,
                                nFrom.getTopMiddleVertex().getY() + scale(20)),
                        new Point2D.Double(nFrom.getTopMiddleVertex().getX() + (int) length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nTo.getTopMiddleVertex().getX(), nFrom.getBotMiddleVertex().getY()) / 2,
                                (nTo.getBotMiddleVertex().getY() - scale(20) + nFrom.getTopMiddleVertex().getY() + scale(20)) / 2),
                        abscissae, ordinates);
                getCanvas().fillPolygon(abscissae, ordinates, 3);
                getCanvas().drawPolygon(abscissae, ordinates, 1);

                getCanvas().drawLine(
                        (int)    nFrom.getTopMiddleVertex().getX() + (int) length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nTo.getTopMiddleVertex().getX(), nFrom.getBotMiddleVertex().getY()) / 2,
                        (int)    nTo.getBotMiddleVertex().getY() - scale(20),
                        (int)    nTo.getTopMiddleVertex().getX(),
                        (int)    nTo.getBotMiddleVertex().getY() - scale(20));


                getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().getX() + length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nTo.getTopMiddleVertex().getX(), nFrom.getBotMiddleVertex().getY()) / 2,
                                nTo.getBotMiddleVertex().getY() - scale(20)),
                        new Point2D.Double(nTo.getTopMiddleVertex().getX(),
                                nTo.getBotMiddleVertex().getY() - scale(20)), abscissae, ordinates);
                getCanvas().fillPolygon(abscissae, ordinates, 3);
                getCanvas().drawPolygon(abscissae, ordinates, 1);

            }


            getCanvas().drawLine(
                    (int)   nTo.getTopMiddleVertex().getX(),
                    (int)   nTo.getBotMiddleVertex().getY() - scale(20),
                    (int)   nTo.getBotMiddleVertex().getX(),
                    (int)   nTo.getBotMiddleVertex().getY());
            getArrowPoints(new Point2D.Double(nTo.getTopMiddleVertex().getX(), nTo.getBotMiddleVertex().getY() - 20), new Point2D.Double(nTo.getBotMiddleVertex().getX(), nTo.getBotMiddleVertex().getY()), abscissae, ordinates);
            getCanvas().fillPolygon(abscissae, ordinates, 3);
            getCanvas().drawPolygon(abscissae, ordinates, 1);


        } else {
            getCanvas().drawLine(
                    (int)   nFrom.getTopMiddleVertex().getX(),
                    (int)   nFrom.getTopMiddleVertex().getY(),
                    (int)   nFrom.getTopMiddleVertex().getX(),
                    (int)   nFrom.getTopMiddleVertex().getY() + scale(20));

            if (nFrom.getBotMiddleVertex().getX() >= nTo.getBotMiddleVertex().getX()) {
                getCanvas().drawLine(
                        (int)       nFrom.getTopMiddleVertex().getX(),
                        (int)       nFrom.getTopMiddleVertex().getY() + scale(20),
                        (int)       nFrom.getTopMiddleVertex().getX() + scale(130),
                        (int)       nFrom.getTopMiddleVertex().getY() + scale(20));

                getCanvas().drawLine(
                        (int)     nFrom.getTopMiddleVertex().getX() + scale(130),
                        (int)     nFrom.getTopMiddleVertex().getY() + scale(20),
                        (int)     nFrom.getTopMiddleVertex().getX() + scale(130),
                        (int)     nTo.getBotMiddleVertex().getY() - scale(20));

                getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().getX() + scale(130),
                                nFrom.getTopMiddleVertex().getY() + scale(20)),
                        new Point2D.Double(nFrom.getTopMiddleVertex().getX() + scale(130),
                                (nTo.getBotMiddleVertex().getY() - scale(20) + nFrom.getTopMiddleVertex().getY() + scale(20)) / 2),
                        abscissae, ordinates);
                getCanvas().fillPolygon(abscissae, ordinates, 3);
                getCanvas().drawPolygon(abscissae, ordinates, 1);

                getCanvas().drawLine(
                        (int)      nFrom.getTopMiddleVertex().getX() + scale(130),
                        (int)      nTo.getBotMiddleVertex().getY() - scale(20),
                        (int)      nTo.getTopMiddleVertex().getX(),
                        (int)      nTo.getBotMiddleVertex().getY() - scale(20));

                getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().getX() + length(nFrom.getTopMiddleVertex().getX(), nFrom.getTopMiddleVertex().getY(), nTo.getTopMiddleVertex().getX(), nFrom.getBotMiddleVertex().getY()) / 2,
                                nTo.getBotMiddleVertex().getY() - scale(20)),
                        new Point2D.Double(nTo.getTopMiddleVertex().getX(),
                                nTo.getBotMiddleVertex().getY() - scale(20)), abscissae, ordinates);
                getCanvas().fillPolygon(abscissae, ordinates, 3);
                getCanvas().drawPolygon(abscissae, ordinates, 1);

                getCanvas().drawLine(
                        (int)       nTo.getTopMiddleVertex().getX(),
                        (int)       nTo.getBotMiddleVertex().getY() - scale(20),
                        (int)       nTo.getBotMiddleVertex().getX(),
                        (int)       nTo.getBotMiddleVertex().getY());
            } else {
                getCanvas().drawLine(
                        (int)       nFrom.getTopMiddleVertex().getX(),
                        (int)       nFrom.getTopMiddleVertex().getY() + scale(20),
                        (int)       nFrom.getTopMiddleVertex().getX() - scale(130),
                        (int)       nFrom.getTopMiddleVertex().getY() + scale(20));

                getCanvas().drawLine(
                        (int)     nFrom.getTopMiddleVertex().getX() - scale(130),
                        (int)     nFrom.getTopMiddleVertex().getY() + scale(20),
                        (int)     nFrom.getTopMiddleVertex().getX() - scale(130),
                        (int)     nTo.getBotMiddleVertex().getY() - scale(20));

                getArrowPoints(new Point2D.Double(nFrom.getTopMiddleVertex().getX() - scale(130),
                                nFrom.getTopMiddleVertex().getY() + scale(20)),
                        new Point2D.Double(nFrom.getTopMiddleVertex().getX() - scale(130),
                                (nTo.getBotMiddleVertex().getY() - scale(20) + nFrom.getTopMiddleVertex().getY() + scale(20)) / 2),
                        abscissae, ordinates);
                getCanvas().fillPolygon(abscissae, ordinates, 3);
                getCanvas().drawPolygon(abscissae, ordinates, 1);

                getCanvas().drawLine(
                        (int)     nFrom.getTopMiddleVertex().getX() - scale(130),
                        (int)     nTo.getBotMiddleVertex().getY() - scale(20),
                        (int)     nTo.getTopMiddleVertex().getX(),
                        (int)     nTo.getBotMiddleVertex().getY() - scale(20));

                getArrowPoints(new Point2D.Double((int)nFrom.getTopMiddleVertex().getX() - scale(130),
                        (int)     nTo.getBotMiddleVertex().getY() - scale(20)),
                        new Point2D.Double(nTo.getTopMiddleVertex().getX(),
                                (int)     nTo.getBotMiddleVertex().getY() - scale(20)), abscissae, ordinates);
                getCanvas().fillPolygon(abscissae, ordinates, 3);
                getCanvas().drawPolygon(abscissae, ordinates, 1);

                getCanvas().drawLine(
                        (int)    nTo.getTopMiddleVertex().getX(),
                        (int)    nTo.getBotMiddleVertex().getY() - scale(20),
                        (int)    nTo.getBotMiddleVertex().getX(),
                        (int)    nTo.getBotMiddleVertex().getY());
            }
        }
    }

    private double length (double x1, double y1, double x2, double y2){
        return Math.sqrt((x1 - x2) * ( x1 - x2) + ( y1 - y2) * (y1 - y2));
    }
}