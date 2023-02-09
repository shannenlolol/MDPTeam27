package ntu.scse.mdp2022.mainui;

import static java.lang.Math.ceil;
import static ntu.scse.mdp2022.mainui.BoardMap.CAR_CELL_CODE;
import static ntu.scse.mdp2022.mainui.BoardMap.EMPTY_CELL_CODE;
import static ntu.scse.mdp2022.mainui.BoardMap.EXPLORE_CELL_CODE;
import static ntu.scse.mdp2022.mainui.BoardMap.FINAL_PATH_CELL_CODE;
import static ntu.scse.mdp2022.mainui.BoardMap.TARGET_CELL_CODE;
import static ntu.scse.mdp2022.mainui.Robot.ROBOT_WHEEL_CENTRE;
import static ntu.scse.mdp2022.mainui.Robot.ROBOT_WHEEL_LEFT;
import static ntu.scse.mdp2022.mainui.Robot.ROBOT_WHEEL_RIGHT;
import static ntu.scse.mdp2022.mainui.Target.TARGET_FACE_EAST;
import static ntu.scse.mdp2022.mainui.Target.TARGET_FACE_NORTH;
import static ntu.scse.mdp2022.mainui.Target.TARGET_FACE_SOUTH;
import static ntu.scse.mdp2022.mainui.Target.TARGET_FACE_WEST;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public final class MapCanvas extends View{

    private Paint robotColor = new Paint();
    private Paint gridColor = new Paint();
    private Paint pathPaintColor = new Paint();
    private Paint startPaintColor = new Paint();
    private Paint gridNoPaintColor = new Paint();
    private Paint targetPaintColor = new Paint();
    private Paint finalPathPaintColor = new Paint();
    private Paint explorePaintColor = new Paint();
    private Paint endPaintColor = new Paint();

    private int cellSize = 0;
    private ntu.scse.mdp2022.mainui.BoardMap map = new ntu.scse.mdp2022.mainui.BoardMap();
    private int turn = 0;
    private boolean isSolving = false;

    public static final int NEW_TARGET_TURN = 0;
    public static final int CAR_BLOCK_TURN = -1;
    public static final int TARGET_BLOCK_TURN = 1;

    private Canvas canvas;

    public MapCanvas(Context context) {
        super(context);
    }

    public MapCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        gridColor.setColor(Color.parseColor("#BEBFBD"));
        gridColor.setStrokeWidth(2);
    }

    public final ntu.scse.mdp2022.mainui.BoardMap getFinder() {
        return this.map;
    }

    private void setPaint(Paint paintColor, int color) {
        paintColor.setStyle(Paint.Style.FILL);
        paintColor.setColor(color);
        paintColor.setAntiAlias(true);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int dimension = Math.min(height, width);
        this.cellSize = dimension / 20;
        this.setMeasuredDimension(dimension, dimension);
    }

    private void colorCell(Canvas canvas, int r, int c, float radius, Paint paintColor) {
        RectF rectF = new RectF(
                (float) ((c - 1) * this.cellSize),
                (float) ((r - 1) * this.cellSize),
                (float) (c * this.cellSize),
                (float) (r * this.cellSize)
        );
        canvas.drawRoundRect(
                rectF, // rect
                radius, // rx
                radius, // ry
                paintColor // Paint
        );
        this.invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        this.setPaint(this.pathPaintColor, Color.parseColor("#F2EB24"));
        this.drawGrid(canvas);
        this.drawGridNumber(canvas);
        this.drawCar(canvas);

        int i = 0;
        while (i < map.getTargets().size()) {
            this.drawTarget(canvas, i);
            i++;
        }

        for (int x = 1; x < 20; ++x) {
            for (int j = 1; j < 20; ++j) {
                if (this.map.getBoard()[x][j] == EXPLORE_CELL_CODE) {
                    this.setPaint(this.explorePaintColor, Color.parseColor("#6FF8FC"));
                    this.colorCell(canvas, x, j, 12.0F, this.explorePaintColor);
                }

                if (this.map.getBoard()[i][j] == FINAL_PATH_CELL_CODE) {
                    this.setPaint(this.finalPathPaintColor, Color.parseColor("#0C17F0"));
                    this.colorCell(canvas, x, j, 12.0F, this.finalPathPaintColor);
                }
            }
        }
    }

    private void drawGrid(Canvas canvas) {
        for (int i = 0; i <= 20; ++i) {
            for (int j = 0; j <= 20; ++j) {
                RectF rectF = new RectF(
                        (float) ((j - 1) * this.cellSize) + (float) 1,
                        (float) ((i - 1) * this.cellSize) + (float) 1,
                        (float) (j * this.cellSize) - (float) 1,
                        (float) (i * this.cellSize) - (float) 1
                );
                int cornersRadius = 3;
                canvas.drawRoundRect(
                        rectF, // rect
                        (float) cornersRadius, // rx
                        (float) cornersRadius, // ry
                        gridColor // Paint
                );
            }
        }
    }

    private void drawGridNumber(Canvas canvas) {
        this.setPaint(this.gridNoPaintColor, Color.parseColor("#030303"));
        float halfSize = this.cellSize * 0.38f;
        for (int x = 19; x >= 0; --x) {
            // Left Vertical
            canvas.drawText(Integer.toString(x + 1), this.cellSize * x + halfSize, this.cellSize * 19.6f, gridNoPaintColor);
            // Bottom Horizontal
            canvas.drawText(Integer.toString(20 - x), halfSize, this.cellSize * x + halfSize * 1.5f, gridNoPaintColor);
        }
    }

    private void drawCar(Canvas canvas) {
        this.setPaint(this.robotColor, Color.parseColor("#E0B1DC"));

        this.colorCell(canvas, this.map.getRobot().getY(), this.map.getRobot().getX(), 5.0F, this.robotColor);
        this.colorCell(canvas, this.map.getRobot().getY(), this.map.getRobot().getX() + 1, 5.0F, this.robotColor);
        this.colorCell(canvas, this.map.getRobot().getY() + 1, this.map.getRobot().getX(), 5.0F, this.robotColor);
        this.colorCell(canvas, this.map.getRobot().getY() + 1, this.map.getRobot().getX() + 1, 5.0F, this.robotColor);

        this.drawWheel(canvas);

        this.invalidate();
    }

    private void drawWheel(Canvas canvas) {

        int facingRotation = map.getRobot().getPos();
        canvas.save();
        canvas.rotate(90 * facingRotation, (this.map.getRobot().getX()) * this.cellSize, (this.map.getRobot().getY() - 0.05f) * this.cellSize);

        CornerPathEffect corEffect = new CornerPathEffect(7f);

        Paint paintl = new Paint();
        paintl.setColor(Color.parseColor("#3B353B"));
        paintl.setStrokeWidth(6);
        paintl.setPathEffect(corEffect);
        Path pathl = new Path();

        Paint paintr = new Paint();
        paintr.setColor(Color.parseColor("#3B353B"));
        paintr.setStrokeWidth(6);
        paintr.setPathEffect(corEffect);
        Path pathr = new Path();

        Float offsetWheel = 0.9f * this.cellSize;

        float[][] pathWheelLeftCoord = {
                {(this.map.getRobot().getX() - 0.5f) * this.cellSize, (this.map.getRobot().getY() + 0.15f) * this.cellSize},
                {(this.map.getRobot().getX() - 0.9f) * this.cellSize, (this.map.getRobot().getY() - 0.6f) * this.cellSize},
                {(this.map.getRobot().getX() - 0.45f) * this.cellSize, (this.map.getRobot().getY() - 0.85f) * this.cellSize},
                {(this.map.getRobot().getX() - 0.1f) * this.cellSize, (this.map.getRobot().getY() - 0.1f) * this.cellSize}
        };

        float[][] pathWheelRightCoord = {
                {(this.map.getRobot().getX() - 0.75f) * this.cellSize, (this.map.getRobot().getY() - 0.1f) * this.cellSize},
                {(this.map.getRobot().getX() - 0.45f) * this.cellSize, (this.map.getRobot().getY() - 0.85f) * this.cellSize},
                {(this.map.getRobot().getX()) * this.cellSize, (this.map.getRobot().getY() - 0.6f) * this.cellSize},
                {(this.map.getRobot().getX() - 0.35f) * this.cellSize, (this.map.getRobot().getY() + 0.1f) * this.cellSize}
        };

        float[][] pathWheelStraightCoord = {
                {(this.map.getRobot().getX() - 0.7f) * this.cellSize, (this.map.getRobot().getY() + 0.1f) * this.cellSize},
                {(this.map.getRobot().getX() - 0.7f) * this.cellSize, (this.map.getRobot().getY() - 0.75f) * this.cellSize},
                {(this.map.getRobot().getX() - 0.2f) * this.cellSize, (this.map.getRobot().getY() - 0.75f) * this.cellSize},
                {(this.map.getRobot().getX() - 0.2f) * this.cellSize, (this.map.getRobot().getY() + 0.1f) * this.cellSize}
        };

        // Draw Wheel
        if (this.map.getRobot().getWheel() != ROBOT_WHEEL_CENTRE) {

            switch (this.map.getRobot().getWheel()) {
                case ROBOT_WHEEL_LEFT:
                    // LEFT
                    pathl.moveTo(pathWheelLeftCoord[0][0], pathWheelLeftCoord[0][1]); //LB
                    pathl.lineTo(pathWheelLeftCoord[1][0], pathWheelLeftCoord[1][1]); //LT
                    pathl.lineTo(pathWheelLeftCoord[2][0], pathWheelLeftCoord[2][1]); //RT
                    pathl.lineTo(pathWheelLeftCoord[3][0], pathWheelLeftCoord[3][1]); //RB

                    //RIGHT
                    pathr.moveTo(pathWheelLeftCoord[0][0] + offsetWheel, pathWheelLeftCoord[0][1]); //LB
                    pathr.lineTo(pathWheelLeftCoord[1][0] + offsetWheel, pathWheelLeftCoord[1][1]); //LT
                    pathr.lineTo(pathWheelLeftCoord[2][0] + offsetWheel, pathWheelLeftCoord[2][1]); //RT
                    pathr.lineTo(pathWheelLeftCoord[3][0] + offsetWheel, pathWheelLeftCoord[3][1]); //RB

                    // canvas.rotate(-45, this.map.getRobot().getX() + 0.33f * this.cellSize, this.map.getRobot().getY() + 18.25f * this.cellSize);

                    break;
                case ROBOT_WHEEL_RIGHT:
                    // LEFT
                    pathl.moveTo(pathWheelRightCoord[0][0], pathWheelRightCoord[0][1]); //LB
                    pathl.lineTo(pathWheelRightCoord[1][0], pathWheelRightCoord[1][1]); //LT
                    pathl.lineTo(pathWheelRightCoord[2][0], pathWheelRightCoord[2][1]); //RT
                    pathl.lineTo(pathWheelRightCoord[3][0], pathWheelRightCoord[3][1]); //RB
                    //RIGHT
                    pathr.moveTo(pathWheelRightCoord[0][0] + offsetWheel, pathWheelRightCoord[0][1]); //LB
                    pathr.lineTo(pathWheelRightCoord[1][0] + offsetWheel, pathWheelRightCoord[1][1]); //LT
                    pathr.lineTo(pathWheelRightCoord[2][0] + offsetWheel, pathWheelRightCoord[2][1]); //RT
                    pathr.lineTo(pathWheelRightCoord[3][0] + offsetWheel, pathWheelRightCoord[3][1]); //RB
                    // canvas.rotate(45, this.map.getRobot().getX() + 0.5f * this.cellSize, this.map.getRobot().getY() + 18.25f * this.cellSize);
                    break;
            }
        } else {
            // LEFT
            pathl.moveTo(pathWheelStraightCoord[0][0], pathWheelStraightCoord[0][1]); //LB
            pathl.lineTo(pathWheelStraightCoord[1][0], pathWheelStraightCoord[1][1]); //LT
            pathl.lineTo(pathWheelStraightCoord[2][0], pathWheelStraightCoord[2][1]); //RT
            pathl.lineTo(pathWheelStraightCoord[3][0], pathWheelStraightCoord[3][1]); //RB
            //RIGHT
            pathr.moveTo(pathWheelStraightCoord[0][0] + offsetWheel, pathWheelStraightCoord[0][1]); //LB
            pathr.lineTo(pathWheelStraightCoord[1][0] + offsetWheel, pathWheelStraightCoord[1][1]); //LT
            pathr.lineTo(pathWheelStraightCoord[2][0] + offsetWheel, pathWheelStraightCoord[2][1]); //RT
            pathr.lineTo(pathWheelStraightCoord[3][0] + offsetWheel, pathWheelStraightCoord[3][1]); //RB
        }
        pathl.close();
        canvas.drawPath(pathl, paintl);
        pathr.close();
        canvas.drawPath(pathr, paintr);

        canvas.restore();

        this.invalidate();
    }

    private void drawTarget(Canvas canvas, int targetNo) {
        int x = this.map.getTargets().get(targetNo).getX();
        int y = this.map.getTargets().get(targetNo).getY();

        this.setPaint(this.endPaintColor, Color.parseColor("#000000"));
        this.colorCell(canvas, y, x, 5.0F, this.endPaintColor);
        this.setPaint(this.targetPaintColor, Color.parseColor("#FFFFFF"));

        if (this.map.getTargets().get(targetNo).getImg() > -1) {
            Paint textPaint = new Paint();
            textPaint.setTextSize(33);
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            textPaint.setColor(Color.WHITE);
            textPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(Integer.toString(this.map.getTargets().get(targetNo).getImg()),
                    this.cellSize * (x - 1) + this.cellSize * 0.5f,
                    this.cellSize * y - this.cellSize * 0.25f,
                    textPaint
            );
        } else {

            canvas.drawText(Integer.toString(targetNo + 1),
                    this.cellSize * (x - 1) + this.cellSize * 0.4f,
                    this.cellSize * y - this.cellSize * 0.4f,
                    targetPaintColor
            );
        }

        this.setPaint(this.startPaintColor, Color.parseColor("#FFFF00"));
        float leftBound = 0, topBound = 0, rightBound = 0, bottomBound = 0;
        switch (map.getTargets().get(targetNo).getPos()) {
            case TARGET_FACE_NORTH:
                leftBound = -1;
                topBound = -0.9f;
                rightBound = 0;
                bottomBound = -1;
                break;
            case TARGET_FACE_EAST:
                leftBound = -0.1f;
                topBound = -1;
                rightBound = 0;
                bottomBound = 0;
                break;
            case TARGET_FACE_SOUTH:
                leftBound = -1;
                topBound = -0.1f;
                rightBound = 0;
                bottomBound = 0;
                break;
            case TARGET_FACE_WEST:
                leftBound = -1;
                topBound = -1;
                rightBound = -0.9f;
                bottomBound = 0;
                break;
        }

        RectF fRect = new RectF(
                (float) ((map.getTargets().get(targetNo).getX() + leftBound) * this.cellSize), //left
                (float) ((map.getTargets().get(targetNo).getY() + topBound) * this.cellSize), //top
                (float) ((map.getTargets().get(targetNo).getX() + rightBound) * this.cellSize), //right
                (float) ((map.getTargets().get(targetNo).getY() + bottomBound) * this.cellSize) //bottom
        );
        canvas.drawRoundRect(
                fRect, // rect
                5F, // rx
                5F, // ry
                this.startPaintColor // Paint
        );
        this.invalidate();
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();
        if (!this.isSolving) {
            int y;
            int x;
            Target t;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    y = (int) (ceil(eventY / cellSize));
                    x = (int) (ceil(eventX / cellSize));
                    t = map.findTarget(x, y);

                    final GestureDetector gestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
                        public void onLongPress(MotionEvent e) {
                            dragCell(x, y, turn, 1);
                        }
                    });

                    this.turn = (t != null) ? TARGET_BLOCK_TURN :
                            ((x == this.map.getRobot().getX() && y == this.map.getRobot().getY()) ||
                                    (x == this.map.getRobot().getX() && y == this.map.getRobot().getY() + 1) ||
                                    (x == this.map.getRobot().getX() + 1 && y == this.map.getRobot().getY()) ||
                                    (x == this.map.getRobot().getX() + 1 && y == this.map.getRobot().getY() + 1))
                                    ? CAR_BLOCK_TURN : NEW_TARGET_TURN;

                    switch (this.turn) {
                        case TARGET_BLOCK_TURN:
                            t.changePos(true);
                            map.setLastTouched(t); //don't rmb logic to put in this case or all cases
                            break;
                        case NEW_TARGET_TURN:
                            // LONG PRESS TO MAKE NEW
                            return gestureDetector.onTouchEvent(event);
                        case CAR_BLOCK_TURN:
                            map.getRobot().changePos(true);
                            break;
                    }
                    break;
                // MOVE CELL 1 by 1
                case MotionEvent.ACTION_MOVE:
                    y = (int) (ceil(eventY / cellSize));
                    x = (int) (ceil(eventX / cellSize));
                    this.dragCell(x, y, turn, 0);
                    break;
            }
            this.invalidate();
        }
        return true;
    }

    private void dragCell(int x, int y, int turn, int firstTouch) throws ArrayIndexOutOfBoundsException {
        // MOVE BLUE START BLOCK
        boolean isTargetInGrid = (x >= 1) && (y >= 1) && (x <= 20) && (y <= 20);
        if (turn == CAR_BLOCK_TURN) {
            boolean isCarInGrid = (x >= 1) && (y >= 1) && (x + 1 <= 20) && (y + 1 <= 20);
            if (isCarInGrid && (this.map.getBoard()[x][y] == EMPTY_CELL_CODE)
                    && (this.map.getBoard()[x][y] != TARGET_CELL_CODE)
                    && (this.map.getBoard()[x + 1][y + 1] != TARGET_CELL_CODE)
                    && (this.map.getBoard()[x][y + 1] != TARGET_CELL_CODE)
                    && (this.map.getBoard()[x + 1][y] != TARGET_CELL_CODE)
            ) {
                this.map.getBoard()[this.map.getRobot().getX()][this.map.getRobot().getY()] = EMPTY_CELL_CODE;
                this.map.getBoard()[this.map.getRobot().getX()][this.map.getRobot().getY() + 1] = EMPTY_CELL_CODE;
                this.map.getBoard()[this.map.getRobot().getX() + 1][this.map.getRobot().getY()] = EMPTY_CELL_CODE;
                this.map.getBoard()[this.map.getRobot().getX() + 1][this.map.getRobot().getY() + 1] = EMPTY_CELL_CODE;

                this.map.getRobot().setX(x);
                this.map.getRobot().setY(y);

                this.map.getBoard()[this.map.getRobot().getX()][this.map.getRobot().getY()] = CAR_CELL_CODE;
                this.map.getBoard()[this.map.getRobot().getX()][this.map.getRobot().getY() + 1] = CAR_CELL_CODE;
                this.map.getBoard()[this.map.getRobot().getX() + 1][this.map.getRobot().getY()] = CAR_CELL_CODE;
                this.map.getBoard()[this.map.getRobot().getX() + 1][this.map.getRobot().getY() + 1] = CAR_CELL_CODE;
            }
        } else if (turn == TARGET_BLOCK_TURN) {
            if (isTargetInGrid && (this.map.getBoard()[x][y] == EMPTY_CELL_CODE)
                    && (this.map.getBoard()[x][y] != CAR_CELL_CODE)
            ) {
                Target t = map.getLastTouched();
                this.map.getBoard()[t.getX()][t.getY()] = EMPTY_CELL_CODE;
                t.setX(x);
                t.setY(y);
                this.map.getBoard()[t.getX()][t.getY()] = TARGET_CELL_CODE;
            } else if (!isTargetInGrid) {
                Target t = this.map.getLastTouched();
                if (map.getTargets().contains(t))
                    map.removeTarget(t);
                this.map.getBoard()[t.getX()][t.getY()] = EMPTY_CELL_CODE;
            }
        } else if (turn == NEW_TARGET_TURN) {
            if (isTargetInGrid && (this.map.getBoard()[y][x] == EMPTY_CELL_CODE)) {

                Target t = new Target(x, y, map.getTargets().size());
                this.map.getBoard()[t.getX()][t.getY()] = TARGET_CELL_CODE;
                map.getTargets().add(t);
            }
        }
    }

    public final void setSolving(boolean flag) {
        this.isSolving = flag;
    }
}
