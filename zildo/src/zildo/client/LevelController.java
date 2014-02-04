/**
 * 
 */
package zildo.client;

/**
 * Controller for level in map
 * 
 * @author fcastagn
 * 
 */

public class LevelController {
	private boolean displaySubLevel = true;
	private int maxLevel = 0;
	private int currentLevel = 0;

	public LevelController() {
	}

	/**
	 * Display all sub level current included
	 */
	public void toggleDisplaySubLevel() {
		this.displaySubLevel = !this.displaySubLevel;
	}

	/**
	 * Display all sub level current included
	 * 
	 * @return displaySubLevel the display of sub level
	 */
	public boolean isDisplaySubLevel() {
		return displaySubLevel;
	}

	/**
	 * Upper level of the map
	 * 
	 * @return the maxLevel
	 */
	public int getMaxLevel() {
		return maxLevel;
	}

	/**
	 * Upper level of the map
	 * 
	 * @param maxLevel
	 *            the max level of the map
	 */
	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}

	/**
	 * Current level of Zeditor
	 * 
	 * @return the currentLevel
	 */
	public int getCurrentLevel() {
		return currentLevel;
	}

	/**
	 * Current level of Zeditor
	 * 
	 * @param currentLevel
	 *            the currentLevel to set
	 */
	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}

	public void reset() {
		currentLevel = maxLevel;
		displaySubLevel = true;
	}

}
