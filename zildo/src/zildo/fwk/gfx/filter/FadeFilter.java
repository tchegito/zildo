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

package zildo.fwk.gfx.filter;

import zildo.client.ClientEngineZildo;
import zildo.fwk.gfx.GraphicStuff;
import zildo.monde.util.Vector3f;

public abstract class FadeFilter extends FadeScreenFilter {

	/**
	 * @param graphicStuff
	 */
	public FadeFilter(GraphicStuff graphicStuff) {
		super(graphicStuff);
	}

	protected boolean complete = true;
	
	@Override
	public void preFilter() {
		float factor = complete ? 255.0f : 768.0f;
		float coeff = 1.0f - (getFadeLevel() / factor);
		
		Vector3f v = new Vector3f(coeff, coeff, coeff);
		
		ClientEngineZildo.ortho.setAmbientColor(v);
	}
	
	@Override
	public boolean renderFilter() {
		return true;
	}

	@Override
	public void doOnActive(FilterEffect effect) {
		if (effect == FilterEffect.SEMIFADE) {
			complete = false;
		} else {
			complete = true;
		}
	}

}
