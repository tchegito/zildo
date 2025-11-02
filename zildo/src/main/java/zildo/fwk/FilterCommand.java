/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
 * 
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

import zildo.Zildo;
import zildo.fwk.gfx.filter.BilinearFilter;
import zildo.fwk.gfx.filter.BlackBlurFilter;
import zildo.fwk.gfx.filter.BlendFilter;
import zildo.fwk.gfx.filter.BlurFilter;
import zildo.fwk.gfx.filter.CircleFilter;
import zildo.fwk.gfx.filter.CloudFilter;
import zildo.fwk.gfx.filter.EarthQuakeFilter;
import zildo.fwk.gfx.filter.FadeFilter;
import zildo.fwk.gfx.filter.FadeScreenFilter;
import zildo.fwk.gfx.filter.FilterEffect;
import zildo.fwk.gfx.filter.FitToScreenFilter;
import zildo.fwk.gfx.filter.LightningFilter;
import zildo.fwk.gfx.filter.RedFilter;
import zildo.fwk.gfx.filter.RotativeFilter;
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
	
	FilterEffect activeFade;

	FilterEffect scheduled;
	boolean wayScheduled;	// TRUE=in / FALSE=out
	
	public FilterCommand() {
		filters=new ArrayList<ScreenFilter>();

		fadeLevel=0;
		asked_FadeIn = false;
		asked_FadeOut =false;
		activeFade = null;
	}
	
    private void addFilter(ScreenFilter filter) {
    	filters.add(filter);
    }
	
    public void removeAll() {
    	filters.clear();
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

	String previous = "";
	
	/**
	 * Calculate fade level, and render active filters.
	 */
	public void doFilter() {
		// Is a fade scheduled ?
		if (scheduled != null) {
			if (wayScheduled && fadeLevel >= 255) {
				fadeLevel = 255;
				fadeIn( scheduled);
			} else if (!wayScheduled && fadeLevel <= 0) {
				fadeLevel = 0;
				fadeEnd();
				fadeOut(scheduled);
			}
		}
		
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
					activeFade = null;
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
	public void fadeIn(FilterEffect p_effect) {
		fade(true, p_effect);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// fadeOut
	///////////////////////////////////////////////////////////////////////////////////////
	// When lights goes out
	///////////////////////////////////////////////////////////////////////////////////////
	public void fadeOut(FilterEffect p_effect) {
		fade(false, p_effect);
	}
	
	private void fade(boolean way, FilterEffect p_effect) {
		if (p_effect.canBeForced()) {
			fadeEnd();
			// Allow a fade in to be launched, even if fade out hasn't been done
			if (!way) {
				fadeLevel = 0;
			} else {
				fadeLevel = 255;
			}
		}
		if (p_effect == activeFade) {	// Same fade ?
			asked_FadeIn = false;
			asked_FadeOut = false;
			activeFade = null;
		}
		if (activeFade != null) {
			scheduled = p_effect;
			wayScheduled = way;
		} else {
			scheduled = null;
			asked_FadeIn  = way;
			asked_FadeOut = !way;
			fadeStarted = true;
			active(BilinearFilter.class, false, null);
			for (Class<? extends ScreenFilter> clazz : p_effect.getFilterClass()) {
				active(clazz, true, p_effect);
			}
			activeFade = p_effect;
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
	// Returns TRUE wether fade operation is over
	///////////////////////////////////////////////////////////////////////////////////////
	public boolean isFadeOver() {
		if (asked_FadeIn) {
			return fadeLevel < 0;
		} else if (asked_FadeOut) {
			return fadeLevel == 255;
		}
		if (!fadeStarted) {
			return false;
		}
		return true;
	}

	public boolean isFading() {
		return fadeStarted;
	}
	
	public int getFadeLevel() {
		return fadeLevel;
	}
	
	public FilterEffect getActiveFade() {
		return activeFade;
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
			if (clazz == null || clazz.isAssignableFrom(filter.getClass()) && filter.isActive() != activ) {
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
	 * NOTE: we don't touch RedFilter here, because it's special.
	 */
	public void restoreFilters() {
		for (ScreenFilter filter : filters) {
			if (FadeScreenFilter.class.isAssignableFrom(filter.getClass())) {
				active(filter.getClass(), false, null);
			}
		}
		active(BilinearFilter.class, true, null);
		activeFade = null;
	}
	
	public void cleanUp() {
		for (ScreenFilter filter : filters) {
			filter.cleanUp();
		}
	}
	
	public void recreateContext() {
		// Read all filters state
		List<Class<? extends ScreenFilter>> actives = new ArrayList<Class<? extends ScreenFilter>>();
		for (ScreenFilter f : filters) {
			if (f.isActive()) {
				actives.add(f.getClass());
			}
		}
		Zildo.pdPlugin.initFilters();
		addDefaultFilters();
		// Restore all filters state
		for (Class<? extends ScreenFilter> f : actives) {
			active(f, true, null);
		}
	}
	
	public void addDefaultFilters() {
		removeAll();
		// Order is very important : as BilinearFilter should be rendered after all others
		addFilter(Zildo.pdPlugin.getFilter(FitToScreenFilter.class));
		addFilter(Zildo.pdPlugin.getFilter(CloudFilter.class));
		addFilter(Zildo.pdPlugin.getFilter(BlurFilter.class));
		addFilter(Zildo.pdPlugin.getFilter(BlendFilter.class));
		addFilter(Zildo.pdPlugin.getFilter(FadeFilter.class));
		addFilter(Zildo.pdPlugin.getFilter(CircleFilter.class));
		addFilter(new RedFilter(Zildo.pdPlugin.gfxStuff));
		addFilter(new LightningFilter(Zildo.pdPlugin.gfxStuff));
		addFilter(new EarthQuakeFilter(Zildo.pdPlugin.gfxStuff));
		addFilter(Zildo.pdPlugin.getFilter(BlackBlurFilter.class));
		addFilter(Zildo.pdPlugin.getFilter(RotativeFilter.class));
		addFilter(Zildo.pdPlugin.getFilter(BilinearFilter.class));
		active(BilinearFilter.class, true, null);		
	}	
}
