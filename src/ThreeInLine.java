import java.awt.*;
import java.util.*;
import java.util.List;

public class ThreeInLine implements Figure {

    private static final int LEFT_MAX = 0;
    private static final int BLOCK_SIZE = Block.getSideSize();
    private int x;
    private int y;
    private Figure.View view = View.A;
    private final Set<Block> defaultForm;
    private final Set<Block> altForm;
    private Block firstBlock;
    private Block secondDefBlock;
    private Block thirdDefBlock;
    private Block secondAltBlock;
    private Block thirdAltBlock;
    private Color color;
    private static final List<Color> COLORSET = new ArrayList<>();
    private Random random = new Random();

    static {
        COLORSET.add(Color.BLUE);
        COLORSET.add(Color.ORANGE);
        COLORSET.add(Color.PINK);
    }

    public ThreeInLine(int leftX, int topY) {
        x = leftX;
        y = topY;
        color = COLORSET.get(random.nextInt(3));
        defaultForm = new HashSet<>();
        altForm = new HashSet<>();
        init();
    }

    private void init() {
        firstBlock = new Block(x, y);

        secondDefBlock = new Block(x + BLOCK_SIZE, y);
        thirdDefBlock = new Block(x + BLOCK_SIZE * 2, y);

        secondAltBlock = new Block(x, y + BLOCK_SIZE);
        thirdAltBlock = new Block(x, y + BLOCK_SIZE * 2);

        defaultForm.add(firstBlock);
        defaultForm.add(secondDefBlock);
        defaultForm.add(thirdDefBlock);

        altForm.add(firstBlock);
        altForm.add(secondAltBlock);
        altForm.add(thirdAltBlock);
    }

    @Override
    public void move(Set<Block> map, Direction direction) {
        switch (direction) {
            case LEFT:
                if (checkLeftSideOk(map)) {
                    x = x - BLOCK_SIZE;
                    setBlocks();
                }
                break;
            case RIGHT:
                if (checkRightSideOk(map)) {
                    x = x + BLOCK_SIZE;
                    setBlocks();
                }
                break;
        }
    }

    private boolean checkLeftSideOk(Set<Block> map) {
        if (x < LEFT_MAX + BLOCK_SIZE || map.contains(new Block(x - BLOCK_SIZE, y))) {
            return false;
        }
        if (!isViewDefault()) {
            return !map.contains(new Block(x - BLOCK_SIZE, y + BLOCK_SIZE))
                    && !map.contains(new Block(x - BLOCK_SIZE, y + BLOCK_SIZE * 2));
        }
        return true;
    }

    @Override
    public void rollback() {
        y -= BLOCK_SIZE;
        setBlocks();
    }

    private boolean checkRightSideOk(Set<Block> map) {
        if (isViewDefault()) {
            return x + BLOCK_SIZE * 4 <= (GamePanel.SCREEN_WIDTH)
                    && !map.contains(new Block(x + BLOCK_SIZE * 3, y));
        } else {
            return x + BLOCK_SIZE * 2 <= GamePanel.SCREEN_WIDTH
                    && !map.contains(new Block(x + BLOCK_SIZE, y + BLOCK_SIZE))
                    && !map.contains(new Block(x + BLOCK_SIZE, y + BLOCK_SIZE * 2))
                    && !map.contains(new Block(x + BLOCK_SIZE, y + BLOCK_SIZE * 3));
        }
    }

    private boolean isViewDefault() {
        switch (view) {
            case A:
            case B:
                return true;
            case C:
            case D:
                return false;
            default: throw new RuntimeException("UNSUPPORTED VIEW");
        }
    }

    private boolean checkCanChangeViewToDef(Set<Block> map) {
        return x + BLOCK_SIZE * 3 <= GamePanel.SCREEN_WIDTH
                && checkAdjacentBlocks(map)
                && !map.contains(new Block(x + BLOCK_SIZE, y))
                && !map.contains(new Block(x + BLOCK_SIZE * 2, y));
    }

    private boolean checkAdjacentBlocks(Set<Block> map) {
        return !map.contains(new Block(x + BLOCK_SIZE, y + BLOCK_SIZE))
                && !map.contains(new Block(x + BLOCK_SIZE * 2, y + BLOCK_SIZE))
                && !map.contains(new Block(x + BLOCK_SIZE, y + BLOCK_SIZE * 2))
                && !map.contains(new Block(x + BLOCK_SIZE * 2, y + BLOCK_SIZE * 2));
    }

    private boolean checkCanChangeViewToAlt(Set<Block> map) {
        return y + BLOCK_SIZE * 3 <= GamePanel.SCREEN_HEIGHT
                && checkAdjacentBlocks(map)
                && !map.contains(new Block(x, y + BLOCK_SIZE))
                && !map.contains(new Block(x, y + BLOCK_SIZE * 2));
    }

    @Override
    public int getLowerY() {
        return isViewDefault()
                ? y + BLOCK_SIZE
                : y + BLOCK_SIZE * 3;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(color);
        if (isViewDefault()) {
            defaultForm.forEach(g::fill);
            g.setColor(Color.BLACK);
            defaultForm.forEach(g::draw);
        } else {
            altForm.forEach(g::fill);
            g.setColor(Color.BLACK);
            altForm.forEach(g::draw);
        }
        g.setColor(Color.BLACK);
    }

    @Override
    public Set<Block> getForm() {
        return isViewDefault() ? defaultForm : altForm;
    }

    private void setBlocks() {
        firstBlock.setLocation(x, y);
        if (isViewDefault()) {
            secondDefBlock.setLocation(x + BLOCK_SIZE, y);
            thirdDefBlock.setLocation(x + BLOCK_SIZE * 2, y);
        } else {
            secondAltBlock.setLocation(x, y + BLOCK_SIZE);
            thirdAltBlock.setLocation(x, y + BLOCK_SIZE * 2);
        }
    }

    @Override
    public void updateWithYChange() {
        y += BLOCK_SIZE;
        updateWithoutYChange();
    }

    @Override
    public void updateWithoutYChange() {
        setBlocks();
    }

    @Override
    public void nextView(Set<Block> map) {
        switch (view) {
            case A:
            case B:
                if (checkCanChangeViewToAlt(map)) {
                    view = View.C;
                }
                break;
            case C:
            case D:
                if (checkCanChangeViewToDef(map)) {
                    view = View.A;
                }
                break;
        }
    }
}
