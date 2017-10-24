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
package com.diversityarrays.dartapps.barcode.util;

/**
 * 
 * @author Alex Spence
 *
 */
public class HeadingUtil {

	public static String[] NAMES = {
			"barcode"
	};
	
	public static int guessBarcodeColIndex(String line) {
		int result = 0;
		
		String[] cols = line.split(",");
		
		boolean found = false;
		
		int count = 0;
		for (String col : cols) {
			for (String name : NAMES) {
				if (! found && col.toLowerCase().trim().contains(name)) {
					result = count;
				}
			}
			count++;
		}
		
		return result;
	}	
}
