/**
 * The Land of Alembrum
 * Copyright (C) 2006-2016 Evariste Boussaton
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

package zildo.monde.sprites.magic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zildo.monde.items.Item;
import zildo.monde.sprites.magic.Affection.AffectionKind;
import zildo.monde.sprites.persos.Perso;

/**
 * @author Tchegito
 *
 */
public class PersoAffections {
	private List<Affection> affections = new ArrayList<Affection>();
	
	private Perso perso;
	
	public PersoAffections(Perso p_perso) {
		perso = p_perso;
	}
	
	public void add(AffectionKind kind) {
		affections.add(new Affection(perso, kind));
	}
	
	public void render() {
		for (Iterator<Affection> it = affections.iterator(); it.hasNext();) {
			if (it.next().render()) {
				it.remove();
			}
		}
	}
	
	public void clear() {
		affections.clear();
	}
	
	/** Toggles an affection **/
	public void toggle(AffectionKind kind, Item item) {
		Affection aff = find(kind);
		if (aff != null) {
			affections.remove(aff);
		} else {
			affections.add(new Affection(perso, kind, item));
		}
	}
	
	private Affection find(AffectionKind kind) {
		for (Affection aff : affections) {
			if (aff.kind == kind) {
				return aff;
			}
		}
		return null;
	}
	
	public boolean isAffectedBy(AffectionKind kind) {
		return find(kind) != null;
	}
}
