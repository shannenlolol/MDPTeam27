package ntu.scse.mdp2022.mainui;

import static ntu.scse.mdp2022.mainui.BoardMap.TARGET_CELL_CODE;

public class Robot {
    private int x, y, w, m, p;
    private ntu.scse.mdp2022.mainui.BoardMap map;

    public static final int ROBOT_WHEEL_RIGHT = 1;
    public static final int ROBOT_WHEEL_CENTRE = 0;
    public static final int ROBOT_WHEEL_LEFT = -1;

    public static final int ROBOT_MOTOR_FORWARD = 1;
    public static final int ROBOT_MOTOR_STOP = 0;
    public static final int ROBOT_MOTOR_BACKWARD = -1;

    public static final int ROBOT_POS_NORTH = 0;
    public static final int ROBOT_POS_EAST = 1;
    public static final int ROBOT_POS_SOUTH = 2;
    public static final int ROBOT_POS_WEST = 3;

    public static final String ROBOT_COMMAND_POS = "ROBOT";
    public static final String STM_COMMAND_FORWARD = "w";
    public static final String STM_COMMAND_BACKWARD = "s";
    public static final String STM_COMMAND_STOP = "x";
    public static final String STM_COMMAND_LEFT = "a";
    public static final String STM_COMMAND_RIGHT = "d";
    public static final String STM_COMMAND_CENTRE = "c";

