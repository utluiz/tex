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

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.starcode.tex.column.ColumnDefinition;
import br.com.starcode.tex.format.DataFormatter;
import br.com.starcode.tex.layout.Layout;
import br.com.starcode.tex.structure.PositionalRowStructure;
import br.com.starcode.tex.structure.RowStructure;


/**
 * TODO this class should be a sort of plugin ir order to allow extension and new layout builders
 */
public class XMLLayoutBuilder {

	final static Logger logger = LoggerFactory.getLogger(XMLLayoutBuilder.class);
	
	static Layout buildLayout(Tex tex, Element layoutElement, RowStructure defaultRowStructure) {
		
		logger.debug("Preparing new layout...");
		
		//defines id and identification label 
		String id = layoutElement.getName();
		String label = id;
		String name = layoutElement.getAttributeValue("name");
		if (name != null && id.trim().length() > 0) {
			
			id = name;
			label = label + "[" + name + "]";
			
		}
		
		logger.debug("layout label is " + label);
		
		//default row structure for layouts
		RowStructure rowStructure = XMLLayoutBuilder.getRowStructure(tex, layoutElement, label);
		if (rowStructure == null) {
			rowStructure = defaultRowStructure;
		}
		if (rowStructure == null) {
			throw new IllegalArgumentException("Row structure not defined for layout '" + label + "', neither a default row structure was defined in root element!");
		}
		logger.debug("rowStructure is " + rowStructure);
		
		//layout width
		String widthStr = layoutElement.getAttributeValue("width");
		logger.debug("layout width is " + widthStr);

		//layout width is required if positional structure
		if (rowStructure instanceof PositionalRowStructure && (widthStr == null || widthStr.trim().length() == 0)) {
			throw new IllegalArgumentException("Width required for layout '" + label + "!");
		}
		
		//checks width validity
		int width = -1;
		if (widthStr != null) {
			
			try {
				width = Integer.parseInt(widthStr);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Width is invalid for layout '" + label + "!");
			}
			
			if (width < 0) {
				throw new IllegalArgumentException("Width is negative for layout " + label + "!");
			}
			
		}
		
		//read column definitions
		List<ColumnDefinition> columnDefinitionList = XMLLayoutBuilder.buildColumnDefinitions(tex, layoutElement, label, rowStructure, width);
		
		return tex.getComponentFactory().createLayout(id, label, width, rowStructure, columnDefinitionList);
		
	}
	
