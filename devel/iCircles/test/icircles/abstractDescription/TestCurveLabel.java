package icircles.abstractDescription;

import org.junit.*;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.jcheck.*;
import org.jcheck.annotations.Configuration;

import icircles.abstractDescription.CurveLabel;
/** 
 * Tests for @CurveLabel
 *
 * As @CurveLabel is a simple wrapper around @String, these tests
 * are simplified.  @CurveLabel also stores a static @TreeSet of all
 * labels.  We simply ensure that the get(String s) method which 
 * provides access to the underlying @Set implements the required
 * contract.
 *
 * @author Aidan Delaney <aidan@phoric.eu>
 */

@RunWith(org.jcheck.runners.JCheckRunner.class)
public class TestCurveLabel {
    @Test
    @Configuration(tests=100)
    public void testGetReturnsSameObject(String s1) {
	CurveLabel c1 = CurveLabel.get (s1);
	CurveLabel c2 = CurveLabel.get (s1);

	// Check reference equality
	assertTrue(c1 == c2);
    }

    @Test
    @Configuration(tests=100)
    public void testChecksum (String s1, String s2) {
	CurveLabel c1 = CurveLabel.get(s1);
	CurveLabel c2 = CurveLabel.get(s2);

	assertTrue(c1.checksum() != c2.checksum());
    }
}
