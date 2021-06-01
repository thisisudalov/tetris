import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Set;

public class GamePanel extends JPanel implements Runnable {
    public static final int SCREEN_SIDE = 800;
    public static final int SCREEN_WIDTH = SCREEN_SIDE / 2;
    public static final int SCREEN_HEIGHT = SCREEN_SIDE;
    private static final int BLOCK_SIZE = Block.getSideSize();
    private static final Dimension DIMENSION = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
    private static final int NEW_FIGURE_START_X = SCREEN_WIDTH / 2;
    private static final int MULTIPLIER = 10;
    public static final int X_ELEMS_NUM = SCREEN_WIDTH / BLOCK_SIZE;
    public static final int Y_ELEMS_NUM = SCREEN_HEIGHT / BLOCK_SIZE;
    private Thread gameThread;
    private Figure currentFigure;
    private GameMap gameMap = GameMap.getInstance();
    private static int score = 0;

    public GamePanel() {
        this.setPreferredSize(DIMENSION);
        this.setFocusable(true);
        this.addKeyListener(new MyAdapter());

        gameThread = new Thread(this, "Game Thread");
        gameThread.start();
        setNewFigure();
    }

    public void paint(Graphics g) {
        draw(g);
    }

    public static void increaseScore() {
        score += MULTIPLIER * X_ELEMS_NUM;
    }

    public void draw(Graphics g) {
        Graphics2D graphics2D = (Graphics2D) g;
        currentFigure.draw(graphics2D);
        gameMap.draw(graphics2D);
        g.drawString(Integer.toString(score), BLOCK_SIZE, BLOCK_SIZE);
    }

    private void setNewFigure() {
        currentFigure = new ThreeInLine(NEW_FIGURE_START_X, 0);
    }

    private synchronized void nextFigure(Set<Block> figureForm) {
        gameMap.saveFigure(figureForm);
        setNewFigure();
    }

    private void updateWithMove() {
        currentFigure.updateWithYChange();
        update();
    }

    private void updateWithoutMove() {
        currentFigure.updateWithoutYChange();
        update();
    }

    private synchronized void update() {
        Set<Block> figureForm = currentFigure.getForm();
        if (gameMap.doesIntersect(figureForm)) {
            currentFigure.rollback();
            nextFigure(figureForm);
        } else if (currentFigure.getLowerY() >= SCREEN_HEIGHT) {
            nextFigure(figureForm);
        }
    }

    @Override
    public void run() {
        while (true) {
            sleep();
            updateWithMove();
            repaint();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            System.out.println("INTERRUPTED");
        }
    }

    public class MyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    currentFigure.move(GameMap.getInstance().getBlocksLocation(), Direction.LEFT);
                    repaint();
                    break;
                case KeyEvent.VK_RIGHT:
                    currentFigure.move(GameMap.getInstance().getBlocksLocation(), Direction.RIGHT);
                    repaint();
                    break;
                case KeyEvent.VK_SPACE:
                    currentFigure.nextView(GameMap.getInstance().getBlocksLocation());
                    updateWithoutMove();
                    repaint();
                    break;
                case KeyEvent.VK_DOWN:
                    updateWithMove();
                    repaint();
                    break;
            }
        }
    }
}
