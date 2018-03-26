package io.github.jokoframework.utils.reflection;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtils {
    
    private static final Logger log = LoggerFactory.getLogger(ReflectionUtils.class);

	public static Object callGetter(Object bean, String fieldName)
			throws IllegalStateException {
		Method m = null;
		Object ret = null;
		try {
			if (fieldName.indexOf(".") != -1) {
				ret = PropertyUtils.getNestedProperty(bean, fieldName);
			} else {
				m = getReadMethod(bean.getClass(), fieldName);
				ret = m.invoke(bean, new Object[] {});
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return ret;
	}

	public static String callGetterDynamic(Object bean, String field, List<Integer> parameters) {
		String methodName = "get" + firstLetterToUppercase(field);
		String ret = "_EMPTY_";
		Class<?>[] parameterTypes = new Class<?>[parameters.size()];
		Object[] paramsArray = parameters.toArray();
		int i = 0;
		for (Iterator<?> iterator = parameters.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			parameterTypes[i++] = object.getClass();
		}
		try {
			Method m = bean.getClass().getMethod(methodName, parameterTypes);
			ret = (String) m.invoke(bean, paramsArray);
		} catch (Exception e) {
			throw new IllegalStateException("Couldn't get method : "
					+ methodName, e);
		}
		return ret;
	}

	public static void callSetter(Object bean, String fieldName, Object valor) {
		Method m = null;
		try {
			if (fieldName.indexOf(".") > 0) {
				PropertyUtils.setNestedProperty(bean, fieldName, valor);
			} else {
				m = getWriteMethod(bean.getClass(), fieldName);
				m.invoke(bean, new Object[] { valor });
			}
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static String describe(Object objeto) {
		return describe(objeto, true);
	}
	
	/**
	 * Describes the class and the instance with the format "propertyName"
	 * data type: value
	 * 
	 * @param object instanced object
	 * @param withHash indicates if you want it with the hashCode
	 * @return string that contains the described object
	 */
	public static String describe(Object object, Boolean withHash) {
		String name = object.getClass().getSimpleName();
		String className = object.getClass().getName();
		StringBuffer ret = new StringBuffer();
		StringBuffer sbNullValues = new StringBuffer("{");
		PropertyDescriptor[] descs;
		int i = 0;
		try {
			ret.append("<").append(name);
			if (withHash)
				ret.append(" hashCode:").append(object.hashCode());
			ret.append(" >");
			descs = getPropertyDescriptors(object.getClass());
			for (PropertyDescriptor pd : descs) {
				i++;
				if (!pd.getName().equalsIgnoreCase("class")) {
					String stringValue = null;
					String propertyName = pd.getName();
					Method readMethod = pd.getReadMethod();
					Method writeMethod = pd.getWriteMethod();
					Object val = null;
					if (readMethod != null && writeMethod != null)
						val = readMethod.invoke(object, new Object[] {});
					if (val != null) {

						stringValue = val.toString();
						ret.append("{");
						ret.append(propertyName);
						ret.append("=");
						ret.append(stringValue);
						ret.append("}");
						if (i < (descs.length - 1))
							ret.append("; ");
					} else {
						sbNullValues.append(propertyName);

						sbNullValues.append(",");
					}
				}
			}
			if (sbNullValues.length() > 1) { // it means that there are null values
				sbNullValues.append(" = null}");
				ret.append(sbNullValues);
			}
			ret.append("; Class: ").append(className);
			ret.append("</").append(name).append(">");
		} catch (Exception e) {
		    //in case of error it returns super
			ret = null;
		}
		if (ret == null) {
			return null;
		} else {
			return ret.toString();
		}
	}

	public static String firstLetterToUppercase(String value) {
		StringBuffer ret = new StringBuffer();
		ret.append(value.substring(0, 1).toUpperCase());
		ret.append(value.substring(1));
		return ret.toString();
	}

	public static Class<?> getClassForField(Object bean, String fieldName) {
		Class<?> ret = null;
		try {
			PropertyDescriptor pd = obtenerPropertyDescriptor(bean.getClass(), fieldName);
			ret = pd.getPropertyType();
		} catch (Exception e) {
			ret = Object.class;
		}
		return ret;
	}

	public static String getCurrentMethodName() {
		boolean doNext = false;
		String ret = null;
		StackTraceElement[] e = Thread.currentThread().getStackTrace();
		for (StackTraceElement s : e) {
			ret = s.getMethodName();
			if (doNext) {
				break;
			}
			doNext = ret.equals("getCurrentMethodName");
		}
		return ret;
	}

	/**
	 * Search for the {@link Field} in the class and all they subclases until 
	 * the {@link Object}. It returns <code>null</code> in case that the field hasn't been found
	 * 
	 * @param fieldName name of the wanted field
	 * @param clazz type
	 * @return declared field
	 */
	public static Field getDeclaredField(String fieldName, Class<?> clazz) {
		Field ret = null;
		if (clazz != null) {
			while (!Object.class.equals(clazz) && ret == null) {
				try {
					ret = clazz.getDeclaredField(fieldName);
				} catch (SecurityException e) {
					ret = null;
					break;
				} catch (NoSuchFieldException e) {
					ret = null;
					clazz = clazz.getSuperclass();
				} catch (NullPointerException npe) {
					ret = null;
					break;
				}
			}
		}
		return ret;
	}

	public static String[] getFieldList(Class<?> clazz)
			throws IllegalStateException {
		ArrayList<String> lista = new ArrayList<String>();
		PropertyDescriptor[] descs = getPropertyDescriptors(clazz);
		for (PropertyDescriptor pd : descs) {
			if (pd.getReadMethod() != null && pd.getWriteMethod() != null)

				lista.add(pd.getName());

		}
		return lista.toArray(new String[0]);
	}

	/**
	 * 
	 * Returns the field list read/write of elementType that aren't of 
	 * type collection
	 * 
	 * @param clazz type
	 * @param elementType element type
	 * @return list of fields
	 */
	public static List<String> getFieldsType(Class<?> clazz,
			Class<?> elementType) {
		List<String> ret = new ArrayList<String>();
		List<String> fieldsNotFound = new ArrayList<String>();
		String[] fields = getFieldList(clazz);
		for (String fieldName : fields) {
			try {
				Field field = clazz.getDeclaredField(fieldName);
				if (!Collection.class.isAssignableFrom(field.getType())) {
					Method readMethod = getReadMethod(clazz, fieldName);
					Method writeMethod = getWriteMethod(clazz, fieldName);
					if (elementType.isAssignableFrom(field.getType())
							&& readMethod != null && writeMethod != null) {
						ret.add(fieldName);
					}
				}
			} catch (Exception e) {
			    fieldsNotFound.add(fieldName);
			}
		}

		return ret;
	}

	public static List<String> getFieldsTypeCollection(Object value,
			Class<?> elementType) {
		List<String> ret = new ArrayList<String>();
		String[] fields = getFieldList(value.getClass());
		for (String fieldName : fields) {
			try {
				Field field = value.getClass().getDeclaredField(fieldName);
				if (Collection.class.isAssignableFrom(field.getType())) {
					ParameterizedType type = (ParameterizedType) field
							.getGenericType();
					Class<?> tipo = (Class<?>) type.getActualTypeArguments()[0];
					Object instanciaTipo = tipo.newInstance();
					if (elementType.isAssignableFrom(instanciaTipo.getClass())) {
						ret.add(fieldName);
					}
				}
			} catch (Exception e) {
				ret = new ArrayList<String>();
			}
		}
		return ret;
	}

	public static String getGetterMethod(String fieldName) {
		return "get" + fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1, fieldName.length());
	}

	public static Method getMethod(Object bean, String name,
			Class<?>... params) {
		Method ret = null;
		try {
			ret = bean.getClass().getMethod(name, params);
		} catch (Exception e) {
			log.debug("Couldn't get method : ", e);
			ret = null;
		}
		return ret;
	}

	/**
	 * Returns the requested method but consumes the exception
	 * 
	 * @param object instanced object
	 * @param methodName name of the wanted method
	 * @param params class parameters
	 * @return requested method
	 */
	public static Method getMethodQuiet(Object object, String methodName,
			Class<?>... params) {
		Method m = null;
		try {
			m = object.getClass().getMethod(methodName, params);
		} catch (Exception e) {
			// consumes the exception
		}
		return m;
	}

	public static List<Method> getPublicMethods(Class<?> clazz) {
		List<Method> ret = new ArrayList<Method>();
		Method[] methods = clazz.getDeclaredMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			int mods = method.getModifiers();
			if (mods == Modifier.PUBLIC
					|| (mods == (Modifier.PUBLIC | Modifier.ABSTRACT))) {
				ret.add(method);
			}
		}
		return ret;
	}

	public static List<String> getPublicMethodsName(Class<?> clazz,
			boolean withParameters) {
		List<String> ret = new ArrayList<String>();
		List<Method> publicMethods = getPublicMethods(clazz);
		for (Method method : publicMethods) {
			int mods = method.getModifiers();
			if (mods == Modifier.PUBLIC
					|| (mods == (Modifier.PUBLIC | Modifier.ABSTRACT))) {
				StringBuffer mDesc = new StringBuffer(method.getName());
				if (withParameters) {
					Class<?>[] params = method.getParameterTypes();
					mDesc.append("(");
					for (int j = 0; j < params.length; j++) {
						Class<?> class1 = params[j];
						mDesc.append(class1.getSimpleName());
						if (j < (params.length - 1))
							mDesc.append(",");
					}
					mDesc.append(")");
				}
				ret.add(mDesc.toString());
			}
		}
		return ret;
	}

	public static Method getReadMethod(Class<?> clazz, String fieldName)
			throws IllegalStateException, NoSuchFieldException {
		PropertyDescriptor desc = obtenerPropertyDescriptor(clazz, fieldName);
		return desc.getReadMethod();
	}
	
	/**
	 * Returns a value for the data type. The class must have a 
	 * constructor with a String as parameter
	 * 
	 * @param type name of the class for instantiation
	 * @param value for the constructor
	 * @return instanced object
	 */
	public static Object getValue(String type, Object value) {
		Object ret = null;
		try {
			Constructor<?> constructor = Class.forName(type).getConstructor(
					String.class);
			ret = constructor.newInstance(value);
		} catch (Exception e) {
			log.error("Couldn't instanciate " + type + " with value : "
					+ value);
		}
		return ret;
	}

	public static Method getWriteMethod(Class<?> clazz, String fieldName)
			throws IllegalStateException, NoSuchFieldException {
		PropertyDescriptor desc = obtenerPropertyDescriptor(clazz, fieldName);
		return desc.getWriteMethod();
	}

	public static Object invokeMethod(Method metodo, Object bean,
			Object... parameters) {
		Object ret = null;
		try {
			ret = metodo.invoke(bean, parameters);
		} catch (Exception e) {
			ret = null;
			log.debug("Couldn't invoke the method ", e);
		}
		return ret;
	}

	public static PropertyDescriptor obtenerPropertyDescriptor(Class<?> clazz,
			String fieldName) throws IllegalStateException,
			NoSuchFieldException {
		PropertyDescriptor ret = null;
		PropertyDescriptor[] props = getPropertyDescriptors(clazz);
		for (PropertyDescriptor desc : props) {
			if (desc.getName().equals(fieldName)) {
				ret = desc;
			}
		}
		if (ret == null) {
			throw new NoSuchFieldException("Couldn't found the field "
					+ fieldName + " in " + clazz.getName());
		}
		return ret;
	}

	/**
	 * Gets property descriptors
	 * 
	 * @param target target class
	 * @return property descriptors
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> target) throws IllegalStateException {
		BeanInfo info;
		PropertyDescriptor[] ret;
		Comparator<PropertyDescriptor> comparator = new Comparator<PropertyDescriptor>() {

			public int compare(PropertyDescriptor o1, PropertyDescriptor o2) {
				int ret = -1;
				// hack so that it always shows the "id" first
				if (o1.getName().equals("id")) {
					ret = -1;
				} else if (o2.getName().equals("id")) {
					ret = 1;
				} else {
					ret = o1.getName().compareTo(o2.getName());
				}
				return ret;
			}
		};
		try {
			info = Introspector.getBeanInfo(target);
			ret = info.getPropertyDescriptors();
			Arrays.sort(ret, comparator);
			return ret;
		} catch (IntrospectionException e) {
			throw new IllegalStateException(e);
		}
	}

	public static Object read(Object fuente, String fieldName)
			throws IllegalStateException {
		try {
			Method m = getReadMethod(fuente.getClass(), fieldName);
			Object o = m.invoke(fuente, new Object[] {});
			return o;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static Object read(PropertyDescriptor property, Object object)
			throws IllegalStateException {
		try {
			return property.getReadMethod().invoke(object, new Object[] {});
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public static Map<Integer, String> salidaComandoPorLinea(String cmdline) {
		Map<Integer, String> ret = new HashMap<Integer, String>();
		try {
			Integer nroLinea = 1;
			String line;
			Process p = Runtime.getRuntime().exec(cmdline);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			while ((line = input.readLine()) != null) {
				ret.put(nroLinea, line);
				nroLinea = new Integer(nroLinea + 1);
			}
			input.close();
		} catch (Exception err) {
			log.error("Command execution failed:" + cmdline);
		}
		return ret;
	}
	
	/**
	 * Verifies the list of fields to have non-null values loaded 
     * or if they are of type String they must not be empty
     * 
	 * @param object to verify
	 * @return boolean
	 */
	public static boolean hasNonEmptyValues(Object object) {
		boolean ret = false;
		String[] fields = ReflectionUtils.getFieldList(object.getClass());
		for (String field : fields) {
			Object value = ReflectionUtils.callGetter(object, field);
			if (value == null
					|| ((value instanceof String && StringUtils.isBlank(value.toString())))) {
				ret = true;
			}
			if (ret)
				break;
		}
		return ret;
	}

	public static void write(PropertyDescriptor property, Object object,
			Object value) throws IllegalStateException {
		try {
			property.getWriteMethod().invoke(object, value);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
