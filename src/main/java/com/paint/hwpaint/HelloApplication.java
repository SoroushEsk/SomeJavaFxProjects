package com.paint.hwpaint;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HelloApplication extends Application {
    private final int scale = 5;
    private final int scaleWidth = 2;
    private LineWidth lw = LineWidth.OnePixel;
    private double startX, startY, endX, endY;
    private double iX, iY;
    private boolean drawingLine = false;
    private boolean drawingRectangle = false;
    private boolean drawingCircle = false;
    private boolean drawingTriangle = false;
    private boolean drawingOval = false;
    private Color allColors = Color.BLACK;
    private boolean flag = false;
    private boolean isInOp = true;
    private Shape currentShape = new Shape(ShapeType.Line, 0, 0, 0,0, Color.BLACK);
    private WritableImage savedImage;
    private  GraphicsContext gc;
    private  MousePosition mp = MousePosition.OutSide;
    @Override
    public void start(Stage primaryStage) throws IOException {
        Image appIcon = new Image(getClass().getResource("/image/paint.png").toExternalForm());
        primaryStage.getIcons().add(appIcon);
        primaryStage.setTitle("Paint");


        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);


        HBox shapeButtons = new HBox();
        addShapeButton(shapeButtons);


        ToggleButton sizeButton = new ToggleButton();
        setSizeButton(sizeButton);

        ToggleButton colorButton = new ToggleButton();
        setColorButton(colorButton);

        Button rotateLeft = new Button();
        Image rotateLeftImage = new Image(getClass().getResource("/image/rotateL.png").toExternalForm());
        ImageView rotateLeftView = new ImageView(rotateLeftImage);
        rotateLeftView.setFitWidth(20);rotateLeftView.setFitHeight(20);
        rotateLeft.setGraphic(rotateLeftView);
        rotateLeft.setOnAction(event ->{
            if(flag){
                rotateShape(currentShape, Direction.LEFT);
                restoreGraphicsContextState(gc);
                double radius = Math.hypot(endX - startX, endY - startY);
                gc.setStroke(Color.LIGHTBLUE);
                gc.setLineWidth(1);
                if (drawingCircle) {
                    drawDottedRectangle(gc, startX - radius, startY - radius,
                            2*radius, 2*radius);
                }else if (!drawingTriangle){
                    drawDottedRectangle(gc, Math.min(startX, endX), Math.min(startY, endY),
                            Math.abs(endX - startX), Math.abs(endY - startY));
                }else if (drawingTriangle){
                    drawRectangleAroundTriangle();
                }
                gc.setLineWidth(lw.getSize());
                gc.setStroke(allColors);
                currentShape.draw(gc);
            }
        });

        Button rotateRight = new Button();
        Image rotateRightImage = new Image(getClass().getResource("/image/rotateR.png").toExternalForm());
        ImageView rotateRightView = new ImageView(rotateRightImage);
        rotateRightView.setFitWidth(20);rotateRightView.setFitHeight(20);
        rotateRight.setGraphic(rotateRightView);
        rotateRight.setOnAction(event ->{
            if(flag){
                restoreGraphicsContextState(gc);    rotateShape(currentShape, Direction.RIGHT);
                double radius = Math.hypot(endX - startX, endY - startY);
                gc.setStroke(Color.LIGHTBLUE);
                gc.setLineWidth(1);
                if (drawingCircle) {
                    drawDottedRectangle(gc, startX - radius, startY - radius,
                            2*radius, 2*radius);
                }else if (!drawingTriangle){
                    drawDottedRectangle(gc, Math.min(startX, endX), Math.min(startY, endY),
                            Math.abs(endX - startX), Math.abs(endY - startY));
                }else if (drawingTriangle){
                    drawRectangleAroundTriangle();
                }
                gc.setLineWidth(lw.getSize());
                gc.setStroke(allColors);
                currentShape.draw(gc);
            }
        });

        HBox controllerLayout = new HBox(shapeButtons,sizeButton,colorButton, rotateLeft, rotateRight);
        controllerLayout.setSpacing(5);
        controllerLayout.setStyle("-fx-background-color: #dae7e5; -fx-border-color: black;");

        HBox.setMargin(shapeButtons, new Insets(3));
        HBox.setMargin(sizeButton, new Insets(3));
        HBox.setMargin(colorButton, new Insets(3));
        HBox.setMargin(rotateLeft, new Insets(3));
        HBox.setMargin(rotateRight, new Insets(3));

        Canvas canvas = new Canvas(root.getWidth(), root.getHeight()-controllerLayout.getHeight());
        gc = canvas.getGraphicsContext2D();
        drawingLayout(canvas, gc);


        VBox all = new VBox(controllerLayout,canvas);
        VBox.setMargin(controllerLayout, new Insets(0, 0, 3, 0));
        all.setStyle("-fx-background-color: #dae7e5;");

        BorderPane.setMargin(all, new Insets(4));
        root.setTop(all);
        root.setStyle("-fx-background-color: #dae7e5;");


        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateCanvasSize(canvas, scene);
            }
        });
        primaryStage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                updateCanvasSize(canvas, scene);
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void updateCanvasSize(Canvas canvas, Scene s) {
        double width = s.getWidth();
        double height = s.getHeight();
        canvas.setWidth(width);
        canvas.setHeight(height);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        restoreGraphicsContextState(gc);
    }
    private void setSizeButton(ToggleButton sizeButton) {
        Image sizeImage = new Image(getClass().getResource("/image/size.png").toExternalForm());
        ImageView sizeImageView = new ImageView(sizeImage);
        sizeImageView.setFitWidth(20);sizeImageView.setFitHeight(20);

        sizeButton.setGraphic(sizeImageView);
        sizeButton.setText("Size");



        ContextMenu sizeMenu = new ContextMenu();


        MenuItem small = new MenuItem(LineWidth.OnePixel.toString());
        MenuItem medium = new MenuItem(LineWidth.ThreePixel.toString());
        MenuItem large = new MenuItem(LineWidth.FivePixel.toString());
        MenuItem veryBig = new MenuItem(LineWidth.EightPixel.toString());

        Image one    = new Image(getClass().getResource("/image/1.png").toExternalForm());
        Image three  = new Image(getClass().getResource("/image/2.png").toExternalForm());
        Image five   = new Image(getClass().getResource("/image/3.png").toExternalForm());
        Image eight  = new Image(getClass().getResource("/image/4.png").toExternalForm());

        ImageView oneView = new ImageView(one);
        ImageView threeView = new ImageView(three);
        ImageView fiveView = new ImageView(five);
        ImageView eightView = new ImageView(eight);

        oneView.setFitHeight(one.getHeight()/scale);
        threeView.setFitHeight(three.getHeight()/scale);
        fiveView.setFitHeight(five.getHeight()/scale);
        eightView.setFitHeight(eight.getHeight()/scale);

        oneView.setFitWidth(one.getHeight()/scaleWidth);
        threeView.setFitWidth(three.getHeight()/scaleWidth);
        fiveView.setFitWidth(five.getHeight()/scaleWidth);
        eightView.setFitWidth(eight.getHeight()/scaleWidth);

        small.setGraphic(oneView);
        medium.setGraphic(threeView);
        large.setGraphic(fiveView);
        veryBig.setGraphic(eightView);

        small.setOnAction(event ->{
            lw =LineWidth.OnePixel;
        });
        medium.setOnAction(event ->{
            lw = LineWidth.ThreePixel;
        });
        large.setOnAction(event ->{
            lw = LineWidth.FivePixel;
        });
        veryBig.setOnAction(event ->{
            lw = LineWidth.EightPixel;
        });


        sizeMenu.getItems().addAll(small, medium, large, veryBig);
        sizeButton.setOnAction(event -> {

            restoreGraphicsContextState(gc);
            gc.setStroke(allColors);
            gc.setLineWidth(lw.getSize());
            currentShape.draw(gc);
            saveGraphicsContextState(gc);
            flag = false;
            if (sizeButton.isSelected()) {
                sizeMenu.show(sizeButton, Side.BOTTOM, 0, 0);
            } else {
                sizeMenu.hide();
            }
        });

    }
    private void setColorButton(ToggleButton colorButton) {
        Image colorImage = new Image(getClass().getResource("/image/color.png").toExternalForm());
        ImageView sizeImageView = new ImageView(colorImage);
        sizeImageView.setFitWidth(20);
        sizeImageView.setFitHeight(20);

        colorButton.setGraphic(sizeImageView);
        colorButton.setText("Color");

        Popup sizeMenu = new Popup();

        Button blueItem = new Button();
        Button redItem = new Button();
        Button blackItem = new Button();
        Button yellowItem = new Button();

        Image blue = new Image(getClass().getResource("/image/blue.png").toExternalForm());
        Image red = new Image(getClass().getResource("/image/red.png").toExternalForm());
        Image black = new Image(getClass().getResource("/image/black.png").toExternalForm());
        Image yellow = new Image(getClass().getResource("/image/yellow.png").toExternalForm());

        ImageView blueView = new ImageView(blue);
        ImageView redView = new ImageView(red);
        ImageView blackView = new ImageView(black);
        ImageView yellowView = new ImageView(yellow);

        blueView.setFitHeight(30);
        redView.setFitHeight(30);
        blackView.setFitHeight(30);
        yellowView.setFitHeight(30);

        blueView.setFitWidth(30);
        redView.setFitWidth(30);
        blackView.setFitWidth(30);
        yellowView.setFitWidth(30);

        blueItem.setGraphic(blueView);
        redItem.setGraphic(redView);
        yellowItem.setGraphic(yellowView);
        blackItem.setGraphic(blackView);

        HBox allColor = new HBox(blackItem, blueItem, yellowItem, redItem);
        allColor.setSpacing(10);
        allColor.setPadding(new Insets(5));

        sizeMenu.getContent().add(allColor);

        colorButton.setOnAction(event -> {

            restoreGraphicsContextState(gc);
            gc.setStroke(allColors);
            gc.setLineWidth(lw.getSize());
            currentShape.draw(gc);
            saveGraphicsContextState(gc);
            flag = false;
            if (colorButton.isSelected()) {
                sizeMenu.show(colorButton,
                        colorButton.localToScreen(colorButton.getBoundsInLocal()).getMinX(),
                        colorButton.localToScreen(colorButton.getBoundsInLocal()).getMaxY());
            } else {
                sizeMenu.hide();
            }
        });

        blueItem.setOnAction(event ->{
            allColors = Color.BLUE;
            sizeMenu.hide();
        });
        blackItem.setOnAction(event ->{
            allColors = Color.BLACK;
            sizeMenu.hide();
        });
        redItem.setOnAction(event ->{
            allColors = Color.RED;
            sizeMenu.hide();
        });
        yellowItem.setOnAction(event ->{
            allColors = Color.YELLOW;
            sizeMenu.hide();});
    }
    private void drawingLayout(Canvas canvas, GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        saveGraphicsContextState(gc);

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            startX = e.getX();
            startY = e.getY();
            iX = startX;
            iY = startY;
            double status = pointInShapeBorder(startX, startY);
            if (status == 1) {
                mp = MousePosition.Inside;
                iX = startX;
                iY = startY;
            } else if (status == 0) {
                mp = MousePosition.Border;
                iX = startX;
                iY = startY;
            } else if (status == -1) {
                mp = MousePosition.OutSide;
                if (drawingLine || drawingRectangle || drawingCircle || drawingTriangle || drawingOval) {
                    if (flag) {
                        restoreGraphicsContextState(gc);
                        gc.setStroke(allColors);
                        gc.setLineWidth(lw.getSize());
                        currentShape.draw(gc);
                        saveGraphicsContextState(gc);
                        flag = false;
                    }
                }
                gc.setLineWidth(lw.getSize());
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e -> {
            if (mp == MousePosition.Inside) {
                double dx = e.getX() - iX;
                double dy = e.getY() - iY;
                iX = e.getX();
                iY = e.getY();
                restoreGraphicsContextState(gc);
                gc.setStroke(allColors);
                gc.setLineWidth(lw.getSize());
                currentShape.setStartX(currentShape.getStartX() + dx);
                currentShape.setEndX(currentShape.getEndX() + dx);
                currentShape.sX += dx;
                currentShape.setStartY(currentShape.getStartY() + dy);
                currentShape.setEndY(currentShape.getEndY() + dy);
                currentShape.sY += dy;

                currentShape.draw(gc);
            } else if (mp == MousePosition.Border) {
                double dx = e.getX() - iX;
                double dy = e.getY() - iY;
                iX = e.getX();
                iY = e.getY();
                restoreGraphicsContextState(gc);
                gc.setStroke(allColors);
                gc.setLineWidth(lw.getSize());

                // Update shape's size based on mouse drag
                if (currentShape.getShapeType() == ShapeType.Triangle) {
                    // Resize triangle by adjusting vertices
                    double[] xTriangle = {currentShape.getStartX(), currentShape.getEndX(), currentShape.sX};
                    double[] yTriangle = {currentShape.getStartY(), currentShape.getEndY(), currentShape.sY};

                    for (int i = 0; i < 3; i++) {
                        if (Math.abs(iX - xTriangle[i]) < 10 && Math.abs(iY - yTriangle[i]) < 10) {
                            xTriangle[i] += dx;
                            yTriangle[i] += dy;
                        }
                    }

                    currentShape.setStartX(xTriangle[0]);
                    currentShape.setEndX(xTriangle[1]);
                    currentShape.sX = xTriangle[2];
                    currentShape.setStartY(yTriangle[0]);
                    currentShape.setEndY(yTriangle[1]);
                    currentShape.sY = yTriangle[2];
                } else {
                    double newStartX = currentShape.getStartX();
                    double newStartY = currentShape.getStartY();
                    double newEndX = currentShape.getEndX();
                    double newEndY = currentShape.getEndY();

                    if (Math.abs(iX - newStartX) < 10) {
                        newStartX += dx;
                    } else if (Math.abs(iX - newEndX) < 10) {
                        newEndX += dx;
                    }

                    if (Math.abs(iY - newStartY) < 10) {
                        newStartY += dy;
                    } else if (Math.abs(iY - newEndY) < 10) {
                        newEndY += dy;
                    }

                    currentShape = new Shape(currentShape.getShapeType(), newStartX, newStartY, newEndX, newEndY, currentShape.getColor());
                }

                currentShape.draw(gc);
            } else if (mp == MousePosition.OutSide) {
                if (drawingLine || drawingRectangle || drawingCircle || drawingTriangle || drawingOval) {
                    endX = e.getX();
                    endY = e.getY();
                    restoreGraphicsContextState(gc);
                    gc.setStroke(allColors);
                    chooseShapeDraw(gc);
                }
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (mp == MousePosition.Inside) {
                double dx = e.getX() - iX;
                double dy = e.getY() - iY;
                gc.setStroke(allColors);
                currentShape.setStartX(currentShape.getStartX() + dx);
                currentShape.setEndX(currentShape.getEndX() + dx);
                currentShape.sX += dx;
                currentShape.setStartY(currentShape.getStartY() + dy);
                currentShape.setEndY(currentShape.getEndY() + dy);
                currentShape.sY += dy;
                gc.setLineWidth(lw.getSize());
                gc.setStroke(currentShape.getColor());
            } else if (mp == MousePosition.Border) {
                double dx = e.getX() - iX;
                double dy = e.getY() - iY;
                iX = e.getX();
                iY = e.getY();
                restoreGraphicsContextState(gc);
                gc.setStroke(allColors);
                gc.setLineWidth(lw.getSize());

                // Update shape's size based on mouse release
                if (currentShape.getShapeType() == ShapeType.Triangle) {
                    // Resize triangle by adjusting vertices
                    double[] xTriangle = {currentShape.getStartX(), currentShape.getEndX(), currentShape.sX};
                    double[] yTriangle = {currentShape.getStartY(), currentShape.getEndY(), currentShape.sY};

                    for (int i = 0; i < 3; i++) {
                        if (Math.abs(iX - xTriangle[i]) < 10 && Math.abs(iY - yTriangle[i]) < 10) {
                            xTriangle[i] += dx;
                            yTriangle[i] += dy;
                        }
                    }

                    currentShape.setStartX(xTriangle[0]);
                    currentShape.setEndX(xTriangle[1]);
                    currentShape.sX = xTriangle[2];
                    currentShape.setStartY(yTriangle[0]);
                    currentShape.setEndY(yTriangle[1]);
                    currentShape.sY = yTriangle[2];
                } else {
                    double newStartX = currentShape.getStartX();
                    double newStartY = currentShape.getStartY();
                    double newEndX = currentShape.getEndX();
                    double newEndY = currentShape.getEndY();

                    if (Math.abs(iX - newStartX) < 10) {
                        newStartX += dx;
                    } else if (Math.abs(iX - newEndX) < 10) {
                        newEndX += dx;
                    }

                    if (Math.abs(iY - newStartY) < 10) {
                        newStartY += dy;
                    } else if (Math.abs(iY - newEndY) < 10) {
                        newEndY += dy;
                    }

                    currentShape = new Shape(currentShape.getShapeType(), newStartX, newStartY, newEndX, newEndY, currentShape.getColor());
                }

                currentShape.draw(gc);
            } else if (mp == MousePosition.OutSide) {
                if (drawingLine || drawingRectangle || drawingCircle || drawingTriangle || drawingOval) {
                    endX = e.getX();
                    endY = e.getY();
                    gc.setLineWidth(lw.getSize());
                    gc.setStroke(allColors);
                    flag = true;
                    currentShape = new Shape(chooseShapeDraw(gc), startX, startY, endX, endY, allColors);
                }
            }
            gc.setStroke(Color.LIGHTBLUE);
            gc.setLineWidth(1);
            startX = currentShape.getStartX();
            startY = currentShape.getStartY();
            endY = currentShape.getEndY();
            endX = currentShape.getEndX();
            double radius = Math.hypot(endX - startX, endY - startY);
            if (drawingCircle) {
                drawDottedRectangle(gc, startX - radius, startY - radius, 2 * radius, 2 * radius);
            } else if (!drawingTriangle) {
                drawDottedRectangle(gc, Math.min(startX, endX), Math.min(startY, endY), Math.abs(endX - startX), Math.abs(endY - startY));
            } else if (drawingTriangle) {
                drawRectangleAroundTriangle();
            }
            isInOp = true;
        });
    }

    private int pointInShapeBorder(double x, double y) {
        double errorMargin = 10.0;
        double minX, maxX, minY, maxY;
        if(currentShape == null|| !flag) return -1;
        switch (currentShape.getShapeType()) {
            case Line:
                minX = Math.min(currentShape.getStartX(), currentShape.getEndX()) - errorMargin;
                maxX = Math.max(currentShape.getStartX(), currentShape.getEndX()) + errorMargin;
                minY = Math.min(currentShape.getStartY(), currentShape.getEndY()) - errorMargin;
                maxY = Math.max(currentShape.getStartY(), currentShape.getEndY()) + errorMargin;
                break;
            case Rectangle:
                minX = Math.min(currentShape.getStartX(), currentShape.getEndX());
                maxX = Math.max(currentShape.getStartX(), currentShape.getEndX());
                minY = Math.min(currentShape.getStartY(), currentShape.getEndY());
                maxY = Math.max(currentShape.getStartY(), currentShape.getEndY());
                break;
            case Circle:
                double radius = Math.hypot(currentShape.getEndX() - currentShape.getStartX(), currentShape.getEndY() - currentShape.getStartY());
                minX = currentShape.getStartX() - radius;
                maxX = currentShape.getStartX() + radius;
                minY = currentShape.getStartY() - radius;
                maxY = currentShape.getStartY() + radius;
                break;
            case Triangle:
                double[] xTriangle = {currentShape.getStartX(), currentShape.getEndX(), currentShape.sX};
                double[] yTriangle = {currentShape.getStartY(), currentShape.getEndY(), currentShape.sY};

                minX = Math.min(xTriangle[0], Math.min(xTriangle[1], xTriangle[2]));
                maxX = Math.max(xTriangle[0], Math.max(xTriangle[1], xTriangle[2]));
                minY = Math.min(yTriangle[0], Math.min(yTriangle[1], yTriangle[2]));
                maxY = Math.max(yTriangle[0], Math.max(yTriangle[1], yTriangle[2]));
                break;
            case Oval:
                minX = Math.min(currentShape.getStartX(), currentShape.getEndX());
                maxX = Math.max(currentShape.getStartX(), currentShape.getEndX());
                minY = Math.min(currentShape.getStartY(), currentShape.getEndY());
                maxY = Math.max(currentShape.getStartY(), currentShape.getEndY());
                break;
            default:
                return -1;
        }

        boolean onBorder = (
                (Math.abs(x - minX) <= errorMargin || Math.abs(x - maxX) <= errorMargin) && (y >= minY && y <= maxY)) ||
                ((Math.abs(y - minY) <= errorMargin || Math.abs(y - maxY) <= errorMargin) && (x >= minX && x <= maxX));

        if (onBorder) {
            return 0;
        } else if (x > minX && x < maxX && y > minY && y < maxY) {
            return 1;
        } else {
            return -1;
        }
    }
    private void drawDottedRectangle(GraphicsContext gc, double x, double y, double width, double height) {
        gc.setLineDashes(5);
        gc.setLineDashOffset(0);
        gc.strokeRect(x, y, width, height);
        gc.setLineDashes(0);
    }
    private void drawOval(GraphicsContext gc){
        double x = Math.min(startX, endX);
        double y = Math.min(startY, endY);
        double width = Math.abs(endX - startX);
        double height = Math.abs(endY - startY);
        gc.strokeOval(x, y, width, height);

    }
    private ShapeType chooseShapeDraw(GraphicsContext gc){
        ShapeType result = null;
        if (drawingLine) {
            gc.strokeLine(startX, startY, endX, endY);
            result = ShapeType.Line;
        } else if (drawingRectangle) {
            gc.strokeRect(Math.min(startX, endX), Math.min(startY, endY),
                    Math.abs(endX - startX), Math.abs(endY - startY));
            result = ShapeType.Rectangle;
        } else if (drawingCircle) {
            double radius = Math.hypot(endX - startX, endY - startY);
            gc.strokeOval(startX - radius, startY - radius, 2 * radius, 2 * radius);
            result = ShapeType.Circle;
        }else if (drawingTriangle){
            drawTriangle(gc);
            result = ShapeType.Triangle;
        }else if (drawingOval){
            drawOval(gc);
            result = ShapeType.Oval;
        }
        return result;
    }
    private void addShapeButton(HBox root){
        Button circleButton    = new Button();
        Button rectangleButton = new Button();
        Button lineButton      = new Button();
        Button ovalButton      = new Button();
        Button triangleButton  = new Button();

        Image rectangleImage = new Image(getClass().getResource("/image/rectangle.png").toExternalForm());
        Image triangleImage  = new Image(getClass().getResource("/image/triangle.png").toExternalForm());
        Image circleImage    = new Image(getClass().getResource("/image/circle.png").toExternalForm());
        Image ovalImage      = new Image(getClass().getResource("/image/oval.png").toExternalForm());
        Image lineImage      = new Image(getClass().getResource("/image/line.png").toExternalForm());


        ImageView rectangleImageView  = new ImageView(rectangleImage);
        ImageView triangleImageView  = new ImageView(triangleImage);
        ImageView circleImageView  = new ImageView(circleImage);
        ImageView ovalImageView  = new ImageView(ovalImage);
        ImageView lineImageView  = new ImageView(lineImage);


        rectangleButton.setGraphic(rectangleImageView);
        circleButton.setGraphic(circleImageView);
        lineButton.setGraphic(lineImageView);
        ovalButton.setGraphic(ovalImageView);
        triangleButton.setGraphic(triangleImageView);

        rectangleImageView.setFitWidth(20);rectangleImageView.setFitHeight(20);
        triangleImageView.setFitWidth(20);triangleImageView.setFitHeight(20);
        circleImageView.setFitWidth(20);circleImageView.setFitHeight(20);
        ovalImageView.setFitWidth(20);ovalImageView.setFitHeight(20);
        lineImageView.setFitWidth(20);lineImageView.setFitHeight(20);



        lineButton.setOnAction(event -> {
            drawingLine = true;
            drawingRectangle = false;
            drawingCircle = false;
            drawingOval   = false;
            drawingTriangle = false;
            restoreGraphicsContextState(gc);
            gc.setStroke(allColors);
            gc.setLineWidth(lw.getSize());
            currentShape.draw(gc);
            saveGraphicsContextState(gc);
            flag = false;
        });
        ovalButton.setOnAction(event -> {
            drawingLine = false;
            drawingRectangle = false;
            drawingCircle = false;
            drawingOval   = true;
            drawingTriangle = false;
            restoreGraphicsContextState(gc);
            gc.setStroke(allColors);
            gc.setLineWidth(lw.getSize());
            currentShape.draw(gc);
            saveGraphicsContextState(gc);
            flag = false;
        });
        triangleButton.setOnAction(event -> {
            drawingLine = false;
            drawingRectangle = false;
            drawingCircle = false;
            drawingOval   = false;
            drawingTriangle = true;
            restoreGraphicsContextState(gc);
            gc.setStroke(allColors);
            gc.setLineWidth(lw.getSize());
            currentShape.draw(gc);
            saveGraphicsContextState(gc);
            flag = false;
        });
        rectangleButton.setOnAction(event -> {
            drawingRectangle = true;
            drawingLine = false;
            drawingCircle = false;
            drawingOval   = false;
            drawingTriangle = false;
            restoreGraphicsContextState(gc);
            gc.setStroke(allColors);
            gc.setLineWidth(lw.getSize());
            currentShape.draw(gc);
            saveGraphicsContextState(gc);
            flag = false;
        });
        circleButton.setOnAction(event -> {
            drawingCircle = true;
            drawingLine = false;
            drawingRectangle = false;
            drawingOval   = false;
            drawingTriangle = false;
            restoreGraphicsContextState(gc);
            gc.setStroke(allColors);
            gc.setLineWidth(lw.getSize());
            currentShape.draw(gc);
            saveGraphicsContextState(gc);
            flag = false;
        });

        root.getChildren().addAll(circleButton, rectangleButton, lineButton, triangleButton, ovalButton);
        root.setStyle("-fx-background-color: #F3F3F3;");
    }
    private void saveGraphicsContextState(GraphicsContext gc) {
        Canvas canvas = gc.getCanvas();
        savedImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, savedImage);
    }
    private void drawTriangle(GraphicsContext gc){
        int end = (int) Math.max(endY, startY);
        int endMin = (int) Math.min(endY, startY);
        gc.strokeLine(startX, end, endX, end);
        gc.strokeLine((startX+endX)/2, endMin, endX, end);
        gc.strokeLine((startX+endX)/2, endMin, startX, end);
    }
    private void restoreGraphicsContextState(GraphicsContext gc) {
        gc.drawImage(savedImage, 0, 0);
    }
    enum Direction {
        LEFT, RIGHT
    }

    private void rotateShape(Shape shape, Direction direction) {
        double centerX = (shape.getStartX() + shape.getEndX()) / 2;
        double centerY = (shape.getStartY() + shape.getEndY()) / 2;

        double width = shape.getEndX() - shape.getStartX();
        double height = shape.getEndY() - shape.getStartY();



        if (shape.getShapeType() == ShapeType.Triangle) {
            rotateTriangle(shape, direction);
        } else if (shape.getShapeType() != ShapeType.Circle){
            if (direction == Direction.LEFT) {

                startX = (centerX - height / 2);
                endX   = (centerX + height / 2);
                startY = (centerY + width / 2);
                endY   = (centerY - width / 2);
                shape.setStartX(centerX - height / 2);
                shape.setEndX(centerX + height / 2);
                shape.setStartY(centerY + width / 2);
                shape.setEndY(centerY - width / 2);
            } else if (direction == Direction.RIGHT) {

                startX = (centerX + height / 2);
                endX   = (centerX - height / 2);
                startY = (centerY - width / 2);
                endY   = (centerY + width / 2);
                shape.setStartX(centerX + height / 2);
                shape.setEndX(centerX - height / 2);
                shape.setStartY(centerY - width / 2);
                shape.setEndY(centerY + width / 2);
            }
        }
    }
    enum MousePosition{
        Inside, OutSide, Border
    }
    private void drawRectangleAroundTriangle(){
        double[] xTriangle = {currentShape.getStartX(), currentShape.getEndX(), currentShape.sX};
        double[] yTriangle = {currentShape.getStartY(), currentShape.getEndY(), currentShape.sY};

        double[] xRectangle = new double[4];
        double[] yRectangle = new double[4];

        double minX = Math.min(xTriangle[0], Math.min(xTriangle[1], xTriangle[2]));
        double maxX = Math.max(xTriangle[0], Math.max(xTriangle[1], xTriangle[2]));
        double minY = Math.min(yTriangle[0], Math.min(yTriangle[1], yTriangle[2]));
        double maxY = Math.max(yTriangle[0], Math.max(yTriangle[1], yTriangle[2]));

        xRectangle[0] = minX; // Bottom-left x
        yRectangle[0] = minY; // Bottom-left y

        xRectangle[1] = minX; // Top-left x
        yRectangle[1] = maxY; // Top-left y

        xRectangle[2] = maxX; // Top-right x
        yRectangle[2] = maxY; // Top-right y

        xRectangle[3] = maxX; // Bottom-right x
        yRectangle[3] = minY; // Bottom-right y
        gc.setLineDashes(5);
        gc.setLineDashOffset(0);
        gc.strokePolygon(xRectangle, yRectangle, 4);
        gc.setLineDashes(0);
    }
    private void rotateTriangle(Shape shape, Direction direction) {
        double[] xPoints = {shape.getStartX(), shape.getEndX(), shape.sX};
        double[] yPoints = {shape.getStartY(), shape.getEndY(), shape.sY};
        double centerX = 0, centerY = 0;
        for (int i = 0; i < xPoints.length; i++) {
            centerY += yPoints[i];
            centerX += xPoints[i];
        }
        centerY/=3; centerX/=3;
        for (int i = 0; i < xPoints.length; i++) {
            double dx = xPoints[i] - centerX;
            double dy = yPoints[i] - centerY;

            if (direction == Direction.LEFT) {
                xPoints[i] = centerX - dy;
                yPoints[i] = centerY + dx;
            } else if (direction == Direction.RIGHT) {
                xPoints[i] = centerX + dy;
                yPoints[i] = centerY - dx;
            }
        }

        shape.setStartX(xPoints[0]);
        shape.setEndX(xPoints[1]);
        shape.sX = xPoints[2];
        shape.setStartY(yPoints[0]);
        shape.setEndY(yPoints[1]);
        shape.sY = yPoints[2];

    }

    public static void main(String[] args) {
        launch();
    }
}