/*
    Copyright (C) 2015,2016,2017  Diversity Arrays Technology, Pty Ltd.
    
    Barcode Printer may be redistributed and may be modified under the terms
    of the GNU General Public License as published by the Free Software
    Foundation, either version 3 of the License, or (at your option)
    any later version.
    
    Barcode Printer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with Barcode Printer.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.diversityarrays.dartapps.barcode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import com.diversityarrays.dartapps.barcode.util.HeadingUtil;
import com.diversityarrays.dartapps.barcode.util.Pair;

/**
 * 
 * @author Alex Spence
 *
 */
public class BarcodeFile {

	public final static String CSV_SEP = ",";
	public final static String TSV_SEP = "	";

	public final static String CSV_EXT = ".csv";
	public final static String TSV_EXT = ".tsv";
	
	private final File file;

	public int col;

	public int rowCount;

	public List<String> lines = new ArrayList<>();

	public Integer colCount;
	
	private String separator = CSV_SEP;
	
	private String[] headers;
	
	public BarcodeFile(File file) {
		if (file.getAbsolutePath().trim().toLowerCase().endsWith(TSV_EXT)) {
			separator = TSV_SEP;
		}
		
		this.file = file;
		this.col = 0;
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));

			rowCount = 0;
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}

				// Reading headings
				if (rowCount == 0) {
					headers = line.split(separator);
					colCount = headers.length;

					this.col = HeadingUtil.guessBarcodeColIndex(line);				
				} else { 
					lines.add(line);
				}

				rowCount++;
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getInfo(String line) {
		StringJoiner joiner = new StringJoiner(" ");
		String[] vals = line.split(separator);
		
		for (int i = 0; i < this.colCount; i++) {
			String heading = headers[i];
			
			String value = "";
			if (vals.length > i) {
				value = vals[i];
			}
			
			joiner.add(heading + ": " + value);
		}
		
		return joiner.toString();
	}
	
	public List<Pair<String, String>> getBarcodes() {
		List<Pair<String, String>> result = new ArrayList<>();
		
		for (String line : lines) {
			String[] cols = line.split(separator);	
			String value = cols[col].trim();
			
			String info = getInfo(line);
			
			result.add(new Pair<>(value, info));
		}
		
		return result;
	}

	public File getFile() {
		return file;
	}

}
