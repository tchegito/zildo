package testable;
import zildo.fwk.collection.IdGenerator;


public class TestableIdGenerator extends IdGenerator {

	public TestableIdGenerator(int maxId) {
		super(maxId);
	}

	public boolean[] getBuffer() {
		return buffer;
	}
	
	public int getAvailable() {
		int size = 0;
		for (int i=0;i<buffer.length;i++) {
			if (!buffer[i]) {
				size++;
			}
		}
		return size;
	}
}
