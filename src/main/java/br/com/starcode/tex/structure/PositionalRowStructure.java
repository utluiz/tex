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
package br.com.starcode.tex.structure;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.starcode.tex.Util;
import br.com.starcode.tex.appender.Appender;
import br.com.starcode.tex.column.Column;
import br.com.starcode.tex.column.RowDataSet;


/**
 * Print values in the specified position (in characters) 
 */
public class PositionalRowStructure implements RowStructure {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	public PositionalRowStructure() {
		logger.debug("Instantiated: " + getClass().getSimpleName());
	}
	
	public String createLine(RowDataSet rowDataSet, Appender appender) {
		
		logger.trace("Formatting positional row");
		
		int rowWidth = rowDataSet.getLayout().getLayoutWidth();
		StringBuffer sb = new StringBuffer(rowWidth);
		
		List<Column> columnList = rowDataSet.getDataSet();
		for (int i = 0; i < columnList.size(); i++) {
			
			Column column = columnList.get(i);
			String formattedData = column.getFormattedData();
			int startPosition = column.getDefinition().getPosition();
			int width = column.getDefinition().getWidth();
			int endPosition = startPosition + width;
			
			if (formattedData.length() > width) {
				throw new RuntimeException("Value of column " + (i + 1) + " ('" + formattedData + "') bigger than column width (" + width + ")! Check this value or layout '" + rowDataSet.getLayout().getLabel() + "'.");
			}
			
			String content = Util.alignContent(formattedData, column.getDefinition());
			logger.trace("Column " + (i + 1) + " has value: '" + content + "'");
			sb.replace(startPosition, endPosition, content);
			
		}
		return sb.toString();
		
	}
	
}
