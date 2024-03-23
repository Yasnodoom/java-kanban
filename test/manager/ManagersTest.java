package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {

    @Test
    public void managersReturnNotNullForGetDefault() {
        assertNotNull(Managers.getDefault());
    }

    @Test
    public void managersReturnNotNullForGetDefaultHistory() {
        assertNotNull(Managers.getDefaultHistory());
    }
}
