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

import br.com.starcode.tex.Util;
import br.com.starcode.tex.column.ColumnDefinition;


public class IntegerFormatter extends AbstractFormatter {

	public IntegerFormatter() {
		super(Integer.class);
	}

	public String format(Object data, ColumnDefinition columnDefinition) {
		
		checkType(data, columnDefinition);
		Integer i = (Integer) data;
		logger.trace("Formatting " + i + " with format " + columnDefinition.getFormat());
		if (i == null) {
			return null;
		} else if (columnDefinition.getFormat() == null || columnDefinition.getFormat().isEmpty()) {
			return i.toString();
		} else {
			return Util.getNumberFormat(columnDefinition).format(i);
		}
		
	}
	
}
