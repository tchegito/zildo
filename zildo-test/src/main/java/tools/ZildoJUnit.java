package tools;

import java.lang.annotation.Annotation;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import tools.annotations.ClientMainLoop;
import tools.annotations.DisableFreezeMonitor;
import tools.annotations.DisableSpyGuiDisplay;
import tools.annotations.InfoPersos;
import tools.annotations.SoundEnabled;
import tools.annotations.SpyHero;
import tools.annotations.SpyMapManagement;

/**
 * Runner allowing us to benefit from custom annotations. Then code gets clearer than a bunch of 'if' inside test methods.
 * 
 * For now, there is 7 annotations:<ul>
 * <li>{@link DisableFreezeMonitor}</li>
 * <li>{@link InfoPersos}</li>
 * <li>{@link SpyHero}</li>
 * <li>{@link SpyMapManagement}</li>
 * <li>{@link ClientMainLoop}</li>
 * <li>{@link DisableSpyGuiDisplay}</li>
 * <li>{@link SoundEnabled}</li>
 * </ul>
 * 
 * @author tchegito
 *
 */
public class ZildoJUnit extends BlockJUnit4ClassRunner {

	public ZildoJUnit(Class<?> klass) throws org.junit.runners.model.InitializationError {
		super(klass);
	}
	
	@Override
	protected Statement withBefores(FrameworkMethod method, Object target, Statement statement) {
		// Check if test method has any custom annotations
		if (!(target instanceof EngineUT)) {
			throw new RuntimeException("Test class "+target+" MUST be a subclass of EngineUT !");
		}
		EngineUT testClass = (EngineUT) target;
		// Check annotations on method
		handleAnnotations(method.getAnnotations(), testClass);
		// Check annotations on class
		handleAnnotations(testClass.getClass().getAnnotations(), testClass);

		System.out.println(Runtime.getRuntime().freeMemory()+" / " + Runtime.getRuntime().totalMemory());
		return super.withBefores(method, target, statement);
	}
	
	private void handleAnnotations(Annotation[] anns, EngineUT testClass) {
		for (Annotation ann : anns) {
			Class<? extends Annotation> clazz = ann.annotationType();
			if (clazz == SpyHero.class) {
				testClass.spyHero = true;
			} else if (clazz == SpyMapManagement.class) {
				testClass.spyMapManagement = true;
			} else if (clazz == InfoPersos.class) {
				testClass.debugInfosPersos = true;
			} else if (clazz == DisableFreezeMonitor.class) {
				testClass.disableFreezeMonitor = true;
			} else if (clazz == ClientMainLoop.class) {
				testClass.clientMainLoop = true;
			} else if (clazz == SoundEnabled.class) {
				testClass.soundEnabled = true;
			} else if (clazz == DisableSpyGuiDisplay.class) {
				testClass.disableGuiDisplay = true;
			}
		}
	}
}
