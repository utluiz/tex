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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.starcode.tex.column.RowDataSet;
import br.com.starcode.tex.structure.RowStructure;

public class XlsAppender implements Appender {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	private List<String[]> arrayList;
	private File outputFile;
	private boolean append;
	private int rowCount;

	public XlsAppender(File outputFile, boolean append) throws IOException {
		
		///if appending, check previous row count for corret counting of lines
		if (append && outputFile.exists()) {
			
			this.rowCount = getSeqArquivo(outputFile);
			
		} else {
			
			this.rowCount = 0;
			
		}
		
		//make sure output directory exists
		if (!outputFile.getParentFile().exists()) {
			
			outputFile.getParentFile().mkdirs();
			
		}

		this.outputFile = outputFile;
		this.append = append;
		
		arrayList = new ArrayList<String[]>();
		
	}

	public void append(RowDataSet data, RowStructure rowStructure) {
		
		try {
			
			String[] columns = new String[data.getDataSet().size()];
			for (int i = 0; i < columns.length; i++) {
				columns[i] = data.getColumn(i).getFormattedData();
			}
			arrayList.add(columns);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			throw new RuntimeException(e);
			
		}
		
	}

	public void close() {
		
		try {
			
			String[][] arquivoExportadoXls = (String[][]) arrayList.toArray();
			if (append && outputFile.exists()) {
				appendArquivoXls(outputFile, arquivoExportadoXls);
			} else {
				//TODO
				//ManipulaArquivos.criaArquivoXls(arquivoDestino.getAbsolutePath(), "Plan1", arquivoExportadoXls);
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			throw new RuntimeException(e);
			
		}
		
	}

	public int count() {
		return rowCount;
	}

	private int getSeqArquivo(File arquivoDestino) throws IOException {
		
		//TODO
		//String[][] arquivo = ManipulaArquivos.leArquivoXls(arquivoDestino.getAbsolutePath(), 0, 0, 50);
		//return arquivo.length;
		return 0;
		
	}
	
	private void appendArquivoXls(File arquivoDestino, String[][] registros) throws IOException {
		
		/*
		TODO
	    String[][] arquivo = ManipulaArquivos.leArquivoXls(arquivoDestino.getAbsolutePath(), 0, 0, registros[0].length);
		
		String[][] arquivoNovo = new String[arquivo.length + registros.length][registros[0].length];
		System.arraycopy(arquivo, 0, arquivoNovo, 0, arquivo.length);
		System.arraycopy(registros, 0, arquivoNovo, arquivo.length, registros.length);
		
		ManipulaArquivos.criaArquivoXls(arquivoDestino.getAbsolutePath(), "Plan1", arquivoNovo);
		*/
	}

	public Object result() {
		return null;
	}

	@Override
	public String toString() {
		return "XlsAppender [outputFile=" + outputFile + ", append=" + append + ", rowCount=" + rowCount + "]";
	}

}
