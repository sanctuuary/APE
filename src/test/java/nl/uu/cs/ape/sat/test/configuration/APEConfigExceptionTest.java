package nl.uu.cs.ape.sat.test.configuration;

import nl.uu.cs.ape.sat.configuration.APEConfigException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;


/**
 * Tests whether exceptions contain the info for the user.
 */
class APEConfigExceptionTest {

    @Test
    public void testException() {

        String message = "some_info";

        APEConfigException e = new APEConfigException(message);

        assertEquals(message, e.getMessage());
        assertEquals(String.format("%s: %s", e.getClass().getName(), message), e.toString());
        assertThrows(APEConfigException.class, () -> {
            throw e;
        });
    }

    @Test
    public void testInvalidValue() {

        String message = "some_info";
        String tag = "some_tag";
        Integer value = 99;

        APEConfigException invalidValueException = APEConfigException.invalidValue(tag, value, message);

        assertTrue(invalidValueException.getMessage().contains(tag));
        assertTrue(invalidValueException.getMessage().contains(value.toString()));
        assertTrue(invalidValueException.getMessage().contains(message));
        assertThrows(APEConfigException.class, () -> {
            throw invalidValueException;
        });
    }

    @Test
    public void testMissingTag() {

        String tag = "some_tag";

        APEConfigException missingTagException = APEConfigException.missingTag(tag);

        assertTrue(missingTagException.getMessage().contains(tag));
        assertThrows(APEConfigException.class, () -> {
            throw missingTagException;
        });
    }
}
