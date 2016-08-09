import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexander on 09.08.16.
 */
public class BeanUtils {
    public static void assign(Object to, Object from) {
        if (to == null || from == null) {
            throw new IllegalArgumentException("argument(s) is(are) null");
        }
        List<Method> methods_to = search_settersTo(to);
        Class<?> clazz = from.getClass();
        while (clazz != null) {
            Method[] methods_from = clazz.getMethods();
            for (Method mm_from : methods_from) {
                if (mm_from.getName().startsWith("get") && (mm_from.getParameterTypes().length == 0) && (mm_from.getReturnType() != void.class)) {
                    invoke_set(to, from, methods_to, mm_from);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    private static List<Method> search_settersTo(Object o) {

        List<Method> our_methods = new ArrayList<Method>();
        Class<?> clazz = o.getClass();
        while (clazz != null) {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if ((method.getReturnType() == void.class)  & (method.getParameterTypes().length == 1) & (method.getName().startsWith("set"))) {
                    our_methods.add(method);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return our_methods;
    }

    private static boolean equals_property(Method to, Method from) {

        if (((to.getName().length() == from.getName().length()) || (from.getReturnType().isAssignableFrom(to.getParameterTypes()[0]))) && (to.getName().substring(3).equals(from.getName().substring(3)))){
            return true;
        }
        else {
            return false;
        }
    }

    private static void invoke_set(Object to, Object from, List<Method> methods_to, Method mm_from) {
        for (Method mm_to : methods_to) {
            if (equals_property(mm_to, mm_from)) {
                try {
                    mm_to.invoke(to, mm_from.invoke(from));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
