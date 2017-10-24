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
package com.diversityarrays.dartapps.barcode.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.diversityarrays.dartapps.barcode.BarcodeFile;
import com.diversityarrays.dartapps.barcode.models.BarcodeFileTableModel;
import com.diversityarrays.dartapps.barcode.util.BarcodeUtilsLite;
import com.diversityarrays.dartapps.barcode.util.Pair;
import com.diversityarrays.kdxplore.ui.Toast;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * 
 * @author Alex Spence
 *
 */
public class BarcodePrinterPanel extends JPanel {

	private Consumer<String> warnUser = new Consumer<String>() {
		@Override
		public void accept(String t) {
			new Toast(fileTable, t, Toast.SHORT);
		}		
	};

	private DropPanel dropPanel = new DropPanel(new Consumer<List<BarcodeFile>>() {
		@Override
		public void accept(List<BarcodeFile> t) {
			simpleFileTableModel.addData(t);
		}	
	});
	
	private BarcodeFileTableModel simpleFileTableModel = new BarcodeFileTableModel(warnUser);

	private JTable fileTable = new JTable(simpleFileTableModel);

	private JTextField dir = new JTextField();

	private JTextField output = new JTextField();

	private Action browse = new AbstractAction("Browse") {
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser f = new JFileChooser();
			f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
			f.showOpenDialog(BarcodePrinterPanel.this);

			File file = f.getSelectedFile();

			if (file != null) {
				processNewDir(file);
			}
		}		
	};

	private Action refresh = new AbstractAction("Refresh") {
		@Override
		public void actionPerformed(ActionEvent e) {
			File file = new File(dir.getText());

			if (file.exists() && file.isDirectory()) {
				processNewDir(file);
			}
		}		
	};

	private MouseAdapter mouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 2) {
				int[] tableRows = fileTable.getSelectedRows();

				if (null != tableRows && tableRows.length > 0) {
					for (int row : tableRows) {
						if (fileTable.getRowCount() > row) {
							BarcodeFile file = simpleFileTableModel.getBarcodeFileAt(row);
							try {
								Desktop.getDesktop().open(file.getFile());
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				}		
			}
		}
	};

	private Action save = new AbstractAction("Save") {
		@Override
		public void actionPerformed(ActionEvent e) {

			Boolean success = false;
			String filePath = output.getText();

			if (filePath != null) {
				// Correct extension
				if (! filePath.endsWith(".pdf")) {
					filePath += ".pdf";
				}

				File outFile = new File(filePath);

				// Already exist?
				if (outFile.exists()) {
					warnUser.accept(filePath + " already exists!");
				}

				// Dirs exist?
				String parent = outFile.getParent();

				if (parent != null) {
					File parentDir = new File(parent);
					if (! parentDir.exists()) {
						parentDir.mkdirs();
					} else if (! parentDir.isDirectory()) {
						warnUser.accept(parentDir.getAbsolutePath() + " is not a directory!");
					}
				}

				//				try {
				////					outFile.createNewFile();
				//				} catch (IOException e1) {
				//					e1.printStackTrace();
				//				}

				success = processOutFile(outFile);
			}
		}
	};

	private JPanel colourPane = new JPanel(new BorderLayout());

	private final JFrame parent;

	public BarcodePrinterPanel(JFrame parent) {
		super(new BorderLayout());

		this.parent = parent;

		// Working dir selector Box
		Box topBox = Box.createHorizontalBox();
		topBox.add(new JLabel("Working Directory:"));
		topBox.add(dir);
		topBox.add(new JButton(browse));
		topBox.add(new JButton(refresh));
		dir.setEditable(false);

		// Output file selector Box
		Box bottomBox = Box.createHorizontalBox();
		bottomBox.add(new JLabel("Output File:"));
		bottomBox.add(output);
		bottomBox.add(new JButton(save));

		Box middleBox = Box.createVerticalBox();
		middleBox.add(new JScrollPane(fileTable));
		middleBox.add(dropPanel);

		this.add(topBox, BorderLayout.NORTH);
		this.add(bottomBox, BorderLayout.SOUTH);

		this.add(middleBox, BorderLayout.CENTER);

		String currentDir = System.getProperty("user.dir");
		String homeDir = System.getProperty("user.home");

		fileTable.addMouseListener(mouseListener);

		File file = new File(currentDir);
		processNewDir(file);

		colourPane.setBackground(new Color(0, 255, 128, 20));
		JLabel loadingLabel = new JLabel("   Loading...");
		loadingLabel.setFont(new Font("arial", Font.BOLD, 16));
		
		colourPane.add(loadingLabel, BorderLayout.CENTER);
//		colourPane.setOpaque(false);
		
		parent.setGlassPane(colourPane);
		parent.getGlassPane().setVisible(false);

		String date = simpleDateFormat.format(new Date());
		this.output.setText(homeDir + "/Barcodes_" + date + ".pdf");		
	}

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm");

	private void processNewDir(File file) {
		List<BarcodeFile> result = new ArrayList<>();
		dir.setText(file.getAbsolutePath());

		if (file.isDirectory()) {			
			for (File fileFound : file.listFiles()) {
				String fileName = fileFound.getAbsolutePath().toLowerCase().trim();
				if (fileName.endsWith(".csv") || fileName.endsWith(".tsv")) {
					BarcodeFile bf = new BarcodeFile(fileFound);
					result.add(bf);
				}
			}		
		}

		simpleFileTableModel.setData(result);
	}

	private final Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 10);

	private final int width = 200;

	private final int height = 80;

	private final static int DEBUG = 1;

	private Boolean processOutFile(File outFile) {		

		List<Boolean> error = new ArrayList<>();
		parent.getGlassPane().setVisible(true);

		Runnable runnable = new Runnable() {

			@Override
			public void run() {

				try {
					Document document = new Document();
					PdfWriter.getInstance(document, new FileOutputStream(outFile));

					document.open();

					// Gather barcodes
					List<BarcodeFile> files = simpleFileTableModel.getOutFiles();

					int i = 0;
					for (BarcodeFile file : files) {
						List<Pair<String,String>> barcodes = file.getBarcodes();

						for (Pair<String,String> barcode : barcodes) {

							if (barcode.first.contains("\t")) {
								System.out.println("Found faulty barcode: " + barcode.first + " @ line " + i);
							}

							if (DEBUG > 0) {
								System.out.println("Building Barcode Image for: " + barcode.first + " @ line " + i);						
							}

							BufferedImage image = BarcodeUtilsLite.createBarcodeImage(BarcodeFormat.CODE_128, barcode.first, width, height);
							Image outImg = Image.getInstance(image, null);

							if (DEBUG > 0) {
								System.out.println("Success!");						
							}

							document.add(outImg);
							document.add(new Paragraph(barcode.second));

							i++;
						}
					}

					document.close();

					// Finally open the thing
					Desktop.getDesktop().open(outFile);

					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							parent.getGlassPane().setVisible(false);
						}		
					});
				} catch (DocumentException | IOException | IllegalArgumentException | WriterException e) {
					e.printStackTrace();
					error.add(false);
				}
			}
		};

		ExecutorService executor = Executors.newCachedThreadPool();
		executor.submit(runnable);

		return error.isEmpty();
	}

}
