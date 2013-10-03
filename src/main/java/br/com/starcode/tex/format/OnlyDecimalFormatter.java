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

import java.math.BigDecimal;
import java.text.FieldPosition;
import java.text.NumberFormat;

import br.com.starcode.tex.Util;
import br.com.starcode.tex.column.ColumnDefinition;


public class OnlyDecimalFormatter extends AbstractFormatter {
	
	private static ThreadLocal<NumberFormat> decimalNumberFormat = new ThreadLocal<NumberFormat>();

	public OnlyDecimalFormatter() {
		
		super(BigDecimal.class);
		
	}

	public String format(Object data, ColumnDefinition columnDefinition) {
		
		checkType(data, columnDefinition);
		BigDecimal b = (BigDecimal) data;
		logger.trace("Formatting decimal part of " + b + " with format " + columnDefinition.getFormat());
		if (b == null) {
			
			return null;
			
		} else {
			
			NumberFormat numberFormat;
			if (decimalNumberFormat.get() == null) {
				numberFormat = Util.getNumberFormat(columnDefinition);
				decimalNumberFormat.set(numberFormat);
			} else {
				numberFormat = decimalNumberFormat.get();
			}
			FieldPosition fieldPosition = new FieldPosition(NumberFormat.FRACTION_FIELD);
			StringBuffer sb = new StringBuffer();
			numberFormat.format(b, sb, fieldPosition);
			logger.trace("Formatted number was " + sb.toString() + " and decimal position is in char " + fieldPosition.getBeginIndex());
			String decimalPart = sb.substring(fieldPosition.getBeginIndex());
			logger.trace("Formatted number was  " + sb.toString());
			return decimalPart;
			
		}
		
	}
	
}
