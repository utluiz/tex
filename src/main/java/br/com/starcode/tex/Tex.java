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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.starcode.tex.appender.Appender;
import br.com.starcode.tex.appender.FileAppender;
import br.com.starcode.tex.column.ColumnDefinition;
import br.com.starcode.tex.column.RowDataSet;
import br.com.starcode.tex.format.BigDecimalFormatter;
import br.com.starcode.tex.format.DataFormatter;
import br.com.starcode.tex.format.DateFormatter;
import br.com.starcode.tex.format.IntegerFormatter;
import br.com.starcode.tex.format.OnlyDecimalFormatter;
import br.com.starcode.tex.format.StringFormatter;
import br.com.starcode.tex.layout.Layout;
import br.com.starcode.tex.source.FixedValueSource;
import br.com.starcode.tex.source.MapValueSource;
import br.com.starcode.tex.source.NullValueSource;
import br.com.starcode.tex.source.SingleValueSource;
import br.com.starcode.tex.source.ValueSource;
import br.com.starcode.tex.structure.RowStructure;

/**
 * Text EXporter main class, for creation mainly of text files.
 */
public class Tex {
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Date beginTimestamp;
	private Date currentTimestamp;
	private boolean finished = false;
	private boolean initiated = false;
	
	private Map<String, Object> paramMap;
	private List<Appender> appenderList;
	private Map<String, DataFormatter> formatterMap;
	private Map<String, Layout> layoutMap;
	private Map<String, ValueSource> valueSourceMap;
	
	private TexComponentFactoryImpl componentFactory;
	
	/**
	 * Creates a Text EXporter instance
	 * @param params Map of static params to all layouts
	 * @param componentFactory Factory of Tex components, so you can override them 
	 */
	public Tex(Map<String, Object> params) {
	
		this(params, new TexComponentFactoryImpl());
		
	}
	
	/**
	 * Creates a Text EXporter instance
	 * @param params Map of static params to all layouts
	 * @param componentFactory Factory of Tex components, so you can override them 
	 */
	public Tex(Map<String, Object> params, TexComponentFactoryImpl componentFactory) {
		
		this.beginTimestamp = new Date();

		this.paramMap = params;
		this.appenderList = new ArrayList<Appender>();
		this.formatterMap = new HashMap<String, DataFormatter>();
		this.layoutMap = new HashMap<String, Layout>();
		this.valueSourceMap = new HashMap<String, ValueSource>();
		
		//default formatters
		this.formatterMap.put("String", new StringFormatter());
		this.formatterMap.put("Integer", new IntegerFormatter());
		this.formatterMap.put("BigDecimal", new BigDecimalFormatter());
		this.formatterMap.put("OnlyDecimal", new OnlyDecimalFormatter());
		this.formatterMap.put("Date", new DateFormatter());
		
		//default value sources
		this.valueSourceMap.put("column", null);
		this.valueSourceMap.put("fixed", new FixedValueSource());
		this.valueSourceMap.put("param", new MapValueSource(paramMap));
		this.valueSourceMap.put("counter", new NullValueSource());
		this.valueSourceMap.put("timestamp", new SingleValueSource(beginTimestamp));
		
		this.componentFactory = componentFactory;
		
		logger.debug("Text started successfully!");

	}
	
	/**
	 * Checks if tex can be configured (export process not initiated nor finished)
	 * @param type Type of blocked configuration only for user information
	 */
	protected void checkConfigureState(String type) {
		
		if (finished) {
			throw new RuntimeException("Tex already finished!");
		} else if (initiated) {
			throw new RuntimeException("Cannot register a new " + type + " because you already started exporting!");
		}
		
	}
	
	/**
	 * Registers a new formatter for some type. You can replace any default formatter.
	 * @param columnType Name for new column type or existing one
	 * @param formatter Instance of formatter
	 */
	public Tex registerFormatter(String columnType, DataFormatter formatter) {
		
		checkConfigureState("Formatter");
		formatterMap.put(columnType, formatter);
		logger.debug("Added formatter " + formatter);
		return this;
		
	}
	
