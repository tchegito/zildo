package zildo.fwk.net.www;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import zildo.monde.dialog.HistoryRecord;
import zildo.resource.Constantes;
import zildo.server.EngineZildo;

/** Object able to send a detailed exception report to Alembrume site.
 * As Google limits information in a stacktrace from now on, we need to reach these precious one by another way.
 * 
 * @author Tchegito
 *
 */
public class CrashReporter {

	String fullStack;
	
	StringBuilder details = new StringBuilder();
	
	public CrashReporter(Throwable t) {
		// Try to send report if player has any communication enabled (3G or Wifi)
		fullStack = stackTrace(t);
	}

	public CrashReporter addContext() {
		try {
			// Add contextual infos
			addDetail("version", "v" + Constantes.CURRENT_VERSION_DISPLAYED);
			addDetail("map", EngineZildo.mapManagement.getCurrentMap());
			addDetail("sprites", EngineZildo.spriteManagement.getSpriteEntities(null));
			addDetail("persos", EngineZildo.persoManagement.tab_perso);
			addDetail("variables", EngineZildo.scriptManagement.getVariables());
			addDetail("scripts", EngineZildo.scriptManagement.verbose());
			addDetail("quests", EngineZildo.scriptManagement.getAccomplishedQuests());
			List<HistoryRecord> records = EngineZildo.game.getLastDialog();
			String wholeText = HistoryRecord.getDisplayString(records);
			addDetail("lastdialog", wholeText);
		} catch (Exception e) {
			// Don't let us fail because of something here !
			
			addDetail("special", "We have an additional failure here !" + stackTrace(e));
		}
		return this;	// Fluent ;)
	}
	
	private String stackTrace(Throwable t) {
		StringWriter errorMessage = new StringWriter();
		PrintWriter pw = new PrintWriter(errorMessage);
		t.printStackTrace(pw);
		return errorMessage.toString();
	}
	
	private void addDetail(String kind, Object o) {
		details.append(kind).append("=").append(o).append("\n");
	}
	
	public String getMessage() {
		return details.toString() + "\n\n\n" + fullStack;
	}
	
	public void sendReport() {
		StringBuilder sb = new StringBuilder(WorldRegister.url);
		sb.append("?command=STACK");
		try {
			String encoded = URLEncoder.encode(getMessage(), WorldRegister.charset);
			sb.append("&message=").append(encoded);
			System.out.println(encoded);
		} catch (UnsupportedEncodingException e) {
			
		}
		new AlembrumeHttpRequest(sb.toString()).send();
	}
}
