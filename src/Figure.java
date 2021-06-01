import java.awt.*;
import java.util.Set;

public interface Figure {

    void draw(Graphics2D g);

    void updateWithoutYChange();

    void updateWithYChange();

    void move(Set<Block> map, Direction direction);

    void nextView(Set<Block> map);

    int getLowerY();

    void rollback();

    Set<Block> getForm();

    enum View {
        A,
        B,
        C,
        D
    }
}
