package com.jobs.snake;

class Point {
    byte x, y;

    Point(byte x, byte y) { this.x = x; this.y = y; }

    @Override
    public boolean equals(Object obj) { return obj instanceof Point && (((Point) obj).x == x && ((Point) obj).y == y); }

    @Override
    public String toString()
    {
        return x + ", " + y;
    }
}