	static List<ColumnDefinition> buildColumnDefinitions(Tex tex, Element layoutElement, String layoutLabel, RowStructure rowStructure, int layoutWidth) {
		
		logger.debug("Reading column definitions...");
		
		@SuppressWarnings("unchecked")
		Element[] columnElementArray = (Element[]) (layoutElement.getChildren()).toArray(new Element[0]);
		if (columnElementArray.length == 0) {
			throw new IllegalArgumentException("No columns found for layout" + layoutLabel); 
		}
		
		List<ColumnDefinition> definicoes = new ArrayList<ColumnDefinition>();
		for (int i = 0; i < columnElementArray.length; i++) {
			
			Element columnElement = columnElementArray[i];

			//row start position
			int position = 0;
			
			//position is required if structure is positional
			if (rowStructure instanceof PositionalRowStructure) {
				
				//read attribute
				String positionStr = columnElement.getAttributeValue("position");
				logger.debug("position is " + positionStr);

				if (positionStr == null || positionStr.trim().length() == 0) {
					throw new IllegalArgumentException("Position not defined for" + getErrorLocation(layoutLabel, i));
				}
				
				//checks if position is a valid number
				try {
					position = Integer.parseInt(positionStr) - 1;
					if (position < 0) {
						throw new IllegalArgumentException("Negative position value for" + getErrorLocation(layoutLabel, i));
					}
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Invalid position value for" + getErrorLocation(layoutLabel, i));
				}
				
			}
			
			//column type
			String sourceOfValue = columnElement.getName().trim();
			if (!tex.getValueSourceMap().containsKey(sourceOfValue)) {
				throw new IllegalArgumentException("Invalid tag name for" + getErrorLocation(layoutLabel, i) + " Allowed are: " + tex.getValueSourceMap().keySet().toString() + ".");
			}
			logger.debug("tag name is " + sourceOfValue);
			
			//source of value
			String columnType;
			
			if ("fixed".equals(sourceOfValue)) {
				
				columnType = "String";
				
			} else if ("counter".equals(sourceOfValue)) {
				
				columnType = "Integer";
				
			} else if ("timestamp".equals(sourceOfValue)) {
				
				columnType = "Date";
				
			} else {
				
				columnType = columnElement.getAttributeValue("type");
				if (columnType == null || columnType.trim().length() == 0) {
					throw new IllegalArgumentException("Type not defined for" + getErrorLocation(layoutLabel, i));
				}
				
			}
			
			logger.debug("column type is " + columnType);

			//verifica se possui um formatter
			DataFormatter formatter = tex.getFormatter(columnType);
			if (formatter == null) {
				throw new IllegalArgumentException("Formatter not defined for type '" + columnType + "' in" + getErrorLocation(layoutLabel, i));
			}
			logger.debug("formatter is " + formatter);
			
			//column value
			String value = columnElement.getAttributeValue("value");
			if (!"counter".equals(sourceOfValue) && (value == null || value.trim().length() == 0)) {
				throw new IllegalArgumentException("Value not defined for" + getErrorLocation(layoutLabel, i));
			}
			logger.debug("value is " + value);
			
			//format
			String format = columnElement.getAttributeValue("format");
			if (format != null) {
				format = format.trim();
			}
			logger.debug("format is " + format);
			
			//alignment
			String align = columnElement.getAttributeValue("align");
			if (align == null) {
				align = "left";
			}
			if (!align.equalsIgnoreCase("right") && !align.equalsIgnoreCase("left")) {
				throw new IllegalArgumentException("Invalid align value for" + getErrorLocation(layoutLabel, i) + " Options are: 'left' or 'right'.");
			}
			logger.debug("alignment is " + align);
			
			//column width
			String widthStr = columnElement.getAttributeValue("width");
			logger.debug("width is " + widthStr);
			
			//check width vallidity
			int width = -1;
			if (widthStr != null) {
				try {
					width = Integer.parseInt(widthStr);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("Width invalid for" + getErrorLocation(layoutLabel, i));
				}
				if (width < 0) {
					throw new IllegalArgumentException("Width is negative for" + getErrorLocation(layoutLabel, i));
				}
			}
			
			//if positional structure, checks whether width and position of the next element are compatible
			if (rowStructure instanceof PositionalRowStructure) {
				
				//position of next column
				int nextColumnPosition;
				if (i < columnElementArray.length - 1) {
					
					//start position next column
					String nextColumnPositionStr = columnElementArray[i + 1].getAttributeValue("position");

					//required if positional
					if (nextColumnPositionStr == null || nextColumnPositionStr.trim().length() == 0) {
						throw new IllegalArgumentException("Position required for" + getErrorLocation(layoutLabel, i + 1));
					}
					
					//checks validity
					try {
						nextColumnPosition = Integer.parseInt(nextColumnPositionStr);
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException("Position value is invalid for" + getErrorLocation(layoutLabel, i + 1));
					}
					
				} else {
					nextColumnPosition = layoutWidth;
				}
				
				if (width < 0) {
					width = nextColumnPosition - position;
				} else if (width != nextColumnPosition - position) {
					throw new IllegalArgumentException("Invalid size for" + getErrorLocation(layoutLabel, i));
				}
				
			}
			
			//filling character
			String filling = columnElement.getAttributeValue("fill");
			if (filling == null) {
				filling = " ";
			} else if (filling != null && filling.length() != 1) {
				throw new IllegalArgumentException("Invalid fill in" + getErrorLocation(layoutLabel, i) + " Filling should be one single character.");
			}
			logger.debug("filling is " + filling);
			
			//number decimal separator
			String decimalSeparator = columnElement.getAttributeValue("decimal-separator");
			if (decimalSeparator != null && decimalSeparator.length() != 1) {
				throw new IllegalArgumentException("Invalid decimal separator for" + getErrorLocation(layoutLabel, i) + " Decimal separator should be one single character.");
			}
			logger.debug("decimal separator is " + decimalSeparator);
			
			//number grouping separator
			String groupingSeparator = columnElement.getAttributeValue("grouping-separator");
			if (groupingSeparator != null && groupingSeparator.length() != 1) {
				throw new IllegalArgumentException("Invalid grouping separator for" + getErrorLocation(layoutLabel, i) + " Grouping separator should be one single character.");
			}
			logger.debug("grouping separator is " + groupingSeparator);
			
			definicoes.add(tex.getComponentFactory().createColumnDefinition(
					columnType, 
					sourceOfValue,
					position, 
					value,
					format,
					align,
					width,
					filling,
					decimalSeparator,
					groupingSeparator
				));
			
		}
		
		return definicoes;
		
	}
	
	static RowStructure getRowStructure(Tex tex, Element layoutElement, String layoutLabel) {
		
		String structure = layoutElement.getAttributeValue("structure");
		logger.debug("structure is " + structure);
		if (structure == null || structure.trim().length() == 0) {
			
			return tex.getComponentFactory().createPositionalRowStructure();
			
		} else if ("positional".equals(structure.trim())) {
			
			return tex.getComponentFactory().createPositionalRowStructure();
			
		} else if ("separator".equals(structure.trim())) {
			
			String separator = layoutElement.getAttributeValue("separator");
			if (separator == null) {
				throw new IllegalArgumentException("Separator should be defined for layout '" + layoutLabel + "' with structure 'separator'!");
			}
			return tex.getComponentFactory().createSeparatorRowStructure(separator);
			
		} else {
			
			throw new IllegalArgumentException("Structure not defined for layout '" + layoutLabel + "'!");
			
		}
		
	}
	
	static String getErrorLocation(String label, int column) {
		
		return " column " + (column + 1) + " of layout '" + label + "'!";
		
	}
	
}
