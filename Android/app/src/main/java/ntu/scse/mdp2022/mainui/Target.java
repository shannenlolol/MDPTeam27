package ntu.scse.mdp2022.mainui;

public class Target {
    private int x, y, n, pos, img;

    public static final int TARGET_FACE_NORTH = 0;
    public static final int TARGET_FACE_EAST = 1;
    public static final int TARGET_FACE_SOUTH = 2;
    public static final int TARGET_FACE_WEST = 3;
    public static final int TARGET_IMG_EMPTY = -1;

    public static final String BLUETOOTH_TARGET_IDENTIFIER = "TARGET";

    public Target(int x, int y, int n) {
        this.x = x;
        this.y = y;
        this.n = n;
        this.pos = TARGET_FACE_NORTH;
        this.img = TARGET_IMG_EMPTY;
    }

//    public Target(int x, int y, int n, int pos) {
//        this.x = x;
//        this.y = y;
//        this.n = n;
//        this.pos = pos;
//        this.img = TARGET_IMG_EMPTY;
//    }

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

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getPos() {
        return pos;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public void changePos(boolean clockwise) {
        if (clockwise)
            this.pos = this.pos == TARGET_FACE_WEST ? TARGET_FACE_NORTH : this.pos + 1;
        else
            this.pos = this.pos == TARGET_FACE_NORTH ? TARGET_FACE_WEST : this.pos - 1;
    }

    @Override
    public String toString() {
        return "Target{x = " + x + ", y = " + y + ", n = " + n + ", pos = " + pos + ", i = " + img + "}";
    }
}
