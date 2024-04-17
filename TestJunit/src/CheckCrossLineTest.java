import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import static org.junit.jupiter.api.Assertions.*;

public class CheckCrossLineTest {
    private CheckCrossLine checkCrossLine;

    @BeforeEach
    public void setUp() {
        Point l1 = new Point(0, 0);
        Point l2 = new Point(10, 10);
        checkCrossLine = new CheckCrossLine(l1, l2);
    }

    @Test
    public void shouldReturnTrueWhenRectContainsLine() {
        Rect rect = new Rect(0, 0, 15, 15);
        assertTrue(checkCrossLine.rectContainLine(rect));
    }

    @Test
    public void shouldReturnFalseWhenRectDoesNotContainLine() {
        Rect rect = new Rect(15, 15, 20, 20);
        assertFalse(checkCrossLine.rectContainLine(rect));
    }

    @Test
    public void shouldReturnFalseWhenLineIsVertical() {
        checkCrossLine = new CheckCrossLine(new Point(5, 0), new Point(5, 10));
        Rect rect = new Rect(0, 0, 10, 10);
        assertFalse(checkCrossLine.rectContainLine(rect));
    }

    @Test
    public void shouldReturnFalseWhenLineIsHorizontal() {
        checkCrossLine = new CheckCrossLine(new Point(0, 5), new Point(10, 5));
        Rect rect = new Rect(0, 0, 10, 10);
        assertFalse(checkCrossLine.rectContainLine(rect));
    }
}