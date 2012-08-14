/**
 * Legend of Zildo
 * Copyright (C) 2006-2012 Evariste Boussaton
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

import zildo.fwk.gfx.filter.BilinearFilter;
import zildo.fwk.gfx.filter.FadeScreenFilter;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.gfx.filter.ScreenFilter;
import zildo.resource.Constantes;

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
		//System.out.println(displayActive());
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
				if (fadeLevel >= 0) {
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
		// Disable all filters
		restoreFilters();	
		// Even bilinear
		active(BilinearFilter.class, false, null);
		for (FilterEffect effect : p_effects) {
			for (Class<? extends ScreenFilter> clazz : effect.getFilterClass()) {
				active(clazz, true, effect);
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
		active(BilinearFilter.class, false, null);
		for (FilterEffect effect : p_effects) {
			for (Class<? extends ScreenFilter> clazz : effect.getFilterClass()) {
				active(clazz, true, effect);
			}
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// fadeEnd
	///////////////////////////////////////////////////////////////////////////////////////
	// Fade is over, so put back in default position
	///////////////////////////////////////////////////////////////////////////////////////
	public void fadeEnd() {
		restoreFilters();
		asked_FadeOut=false;
		asked_FadeIn=false;
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
		if (asked_FadeIn && fadeLevel < 0) {
			return true;
		} else if (asked_FadeOut && fadeLevel == 255) {
			return true;
		}
		return false;
	}

	public boolean isFading() {
		return fadeStarted;
	}
	
	public int getFadeLevel() {
		return fadeLevel;
	}
	
	/**
	 * Active/Inactive all filters from given class.
	 * If 'clazz' is null, so all filters will be targeted.
	 * @param clazz
	 * @param activ
	 * @param effect kind of filter
	 */
	public void active(Class<? extends ScreenFilter> clazz, boolean activ, FilterEffect effect) {
		for (ScreenFilter filter : filters) {
			if (clazz == null || clazz.isAssignableFrom(filter.getClass())) {
				filter.setActive(activ, effect);
			}
		}
	}
	
	public String displayActive() {
		StringBuilder sb = new StringBuilder();
		for (ScreenFilter filter : filters) {
			if (filter.isActive()) {
				sb.append(filter.getClass().getSimpleName()).append(",");
			}
		}
		return sb.toString();
	}
	/**
	 * Restore default filters.
	 */
	private void restoreFilters() {
		for (ScreenFilter filter : filters) {
			if (FadeScreenFilter.class.isAssignableFrom(filter.getClass())) {
				active(filter.getClass(), false, null);
			}
		}
		active(BilinearFilter.class, true, null);
	}
	
	public void cleanUp() {
		for (ScreenFilter filter : filters) {
			filter.cleanUp();
		}
	}
}
