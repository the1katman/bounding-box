import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BoundingBoxAppTests {

    @Test
    public void test() {
        final int a = 1;
        final int b = 2;
        final int result = a + b;
        assertThat(result)
                .as("The result of a + b is unexpected.")
                .isEqualTo(3);
    }

}
