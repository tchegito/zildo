package zildo.monde.dialog;

public class Behavior {

    public String persoName;	// max length=8
    public int[] replique=new int[10];
    
    public Behavior() {
    	for (int i=0;i<replique.length;i++) {
    		replique[i]=0;
    	}
    }
}
