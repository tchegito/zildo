package tools.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import zildo.client.Client;

/** This annotation means that in UT, {@link Client#mainLoop()} method will be called.
 * That is mandatory to deal with menus for example. **/
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface ClientMainLoop {

}
