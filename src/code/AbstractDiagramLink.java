package code;

import org.json.simple.JSONObject;

import java.awt.geom.Point2D;
import java.util.ArrayList;


/**
 * Base class for connection lines and arrows.
 */
public abstract class AbstractDiagramLink extends DiagramObject {

    protected AbstractDiagramNode nFrom;
    protected AbstractDiagramNode nTo;
    protected AbstractDiagramNode getnFrom() {
        return nFrom;
    }
    protected AbstractDiagramNode getnTo() {
        return nTo;
    }

    Point2D.Double getBorder(double dX, double dY, AbstractDiagramNode abstractDiagramNode) {

        double dXFrom;

        if (dY == 0) {
            dXFrom = abstractDiagramNode.getWIDTH();
        } else {
            dXFrom = Math.min(abstractDiagramNode.getHEIGHT() * Math.abs(dX), abstractDiagramNode.getWIDTH() * Math.abs(dY))
                    / Math.abs(dY);
        }
        dXFrom = Math.signum(dX) * dXFrom / 2;

        double dYFrom;
        if (dX == 0) {
            dYFrom = abstractDiagramNode.getHEIGHT();
        } else {
            dYFrom = Math.min(abstractDiagramNode.getWIDTH() * Math.abs(dY), abstractDiagramNode.getHEIGHT() * Math.abs(dX))
                    / Math.abs(dX);
        }
        dYFrom = -Math.signum(dY) * dYFrom / 2;

        return new Point2D.Double(dXFrom, dYFrom);


    }

    @Override
    protected String getCaption() {
        return caption;
    }

    protected JSONObject getJSON(){
        JSONObject elementDetails = new JSONObject();
        elementDetails.put("idstart", this.getnFrom().getId());
        elementDetails.put("idend", this.getnTo().getId());
        elementDetails.put("text", this.getCaption());
        elementDetails.put("shape", this.getClass().getName());
        elementDetails.put(("id"), this.getId());
        return elementDetails;
    }

    void getArrowPoints(Point2D.Double from, Point2D.Double to, int[] abscissae, int[] ordinates) {
        final double tgAngle = 0.577; // tg 30 degr.
        final double arLength = 5;
        final double x1 = from.x;
        final double x2 = to.x;
        final double y1 = from.y;
        final double y2 = to.y;

        final double len = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

        double ax1;
        double ay1;
        double ax2;
        double ay2;

        if ((y2 != y1) && (x2 != x1)) {
            double k = -(x2 - x1) / (y2 - y1);
            double d = y1 - k * x1;
            double a = 1 + k * k;
            double b = k * d - x1 - k * y1;
            double c = x1 * x1 + d * d - 2 * d * y1 + y1 * y1 - tgAngle * tgAngle * len * len;
            if ((b * b - a * c) > 0) {
                ax1 = (-b - Math.sqrt(b * b - a * c)) / a;
                ax2 = (-b + Math.sqrt(b * b - a * c)) / a;
            } else {
                c = x1 * x1 + d * d - 2 * d * y1 + y1 * y1 + tgAngle * tgAngle * len * len;
                ax1 = (-b - Math.sqrt(b * b - a * c)) / a;
                ax2 = (-b + Math.sqrt(b * b - a * c)) / a;
            }
            ay1 = k * ax1 + d;
            ay2 = k * ax2 + d;
            ax1 = scale(arLength) * (ax1 - x2) / len + x2;
            ax2 = scale(arLength) * (ax2 - x2) / len + x2;
            ay1 = scale(arLength) * (ay1 - y2) / len + y2;
            ay2 = scale(arLength) * (ay2 - y2) / len + y2;
        } else {
            if (y1 == y2) {
                ax1 = x2 - Math.signum(x2 - x1) * scale(arLength);
                ax2 = ax1;
                ay1 = y2 - scale(arLength) * tgAngle;
                ay2 = y2 + scale(arLength) * tgAngle;
            } else if (x1 == x2) {
                ay1 = y2 - Math.signum(y2 - y1) * scale(arLength);
                ay2 = ay1;
                ax1 = x2 - scale(arLength) * tgAngle;
                ax2 = x2 + scale(arLength) * tgAngle;
            } else { // this should never happen
                return;
            }
        }

        abscissae[0] = (int)(ax1);
        abscissae[1] = (int)(x2);
        abscissae[2] = (int)(ax2);
        ordinates[0] = (int)(ay1);
        ordinates[1] = (int)(y2);
        ordinates[2] = (int)(ay2);
    }
}
