package zildo.fwk.script.command;

import zildo.SinglePlayer;
import zildo.client.ClientEngineZildo;
import zildo.client.ClientEvent;
import zildo.client.ClientEventNature;
import zildo.fwk.filter.FilterEffect;
import zildo.fwk.script.xml.ActionElement;
import zildo.monde.items.ItemKind;
import zildo.monde.map.Angle;
import zildo.monde.map.Point;
import zildo.monde.quest.actions.ScriptAction;
import zildo.monde.sprites.desc.PersoDescription;
import zildo.monde.sprites.persos.Perso;
import zildo.monde.sprites.persos.PersoZildo;
import zildo.monde.sprites.utils.MouvementPerso;
import zildo.server.EngineZildo;

/**
 * Class splitted from ScriptExecutor, in order to clarify things.
 * <p/>
 * This class has just to render one action.
 * @author tchegito
 */

public class ActionExecutor {

    ScriptExecutor scriptExec;
    int count;
    
    public ActionExecutor(ScriptExecutor p_scriptExec) {
        scriptExec = p_scriptExec;
    }

    /**
     * @param p_action
     * @return boolean
     */
    public boolean render(ActionElement p_action) {
        boolean achieved = false;
        if (p_action.waiting) {
            waitForEndAction(p_action);
            achieved = p_action.done;
        } else {
            Perso perso = EngineZildo.persoManagement.getNamedPerso(p_action.who);
            if (perso != null) {
                scriptExec.involved.add(perso); // Note that this perso is concerned
            }
            Point location = p_action.location;
            String text = p_action.text;
            switch (p_action.kind) {
                case pos:
                    if (perso != null) {
                        perso.x = location.x;
                        perso.y = location.y;
                    } else if ("camera".equals(p_action.what)) {
                        ClientEngineZildo.mapDisplay.setCamera(location);
                        ClientEngineZildo.mapDisplay.setFocusedEntity(null);
                    }
                    achieved = true;
                    break;
                case moveTo:
                    if (perso != null) {
                        if (perso.getTarget() != null) { // Perso has already a target
                            return false;
                        } else {
                            perso.setGhost(true);
                            perso.setTarget(location);
                            perso.setForward(p_action.backward);
                            perso.setSpeed(p_action.speed);
                            perso.setOpen(p_action.open);
                        }
                    } else if ("camera".equals(p_action.what)) {
                        ClientEngineZildo.mapDisplay.setTargetCamera(location);
                    }
                    break;
                case speak:
                    EngineZildo.dialogManagement.launchDialog(SinglePlayer.getClientState(), null, new ScriptAction(text));
                    scriptExec.userEndedAction = false;
                    break;
                case script:
                    perso.setQuel_deplacement(MouvementPerso.fromInt(p_action.val));
                    achieved = true;
                    break;
                case angle:
                	if (perso.getTarget() != null) {
                		return false;
                	}
                    perso.setAngle(Angle.fromInt(p_action.val));
                    achieved = true;
                    break;
                case wait:
                	count=p_action.val;
                	break;
                case fadeIn:
                	EngineZildo.askEvent(new ClientEvent(ClientEventNature.FADE_IN, FilterEffect.fromInt(p_action.val)));
                	break;
                case fadeOut:
                	EngineZildo.askEvent(new ClientEvent(ClientEventNature.FADE_OUT, FilterEffect.fromInt(p_action.val)));
                	break;
                case map:	// Change current map
        			EngineZildo.mapManagement.charge_map(p_action.text);
        			ClientEngineZildo.mapDisplay.setCurrentMap(EngineZildo.mapManagement.getCurrentMap());
                	achieved=true;
                	break;
                case focus:	// Camera focus on given character
                    ClientEngineZildo.mapDisplay.setFocusedEntity(perso);
                    achieved = true;
                    break;
                case spawn:	// Spawn a new character
                    PersoDescription desc = PersoDescription.fromString(p_action.text);
                    String name = p_action.who != null ? p_action.who : p_action.what;
                    Perso newOne = EngineZildo.persoManagement.createPerso(desc, location.x, location.y, 0, name, p_action.val);
                   	newOne.setSpeed(p_action.speed);
                   	newOne.setEffect(p_action.fx);
                   	newOne.initPersoFX();
                    EngineZildo.spriteManagement.spawnPerso(newOne);
                    achieved = true;
                    break;
                case take:	// Zildo takes an item
                	PersoZildo zildo=EngineZildo.persoManagement.getZildo();
                	zildo.pickItem(ItemKind.fromString(text));
                	achieved=true;
                	break;
                case mapReplace:
                	EngineZildo.scriptManagement.addReplacedMapName(p_action.what, text);
                	achieved = true;
                	break;
                case exec:
                	//new ScriptExecutor();
                	// Warning : with this, we totally replace the current script with the new one.
                	// So we can't sequence scripts in an action tag.
                	EngineZildo.scriptManagement.execute(text);
                	break;
            }

            p_action.done = achieved;
            p_action.waiting = !achieved;
        }
        return achieved;
    }

    /**
     * An action has started. Here we're waiting for it to finish.
     * @param p_action
     */
    private void waitForEndAction(ActionElement p_action) {
        String who = p_action.who;
        Perso perso = EngineZildo.persoManagement.getNamedPerso(who);
        boolean achieved = false;
        switch (p_action.kind) {
            case moveTo:
            	if (perso != null) {
	                achieved=perso.hasReachedTarget();
            	} else if ("camera".equals(p_action.what)) {
            		achieved=ClientEngineZildo.mapDisplay.getTargetCamera() == null;
            	}
                break;
            case speak:
                achieved = scriptExec.userEndedAction;
                break;
            case wait:
            	achieved = (count-- == 0);
            	break;
            case fadeIn:
            case fadeOut:
           		achieved=ClientEngineZildo.guiDisplay.isFadeOver();
            	break;
            case exec:
            	achieved=true;
            	break;
        }
        p_action.waiting = !achieved;
        p_action.done = achieved;
    }
}
