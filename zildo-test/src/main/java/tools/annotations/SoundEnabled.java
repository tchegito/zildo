package tools.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/** This annotation enables sound in unit test. Hence, we could check if particular sound was played. **/
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface SoundEnabled {

}
