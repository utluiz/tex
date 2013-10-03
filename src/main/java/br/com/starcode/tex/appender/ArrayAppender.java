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
package br.com.starcode.tex.appender;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.starcode.tex.column.RowDataSet;
import br.com.starcode.tex.structure.RowStructure;


public class ArrayAppender implements Appender {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	private List<String> arrayList;

	public ArrayAppender() {
		arrayList = new ArrayList<String>();
	}

	public void append(RowDataSet data, RowStructure rowStructure) {
		arrayList.add(rowStructure.createLine(data, this));
	}

	public void close() {
	}

	public int count() {
		return arrayList.size();
	}

	public Object result() {
		return arrayList.toArray();
	}

	@Override
	public String toString() {
		return "ArrayAppender [arrayList size=" + arrayList.size() + "]";
	}
	
}
