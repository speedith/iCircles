package icircles.util;

import java.awt.Color;

public class Colors {
	
	private static Color[] COLORS_TINY = {
        new Color(0, 100, 0), // dark green
        Color.red,
        Color.blue,
        new Color(150, 50, 0),
        new Color(0, 50, 150),
        new Color(100, 0, 100)};
                      
    private static Color[] COLORS_MED = {
    	new Color(0xFF803E75), //Strong Purple
    	new Color(0xFFFF6800), //Vivid Orange
    	new Color(0xFFC10020), //Vivid Red
    	new Color(0xFF817066), //Medium Gray

    	new Color(0xFF007D34), //Vivid Green
    	new Color(0xFFF6768E), //Strong Purplish Pink
    	new Color(0xFF00538A), //Strong Blue
    	new Color(0xFFFF7A5C), //Strong Yellowish Pink
    	new Color(0xFF53377A), //Strong Violet
    	new Color(0xFFFF8E00), //Vivid Orange Yellow
    	new Color(0xFFB32851), //Strong Purplish Red
    	new Color(0xFFF4C800), //Vivid Greenish Yellow
    	new Color(0xFF7F180D), //Strong Reddish Brown
    	new Color(0xFF93AA00), //Vivid Yellowish Green
    	new Color(0xFF593315), //Deep Yellowish Brown
    	new Color(0xFFF13A13), //Vivid Reddish Orange
    	new Color(0xFF232C16), //Dark Olive Green
                      };
    
    private static Color[] COLORS_BASIC = {
    	new Color(0, 0, 255),      //Blue
    	new Color(255, 0, 0),      //Red
    	new Color(0, 255, 0),      //Green
    	new Color(255, 0, 255),    //Magenta
    	new Color(255, 128, 128),  //Pink
    	new Color(128, 128, 128),  //Gray
    	new Color(128, 0, 0),      //Brown
    	new Color(255, 128, 0),    //Orange
    };
    
    public static Color[] COLORS = COLORS_MED;

}
