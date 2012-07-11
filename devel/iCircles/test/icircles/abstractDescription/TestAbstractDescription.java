package icircles.abstractDescription;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Random;

import org.junit.*;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.jcheck.*;
import org.jcheck.annotations.Configuration;
import org.jcheck.annotations.Generator;

import org.junit.runner.RunWith;

@RunWith(org.jcheck.runners.JCheckRunner.class)
public class TestAbstractDescription {

	
    Set<AbstractBasicRegion> m_zones;
    Set<AbstractSpider> m_spiders;
    Set<AbstractBasicRegion> m_shaded_zones;
/*    
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
    		    b.append(zone.journalString());
    		    b.append(" ");
    		}
    	}
    	b.append(", ");
    	for(AbstractBasicRegion zone : m_shaded_zones){
    		b.append(zone.journalString());
    		b.append(" ");
    	}
    	for(AbstractSpider s : m_spiders){
    		b.append(", ");
    		b.append(s.journalString());
    	}
    	return b.toString();
    }

    public static AbstractDescription makeForTesting(String s) {
    	return makeForTesting(s, false);
    }
    */
}