    public Robot(ntu.scse.mdp2022.mainui.BoardMap map){
        this.x = 1;
        this.y = 19;
        this.w = ROBOT_WHEEL_CENTRE;
        this.m = ROBOT_MOTOR_STOP;
        this.p = ROBOT_POS_NORTH;
        this.map = map;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWheel() {
        return w;
    }

    public void setWheel(int w) {
        this.w = w;
    }

    public int getMotor() {
        return m;
    }

    public void setMotor(int m) {
        this.m = m;
    }

    public int getPos() {
        return p;
    }

    public void setPos(int p) {
        this.p = p;
    }

    public String getPosText(){
        switch (this.p) {
            case 0: return "NORTH";
            case 1: return "EAST";
            case 2: return "SOUTH";
            case 3: return "WEST";
        }
        return " ";
    }

    public void wheelPos(int direction) {
        this.w = direction == ROBOT_WHEEL_LEFT ? ROBOT_WHEEL_LEFT : ROBOT_WHEEL_RIGHT;
    }

    public void changePos(boolean clockwise){
        if(clockwise)
            this.p = this.p == ROBOT_POS_WEST ? ROBOT_POS_NORTH : this.p + 1;
        else
            this.p = this.p == ROBOT_POS_NORTH ? ROBOT_POS_WEST : this.p - 1;
    }

    @Override
    public String toString() {
        return "Robot{x = "+ x +", y = "+ y +", w = "+ w +", m = "+ m +", p = "+ p +"}";
    }

    public void robotRotate(int direction) {

        boolean forwardInGrid = false;
        boolean backwardInGrid = false;
        boolean forwardAvoidTarget = false;
        boolean backwardAvoidTarget = false;
        int countForward = 0;
        int countBackward = 0;

        switch(p) {
            case ROBOT_POS_NORTH:
                forwardInGrid = y-1 >= 1;
                backwardInGrid = y+1 < 20;
                forwardAvoidTarget = (this.map.getBoard()[x][y - 1] != TARGET_CELL_CODE) && (this.map.getBoard()[x + 1][y - 1] != TARGET_CELL_CODE);
                backwardAvoidTarget = false;
                countForward = -1;
                countBackward = 1;
                if (backwardInGrid && direction == ROBOT_MOTOR_BACKWARD)
                    backwardAvoidTarget = (this.map.getBoard()[x][y + 2] != TARGET_CELL_CODE) && (this.map.getBoard()[x + 1][y + 2] != TARGET_CELL_CODE);
                break;
            case ROBOT_POS_EAST:
                forwardInGrid = x+1 < 20;
                backwardInGrid = x-1 >= 1;
                forwardAvoidTarget = false;
                backwardAvoidTarget = (this.map.getBoard()[x - 1][y] != TARGET_CELL_CODE) && (this.map.getBoard()[x - 1][y + 1] != TARGET_CELL_CODE);
                countForward = 1;
                countBackward = -1;
                if (forwardInGrid && direction == ROBOT_MOTOR_FORWARD)
                    forwardAvoidTarget = (this.map.getBoard()[x + 2][y] != TARGET_CELL_CODE) && (this.map.getBoard()[x + 2][y+1] != TARGET_CELL_CODE);
                break;
            case ROBOT_POS_SOUTH:
                forwardInGrid = y+1 < 20;
                backwardInGrid = y-1 >= 1;
                forwardAvoidTarget = false;
                backwardAvoidTarget = (this.map.getBoard()[x][y - 1] != TARGET_CELL_CODE) && (this.map.getBoard()[x + 1][y - 1] != TARGET_CELL_CODE);
                countForward = 1;
                countBackward = -1;
                if (forwardInGrid && direction == ROBOT_MOTOR_FORWARD)
                    forwardAvoidTarget = (this.map.getBoard()[x][y + 2] != TARGET_CELL_CODE) && (this.map.getBoard()[x + 1][y + 2] != TARGET_CELL_CODE);
                break;
            case ROBOT_POS_WEST:
                forwardInGrid = x-1 >= 1;
                backwardInGrid = x+1 < 20;
                forwardAvoidTarget = (this.map.getBoard()[x - 1][y] != TARGET_CELL_CODE) && (this.map.getBoard()[x - 1][y + 1] != TARGET_CELL_CODE);
                backwardAvoidTarget = false;
                countForward = -1;
                countBackward = 1;
                if (backwardInGrid && direction == ROBOT_MOTOR_BACKWARD)
                    backwardAvoidTarget = (this.map.getBoard()[x + 2][y] != TARGET_CELL_CODE) && (this.map.getBoard()[x + 2][y + 1] != TARGET_CELL_CODE);
                break;
        }

        if ((direction == ROBOT_MOTOR_FORWARD && forwardInGrid && forwardAvoidTarget)
                || (direction == ROBOT_MOTOR_BACKWARD && backwardInGrid && backwardAvoidTarget)) {
            this.m = direction == ROBOT_MOTOR_FORWARD ? ROBOT_MOTOR_FORWARD : ROBOT_MOTOR_BACKWARD;
            switch(this.p) {
                case ROBOT_POS_NORTH:
                case ROBOT_POS_SOUTH:
                    this.y += direction == ROBOT_MOTOR_FORWARD ? countForward : countBackward;
                    break;
                case ROBOT_POS_EAST:
                case ROBOT_POS_WEST:
                    this.x += direction == ROBOT_MOTOR_FORWARD ? countForward : countBackward;
                    break;
            }
        }
    }

    public void robotClockwiseRotate(){

        boolean turnAngle = false;

        boolean firstObstacle = false;
        boolean secondObstacle = false;
        boolean thirdObstacle = false;

        switch(p){
            case ROBOT_POS_NORTH:
                if (y-2 >= 0 && x+1 <= 19){ //
                    turnAngle = true;
                    firstObstacle = (this.map.getBoard()[x+1][y-1] != TARGET_CELL_CODE);
                    secondObstacle = (this.map.getBoard()[x+2][y-1] != TARGET_CELL_CODE);
                    thirdObstacle = (this.map.getBoard()[x+2][y] != TARGET_CELL_CODE);
                }
                break;

            case ROBOT_POS_EAST:
                if (x + 1 <= 19 && y+1 <= 19){
                    turnAngle = true;
                    firstObstacle = (this.map.getBoard()[x+2][y+1] != TARGET_CELL_CODE);
                    secondObstacle = (this.map.getBoard()[x+2][y+2] != TARGET_CELL_CODE);
                    thirdObstacle = (this.map.getBoard()[x+1][y+2] != TARGET_CELL_CODE);
                }
                break;

            case ROBOT_POS_SOUTH:
                if (x - 2 >= 0 && y + 1 <= 19){
                    turnAngle = true;
                    firstObstacle = (this.map.getBoard()[x][y+2] != TARGET_CELL_CODE);
                    secondObstacle = (this.map.getBoard()[x-1][y+2] != TARGET_CELL_CODE);
                    thirdObstacle = (this.map.getBoard()[x-1][y+1] != TARGET_CELL_CODE);
                }
                break;

            case ROBOT_POS_WEST:
                if (x-2 >= 0 && y-2 >=0){
                    turnAngle = true;
                    firstObstacle = (this.map.getBoard()[x-1][y] != TARGET_CELL_CODE);
                    secondObstacle = (this.map.getBoard()[x-1][y-1] != TARGET_CELL_CODE);
                    thirdObstacle = (this.map.getBoard()[x][y-1] != TARGET_CELL_CODE);
                }
                break;

        }

        if (turnAngle && firstObstacle && secondObstacle && thirdObstacle){
            switch(p){
                case ROBOT_POS_NORTH:
                    this.y -= 1;
                    this.x += 1;
                    this.setPos(ROBOT_POS_EAST);
                    break;
                case ROBOT_POS_EAST:
                    this.x += 1;
                    this.y += 1;
                    this.setPos(ROBOT_POS_SOUTH);
                    break;
                case ROBOT_POS_SOUTH:
                    this.y += 1;
                    this.x -= 1;
                    this.setPos(ROBOT_POS_WEST);
                    break;
                case ROBOT_POS_WEST:
                    this.x -= 1;
                    this.y -= 1;
                    this.setPos(ROBOT_POS_NORTH);
                    break;
            }
        }
    }

    public void robotAnticlockwiseRotate() {

        boolean turnAngle = false;

        boolean firstObstacle = false;
        boolean secondObstacle = false;
        boolean thirdObstacle = false;

        switch (p) {
            case ROBOT_POS_NORTH:
                if (y - 2 >= 0 && x - 2 >= 0) {
                    turnAngle = true;
                    firstObstacle = (this.map.getBoard()[x][y - 1] != TARGET_CELL_CODE);
                    secondObstacle = (this.map.getBoard()[x - 1][y - 1] != TARGET_CELL_CODE);
                    thirdObstacle = (this.map.getBoard()[x - 1][y] != TARGET_CELL_CODE);
                }
                break;
            case ROBOT_POS_EAST:
                if (x + 1 <= 19 && y - 2 >= 0) {
                    turnAngle = true;
                    firstObstacle = (this.map.getBoard()[x + 2][y] != TARGET_CELL_CODE);
                    secondObstacle = (this.map.getBoard()[x + 2][y - 1] != TARGET_CELL_CODE);
                    thirdObstacle = (this.map.getBoard()[x + 1][y - 1] != TARGET_CELL_CODE);
                }
                break;
            case ROBOT_POS_SOUTH:
                if (x + 1 <= 19 && y + 1 <= 19) {
                    turnAngle = true;
                    firstObstacle = (this.map.getBoard()[x + 1][y + 2] != TARGET_CELL_CODE);
                    secondObstacle = (this.map.getBoard()[x + 2][y + 2] != TARGET_CELL_CODE);
                    thirdObstacle = (this.map.getBoard()[x + 2][y + 1] != TARGET_CELL_CODE);
                }
                break;
            case ROBOT_POS_WEST:
                if (x - 2 >= 0 && y + 1 <= 19) {
                    turnAngle = true;
                    firstObstacle = (this.map.getBoard()[x - 1][y + 1] != TARGET_CELL_CODE);
                    secondObstacle = (this.map.getBoard()[x - 1][y + 2] != TARGET_CELL_CODE);
                    thirdObstacle = (this.map.getBoard()[x][y + 2] != TARGET_CELL_CODE);
                }
                break;

        }

        if (turnAngle && firstObstacle && secondObstacle && thirdObstacle) {
            switch (p) {
                case ROBOT_POS_NORTH:
                    this.y -= 1;
                    this.x -= 1;
                    this.setPos(ROBOT_POS_WEST);
                    break;
                case ROBOT_POS_EAST:
                    this.x += 1;
                    this.y -= 1;
                    this.setPos(ROBOT_POS_NORTH);
                    break;
                case ROBOT_POS_SOUTH:
                    this.y += 1;
                    this.x += 1;
                    this.setPos(ROBOT_POS_EAST);
                    break;
                case ROBOT_POS_WEST:
                    this.x -= 1;
                    this.y += 1;
                    this.setPos(ROBOT_POS_SOUTH);
                    break;
            }
        }
    }
}
