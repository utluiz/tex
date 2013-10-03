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
package br.com.starcode.tex.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Return values from a map, given column "value" 
 */
public class MapValueSource implements ValueSource {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected Map<String, Object> map;
	
	public MapValueSource(Map<String, Object> map) {
		setMap(map);
	}

	@Override
	public Object calculateValue(String columnValue) {
		return map.get(columnValue);
	}
	
	/**
	 * You can use to avoid creating a new object every time
	 * @param map Map with values
	 */
	public void setMap(Map<String, Object> map) {
		if (map == null) {
			throw new IllegalArgumentException("Map cannot be null!");
		}
		logger.debug("Updating map");
		this.map = map;
	}

	@Override
	public String toString() {
		return "MapValueSource [map=" + map + "]";
	}

}
