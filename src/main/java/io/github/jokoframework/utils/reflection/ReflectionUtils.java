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

	/**
	 * El método retorna el resultado de llamar el getter para cierto atributo "fieldName" para cierto objeto "bean",
	 * permite atributos anidados.
	 *
	 * @param bean Objeto con un getter para el atributo "fieldName"
	 * @param fieldName Nombre del atributo cuyo getter se quiere invocar, puede ser un atributo anidado
	 * @return El valor del atributo "fieldName" del objeto "bean"
	 * @throws IllegalStateException
	 */
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

	/**
	 * El método retorna el resultado de llamar el getter de un objeto "bean" para cierto campo "field" y cuyo getter
	 * acepta una lista de Strings "parameters" como argumento, permitiendo getters mas dinámicos.
	 *
	 * @param bean Objeto con un getter con el nombre "field"
	 * @param field Nombre del campo cuyo getter se quiere invocar
	 * @param parameters Lista de Integers que es un argumento del getter a invocar
	 * @return El valor del getter de "field" del objeto "bean" usando como argumento "parameters" para el getter
	 */
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

	/**
	 * El método llama el setter para cierto atributo "fieldName" para cierto objeto "bean"
	 * dado un valor a introducir "valor", permite atributos anidados.
	 *
	 * @param bean Objeto con un setter para el atributo "fieldName"
	 * @param fieldName Nombre del atributo cuyo setter se quiere invocar, puede ser un atributo anidado
	 * @param valor Valor que se quiere introducir en el atributo "fieldName" del objeto "bean"
	 * @throws IllegalStateException
	 */
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

	/**
	 * Describe la clase y la instancia con el formato "nombreDePropiedad" tipo de dato: valor.
	 *
	 * @param objeto Objeto instanciado
	 * @return String con la descripción del objeto incluyendo el hashCode
	 */
	public static String describe(Object objeto) {
		return describe(objeto, true);
	}

	/**
	 * Describe la clase y la instancia con el formato "nombreDePropiedad" tipo de dato: valor.
	 *
	 * @param object Objeto instanciado
	 * @param withHash True si se quiere que se muestre con el hashCode, sino False
	 * @return String con la descripción del objeto
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

	/**
	 * Retorna el String pasado con su primer caracter pasado a mayúscula, si el primer caracter no es una letra o ya
	 * esta en mayúscula no hace nada.
	 *
	 * @param value String
	 * @return "value" con su primer caracter pasado a mayúscula
	 */
	public static String firstLetterToUppercase(String value) {
		StringBuffer ret = new StringBuffer();
		ret.append(value.substring(0, 1).toUpperCase());
		ret.append(value.substring(1));
		return ret.toString();
	}

	/**
	 * Retorna la clase del atributo "fieldName" del objeto "bean", si no se encuentra el atributo se retorna la clase
	 * Objeto.
	 *
	 * @param bean Objeto
	 * @param fieldName Atributo del objeto "bean"
	 * @return La clase del atributo "fieldName" del objeto "bean", si no se encuentra el atributo se retorna la clase
	 * 		   Objeto
	 */
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

	/**
	 * Retorna el nombre del método en el que se ejecuta en un String.
	 *
	 * @return Nombre del método en el que se ejecuta
	 */
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
	 * Busca {@link Field} en la clase y todas sus subclases hasta {@link Object}. Retorna <code>null</code> en caso
	 * que el campo no sea encontrado.
	 *
	 * @param fieldName Nombre del campo buscado
	 * @param clazz Tipo del objeto en donde buscar
	 * @return Campo declarado
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

	/**
	 * Retorna un array de Strings conteniendo todos los atributos de la clase "clazz" que tengan ambos un getter sin
	 * argumentos y un setter solo con un argumento de tipo igual al atributo correspondiente.
	 *
	 * @param clazz Clase de cual obtener la lista de atributos
	 * @return Array de atributos de la clase "clazz"
	 * @throws IllegalStateException
	 */
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
	 * Retorna una lista de atributos lectura/escritura de la clase "clazz" de tipo "elementType" que no sean del tipo
	 * collection.
	 *
	 * @param clazz Clase a mirar
	 * @param elementType Tipo de los atributos a retornar en la lista
	 * @return Lista de campos
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

	/**
	 * Retorna una lista de atributos lectura/escritura de la clase "clazz" de tipo "elementType" que sean del tipo
	 * collection.
	 *
	 * @param value Objeto a evaluar
	 * @param elementType Tipo de los atributos a retornar en la lista
	 * @return Lista de campos
	 */
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

	/**
	 * Pasado un String que se supone que es un atributo de un objeto retorna le nombre del getter poniendo al comienzo
	 * la palabra get y pasando a mayúscula la primera letra del String proveido.
	 *
	 * @param fieldName Nombre de un atributo para obtener su getter
	 * @return El getter de "fieldName"
	 */
	public static String getGetterMethod(String fieldName) {
		return "get" + fieldName.substring(0, 1).toUpperCase()
				+ fieldName.substring(1, fieldName.length());
	}

	/**
	 * Se retornara el método de nombre "name" y parámetros "paramas" de la clase del objeto "bean" proveído.
	 * El orden de las clases de "params" debe ser igual al de los parámetros del método.
	 *
	 * @param bean Objeto instanciado
	 * @param name Nombre del método buscado
	 * @param params Parámetros de la clase
	 * @return Método pedido
	 */
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
	 * Retorna el método pedido pero consume la excepción.
	 *
	 * @param object Objeto instanciado
	 * @param methodName Nombre del método buscado
	 * @param params Parámetros de la clase
	 * @return Método pedido
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

	/**
	 * Retorna una lista con los métodos de la clase "clazz" que sean públicos.
	 *
	 * @param clazz Clase de donde buscar los métodos públicos
	 * @return Lista de Métodos públicos pertenecientes a "clazz"
	 */
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

	/**
	 * Retorna una lista con los métodos de la clase "clazz" que sean públicos.
	 *
	 * @param clazz Clase de donde buscar los métodos públicos
	 * @param withParameters True para que se incluyan los tipos de los parametros, sino False
	 * @return Lista de Strings públicos pertenecientes a "clazz"
	 */
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

	/**
	 * Retorna el Method del getter para el atributo de la clase "clazz" y de nombre "fieldName".
	 *
	 * @param clazz Clase
	 * @param fieldName Atributo de "clazz" de donde sacar el getter
	 * @return Getter del atributo
	 * @throws IllegalStateException
	 * @throws NoSuchFieldException
	 */
	public static Method getReadMethod(Class<?> clazz, String fieldName)
			throws IllegalStateException, NoSuchFieldException {
		PropertyDescriptor desc = obtenerPropertyDescriptor(clazz, fieldName);
		return desc.getReadMethod();
	}

	/**
	 * Retorna un valor para el tipo de dato. La clase debe tener un constructor con un String de parametro.
	 *
	 * @param type Nombre de la clase para instanciar
	 * @param value Para el constructor
	 * @return Objeto instanciado
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

	/**
	 * Retorna el Method del setter para el atributo de la clase "clazz" y de nombre "fieldName".
	 *
	 * @param clazz Clase
	 * @param fieldName Atributo de "clazz" de donde sacar el setter
	 * @return Setter del atributo
	 * @throws IllegalStateException
	 * @throws NoSuchFieldException
	 */
	public static Method getWriteMethod(Class<?> clazz, String fieldName)
			throws IllegalStateException, NoSuchFieldException {
		PropertyDescriptor desc = obtenerPropertyDescriptor(clazz, fieldName);
		return desc.getWriteMethod();
	}

	/**
	 * Invoca al método "metodo" del objeto "bean" con los parámetros "parameters" ("null" si no tiene parámetros).
	 *
	 * @param metodo Método que se quiere ejecutar
	 * @param bean Objeto del cual ejecutar el método
	 * @param parameters Parámetros del método ("null" si no tiene parámetros)
	 * @return Lo retornado por el método invocado
	 */
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

	/**
	 * Retornara el PropertyDescriptor del atributo "fieldName" de la clase "class".
	 *
	 * @param clazz Clase en cuestión
	 * @param fieldName Atributo
	 * @return PropertyDescriptor del atributo "fieldName" de la clase "clazz"
	 * @throws IllegalStateException
	 * @throws NoSuchFieldException
	 */
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
	 * Obtiene todos los PropertyDescriptor de la clase "target".
	 *
	 * @param target Clase a analizar
	 * @return Lista de PropertyDescriptor relacionados a la clase "target"
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

	/**
	 * Dado un objeto "fuente" se retorna el valor del atributo "fieldName" proveído por su getter.
	 *
	 * @param fuente Objeto a mirar
	 * @param fieldName Atributo del objeto a mirar
	 * @return Valor contenido en el atributo del objeto
	 * @throws IllegalStateException
	 */
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

	/**
	 * Dado un objeto "object" se retorna el valor del atributo proveído por su getter pasando el PropertyDescriptor
	 * "property".
	 *
	 * @param property PropertyDescriptor del atributo a obtener
	 * @param object Objeto a mirar
	 * @return Valor contenido en el atributo del objeto
	 * @throws IllegalStateException
	 */
	public static Object read(PropertyDescriptor property, Object object)
			throws IllegalStateException {
		try {
			return property.getReadMethod().invoke(object, new Object[] {});
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * Ejecuta el String en la linea de comando en el directorio raiz del proyecto por defecto.
	 *
	 * @param cmdline Comando a ejecutar
	 * @return Resultado obtenido al ejecutar el comando
	 */
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
	 * Verifica la lista de campos de un objeto, retorna False si ningún campo contiene "null" y si ningún String esta
	 * vacío "" o solo contiene espacios "   ", sino True.
	 *
	 * @param object Objeto a verificar
	 * @return Boolean
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

	/**
	 * Dado un objeto "object" se escribe el valor "value" en el atributo encontrado usando su PropertyDescriptor
	 * "property".
	 *
	 * @param property PropertyDescriptor del atributo en donde escribir
	 * @param object Objeto a mirar
	 * @param value Valor a escribir
	 * @throws IllegalStateException
	 */
	public static void write(PropertyDescriptor property, Object object,
			Object value) throws IllegalStateException {
		try {
			property.getWriteMethod().invoke(object, value);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

}
