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
package com.diversityarrays.dartapps.barcode.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.diversityarrays.dartapps.barcode.BarcodeFile;

/**
 * 
 * @author Alex Spence
 *
 */
public class DropPanel extends JPanel {

	private DropTarget dropTarget;
	private DropTargetHandler dropTargetHandler;

	private boolean dragOver = false;
	private JLabel message;
	
	private Consumer<List<BarcodeFile>> consumer;

	public DropPanel(Consumer<List<BarcodeFile>> consumer) {
		super(new BorderLayout());
		setLayout(new GridBagLayout());
		message = new JLabel("Drop .tsv or .csv files here...");
		message.setFont(message.getFont().deriveFont(Font.BOLD, 13));
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		add(message);
		this.consumer = consumer;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(400, 50);
	}

	protected DropTarget getMyDropTarget() {
		if (dropTarget == null) {
			dropTarget = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, null);
		}
		return dropTarget;
	}

	protected DropTargetHandler getDropTargetHandler() {
		if (dropTargetHandler == null) {
			dropTargetHandler = new DropTargetHandler();
		}
		return dropTargetHandler;
	}

	@Override
	public void addNotify() {
		super.addNotify();
		try {
			getMyDropTarget().addDropTargetListener(getDropTargetHandler());
		} catch (TooManyListenersException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		getMyDropTarget().removeDropTargetListener(getDropTargetHandler());
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (dragOver) {
			
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setColor(new Color(0, 255, 0, 64));
			g2d.fill(new Rectangle(getWidth(), getHeight()));
			g2d.dispose();
		}
	}

	public List<File> droppedFiles = new ArrayList<>();

	public List<BarcodeFile> barcodeFiles = new ArrayList<>();

	protected void importFiles(final List<File> in) {

		List<File> files = new ArrayList<>();
		for (File dropped : in) {
			if (dropped.getName().toLowerCase().trim().endsWith(BarcodeFile.CSV_EXT)) {
				files.add(dropped);
			}
		}

		if (files.isEmpty()) {
			return;
		}

		droppedFiles = files;

		for (File file : files) {
			BarcodeFile bf = new BarcodeFile(file);
			this.barcodeFiles.add(bf);
		}

		consumer.accept(barcodeFiles);
	}

	protected class DropTargetHandler implements DropTargetListener {
		
		protected void processDrag(DropTargetDragEvent dtde) {
			
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dtde.acceptDrag(DnDConstants.ACTION_COPY);
			} else {
				dtde.rejectDrag();
			}
		}

		@Override
		public void dragEnter(DropTargetDragEvent dtde) {
			processDrag(dtde);
			SwingUtilities.invokeLater(new DragUpdate(true, dtde.getLocation()));
			repaint();
		}

		@Override
		public void dragOver(DropTargetDragEvent dtde) {
			processDrag(dtde);
			SwingUtilities.invokeLater(new DragUpdate(true, dtde.getLocation()));
			repaint();
		}

		@Override
		public void dropActionChanged(DropTargetDragEvent dtde) {
		}

		@Override
		public void dragExit(DropTargetEvent dte) {
			SwingUtilities.invokeLater(new DragUpdate(false, null));
			repaint();
		}

		@Override
		public void drop(DropTargetDropEvent dtde) {
			SwingUtilities.invokeLater(new DragUpdate(false, null));
			Transferable transferable = dtde.getTransferable();
			
			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dtde.acceptDrop(dtde.getDropAction());
				
				try {
					
					@SuppressWarnings("unchecked")
					List<File> transferData = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
					if (transferData != null && transferData.size() > 0) {
						importFiles(transferData);
						dtde.dropComplete(true);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				dtde.rejectDrop();
			}
		}
	}

	public class DragUpdate implements Runnable {
		private boolean dragOver;

		public DragUpdate(boolean dragOver, Point dragPoint) {
			this.dragOver = dragOver;
		}

		@Override
		public void run() {
			DropPanel.this.dragOver = dragOver;
			DropPanel.this.repaint();
		}
	}
}
