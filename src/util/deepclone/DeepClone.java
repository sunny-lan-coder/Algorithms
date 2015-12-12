package util.deepclone;

import java.lang.reflect.*;

public class DeepClone {

	// Returns a clone of d iff it implements Cloneable and possesses a
	// "clone()" method.
	// Returns d itself otherwise.
	@SuppressWarnings("unchecked")
	public static <E> E deepClone(E d) throws IllegalAccessException,
			InvocationTargetException {
		for (@SuppressWarnings("rawtypes")
		Class i : d.getClass().getInterfaces())
			if (i.getName().equals("java.lang.Cloneable")) // Be sure E
															// implements
															// Cloneable.
				for (Method m : d.getClass().getMethods())
					if (m.getName().equals("clone") && // Be sure E has an
														// appropriate clone(),
							m.getParameterTypes().length == 0 && // takes no
																	// parameters,
																	// and
																	// returns
							m.getReturnType().getName()
									.equals("java.lang.Object")) // type Object.
						return (E) m.invoke(d); // Invoke d.clone(), typecast,
												// and return it.
		return d; // If one of the above conditions was not met, then E is not
					// cloneable,
	} // so return a shallow copy instead.
}
