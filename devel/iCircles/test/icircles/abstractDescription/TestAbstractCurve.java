package icircles.abstractDescription;

import org.junit.*;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.jcheck.annotations.Configuration;
import org.jcheck.annotations.Generator;

import icircles.abstractDescription.*;

/**
 * Tests for @AbstractCurve
 *
 * 
 * @author Aidan Delaney <aidan@phoric.eu>
 */

@RunWith(org.jcheck.runners.JCheckRunner.class)
public class TestAbstractCurve {
    @Test
    @Configuration(tests=100)
    @Generator(klass=AbstractCurve.class, generator=CustomAbstractCurveGen.class)
    public void testCompareTo(AbstractCurve c1, AbstractCurve c2) {
    	if (0 != c1.getLabel().compareTo(c2.getLabel())) {
    		assertEquals(c1.getLabel().compareTo(c2.getLabel()), c1.compareTo(c2));
    	}
    	else {
    		// c1 is generated before c2 and the internal comparison is based
    		// on whether a static int is 
    		assertTrue(c1.compareTo(c2) < 0);
    		assertTrue(c2.compareTo(c1) > 0);
    	}
    }
}
