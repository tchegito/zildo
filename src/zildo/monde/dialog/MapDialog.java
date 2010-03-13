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

package zildo.monde.dialog;

import java.util.LinkedHashMap;
import java.util.Map;

import zildo.prefs.Constantes;

public class MapDialog {

	private int n_phrases;
	private int n_comportements;
	private int n_topics;

	private String dialogs[]=new String[Constantes.MAX_DIALOG];
	private Map<String, Behavior> behaviors;
	private DialogTopic[] topics=new DialogTopic[Constantes.MAX_TOPICS];

	public MapDialog() {
		n_phrases=0;
		n_comportements=0;
		n_topics=0;
		behaviors=new LinkedHashMap<String, Behavior>();	// LinkedHashMap to keep order
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// addSentence
	///////////////////////////////////////////////////////////////////////////////////////
	// Add the given sentence to sentence buffer.
	///////////////////////////////////////////////////////////////////////////////////////
	public void addSentence(String sentence) {
		dialogs[n_phrases]=sentence;
		n_phrases++;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// addBehavior
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:character's name and behavior (array of 9 integers)
	///////////////////////////////////////////////////////////////////////////////////////
	public void addBehavior(String nomPerso, short[] replique) {
		Behavior b=new Behavior();
		b.persoName=nomPerso;
		for (int i=0;i<replique.length;i++) {
			b.replique[i]=replique[i];
		}
		behaviors.put(nomPerso, b);
		n_comportements++;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// addBehavior
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:character's name and behavior (array of 9 integers)
	///////////////////////////////////////////////////////////////////////////////////////
	void addTopic(String topicName) {
		// Initialize first one with ID=1;
		n_topics++;
		topics[n_topics]=new DialogTopic(n_topics,topicName);
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// getSentence
	///////////////////////////////////////////////////////////////////////////////////////
	// IN:character's name
	///////////////////////////////////////////////////////////////////////////////////////
	String getSentence(Behavior behav, int numComportement) {
		String reponse="pas trouve !";
		if (behav!=null) {
			int numSentence=behav.replique[numComportement];
			reponse=dialogs[numSentence - 1];
			//char a='#';
			//int posDiese=reponse.indexOf(a);
			//if (posDiese != -1) {
		}
		return reponse;
	}
	
	public int getN_phrases() {
		return n_phrases;
	}
	
	public String[] getDialogs() {
		return dialogs;
	}
	
	public Map<String, Behavior> getBehaviors() {
		return behaviors;
	}
}
