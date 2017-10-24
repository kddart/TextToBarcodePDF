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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.datamatrix.DataMatrixWriter;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.pdf417.PDF417Writer;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * 
 * @author Brian Pearce
 *
 */
public class BarcodeUtilsLite {

	/**
	 * @param barcodeFormat
	 * @param barcode
	 * @param barcodeWidth
	 * @param barcodeHeight
	 * @return
	 * @throws IllegalArgumentException
	 * @throws WriterException
	 * @throws IOException
	 */
	static public BufferedImage createBarcodeImage(
				BarcodeFormat barcodeFormat,
				String barcode, 
				int barcodeWidth,
				int barcodeHeight)
	throws IllegalArgumentException, WriterException, IOException 
	{
		Writer writer = createBarcodeWriter(barcodeFormat);
		
		if (writer == null) {
			throw new IllegalArgumentException(barcodeFormat.name());
		}
		
		if (barcode == null) {
			return null;
		}
		
		try {
		BitMatrix encoded = writer.encode(barcode, barcodeFormat, barcodeWidth, barcodeHeight);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		MatrixToImageWriter.writeToStream(encoded, "png", os); //$NON-NLS-1$
		os.close();
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(os.toByteArray()));
		
		return img;
		
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static Writer createBarcodeWriter(BarcodeFormat barcodeFormat) {
		Writer writer = null;
		switch (barcodeFormat) {
		case AZTEC:
			writer = new AztecWriter();
			break;
		case CODABAR:
			break;
		case CODE_128:
			writer = new Code128Writer();
			break;
		case CODE_39: // Only supports A-Z 0-9 $ / + - % . , SPACE
			//writer = new Code39Writer(); // Won't support some chars: Bad contents: TR/22cNsoy5rG9
			break;
		case CODE_93: // Only supports A-Z 0-9 $ / + - % .SPACE
			// writer = new MultiFormatWriter(); // No encoder available for format CODE_93
			break;
		case DATA_MATRIX: // 2D
			writer = new DataMatrixWriter(); 
			// Can't find a symbol arrangement that matches the message. Data codewords: 13
			break;
		case EAN_13:
			break;
		case EAN_8:
			break;
		case ITF:
			break;
		case MAXICODE:
			break;
		case PDF_417:
			writer = new PDF417Writer(); // stacked linear (1D-ish)
			break;
		case QR_CODE:
			writer = new QRCodeWriter();
			break;
		case RSS_14:
			break;
		case RSS_EXPANDED:
			break;
		case UPC_A:
			break;
		case UPC_E:
			break;
		case UPC_EAN_EXTENSION:
			break;
		default:
			break;
		}
		return writer;
	}  	
}