	/**
	 * Registers a new value source, who tex will call when a column name matches this in order to obtain the calculated value of column.
	 * Note the same instance will be used in every row.
	 * @param name Name of the value source (tag name in XML). There is two reserved names: 'column' and 'counter'; in the first case tex will use the value source passed by the user for a specific row and in the another case each appender will give the number of the current row.
	 */
	public Tex registerValueSource(String name, ValueSource valueSource) {
		
		checkConfigureState("Appender");
		if (name == null) {
			throw new IllegalArgumentException("Value source name cannot be null!");
		}
		if (valueSource == null) {
			throw new IllegalArgumentException("Value source cannot be null!");
		}
		if (name.equals("column")) {
			throw new IllegalArgumentException("Value source cannot override name 'column'!");
		}
		valueSourceMap.put(name, valueSource);
		logger.debug("Added value source " + valueSource);
		return this;
		
	}
	
	/**
	 * Registers a new appender to output content
	 * @see Appender
	 * @param appender Appender
	 * @throws FalhaExportarArquivoException
	 */
	public Appender registerAppender(Appender appender) throws IllegalArgumentException {
		
		checkConfigureState("Appender");
		if (appender == null) {
			throw new IllegalArgumentException("Appender cannot be null!");
		}
		this.appenderList.add(appender);
		logger.debug("Added appender " + appender);
		return appender;
		
	}
	
	/**
	 * Registers a file appender to output
	 * @param file Output file
	 * @param appendIfExists If file already exists, append output to previous content
	 * @throws IOException If there is some exception reading or writing
	 */
	public Appender registerFileAppender(File file, boolean appendIfExists) throws IOException {
		
		checkConfigureState("Appender");
		if (file == null) {
			throw new IllegalArgumentException("File cannot be null!");
		}
		return registerAppender(new FileAppender(file, appendIfExists));
		
	}
	
	/**
	 * Registers a new layout
	 * @param baseClass Base class, looks at package to find the layout file.
	 * @param relativePath Path to layout, relative to package class. e.g.: "xmlmap/layout.xml".
	 */
	public Tex registerLayout(Class<?> baseClass, String relativePath) throws IOException {
		
		return registerLayout(baseClass, relativePath, null);
		
	}
	
	/**
	 * Registers a new layout
	 * @param baseClass Base class, looks at package to find the layout file.
	 * @param relativePath Path to layout, relative to package class (e.g.: "xmlmap/layout.xml"). 
	 * @param defaultRowStructure Default Row Structure, used when some layout does not define it.
	 */
	public Tex registerLayout(Class<?> baseClass, String relativePath, RowStructure defaultRowStructure) throws IOException {
		
		checkConfigureState("Layout");
		
		if (relativePath == null || relativePath.length() == 0) {
			throw new IllegalArgumentException("Path cannot be null or empty!");
		}
		
		//calculate absolute path
		String absolutePath = "/" + baseClass.getPackage().getName().replace('.', '/') + "/" + relativePath;
		
		//try read layout file
		logger.debug("Loading resource at " + absolutePath);
		InputStream url = baseClass.getResourceAsStream(absolutePath);
		if (url == null) {
			throw new FileNotFoundException("Layout file not found: '" + absolutePath + "'!");
		}
		
		//read XML
		Element layoutRoot;
		try {
			
			logger.debug("Loading xml!");
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build(new StringReader(Util.readContent(url, "UTF-8")));
			layoutRoot = document.getRootElement();
			
		} catch (JDOMException e) {

			logger.error("Can't read xml!", e);
			throw new RuntimeException(e.getLocalizedMessage(), e);
			
		}
		
		//get default type for file structure
		if (defaultRowStructure == null) {
			defaultRowStructure = XMLLayoutBuilder.getRowStructure(this, layoutRoot, layoutRoot.getName());
		}
		
		//read layout elements
		@SuppressWarnings("unchecked")
		List<Element> children = (List<Element>) layoutRoot.getChildren();
		if (children.isEmpty()) {
			throw new IllegalArgumentException("File does not contain at least one layout!");
		}
		
		for (Iterator<Element> iterator = children.iterator(); iterator.hasNext();) {

			//read one layout
			Layout layout = XMLLayoutBuilder.buildLayout(this, iterator.next(), defaultRowStructure);
			
			//ckeck if it already exists
			if (layoutMap.containsKey(layout.getId())) {
				throw new IllegalArgumentException("Layout '" + layout + "' duplicado!");
			}
			
			//adds to map
			layoutMap.put(layout.getId(), layout);
			logger.debug("Added layout " + layout);
		}
		
		return this;
		
	}
	
