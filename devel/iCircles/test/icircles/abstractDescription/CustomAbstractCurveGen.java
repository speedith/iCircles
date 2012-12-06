package icircles.abstractDescription;

import java.util.Random;

import org.jcheck.*;
import org.jcheck.generator.*;
import org.jcheck.generator.primitive.*;

import icircles.abstractDescription.*;

/**
 * A JCheck @Generator for @AbstractCurve
 *
 * @author Aidan Delaney <aidan@phoric.eu>
 */

public class CustomAbstractCurveGen implements Gen<AbstractCurve> {
    public AbstractCurve arbitrary(Random random, long size)
    {
	StringGen  sg = new StringGen();
	return (new AbstractCurve(sg.arbitrary(random, size)));
    }
}
