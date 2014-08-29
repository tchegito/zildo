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
public interface IEvaluationContext {

	public float getValue(String key);
	
	public Object getActor();
}
