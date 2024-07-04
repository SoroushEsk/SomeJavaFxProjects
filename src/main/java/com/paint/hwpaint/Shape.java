package com.paint.hwpaint;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Shape {
    double maxLen;
    public ShapeType getShapeType() {
        return shapeType;
    }

    public void setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType;
    }

    private ShapeType shapeType;
    private double startX;

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
    }

    private double startY;
    private double endX;
    private double endY;
    public double sX, sY;
    private Color color;

    public Shape(ShapeType shapeType, double startX, double startY, double endX, double endY, Color color) {
        this.shapeType = shapeType;
        this.color = color;
        if ( shapeType == ShapeType.Triangle){

            int end = (int) Math.max(endY, startY);
            int endMin = (int) Math.min(endY, startY);
            maxLen = Math.abs(end - endMin);
            this.startX = startX;
            this.startY = end;

            this.endX = (startX + endX)/2;
            this.endY = endMin;
            this.sX = endX;
            this.sY = end;
        }else {

            this.startX = startX;
            this.startY = startY;
            this.endX = endX;
            this.endY = endY;
            this.sX = (startX + endX) / 2;
            this.sY = endY;
        }
    }
    public void draw(GraphicsContext gc){
        gc.setStroke(color);
        switch (this.shapeType) {
            case ShapeType.Line :
                gc.strokeLine(startX, startY, endX, endY);
                return;
            case ShapeType.Rectangle:
                gc.strokeRect(Math.min(startX, endX), Math.min(startY, endY),
                    Math.abs(endX - startX), Math.abs(endY - startY));
                return;
            case ShapeType.Circle:
                double radius = Math.hypot(endX - startX, endY - startY);
                gc.strokeOval(startX - radius, startY - radius, 2 * radius, 2 * radius);
                return;
            case ShapeType.Triangle:
                drawTriangle(gc);
                return;
            case ShapeType.Oval:
                drawOval(gc);
                return;
        }
    }
    private void drawTriangle(GraphicsContext gc) {
        double[] xPoints = {startX, endX, sX};
        double[] yPoints = {startY, endY, sY};
        gc.strokePolygon(xPoints, yPoints, 3);
    }

    public Color getColor() {
        return color;
    }

    private void drawOval(GraphicsContext gc){
        double x = Math.min(startX, endX);
        double y = Math.min(startY, endY);
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);
        gc.strokeOval(x, y, width, height);

    }
}
