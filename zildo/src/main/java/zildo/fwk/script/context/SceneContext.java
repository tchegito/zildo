package zildo.fwk.script.context;

/** Context linked to a scene.
 * 
 * That means "self" is nonsense here. But we can use local variables as well.
 * 
 */
public class SceneContext extends LocaleVarContext {

	@Override
	public float getValue(String key) {
		//throw new RuntimeException("Can't find variable "+key+" in a SceneContext !");
		return 0;
	}

	@Override
	public Object getActor() {
		return null;
	}

}
