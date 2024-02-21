package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {

    @Test
    void managersReturnNotNullForGetDefault() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    void managersReturnNotNullForGetDefaultHistory() {
        assertNotNull(Managers.getDefaultHistory());
    }
}
