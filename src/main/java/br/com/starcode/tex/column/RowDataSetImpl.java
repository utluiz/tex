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
package br.com.starcode.tex.column;

import java.util.ArrayList;
import java.util.List;

import br.com.starcode.tex.TexComponentFactory;
import br.com.starcode.tex.format.DataFormatter;
import br.com.starcode.tex.layout.Layout;


public class RowDataSetImpl implements RowDataSet {

	protected List<Column> columns;
	protected List<Column> counterColumns;
	protected Layout layout;
	protected TexComponentFactory componentFactory;
	
	public RowDataSetImpl(Layout layout, TexComponentFactory componentFactory) {
		columns = new ArrayList<Column>();
		counterColumns = new ArrayList<Column>();
		this.layout = layout;
		this.componentFactory = componentFactory;
	}
	
	@Override
	public void addColumn(Object data, ColumnDefinition columnDefinition, DataFormatter formatter) {
		Column column = componentFactory.createColumn(data, columnDefinition, formatter);
		columns.add(column);
		if (columnDefinition.isCounter()) {
			counterColumns.add(column);
		}
	}

	public Column getColumn(int col) {
		return columns.get(col);
	}

	@Override
	public List<Column> getDataSet() {
		return columns;
	}
	
	@Override
	public Layout getLayout() {
		return layout;
	}

	@Override
	public void updateCounters(int currentRow) {
		for (Column column : counterColumns) {
			column.setData(currentRow);
		}
	}

	@Override
	public String toString() {
		return "RowDataSetImpl [columns=" + columns + ", counterColumns="
				+ counterColumns + ", layout=" + layout + ", componentFactory="
				+ componentFactory + "]";
	}

}
