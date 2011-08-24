package zildo.client;

public class ForeBackController {

	// ZEditor specific
	boolean displayBackground = true;
	boolean displayForeground = true;

	public void setDisplaySpecific(boolean p_back, boolean p_fore) {
		displayBackground = p_back;
		displayForeground = p_fore;
	}

	/**
	 * Invert the specific display state.
	 * 
	 * @param p_foreOrBack
	 *            TRUE=fore / FALSE=back
	 */
	public void toggleDisplaySpecific(boolean p_foreOrBack) {
		if (p_foreOrBack) {
			displayForeground = !displayForeground;
		} else {
			displayBackground = !displayBackground;
		}
	}

	/**
	 * @return the displayBackground
	 */
	public boolean isDisplayBackground() {
		return displayBackground;
	}

	/**
	 * @return the displayForeground
	 */
	public boolean isDisplayForeground() {
		return displayForeground;
	}
}
