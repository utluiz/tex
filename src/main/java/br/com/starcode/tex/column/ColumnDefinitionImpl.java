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


public class ColumnDefinitionImpl implements ColumnDefinition {

	protected String type;
	protected String source;
	protected String value;
	protected int position;
	protected String format;
	protected String alignment;
	protected int width;
	protected String fillChar;
	protected String decimalSeparator;
	protected String groupingSeparator;
	
	public ColumnDefinitionImpl(
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
		this.type = type;
		this.source = source;
		this.value = value;
		this.position = position;
		this.format = format;
		this.alignment = alignment;
		this.width = width;
		this.fillChar = fillChar;
		this.decimalSeparator = decimalSeparator;
		this.groupingSeparator = groupingSeparator;
	}
	
	public String getType() {
		return type;
	}
	
	public String getSource() {
		return source;
	}
	
	public int getPosition() {
		return position;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getFormat() {
		return format;
	}
	
	public String getAlignment() {
		return alignment;
	}
	
	public int getWidth() {
		return width;
	}
	
	public String getFillChar() {
		return fillChar;
	}
	
	public String getDecimalSeparator() {
		return decimalSeparator;
	}
	
	public String getGroupingSeparator() {
		return groupingSeparator;
	}
	
	public boolean isCounter() {
		return source.equals("counter");
	}

	@Override
	public String toString() {
		return "ColumnDefinitionImpl [type=" + type + ", source=" + source
				+ ", value=" + value + ", position=" + position + ", format="
				+ format + ", alignment=" + alignment + ", width=" + width
				+ ", fillChar=" + fillChar + ", decimalSeparator="
				+ decimalSeparator + ", groupingSeparator=" + groupingSeparator
				+ "]";
	}
	
}
