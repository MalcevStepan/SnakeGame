package com.example.sneakgame;

class Point {
    int x = 0, y = 0;

    public Point() { x = y = 0; }

    public Point(int x, int y) { this.x = x; this.y = y; }
    public Point(Point point) { x = point.x; y = point.y; }

    public void lerp(Point point, float percent)
    {
        x += (point.x - x) * percent;
        y += (point.y - y) * percent;
    }

    public static Point lerp(Point start, Point end, float percent)
    {
        return new Point((int)(start.x + (end.x - start.x) * percent), (int)(start.y + (end.y - start.y) * percent));
    }

    public void set(int x, int y) { this.x = x; this.y = y; }

    @Override
    public boolean equals(Object obj) { return obj instanceof Point && (((Point) obj).x == x && ((Point) obj).y == y); }

    @Override
    public String toString()
    {
        return x + ", " + y;
    }
}