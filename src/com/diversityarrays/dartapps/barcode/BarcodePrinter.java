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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import com.diversityarrays.dartapps.barcode.ui.BarcodePrinterPanel;

/**
 * 
 * @author Alex Spence
 *
 */
public class BarcodePrinter {

	private final static String VERSION_STRING = "1.0.1";
	
	private final static String TITLE = "Barcode Printer";
	
	private final static JFrame mainFrame = new JFrame(TITLE + " - " + VERSION_STRING );
	
	private final static Action aboutAction = new AbstractAction("About") {
		@Override
		public void actionPerformed(ActionEvent e) {
			BarcodePrinterAbout.showAbout(mainFrame);
		}	
	};
	
	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		
		BarcodePrinterPanel panel = new BarcodePrinterPanel(mainFrame);
		mainFrame.getContentPane().add(panel, BorderLayout.CENTER);
		
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu(TITLE);
		menu.add(aboutAction);
		menuBar.add(menu);
		mainFrame.setJMenuBar(menuBar);
		
		mainFrame.pack();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
	}
	
}
