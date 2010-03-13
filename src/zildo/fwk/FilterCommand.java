/**
 * Legend of Zildo
 * Copyright (C) 2006-2010 Evariste Boussaton
 * Based on original Zelda : link to the past (C) Nintendo 1992
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zildo.fwk;

import java.util.ArrayList;
import java.util.List;

import zildo.fwk.filter.BilinearFilter;
import zildo.fwk.filter.FilterEffect;
import zildo.fwk.filter.ScreenFilter;
import zildo.prefs.Constantes;

/**
 * Command class for screen filters:
 * -can combine several filters
 * -can active/disactive filters (all, or by class specifically)
 * 
 * -manage fade level (go from 0 to 255, and from 255 to 0)
 * 
 * @author Tchegito
 *
 */
public class FilterCommand {

	
	private List<ScreenFilter> filters;
	protected int fadeLevel;		// Integer between 0 and 255
	protected boolean asked_FadeIn;
	protected boolean asked_FadeOut;
	private boolean fadeStarted;
	
	public FilterCommand() {
		filters=new ArrayList<ScreenFilter>();

		fadeLevel=0;
		asked_FadeIn = false;
		asked_FadeOut =false;
	}
	
    public void addFilter(ScreenFilter filter) {
    	filters.add(filter);
    }
	
	public void doPreFilter() {
		for (ScreenFilter filter : filters) {
			if (filter.isActive()) {
				filter.preFilter();
			}
		}
	}

	public void doPostFilter() {
		for (ScreenFilter filter : filters) {
			if (filter.isActive()) {
				filter.postFilter();
			}
		}
	}

	/**
	 * Calculate fade level, and render active filters.
	 */
	public void doFilter() {
		boolean progressFadeLevel=true;
		for (ScreenFilter filter : filters) {
			if (filter.isActive()) {
				progressFadeLevel&=filter.renderFilter();
			}
		}

		if (progressFadeLevel) {
			// Evaluate fade level
			if (asked_FadeOut) {
				if (fadeLevel < 255) {
					fadeLevel+=Constantes.FADE_SPEED;
				} else {
					fadeLevel=255;
					fadeStarted=false;
				}
			}
			if (asked_FadeIn) {
				if (fadeLevel > 0) {
					fadeLevel-=Constantes.FADE_SPEED;
				} else {
					fadeLevel=0;
					fadeStarted=false;
					fadeEnd();
				}
			}
		}

	}

	
	///////////////////////////////////////////////////////////////////////////////////////
	// fadeIn
	///////////////////////////////////////////////////////////////////////////////////////
	// When lights comes back
	///////////////////////////////////////////////////////////////////////////////////////
	public void fadeIn(FilterEffect... p_effects)
	{
		asked_FadeIn  = true;
		asked_FadeOut = false;
		fadeStarted = true;
		active(null, false);
		for (FilterEffect effect : p_effects) {
			for (Class<? extends ScreenFilter> clazz : effect.getFilterClass()) {
				active(clazz, true);
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// fadeOut
	///////////////////////////////////////////////////////////////////////////////////////
	// When lights goes out
	///////////////////////////////////////////////////////////////////////////////////////
	public void fadeOut(FilterEffect... p_effects)
	{
		asked_FadeIn  = false;
		asked_FadeOut = true;
		fadeStarted = true;
		active(null, false);
		for (FilterEffect effect : p_effects) {
			for (Class<? extends ScreenFilter> clazz : effect.getFilterClass()) {
				active(clazz, true);
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// fadeEnd
	///////////////////////////////////////////////////////////////////////////////////////
	// Fade is over, so put back in default position
	///////////////////////////////////////////////////////////////////////////////////////
	public void fadeEnd() {
		active(null, false);
		active(BilinearFilter.class, true);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// isFadeOver
	///////////////////////////////////////////////////////////////////////////////////////
	// Returns TRUE wether fade operation is over, and set the 'ask' boolean to FALSE when
	// it's done.
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean isFadeOver() {
		if (!fadeStarted) {
			return false;
		}
		if (asked_FadeIn && fadeLevel == 0) {
			return true;
		} else if (asked_FadeOut && fadeLevel == 255) {
			return true;
		}
		return false;
	}

	public int getFadeLevel() {
		return fadeLevel;
	}
	
	/**
	 * Active/Inactive all filters from given class.
	 * If 'clazz' is null, so all filters will be targeted.
	 * @param clazz
	 * @param activ
	 */
	public void active(Class<? extends ScreenFilter> clazz, boolean activ) {
		for (ScreenFilter filter : filters) {
			if (clazz == null || filter.getClass().equals(clazz)) {
				filter.setActive(activ);
			}
		}
	}
	
	public void cleanUp() {
		for (ScreenFilter filter : filters) {
			filter.cleanUp();
		}
	}
}
