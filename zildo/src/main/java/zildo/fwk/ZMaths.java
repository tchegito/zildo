package zildo.fwk;

public class ZMaths {

    static byte[] buffer = new byte[10];
    
    /** Returns an array containing decomposition of POSITIVE value in base 10. **/
    public static byte[] decomposeBase10(int value) {
    	int n = value;
    	int numDecimal = 0;
    	while ( n > 0 || numDecimal == 0) {
    		buffer[ numDecimal++ ] = (byte) (n % 10);
    		n = n/10;
    	}
    	byte[] result = new byte[numDecimal];
    	for (int i=0;i<numDecimal;i++) {
    		result[i] = buffer[numDecimal - i - 1];
    	}
    	return result;
    }
    
    public static float decimal(float f) {
    	return f - (float) Math.floor(f);
    }
}
