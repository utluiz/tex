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
package br.com.starcode.tex.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.starcode.tex.column.ColumnDefinition;


public abstract class AbstractFormatter implements DataFormatter {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected Class<?> dataType;
	
	public AbstractFormatter(Class<?> dataType) {
		
		this.dataType = dataType;
		
	}
	
	/**
	 * Check if data type is compatible with formatter type
	 */
	protected void checkType(Object data, ColumnDefinition columnDefinition) {
		
		if (data != null && !dataType.isAssignableFrom(data.getClass())) {
			
			throw new ClassCastException("Value type (" + data.getClass().getName() + ") is not compatible with formatter (" + getClass().getName() + ")!");
				
		}
			
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [dataType=" + dataType + "]";
	}
	
}
