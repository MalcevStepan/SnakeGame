package com.example.sneakgame;

class Point {
    int x = 0, y = 0;

    public Point() { x = y = 0; }

    public Point(int x, int y) { this.x = x; this.y = y; }
    public Point(Point point) { x = point.x; y = point.y; }
    public Point(Vector vector) { x = (int)vector.x; y = (int)vector.y; }

    public void lerp(Point point, float percent)
    {
        x += (point.x - x) * percent;
        y += (point.y - y) * percent;
    }

    public void lerp(Vector vector, float percent)
    {
        x += (vector.x - x) * percent;
        y += (vector.y - y) * percent;
    }

    public static Point lerp(Point start, Point end, float percent)
    {
        return new Point((int)(start.x + (end.x - start.x) * percent), (int)(start.y + (end.y - start.y) * percent));
    }

    public static Point lerp(Vector start, Vector end, float percent)
    {
        return new Point((int)(start.x + (end.x - start.x) * percent), (int)(start.y + (end.y - start.y) * percent));
    }

    public static Point lerp(Point start, Vector end, float percent)
    {
        return new Point((int)(start.x + (end.x - start.x) * percent), (int)(start.y + (end.y - start.y) * percent));
    }

    public static Point lerp(Vector start, Point end, float percent)
    {
        return new Point((int)(start.x + (end.x - start.x) * percent), (int)(start.y + (end.y - start.y) * percent));
    }

    @Override
    public boolean equals(Object obj) { return (obj instanceof Point && (((Point) obj).x == x && ((Point) obj).y == y)) || (obj instanceof Vector && (((Vector) obj).x == x && ((Vector) obj).y == y)); }

    @Override
    public String toString()
    {
        return x + ", " + y;
    }
}

class Vector {
    float x = 0, y = 0;

    public Vector() { x = y = 0; }

    public Vector(int x, int y) { this.x = x; this.y = y; }
    public Vector(float x, float y) { this.x = x; this.y = y; }
    public Vector(Vector vector) { x = vector.x; y = vector.y; }
    public Vector(Point point) { x = point.x; y = point.y; }

    public void lerp(Point point, float percent)
    {
        x += (point.x - x) * percent;
        y += (point.y - y) * percent;
    }

    public void lerp(Vector vector, float percent)
    {
        x += (vector.x - x) * percent;
        y += (vector.y - y) * percent;
    }

    public static Vector lerp(Point start, Point end, float percent)
    {
        return new Vector(start.x + (end.x - start.x) * percent, start.y + (end.y - start.y) * percent);
    }

    public static Vector lerp(Vector start, Vector end, float percent)
    {
        return new Vector(start.x + (end.x - start.x) * percent, start.y + (end.y - start.y) * percent);
    }

    public static Vector lerp(Point start, Vector end, float percent)
    {
        return new Vector(start.x + (end.x - start.x) * percent, start.y + (end.y - start.y) * percent);
    }

    public static Vector lerp(Vector start, Point end, float percent)
    {
        return new Vector(start.x + (end.x - start.x) * percent, start.y + (end.y - start.y) * percent);
    }

    @Override
    public boolean equals(Object obj) { return (obj instanceof Vector && (((Vector) obj).x == x && ((Vector) obj).y == y)) || (obj instanceof Point && (((Point) obj).x == x && ((Point) obj).y == y)); }

    @Override
    public String toString()
    {
        return x + ", " + y;
    }
}