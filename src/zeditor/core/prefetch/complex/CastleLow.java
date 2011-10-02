package zeditor.core.prefetch.complex;

public class CastleLow extends AbstractPatch12 {

	byte[] conv_value = 
	{ 42, 46, 44, 45, 39, 43, -1, 53, 37, -1, 41, 52, 38, 51, 50, 42 };

	byte[] value =
	getReverseTab(conv_value, 37);

	public CastleLow() {
		super(true);
	}

	@Override
	int toBinaryValue(int p_val) {
		int a = p_val - 256 * 7 - 37;
		if (a < 0 || a >= value.length) {
			return 0;
		}
		return value[a];
	}

	@Override
	int toGraphicalValue(int p_val) {
		return conv_value[p_val] + 256 * 7;
	}

}
