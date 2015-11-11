package junit.fwk;

public class ClipFunction {

	public static float master(float x) {
		return Math.max(Math.min(x, 1f), 0f);
	}
	public static float function(int gl_FragCoordy) {
		// Entre 20 et 160
		// Il faudrait 20 et 208 ==> +48
		// Du coup on a entre 9 et 180 avec 198 et 208
		return 208f - (Math.abs(gl_FragCoordy*2-240));
		//return Math.min(Math.max(1f - ((gl_FragCoordy - 120) / 10f), 0f), 0f);
		//return Math.max((float) (1 - (gl_FragCoordy)/10), 1f);
		//return Math.min(Math.max(1 - (gl_FragCoordy+1200)/10, 1), 0);
	}
	
	// max(min(8-abs(x*10-10),1),0)
	public static void main(String[] args) {
		/*
		f(-4) => -4
		f(127) => 1
		f = min(x, 1)
		f(-0,1)=0
	    f(1) = 1
	    f = max(x, 0)
		*/
		for (int i=0;i<240;i++) {
			System.out.println(i+" ==> "+function(i)+" == " + master(function(i)/8));
		}
	}
}
