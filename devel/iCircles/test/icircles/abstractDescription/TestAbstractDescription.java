package icircles.abstractDescription;

/*
 *
 * @author Aidan Delaney <aidan@phoric.eu>
 * Copyright (c) 2012
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of the iCircles Project.
 */

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Random;

import java.lang.reflect.Constructor;

import org.junit.*;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.runner.RunWith;

/**
 * Tests for {@link AbstractDescription}.
 */
public class TestAbstractDescription {

    @Test
    public void testADSystemTests()
    {

	String a  = new String("a");
	String a2 = new String("a");

	/*
	 * Debug.level = 2;
	 * System.out.println("contour labels equal? "+a.debug()+","+a2.debug());
	 * System.out.println("contour labels equal? "+(a==a2));
	 *
	 * replaced by
	 */
	assertEquals(a, a2);


	AbstractCurve ca1 = new AbstractCurve(a);
	AbstractCurve ca2 = new AbstractCurve(a);

	/*
	 * System.out.println("contours equal? "+a.debug()+","+a2.debug());
	 * System.out.println("contours equal? "+(a==a2));
	 *
	 * replaced by
	 */
	// should not be equal as their internal curve id differs
	assertThat(ca1, is(not(ca2)));

	TreeSet<AbstractCurve> ts = new TreeSet<AbstractCurve>();
	AbstractBasicRegion z0 = AbstractBasicRegion.get(ts);

	/*
	 * System.out.println("outside zone "+z0.debug());
	 *
	 * replaced by
	 */
	// break private scope of AbstractBasicRegion constructor for this test
	try {
	    Constructor<AbstractBasicRegion> constructor = AbstractBasicRegion.class.getDeclaredConstructor(new Class[0]);
	    constructor.setAccessible(true);
	    AbstractBasicRegion abc = constructor.newInstance(ts);
	    assertEquals(abc, z0);
	} catch (Exception e) {
	    // do nothing
	}

	ts.add(ca1);
	AbstractBasicRegion za = AbstractBasicRegion.get(ts);

	AbstractBasicRegion za2;
	{
	    TreeSet<AbstractCurve> ts2 = new TreeSet<AbstractCurve>();
	    ts2.add(ca2);
	    za2 = AbstractBasicRegion.get(ts2);
	    /*
	     * System.out.println("za==za2 ?" + (za == za2));
	     *
	     * replaced by
	     */
	    assertThat(za, is(not(za2)));
	}

	String b = new String("b");
	AbstractCurve cb = new AbstractCurve(b);
	ts.add(cb);
	AbstractBasicRegion zab = AbstractBasicRegion.get(ts);
	//System.out.println("zone in ab "+zab.debug());

	ts.remove(ca1);
	AbstractBasicRegion zb = AbstractBasicRegion.get(ts);
	//System.out.println("zone in b "+zb.debug());

	ts.add(ca1);
	AbstractBasicRegion zab2 = AbstractBasicRegion.get(ts);
	//System.out.println("zone2 in ab "+zab2.debug());

	/*
	 * System.out.println("zab==zab2 ?" + (zab == zab2));
	 *
	 * replaced by
	 */
	assertEquals(zab, zab2);
    }
    /*
    // The following pre-existing system tests have not been reformulated in
    // JUnit as I cannot see where any assertions are made.
    // TODO: reformulate these tests in JUnit

	ts.clear();
	TreeSet<AbstractBasicRegion> tsz = new TreeSet<AbstractBasicRegion>();

	debug_abstract_description(ts, tsz);

	ts.add(ca1);
	debug_abstract_description(ts, tsz);

	ts.add(ca1);
	debug_abstract_description(ts, tsz);

	ts.add(ca2);
	debug_abstract_description(ts, tsz);

	ts.add(cb);
	debug_abstract_description(ts, tsz);

	tsz.add(z0);
	debug_abstract_description(ts, tsz);

	tsz.add(za);
	debug_abstract_description(ts, tsz);

	tsz.add(zab);
	debug_abstract_description(ts, tsz);

	tsz.add(zb);
	debug_abstract_description(ts, tsz);

	//ContourLabel c = ContourLabel.get("c");
	//ContourLabel d = ContourLabel.get("d");
	//ContourLabel e = ContourLabel.get("e");

	System.out.println("\"\" is " + makeForTesting("").debug());
	System.out.println("\"a\" is " + makeForTesting("a").debug());
	System.out.println("\"a a\" is " + makeForTesting("a a").debug());
	System.out.println("\"a ab\" is " + makeForTesting("a ab").debug());

    }
    private static void debug_abstract_description(
						   TreeSet<AbstractCurve> ts, TreeSet<AbstractBasicRegion> tsz)
    {
	AbstractDescription ad = new AbstractDescription(ts, tsz);
	System.out.println("ad is " + ad.debug());
    }

    Set<AbstractBasicRegion> m_zones;
    Set<AbstractSpider> m_spiders;
    Set<AbstractBasicRegion> m_shaded_zones;


   public static AbstractDescription makeForTesting(String s, boolean random_shaded_zones) {
    	
    	ArrayList<String> descriptors = getDescriptors(s);
        String diagString = descriptors.get(0);
        String shadingString = descriptors.get(1);
        
        descriptors.remove(0);
        descriptors.remove(0);
        ArrayList<String> spiderStrings = descriptors;
    	
        TreeSet<AbstractBasicRegion> ad_zones = new TreeSet<AbstractBasicRegion>();
        AbstractBasicRegion outsideZone = AbstractBasicRegion.get(new TreeSet<AbstractCurve>());
        ad_zones.add(outsideZone);
        HashMap<CurveLabel, AbstractCurve> contours = new HashMap<CurveLabel, AbstractCurve>();
        if(diagString != null)
        {
	        StringTokenizer st = new StringTokenizer(diagString); // for spaces
	        while (st.hasMoreTokens()) {
	            String word = st.nextToken();
	            TreeSet<AbstractCurve> zoneContours = new TreeSet<AbstractCurve>();
	            for (int i = 0; i < word.length(); i++) {
	                String character = "" + word.charAt(i);
	                CurveLabel cl = CurveLabel.get(character);
	                if (!contours.containsKey(cl)) {
	                    contours.put(cl, new AbstractCurve(cl));
	                }
	                zoneContours.add(contours.get(cl));
	            }
	            AbstractBasicRegion thisZone = AbstractBasicRegion.get(zoneContours);
	            ad_zones.add(thisZone);
	        }
        }
        TreeSet<AbstractCurve> ad_contours = new TreeSet<AbstractCurve>(contours.values());
        
        // set some shaded zones
        TreeSet<AbstractBasicRegion> ad_shaded_zones = new TreeSet<AbstractBasicRegion>();
        if(random_shaded_zones)
        {
        	Random r = new Random();
	        for(AbstractBasicRegion abr: ad_zones)
	        {
	        	if(random_shaded_zones)
	        	{
	        		if(r.nextBoolean())
	        			ad_shaded_zones.add(abr);
	        	}
	        }
        }
        else if(shadingString != null)
        {
        	StringTokenizer st = new StringTokenizer(shadingString); // for spaces
            while (st.hasMoreTokens()) {
                String word = st.nextToken();
                AbstractBasicRegion thisZone = null;
                if(word.equals("."))
                {
                	// this means the outside zone
                	thisZone = outsideZone;
                }
                else
                {
	                TreeSet<AbstractCurve> zoneContours = new TreeSet<AbstractCurve>();
	                for (int i = 0; i < word.length(); i++) {
	                    String character = "" + word.charAt(i);
	                    CurveLabel cl = CurveLabel.get(character);
	                    AbstractCurve ac = contours.get(cl);
	                    if(ac == null)
	                    	throw new RuntimeException("malformed diagram spec : contour "+ac+"\n");
	                    zoneContours.add(ac);
	                }
	                thisZone = AbstractBasicRegion.get(zoneContours);
                }
                if(!ad_zones.contains(thisZone))
                {
                	throw new RuntimeException("malformed diagram spec : zone "+thisZone+"\n");
                }
                ad_shaded_zones.add(thisZone);
            }        	
        }
        AbstractDescription result = new AbstractDescription(ad_contours, ad_zones, ad_shaded_zones);
        
        // add some Spiders
        for(String spiderString: spiderStrings)
        {
        	StringTokenizer st = new StringTokenizer(spiderString); // for spaces
        	TreeSet<AbstractBasicRegion> habitat = new TreeSet<AbstractBasicRegion>();
        	String spiderLabel = null;
            while (st.hasMoreTokens()) {
                String word = st.nextToken();
                AbstractBasicRegion thisZone = null;
                if(word.charAt(0) == '\'')
                {
                	// this string represents the spider's label
                	String name = word.substring(1);
                	spiderLabel = name;
                	continue;
                }
                else if(word.equals("."))
                {
                	// this means the outside zone
                	thisZone = outsideZone;
                }
                else
                {
	                TreeSet<AbstractCurve> zoneContours = new TreeSet<AbstractCurve>();
	                for (int i = 0; i < word.length(); i++) {
	                    String character = "" + word.charAt(i);
	                    CurveLabel cl = CurveLabel.get(character);
	                    AbstractCurve ac = contours.get(cl);
	                    if(ac == null)
	                    	throw new RuntimeException("malformed diagram spec : contour "+ac+"\n");
	                    zoneContours.add(ac);
	                }
	                thisZone = AbstractBasicRegion.get(zoneContours);
                }
                if(!ad_zones.contains(thisZone))
                {
                	throw new RuntimeException("malformed diagram spec : zone "+thisZone+"\n");
                }
                habitat.add(thisZone);
            }        	
            AbstractSpider spider = new AbstractSpider(habitat, spiderLabel);
            result.addSpider(spider);        	
        }
                
        return result;
    }

    private static ArrayList<String> getDescriptors(String input_s)
    {
    	ArrayList<String> strings = new ArrayList<String>();
    	strings.add(""); // diagram zones
    	strings.add(""); // shaded zones
    	// any more are spider descriptions
    	
        StringTokenizer st = new StringTokenizer(input_s, ",", true); // split by commas, return commas as tokens
    	if(!st.hasMoreTokens())
    		return strings;
    	String s = st.nextToken();
    	if(!s.equals(","))
    	{
    		strings.set(0,s);
        	if(!st.hasMoreTokens())
        		return strings;
    		s = st.nextToken();
    	}
    	if(!st.hasMoreTokens())
    		return strings;
    	s = st.nextToken();
    	if(!s.equals(","))
    	{
    		strings.set(1,s);
        	if(!st.hasMoreTokens())
        		return strings;
    		s = st.nextToken();
    	}
    	while(true)
    	{
	    	if(!st.hasMoreTokens())
	    		return strings;
	    	s = st.nextToken();
	    	if(!s.equals(","))
	    	{
	    		strings.add(s);
	        	if(!st.hasMoreTokens())
	        		return strings;
	    		s = st.nextToken();
	    	}
    	}
    }

    public String makeForTesting(){
    	StringBuilder b = new StringBuilder();
    	for(AbstractBasicRegion zone : m_zones){
    		if(!zone.m_in_set.isEmpty()){ // don't journal out "." for empty zone - it's assumed
    		    b.append(zone.toString());
    		    b.append(" ");
    		}
    	}
    	b.append(", ");
    	for(AbstractBasicRegion zone : m_shaded_zones){
    		b.append(zone.toString());
    		b.append(" ");
    	}
    	for(AbstractSpider s : m_spiders){
    		b.append(", ");
    		b.append(s.toString());
    	}
    	return b.toString();
    }

    public static AbstractDescription makeForTesting(String s) {
    	return makeForTesting(s, false);
    }
    */
}
