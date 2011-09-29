package zeditor.core.prefetch.complex;

public class CastleLow extends AbstractPatch12 {


    byte[] conv_value = // Renvoie le motif en fonction de la valeur en zone}
    { 42, 46, 44, 45, 39, 43, -1, 53, 37, -1, 41, 52, 38, 51, 50, 42 };

    byte[] value = // Valeurs en zone des chemins
	getReverseTab(conv_value, 37);
    //{ 8, 12, 4, 0, 10, 15, 5, 2, 3, 1, 0, 0, 0, 14, 13, 11, 7 };

    public CastleLow() {
	super(true);
    }

    @Override
    int toBinaryValue(int p_val) {
	// TODO Auto-generated method stub
	int a = p_val - 256 * 7 - 37;
	if (a < 0 || a >= value.length) {
	    return 0;
	}
	return value[a];
    }

    @Override
    int toGraphicalValue(int p_val) {
	// TODO Auto-generated method stub
	return conv_value[p_val] + 256 * 7;
    }

}
