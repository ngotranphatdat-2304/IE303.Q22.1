import java.util.*;

class Point {
    long x, y;

    Point(long x, long y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

public class bai3 {

    public static long crossProduct(Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    public static long distSq(Point p1, Point p2) {
        return (p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y);
    }

    public static List<Point> getConvexHull(Point[] points) {
        int n = points.length;
        if (n < 3) return Arrays.asList(points); 
        int min = 0;
        for (int i = 1; i < n; i++) {
            if (points[i].y < points[min].y || (points[i].y == points[min].y && points[i].x < points[min].x)) {
                min = i;
            }
        }

        Point temp = points[0];
        points[0] = points[min];
        points[min] = temp;

        Point pivot = points[0];
        Arrays.sort(points, 1, n, (p1, p2) -> {
            long cp = crossProduct(pivot, p1, p2);
            if (cp == 0) {
                return distSq(pivot, p1) < distSq(pivot, p2) ? -1 : 1;
            }
            return (cp > 0) ? -1 : 1; 
        });

        Stack<Point> stack = new Stack<>();
        stack.push(points[0]);
        stack.push(points[1]);
        stack.push(points[2]);

        for (int i = 3; i < n; i++) {
            while (stack.size() > 1) {
                Point top = stack.pop();
                Point nextToTop = stack.peek();
                
                if (crossProduct(nextToTop, top, points[i]) > 0) {
                    stack.push(top); 
                    break;
                }
            }
            stack.push(points[i]);
        }

        return new ArrayList<>(stack);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            points[i] = new Point(sc.nextLong(), sc.nextLong());
        }

        List<Point> hull = getConvexHull(points);

        System.out.println("Cac diem thuoc bao loi:");
        for (Point p : hull) {
            System.out.println(p);
        }
        
        sc.close();
    }
}