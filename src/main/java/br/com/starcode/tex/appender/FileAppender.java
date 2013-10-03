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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.starcode.tex.column.RowDataSet;
import br.com.starcode.tex.structure.RowStructure;

public class FileAppender implements Appender {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected BufferedWriter bufferedWriter;
	protected int rowCount;
	protected String lineSeparator;
	protected boolean separatorLastLine;
	protected boolean firstLine;

	public FileAppender(File file, boolean appendIfExists) throws IOException {
		
		File dir = file.getParentFile();

		///recover number of lines, it append is true
		if (appendIfExists && file.exists()) {
			
			this.rowCount = getExistingRowCount(file);
			
		} else {
			
			this.rowCount = 0;
			
		}
		
		//make sure directory exists
		if (!dir.exists()) {
			
			dir.mkdirs();
			
		}

		//output buffer
		this.bufferedWriter = new BufferedWriter(new FileWriter(file, appendIfExists));
		
		this.lineSeparator = "" + (char) 13 + (char) 10;
		this.separatorLastLine = true;
		this.firstLine = true;
		
	}
	
	public FileAppender setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
		return this;
	}
	
	public FileAppender setSeparatorLastLine(boolean separatorLastLine) {
		this.separatorLastLine = separatorLastLine;
		return this;
	}
	
	public void append(RowDataSet data, RowStructure rowStructure) {
		
		try {
			
			if (firstLine) {
				firstLine = false;
			} else {
				bufferedWriter.write(lineSeparator);
			}
			bufferedWriter.write(rowStructure.createLine(data, this));
			bufferedWriter.flush();
			rowCount++;
			
		} catch (Exception e) {
			
			e.printStackTrace();
			throw new RuntimeException(e);
			
		}
		
	}

	public void close() {
		
		try {
			
			if (separatorLastLine) {
				bufferedWriter.write(lineSeparator);
			}
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (Exception e) {
			
			e.printStackTrace();
			throw new RuntimeException(e);
			
		}
		
	}

	public int count() {
		return rowCount;
	}

	private int getExistingRowCount(File file) throws IOException {
		
		FileReader fileReader = new FileReader(file);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		int i = 0;
		while(bufferedReader.readLine() != null) {
			i ++;
		}
		fileReader.close();
		bufferedReader.close();
		return i;
		
	}

	public Object result() {
		return null;
	}

	@Override
	public String toString() {
		return "FileAppender [rowCount="
				+ rowCount + ", lineSeparator=" + lineSeparator
				+ ", separatorLastLine=" + separatorLastLine + ", firstLine="
				+ firstLine + "]";
	}

}
