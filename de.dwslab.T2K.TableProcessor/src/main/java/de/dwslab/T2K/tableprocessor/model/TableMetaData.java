/**
 * Copyright (C) 2015 T2K-Team, Data and Web Science Group, University of
							Mannheim (t2k@dwslab.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dwslab.T2K.tableprocessor.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

import au.com.bytecode.opencsv.CSVReader;

public class TableMetaData {

	private String url;
	private String tld;
	private String file;
	private int numCols;
	private int numRows;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTld() {
		return tld;
	}
	public void setTld(String tld) {
		this.tld = tld;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public int getNumCols() {
		return numCols;
	}
	public void setNumCols(int numCols) {
		this.numCols = numCols;
	}
	public int getNumRows() {
		return numRows;
	}
	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}
	
	public static TableMetaData fromStringArray(String[] values) {
	    TableMetaData m = null;
	    
	    try {
	        m = new TableMetaData();
	        
	        m.setUrl(values[0]);
	        m.setTld(values[1]);
	        m.setNumCols(Integer.parseInt(values[2]));
	        m.setNumRows(Integer.parseInt(values[3]));
	        m.setFile(values[5]);
        } catch (Exception e) {
            e.printStackTrace();
        }

	    return m;
	}
	
	public static Collection<TableMetaData> readFromFile(String fileName) {
	    Collection<TableMetaData> result = new LinkedList<>();
	    
	    try {
	        FileInputStream fi = new FileInputStream(new File(fileName));
	        InputStream inStream = null;
	        
	        if(fileName.endsWith("gz")) {
	            inStream = new GZIPInputStream(fi);
	        } else {
	            inStream = fi;
	        }
	        
	        CSVReader r = new CSVReader(new BufferedReader(new InputStreamReader(inStream)), '|');
	        
	        String[] values = null;
	        
	        while((values = r.readNext()) != null) {
	            TableMetaData m = TableMetaData.fromStringArray(values);
	            
	            if(m!=null) {
	                result.add(m);
	            }
	        }
	        
	        r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
	}
}
