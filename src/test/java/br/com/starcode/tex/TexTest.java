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

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;


public class TexTest {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	public void test2() throws IOException {
		
		//IEBDataBaseMgr facadeEBDataBase = (IEBDataBaseMgr4) br.com.autbank.framework.impl.ComponentFactory.componentInstance.getProvidedInterface("IEBDataBaseMgr");
		//escreverLinha("", new ITOResultSetSourceMapper(facadeEBDataBase, null));
		//escreverLinha("", new MapSourceMapper(valueMap));
		
	}
	
	@Test
	public void test() throws IOException {
		
		//parâmetros
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", "João Batista");
		params.put("age", 30);
		
		//create tex
		Tex e = new Tex(params);
		
		//define arquivo de saída
		e.registerFileAppender(new File("c:\\teste-output.txt"), false);
		
		//carrega layout de exportação
		e.registerLayout(getClass(), "layout-01.xml");
		
		//exporta dados do cabeçalho
		e.exportHeader(getDataMap());
		
		//exporta cabeçalho novamente
		e.exportHeader(getDataMap());
		
		//exporta linha de detalha sem name específico
		e.exportDetail(getDataMap());
		
		//exporta linha de detalha alternativa com name
		e.exportRow("alternativa", getDataMap());
		
		//exporta linha de detalha alternativa com name
		e.exportRow("separado", getDataMap());
		
		//esporta detalhe principal novamente
		e.exportDetail(getDataMap());
		
		//exporta rodapé
		e.exportFooter(getDataMap());
		
		//encerra
		e.close();
		
		System.out.println("Exporting duration: " + e.getElapsedTime());
		
	}
	
	Map<String, Object> getDataMap() {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("LINHAARQUIVO", "LINHA0123456789LINHA0123456789LINHA0123456789LINHA0123456789LINHA0123456789LINHA0123456789LINHA0123456789");
		Calendar c = Calendar.getInstance();
		c.set(2013, 2, 28);
		map.put("dataNascimento", c.getTime());
		map.put("saldoConta", new BigDecimal("7899087.5145"));
		map.put("saldoConta2", new BigDecimal("-234.51"));
		return map;
		
	}
	
}
