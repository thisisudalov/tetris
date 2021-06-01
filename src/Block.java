import java.awt.*;

public class Block extends Rectangle {
    private static int sideSize = GamePanel.SCREEN_SIDE / 20;

    public Block(int x, int y) {
        super(x, y, sideSize, sideSize);
    }

    public static int getSideSize() {
        return sideSize;
    }

    @Override
    public String toString() {
        return "x=" + x +
                ", y=" + y;
    }
}
