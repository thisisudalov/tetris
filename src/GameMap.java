import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GameMap {
    private static GameMap instance;
    private final Set<Block> blocksLocation;
    private static final int BLOCK_SIZE = Block.getSideSize();
    private final List<Set<Block>> lines;

    public Set<Block> getBlocksLocation() {
        return blocksLocation;
    }

    public static GameMap getInstance() {
        if (instance == null) {
            instance = new GameMap();
        }
        return instance;
    }

    private GameMap() {
        blocksLocation = new HashSet<>();
        lines = new ArrayList<>();
        fillLines();
    }

    private void tryRemovingLine() {
        lines.forEach(line -> {
            if (blocksLocation.containsAll(line)) {
                int y = line.iterator().next().y;
                blocksLocation.removeAll(line);
                blocksLocation.forEach(block -> {
                    if (block.y < y) {
                        block.y += BLOCK_SIZE;
                    }
                });
                GamePanel.increaseScore();
            }
        });
    }

    public boolean doesIntersect(Set<Block> figureLocation) {
        return figureLocation.stream().anyMatch(blocksLocation::contains);
    }

    public void saveFigure(Set<Block> figureLocation) {
        blocksLocation.addAll(figureLocation);
        tryRemovingLine();
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.GREEN);
        blocksLocation.forEach(g::fill);
        g.setColor(Color.RED);
        blocksLocation.forEach(g::draw);
        g.setColor(Color.BLACK);
    }

    private void fillLines() {
        for (int j = 0; j < GamePanel.Y_ELEMS_NUM; j++) {
            Set<Block> line = new HashSet<>();
            for (int i = 0; i < GamePanel.X_ELEMS_NUM; i++) {
                line.add(new Block(i * BLOCK_SIZE, j * BLOCK_SIZE));
            }
            lines.add(line);
        }
    }
}
