package ntu.scse.mdp2022.mainui;

import static ntu.scse.mdp2022.mainui.Target.TARGET_IMG_EMPTY;

import java.util.ArrayList;

public class BoardMap {
    Robot robot;
    ArrayList<Target> targets = new ArrayList<Target>();
    Target lastTouched;

    private int rows = 21;
    private int cols = 21;
    int[][] board = new int[rows][cols];

    public static final int EMPTY_CELL_CODE = 0;
    public static final int CAR_CELL_CODE = 1;
    public static final int TARGET_CELL_CODE = 3;
    public static final int EXPLORE_CELL_CODE = 4;
    public static final int EXPLORE_HEAD_CELL_CODE = 5;
    public static final int FINAL_PATH_CELL_CODE = 6;

    public BoardMap(){
        super();
        robot = new Robot(this);
        board[robot.getX()][robot.getY()] = CAR_CELL_CODE;
    }

    public final void resetBoardMap(){
        for (int i = 1; i <= 19; i++){
            for (int j = 1; j <= 19; ++j){
                this.board[i][j] = 0;
            }
        }
        getRobot().setX(1);
        getRobot().setY(19);
        getRobot().setPos(Robot.ROBOT_POS_NORTH);
        targets.clear();
        this.board[getRobot().getX()][getRobot().getY()] = CAR_CELL_CODE;
    }

    public boolean hasAllTargets() {
        int targetReceived = 0;
        for(int i = 0; i < targets.size(); i++) {
            if (targets.get(i).getImg() > TARGET_IMG_EMPTY) {
                targetReceived++;
            }
        }
        return targetReceived == targets.size();
    }

    public Target findTarget(int x, int y) {
        int n = 0;
        while (n < targets.size()) {
            if (targets.get(n).getX() == x && targets.get(n).getY() == y) return targets.get(n);
            n++;
        }
        return null;
    }

    public void removeTarget(Target t) {
        int delTargetId = t.getN();
        targets.remove(t.getN());
        while (delTargetId < targets.size()) {
            targets.get(delTargetId).setN(delTargetId);
            delTargetId++;
        }
    }

    public void defaceTargets() {
        for(int i = 0; i < targets.size(); i++) {
            targets.get(i).setImg(TARGET_IMG_EMPTY);
        }
    }

    public Robot getRobot() {
        return robot;
    }

    public ArrayList<Target> getTargets() {
        return targets;
    }

    public Target getLastTouched() {
        return lastTouched;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setLastTouched(Target lastTouched) {
        this.lastTouched = lastTouched;
    }
}
