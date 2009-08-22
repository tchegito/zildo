package zildo.server;

import zildo.monde.persos.Perso;
import zildo.monde.persos.PersoZildo;

/**
 * @author tchegito
 */
public class MessageManagement {

    public void displayDeathMessage(PersoZildo p_zildo, Perso p_shooter) {
        if (p_shooter != null) {
            String shooterName = null;
            if (p_shooter.isZildo()) {
                shooterName = Server.getClientFromZildo((PersoZildo) p_shooter).playerName;
            } else {
                shooterName = "Neutral units";
            }
            String shootedName = Server.getClientFromZildo(p_zildo).playerName;
            EngineZildo.dialogManagement.writeConsole(shooterName + " pawned " + shootedName + " !");
        }
    }
}