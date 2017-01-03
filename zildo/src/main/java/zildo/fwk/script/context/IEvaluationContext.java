package zildo.fwk.script.context;


/**
 * Describe a context for evaluation boolean expression.<p/>
 * 
 * Used predicates are noted by integer values. And each one is mapped to a boolean,
 * thanks to this context.
 * 
 * @author evariste.boussaton
 *
 */
public interface IEvaluationContext extends Cloneable {

	public float getValue(String key);
	
	public Object getActor();
	
	// For local variables
	public String registerVariable(String name);
	public void unregisterVariable(String name);
	public void terminate();
	public boolean hasVariables();
	
	public String getString(String key);
	
	public IEvaluationContext clone();
}
