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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.starcode.tex.column.Column;
import br.com.starcode.tex.column.ColumnDefinition;
import br.com.starcode.tex.column.ColumnDefinitionImpl;
import br.com.starcode.tex.column.ColumnImpl;
import br.com.starcode.tex.column.RowDataSet;
import br.com.starcode.tex.column.RowDataSetImpl;
import br.com.starcode.tex.format.DataFormatter;
import br.com.starcode.tex.layout.Layout;
import br.com.starcode.tex.layout.LayoutImpl;
import br.com.starcode.tex.structure.PositionalRowStructure;
import br.com.starcode.tex.structure.RowStructure;
import br.com.starcode.tex.structure.SeparatorRowStructure;


public class TexComponentFactoryImpl implements TexComponentFactory {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	public TexComponentFactoryImpl() {
		logger.debug("Instantiated: " + getClass().getSimpleName());
	}
	
	@Override
	public Layout createLayout(String id, String label, int layoutWidth, RowStructure rowStructure, List<ColumnDefinition> columnDefinitionList) {
		logger.trace("createLayout id = " + id);
		return new LayoutImpl(id, label, layoutWidth, rowStructure, columnDefinitionList);
	}
	
	@Override
	public Column createColumn(Object data, ColumnDefinition columnDefinition, DataFormatter formatter) {
		logger.trace("createColumn");
		return new ColumnImpl(data, columnDefinition, formatter);
	}

	@Override
	public ColumnDefinition createColumnDefinition(
			String type, 
			String source,
			int position, 
			String value, 
			String format, 
			String alignment, 
			int width, 
			String fillChar,
			String decimalSeparator,
			String groupingSeparator) {
		logger.trace("createColumnDefinition type = " + type + ", source = " + source + ", position = " + position + ", value = " + value);
		return new ColumnDefinitionImpl(
				type,
				source,
				position, 
				value, 
				format, 
				alignment, 
				width, 
				fillChar, 
				decimalSeparator, 
				groupingSeparator);
	}

	@Override
	public RowDataSet createRowDataSet(Layout layout) {
		logger.trace("createRowDataSet");
		return new RowDataSetImpl(layout, this);
	}

	@Override
	public PositionalRowStructure createPositionalRowStructure() {
		logger.trace("createPositionalRowStructure");
		return new PositionalRowStructure();
	}

	@Override
	public SeparatorRowStructure createSeparatorRowStructure(String separator) {
		logger.trace("createSeparatorRowStructure");
		return new SeparatorRowStructure(separator);
	}

}
