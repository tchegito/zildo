/**
 * The Land of Alembrum
 * Copyright (C) 2006-2013 Evariste Boussaton
 * 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package zildo.fwk;

import java.lang.reflect.Constructor;

/**
 * Mini-injection framework.
 * 
 * Able to create instance of classes in the classloader scope, with calling the right constructor
 * depending on the given parameters.
 *  
 * @author Tchegito
 *
 */
public class Injector {

    public <T> T createSingleton(String p_className, Object... parameters) {
		Class<T> clazz = findClass(p_className);
        try {
            T o = createInstance(clazz, parameters);
            //System.out.println("created "+p_className);
            return o;
        } catch (Exception e) {
            throw new RuntimeException("Unable to create instance of class "+p_className, e);
        }
    }
   
	@SuppressWarnings("unchecked")
	public <T> Class<T> findClass(String p_className) {
        Class<T> clazz;
        try {
            clazz = (Class<T>) Class.forName(p_className);
            return clazz;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unable to find class for "+p_className);
        }
	}
	
    /**
     * Call the appropriate constructor, according to provided parameters.
     * @param clazz
     * @param params
     * @return Object
     */
    @SuppressWarnings("unchecked")
    public <T> T createInstance( Class<T> clazz, Object... params ) {
        try {
            // 1st case : no parameters
            if ( params == null ) {
                return clazz.newInstance();
            }
            Class<?>[] provided = new Class[params.length];
            for ( int i = 0; i < params.length; i++ ) {
                provided[i] = params[i].getClass();
            }
            Constructor<T> ctr = null;
            try {
                // 2nd case : exact constructor
                ctr = clazz.getConstructor( provided );
            }
            catch ( NoSuchMethodException e ) {
                Constructor<T>[] ctrs = (Constructor<T>[]) clazz.getConstructors();
                // 3nd case : Look for primitive types, due to potential auto-boxing
                for ( Constructor<T> oneCtr : ctrs ) {
                    Class<?>[] expected = oneCtr.getParameterTypes();
                    boolean right = true;
                    if ( expected.length == provided.length ) {
                        for ( int j = 0; j < expected.length && right; j++ ) {
                            if ( expected[j].isPrimitive() != provided[j].isPrimitive() ) {
                                if ( !isFirstPrimitiveEquals( expected[j], provided[j] )
                                    && !isFirstPrimitiveEquals( provided[j], expected[j] ) ) {
                                    right = false;
                                }
                            }
                        }
                    } else {
                    	right = false;
                    }
                    if ( right ) {
                        ctr = oneCtr;
                        break;
                    }
                }
                if ( ctr == null ) {
                    throw new RuntimeException( "Can't find appropriate constructor for " + clazz.getName()
                        + " with parameters [" + params + "]" );
                }
            }
            return ctr.newInstance( params );
        }
        catch ( Exception e ) {
            throw new RuntimeException( "Can't create instances of " + clazz.getName() + " with parameters [" + params
                + "]", e );
        }
    }

    /**
     * Compare the two provided classes, assuming that second one is primitive, and first one isn't.
     * @param c1
     * @param c2
     * @return TRUE if second class is the native first one.
     */
    private boolean isFirstPrimitiveEquals( Class<?> c1, Class<?> c2 ) {
        if ( c1.isPrimitive() && !c2.isPrimitive() ) {
            return false;
        }
        return (c1 == Integer.class && c2 == Integer.TYPE) || //
            (c1 == Byte.class && c2 == Byte.TYPE) || //
            (c1 == Character.class && c2 == Character.TYPE) || //
            (c1 == Float.class && c2 == Float.TYPE) || //
            (c1 == Double.class && c2 == Double.TYPE) || //
            (c1 == Long.class && c2 == Long.TYPE) || //
            (c1 == Void.class && c2 == Void.TYPE) || //
            (c1 == Short.class && c2 == Short.TYPE) || //
            (c1 == Boolean.class && c2 == Boolean.TYPE);
    }
}
