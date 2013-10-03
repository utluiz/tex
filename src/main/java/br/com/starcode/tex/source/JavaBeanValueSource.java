/**
 * Copyright (c) 2013 Luiz Ricardo, http://luizricardo.eti.br, http://starcode.com.br
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package br.com.starcode.tex.source;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.starcode.tex.Util;

/**
 * This class uses fields (including if they're private) or accessor methods (only if public) to get java bean values.
 * To export many beans, this instance should be reused for best performance. 
 * @param <T> Bean type
 */
public class JavaBeanValueSource<T> implements ValueSource {
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected Object javaBean;
	protected boolean useAccessorMethods;
	protected Map<String, Field> fieldMap;
	protected Map<String, Method> methodMap;
	
	public JavaBeanValueSource(T javaBean, boolean useAccessorMethods) {
		
		if (javaBean == null) {
			throw new IllegalArgumentException("Java Bean cannot be null!");
		}
		this.javaBean = javaBean;
		this.useAccessorMethods = useAccessorMethods;

		if (useAccessorMethods) {
			
			methodMap = new HashMap<String, Method>();
			
		} else {
			
			fieldMap = new HashMap<String, Field>();
			
		}
		logger.debug(getClass().getSimpleName() + " instantiated for bean of type " + javaBean.getClass().getSimpleName() + ", useAccessorMethods = " + useAccessorMethods);
		
	}
	
	@Override
	public Object calculateValue(String columnValue) {
		
		if (useAccessorMethods) {
			
			String accessorName = "get" + columnValue.substring(0, 1).toUpperCase() + columnValue.substring(1);
			Method method = findMethod(accessorName);
			if (method == null) {
				throw new RuntimeException("Java Bean does not have a method called '" + accessorName + "()'!");
			}
			try {
				logger.trace("Invoking accessor method...");
				return method.invoke(javaBean, (Object[]) null);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e.getTargetException());
			}
			
		} else {
			
			Field field = findField(columnValue);
			if (field == null) {
				throw new RuntimeException("Java Bean does not have a field called '" + columnValue + "'!");
			}
			try {
				logger.trace("Reading field value...");
				return field.get(javaBean);
			} catch (IllegalArgumentException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			
		}
		
	}
	
	protected Field findField(String fieldName) {
		
		Field field = fieldMap.get(fieldName);
		if (field == null) {
			
			field = Util.findField(javaBean, fieldName);
			fieldMap.put(field.getName(), field);
			
		}
		return field;
		
	}
	
	protected Method findMethod(String methodName) {
		
		Method method = methodMap.get(methodName);
		if (method == null) {
			
			method = Util.findMethod(javaBean, methodName);
			methodMap.put(method.getName(), method);
			
		}
		return method;
		
	}
	
	/**
	 * You can use this method to avoid creating a new object every time
	 * @param javaBean New instance to get values
	 */
	public void setJavaBean(T javaBean) {
		logger.trace("Updating java bean");
		this.javaBean = javaBean;
	}

	@Override
	public String toString() {
		return "JavaBeanValueSource [javaBean=" + javaBean + ", useAccessorMethods=" 
				+ useAccessorMethods + ", fieldMap=" + fieldMap
				+ ", methodMap=" + methodMap + "]";
	}
	
}
