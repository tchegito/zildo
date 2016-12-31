package zildo.monde.quest.actions;

import zildo.monde.sprites.elements.Element;
import zildo.monde.sprites.elements.ElementGoodies;
import zildo.server.state.ClientState;

public class TakingItemAction extends ScriptAction {

	ElementGoodies goodie;
	
	public TakingItemAction(String p_label, Element p_goodie) {
		super(p_label);
		if (p_goodie.isGoodies()) {
			goodie = (ElementGoodies) p_goodie;
			goodie.setDelegateTaken(true);
		}
	}

	@Override
	public void launchAction(ClientState p_clientState) {
		super.launchAction(p_clientState);
		if (goodie != null) {
			goodie.setDelegateTaken(false);
			goodie.beingTaken();
		}
	}
}
