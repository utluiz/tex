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
package br.com.starcode.tex;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.starcode.tex.column.ColumnDefinition;

public class Util {
	
	final static Logger logger = LoggerFactory.getLogger(Util.class);
	
	//TODO benchmark to verify if threadlocal is faster then cloning
	private static ThreadLocal<NumberFormat> baseNumberFormat = new ThreadLocal<NumberFormat>();
	private static ThreadLocal<Map<ColumnDefinition, NumberFormat>> numberFormatMapThread = new ThreadLocal<Map<ColumnDefinition, NumberFormat>>();

	public static NumberFormat getNumberFormat(ColumnDefinition columnDefinition) {
		
		//check and create a base number format to clone from
		if (baseNumberFormat.get() == null) {
			synchronized (baseNumberFormat) {
				baseNumberFormat.set(NumberFormat.getInstance());
				logger.debug("Created new base number format");
			}
		}
		
		//check and create map
		Map<ColumnDefinition, NumberFormat> numberFormatMap = numberFormatMapThread.get();
		if (numberFormatMap == null) {
			numberFormatMap = new HashMap<ColumnDefinition, NumberFormat>();
			numberFormatMapThread.set(numberFormatMap);
		}
		
		//check and clone specific number format
		NumberFormat numberFormat = numberFormatMap.get(columnDefinition);
		if (numberFormat == null) {
			numberFormat = (NumberFormat) baseNumberFormat.get().clone();
			logger.debug("Number format cloned for " + columnDefinition);
			numberFormatMap.put(columnDefinition, numberFormat);
		}
		
		String format = columnDefinition.getFormat();
		if (numberFormat instanceof DecimalFormat && !(format == null || format.trim().length() == 0)) {
			
			DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
			
			//change grouping and decimal separator if was defined 
			if (columnDefinition.getGroupingSeparator() != null || columnDefinition.getDecimalSeparator() != null) {
				
				DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
				if (columnDefinition.getGroupingSeparator() != null) {
					formatSymbols.setGroupingSeparator(columnDefinition.getGroupingSeparator().charAt(0)); 
				}
				if (columnDefinition.getDecimalSeparator() != null) {
					formatSymbols.setDecimalSeparator(columnDefinition.getDecimalSeparator().charAt(0)); 
				}
				decimalFormat.setDecimalFormatSymbols(formatSymbols);
				
			}
			decimalFormat.applyPattern(format);
			
		}
		
		return numberFormat;
		
	}
	public static String alignContent(String data, ColumnDefinition columnDefinition) {
		
		if (data.length() < columnDefinition.getWidth()) {
			
			return fill(
					data, 
					columnDefinition.getWidth(), 
					columnDefinition.getFillChar().charAt(0), 
					columnDefinition.getAlignment().equalsIgnoreCase("right"));
			
		}
		return data;
		
	}
	
	public static String fill(String text, int size, char character, boolean left) {

		if (text == null) {

			text = "";

		}

		if (text.length() >= size) {

			return text;

		} else {

			StringBuffer strBuffer = new StringBuffer();
			if (!left) {

				strBuffer.append(text);

			}
			for (int i = 0; i < size - text.length(); i++) {

				strBuffer.append(character);

			}
			if (left) {

				strBuffer.append(text);

			}
			return strBuffer.toString();

		}

	}
	
	public static String readContent(InputStream arquivo, String encoding) throws IOException {
		
		logger.debug("Reading content from stream with encoding " + encoding);
		StringBuffer corpo = new StringBuffer();
		InputStreamReader freader = encoding == null ? 
				new InputStreamReader(arquivo) : 
				new InputStreamReader(arquivo, encoding);
		BufferedReader in = null;

		try {
			
			in = new BufferedReader(freader);
			char[] buf = new char[1048576];
			while (in.ready()) {
				
				int qtd = in.read(buf);
				corpo.append(buf, 0, qtd);
							
			}
			
		} finally {
			
			logger.debug("Closing stream");
			if (freader != null) {
				freader.close();
			}
			if (in != null) {
				in.close();
			}
			
		}
		
		return corpo.toString();
		
	}
	
	public static Field findField(Object object, String fieldName) {
		
		Class<?> currentClass = object.getClass();
		logger.debug("Finding field " + fieldName + " in class " + currentClass.getSimpleName());
		while (currentClass != null) {
			
			try {
				Field f = currentClass.getDeclaredField(fieldName);
				logger.debug("Found!");
				return f;
			} catch (NoSuchFieldException e) {
				//nothing, will try parent class
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			}
			
			currentClass = currentClass.getSuperclass();
			logger.debug("Trying superclass " + currentClass.getSimpleName());
			
		}
		return null;
		
	}
	
	public static Method findMethod(Object object, String methodName) {
		
		Class<?> currentClass = object.getClass();
		logger.debug("Finding method " + methodName + " in class " + currentClass.getSimpleName());
		while (currentClass != null) {
			
			try {
				Method m = currentClass.getDeclaredMethod(methodName);
				logger.debug("Found!");
				return m;
			} catch (NoSuchMethodException e) {
				//nothing, will try parent class
			} catch (SecurityException e) {
				throw new RuntimeException(e);
			}
			
			currentClass = currentClass.getSuperclass();
			logger.debug("Trying superclass " + currentClass.getSimpleName());
			
		} 
		return null;
		
	}

}
