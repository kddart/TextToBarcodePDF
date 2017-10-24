/*
    Copyright (C) 2015,2016,2017  Diversity Arrays Technology, Pty Ltd.
    
    TextToBarcodePDF may be redistributed and may be modified under the terms
    of the GNU General Public License as published by the Free Software
    Foundation, either version 3 of the License, or (at your option)
    any later version.
    
    TextToBarcodePDF is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with TextToBarcodePDF.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.diversityarrays.dartapps.barcode.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.table.AbstractTableModel;

import com.diversityarrays.dartapps.barcode.BarcodeFile;

/**
 * 
 * @author Alex Spence
 *
 */
public class BarcodeFileTableModel extends AbstractTableModel {

	private List<BarcodeFile> files = new ArrayList<>();
	
	private Map<BarcodeFile,Boolean> useMap = new HashMap<>();
	
	private final Consumer<String> warnUser;
	
	public BarcodeFileTableModel(Consumer<String> warnUser) {
		this.warnUser = warnUser;
	}
	
	public void setData(List<BarcodeFile> files) {
		this.files = files;
		useMap.clear();
		
		this.fireTableDataChanged();
	}
	
	private static String[] COLS = {
	"File Name",
	"Barcodes",
	"Col",
	"Use?"
	};
	
	@Override
	public String getColumnName(int col) {
		return COLS[col];
	}
	
	@Override
	public int getRowCount() {
		return files.size();
	}

	@Override
	public int getColumnCount() {
		return COLS.length;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		
		if (col == 3 || col == 2) {
			return true;
		}
		
		return false;
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		
		if (col == 3) {
			return Boolean.class;
		}
		
		return Object.class;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		BarcodeFile file = files.get(rowIndex);

		if (file != null) {
			switch (columnIndex) {
			case 0 :
				return file.getFile().getPath();
			case 1 :
				return file.rowCount;
			case 2 :
				return file.col;
			case 3 :
				return useMap.get(file) == null ? false : useMap.get(file);
			}
		}
		
		return null;
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {

		BarcodeFile file = files.get(rowIndex);

		if (file != null) {
			switch (columnIndex) {
			case 2 :
				processNewCol(file, value);
				break;
			case 3 :
				useMap.put(file, (Boolean) value);
				break;
			}
		}		
	}

	private void processNewCol(BarcodeFile file, Object value) {
		
		try {
			Integer result = Integer.parseInt(String.valueOf(value));
			if (file.colCount > result) {
				file.col = result;
			} else {
				warnUser.accept("There are not " + result + " columns in " + file.getFile().getName());
			}
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
			warnUser.accept("'String.valueOf(value)'" + " is not a valid column index");
		}		
	}

	public List<BarcodeFile> getOutFiles() {
		return files.stream()
				.filter(f -> useMap.get(f) != null && useMap.get(f))
				.collect(Collectors.toList());
	}

	public BarcodeFile getBarcodeFileAt(int row) {
		return files.get(row);
	}

	public void addData(List<BarcodeFile> t) {
		this.files.addAll(t);
		
		this.fireTableDataChanged();
	}
}
