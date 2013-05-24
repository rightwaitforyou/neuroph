package org.neuroph.netbeans.visual.widgets;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.widget.ConnectionWidget;

/**
 *
 * @author Maja
 */
public class RouterConnection implements Router {

    Point first;
    Point last;

    public RouterConnection(Point first, Point last) { //point nije centar widgeta, pomeren je ulevo ili udesno za poluprecnik 
        this.first = first;                            //poluprecnik widgeta je sada fiksan = 25
        this.last = last;
        if (first.x < last.x) { //ako je s leva na desno
            first.x -= 25;
            last.x += 25;
        }
        if (first.x > last.x) { // ako je s desna na levo
            first.x += 25;
            last.x -= 25;
        } 
        //ako je veza sa samim sobom, onda Point jeste u centru widgeta
    }

    public List<Point> routeConnection(ConnectionWidget cw) {
        int firstX = first.x;
        int lastX = last.x;
        int distance = Math.abs(lastX - firstX);

        if (distance == 0) { //ako je veza sa samim sobom
            return routeLoop();
        }
        if (distance < 100) { // ako su jedan do drugog; ovo sada radi jer je velicina neuron widgeta fiksna, ali bi trebalo osmisliti neki sigurniji nacin
            return routeLine();
        }
        return routeCurve();
    }

    private List<Point> routeCurve() {
        int firstX = first.x;
        int lastX = last.x;
        int middleX = (firstX + lastX) / 2;
        int currentY = first.y;
        int distance = Math.abs(lastX - firstX);
        int middleY = currentY - distance / 2;

        List<Point> points = new ArrayList<Point>();
        double t = 0;
        while (t < 1) {
            int x = (int) (Math.pow(1 - t, 2) * firstX + 2 * t * (1 - t) * middleX + Math.pow(t, 2) * lastX);
            int y = (int) (Math.pow(1 - t, 2) * currentY + 2 * t * (1 - t) * middleY + Math.pow(t, 2) * currentY);
            t += 0.01;
            if (x < middleX) { //ako je tacka u levoj polovini
                int leftX = lastX;
                if (firstX < lastX) {
                    leftX = firstX;
                }
                double i = Math.sqrt(Math.pow(Math.abs(x - leftX), 2) + Math.pow(y - currentY, 2));
                if (i >= 25) // poluprecnik widgeta = 25; ako tacka nije u prostoru widgeta
                {
                    points.add(new Point(x, y));
                }
            } else { //ako je tacka u desnoj polovini
                int rightX = lastX;
                if (firstX > lastX) {
                    rightX = firstX;
                }
                double i = Math.sqrt(Math.pow(Math.abs(rightX - x), 2) + Math.pow(y - currentY, 2));
                if (i >= 25) // poluprecnik widgeta = 25; ako tacka nije u prostoru widgeta
                {
                    points.add(new Point(x, y));
                }
            }
        }
        return points;
    }

    private List<Point> routeLine() {
        int firstX = first.x;
        int lastX = last.x;
        int currentY = first.y;
        //25 je poluprecnik widgeta
        List<Point> points = new ArrayList<Point>();
        if (firstX < lastX) { //ako je s leva na desno
            points.add(new Point(firstX + 25, currentY));
            points.add(new Point(lastX - 25, currentY));
        } else { //ako je s desna na levo
            points.add(new Point(firstX - 25, currentY));
            points.add(new Point(lastX + 25, currentY));
        }
        return points;
    }

    private List<Point> routeLoop() {
        int currentX = first.x;
        int currentY = first.y;
        // poluprecnik widgeta = 25
        // precnik widgeta = 50
        int leftDownX = (int) (currentX - Math.sqrt(Math.pow(25, 2) / 5)); 
        int leftUpX = currentX - 25; 
        int rightDownX = (int) (currentX + Math.sqrt(Math.pow(25, 2) / 5));
        int rightUpX = currentX + 25; 
        int upY = currentY + 50; 
        int downY = (int) (currentY + 2 * Math.sqrt(Math.pow(25, 2) / 5)); 

        List<Point> points = new ArrayList<Point>();
        double t = 0;
        while (t <= 1) {
            int x = (int) (Math.pow(1 - t, 3) * leftDownX + 3 * t * Math.pow(1 - t, 2) * leftUpX + 3 * (1 - t) * Math.pow(t, 2) * rightUpX + Math.pow(t, 3) * rightDownX);
            int y = (int) (Math.pow(1 - t, 3) * downY + 3 * t * Math.pow(1 - t, 2) * upY + 3 * (1 - t) * Math.pow(t, 2) * upY + Math.pow(t, 3) * downY);
            t += 0.01;
            points.add(new Point(x, y));
        }
        return points;
    }
}