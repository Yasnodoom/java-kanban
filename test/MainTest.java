import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class MainTest {

    @Test
    public void testTest() {
        ArrayList<Integer> history = new ArrayList<>(2);
        history.add(0, 1);
        history.add(0, 2);
        history.add(0, 3);
        history.add(0, 3);
        System.out.println(history);


    }


}
