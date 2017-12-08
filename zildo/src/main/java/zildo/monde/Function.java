package zildo.monde;

public interface Function {

	public float apply(float x);
	
	static class FunctionUtils {
		public static float average(Function f, int borne) {
			float sum = 0;
			for (float x=0;x<borne;x++) {
				sum += f.apply(x);
			}
			return sum / borne;
		}
		
		public static float max(Function f, int borne) {
			float max = 0;
			for (float x=0;x<borne;x++) {
				max = Math.max(f.apply(x), max);
			}
			return max;
		}
	}
}
