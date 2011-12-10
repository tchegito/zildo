/**
 * Legend of Zildo
 * Copyright (C) 2006-2011 Evariste Boussaton
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

package zildo.monde.dialog;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import zildo.resource.Constantes;

public class MapDialog {

	private int n_topics;

	private List<String> dialogs = new ArrayList<String>();
	private Map<String, Behavior> behaviors;
	private DialogTopic[] topics = new DialogTopic[Constantes.MAX_TOPICS];

	public MapDialog() {
		n_topics = 0;
		behaviors = new LinkedHashMap<String, Behavior>(); // LinkedHashMap to
															// keep order
	}

	/**
	 * Useful for test cases.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MapDialog)) {
			return false;
		}
		MapDialog other = (MapDialog) o;
		if (dialogs.size() != other.dialogs.size()) {
			return false;
		}
		if (behaviors.size() != other.behaviors.size()) {
			return false;
		}
		if (n_topics != other.n_topics) {
			return false;
		}
		return true;
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// addSentence
	// /////////////////////////////////////////////////////////////////////////////////////
	// Add the given sentence to sentence buffer.
	// /////////////////////////////////////////////////////////////////////////////////////
	public void addSentence(String sentence) {
		dialogs.add(sentence);
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// addBehavior
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN:character's name and behavior (array of 9 integers)
	// /////////////////////////////////////////////////////////////////////////////////////
	public void addBehavior(String nomPerso, short[] replique) {
		Behavior b = new Behavior(nomPerso);
		for (int i = 0; i < replique.length; i++) {
			b.replique[i] = replique[i];
		}
		behaviors.put(nomPerso, b);
	}

	public void addBehavior(Behavior p_behav) {
		behaviors.put(p_behav.persoName, p_behav);
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// addBehavior
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN:character's name and behavior (array of 9 integers)
	// /////////////////////////////////////////////////////////////////////////////////////
	void addTopic(String topicName) {
		// Initialize first one with ID=1;
		n_topics++;
		topics[n_topics] = new DialogTopic(n_topics, topicName);
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// getSentence
	// /////////////////////////////////////////////////////////////////////////////////////
	// IN:character's name
	// /////////////////////////////////////////////////////////////////////////////////////
	public String getSentence(Behavior behav, int numComportement) {
		String reponse = "pas trouve !";
		if (behav != null) {
			int numSentence = behav.replique[numComportement];
			if (numSentence > 0) {
				reponse = dialogs.get(numSentence - 1);
			}
			// char a='#';
			// int posDiese=reponse.indexOf(a);
			// if (posDiese != -1) {
		}
		return reponse;
	}

	public void setSentence(Behavior behav, int numComportement, String sentence) {
		if (behav != null) {
			int numSentence = behav.replique[numComportement];
			if (numSentence == 0) {
				behav.replique[numComportement] = dialogs.size();
				dialogs.add(sentence);
			} else {
				dialogs.set(numSentence - 1, sentence);
			}
		}
	}

	public int getN_phrases() {
		return dialogs.size();
	}

	public List<String> getDialogs() {
		return dialogs;
	}

	public Map<String, Behavior> getBehaviors() {
		return behaviors;
	}

	public void removePersoDialog(String p_name) {
		Behavior b = behaviors.get(p_name);
		if (b != null) {
			behaviors.remove(p_name);
			for (int i = 0; i < 10; i++) {
				int nSentence = b.replique[i];
				if (nSentence > 0) {
					dialogs.remove(nSentence - 1);
					// Update all others referring to sentence after (way too
					// dirty !)
					for (Behavior behav : behaviors.values()) {
						for (int j = 0; j < 10; j++) {
							if (behav.replique[j] > nSentence) {
								behav.replique[j]--;
							}
						}
					}
				}
			}
		}
	}
}