	/**
	 * Ends export process and closes resources (e.g.: file appenders)
	 */
	public void close() {
		
		if (finished) {
			throw new RuntimeException("Tex already finished!");
		}
		finished = true;
		for (Iterator<Appender> iterator = appenderList.iterator(); iterator.hasNext();) {
			
			Appender appender = iterator.next();
			appender.close();
			
		}
		currentTimestamp = new Date();
		logger.debug("Tex finished in " + getElapsedTime() + "ms");
		
	}
	
	/**
	 * Duration of export in miliseconds
	 * @return
	 */
	public long getElapsedTime() {
		if (!finished) {
			currentTimestamp = new Date();
		}
		return currentTimestamp.getTime() - beginTimestamp.getTime();
	}
	
	public List<Appender> getAppenderList() {
		return appenderList;
	}
	
	public DataFormatter getFormatter(String columnType) {
		return (DataFormatter) formatterMap.get(columnType);
	}
	
	public Map<String, DataFormatter> getFormatterList() {
		return formatterMap;
	}
	
	public Map<String, Object> getParamMap() {
		return paramMap;
	}
	
	public Map<String, ValueSource> getValueSourceMap() {
		return valueSourceMap;
	}
	
	public TexComponentFactoryImpl getComponentFactory() {
		return componentFactory;
	}
	
	/**
	 * Exports one more row to the output through appenders
	 * @param layoutId Name of layout
	 * @param valueSource Source of current row data
	 */
	public void exportRow(String layoutId, ValueSource rowValueSource) {
		
		if (finished) {
			throw new RuntimeException("Tex already finished!");
		}
		initiated = true;
		
		Layout layout = layoutMap.get(layoutId);
		if (layout == null) {
			throw new RuntimeException("Layout '" + layoutId + " not found!");
		}
		
		logger.debug("Exporting row with layout " + layout);
		
		List<ColumnDefinition> columnDefinitionList = layout.getColumnDefinitionList();
		RowDataSet dataSet = componentFactory.createRowDataSet(layout); 
		logger.debug("Calculating values");
		for (int i = 0; i < columnDefinitionList.size(); i++) {
			
			ColumnDefinition columnDefinition = columnDefinitionList.get(i);
			ValueSource valueSource;
			if ("column".equals(columnDefinition.getSource())) {
				
				valueSource = rowValueSource;
				
			} else {
				
				//param map should validate if entry exists, other maps not because user could have null columns 
				if ("param".equals(columnDefinition.getSource()) && !paramMap.containsKey(columnDefinition.getValue())) {
					throw new RuntimeException("Entry '" + columnDefinition.getValue() + "' not defined in params map! Check column " + (i + 1) + " from layout '" + layout.getLabel() + "'");
				}
				
				valueSource = valueSourceMap.get(columnDefinition.getSource());
				
			}

			//get real value (except for counters)
			Object realValue = valueSource.calculateValue(columnDefinition.getValue());
			
			//add to dataset
			try {
				dataSet.addColumn(realValue, columnDefinition, getFormatter(columnDefinition.getType()));
			} catch (ClassCastException e) {
				throw new RuntimeException("Error formatting column " + (i + 1) + " from layout '" + layout.getLabel() + "'", e);
			}
			
		}
		
		//call appenders
		logger.debug("Appending...");
		for (Iterator<Appender> i = getAppenderList().iterator(); i.hasNext();) {
			
			Appender appender = i.next();
			dataSet.updateCounters(appender.count());
			appender.append(dataSet, layout.getRowStructure());
			
		}
		logger.debug("Row exported!");
			
	}
	
	public void exportHeader(ValueSource sm) {
		exportRow("header", sm);
	}
	
	public void exportFooter(ValueSource sm) {
		exportRow("footer", sm);
	}
	
	public void exportDetail(ValueSource sm) {
		exportRow("detail", sm);
	}
	
	public void exportRow(String layoutId, Map<String, Object> valueMap) {
		exportRow(layoutId, new MapValueSource(valueMap));
	}
	
	public void exportHeader(Map<String, Object> map) {
		exportRow("header", map);
	}
	
	public void exportFooter(Map<String, Object> map) {
		exportRow("footer", map);
	}
	
	public void exportDetail(Map<String, Object> map) {
		exportRow("detail", map);
	}
	
}
