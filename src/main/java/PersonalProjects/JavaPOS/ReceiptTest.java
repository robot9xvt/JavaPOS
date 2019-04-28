package PersonalProjects.JavaPOS;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import java.io.File;


import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//StarReceiptTest.java
//This file contains sample code illustrating how to use the POSPrinter class to
//control your Star printer.  The basic printing and status querying mechanisms
//are used here.  For more advanced usage of the POSPrinter class, see the 
//JavaPOS specification.

//usage instructions - Windows
//1. compile from command line - javac -classpath jpos113-controls.jar;jcl.jar StarReceiptTest.java
//2. execute from command line - java -classpath .;starjavapos.jar;ioconnection.jar;jpos113-controls.jar;jcl.jar;xercesimpl.jar;xml-apis.jar StarReceiptTest

//usage instructions - Linux 32bit
//1. compile from command line - javac -classpath jpos113-controls.jar:jcl.jar StarReceiptTest.java
//2. execute from command line - java -classpath .:starjavapos.jar:ioconnection.jar:jpos113-controls.jar:jcl.jar:xercesimpl.jar:xml-apis.jar StarReceiptTest

//usage instructions - Linux 64bit
//1. compile from command line - javac -classpath jpos113-controls.jar:jcl.jar StarReceiptTest.java
//2. execute from command line - java -classpath .:starjavapos.jar:ioconnection.jar:jpos113-controls.jar:jcl.jar:xercesimpl.jar:xml-apis.jar StarReceiptTest

//NOTE: CHANGE THE PRINTER NAME IN THE printer.open STATEMENT BELOW TO MATCH YOUR CONFIGURED DEVICE NAME

import jpos.JposConst;
import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;

import jpos.events.ErrorEvent;
import jpos.events.ErrorListener;
import jpos.events.OutputCompleteEvent;
import jpos.events.OutputCompleteListener;
import jpos.events.StatusUpdateEvent;
import jpos.events.StatusUpdateListener;
import jpos.profile.ProfileFactory;

public class ReceiptTest implements OutputCompleteListener, StatusUpdateListener, ErrorListener {
	static Logger loggerReceiptTest = LogManager.getLogger(ReceiptTest.class);

	
	public void outputCompleteOccurred(OutputCompleteEvent event) {
		System.out.println("OutputCompleteEvent received: time = "
				+ System.currentTimeMillis() + " output id = "
				+ event.getOutputID());
		loggerReceiptTest.info("OutputCompleteEvent received: time = "
				+ System.currentTimeMillis() + " output id = "
				+ event.getOutputID());
	}

	public void statusUpdateOccurred(StatusUpdateEvent event) {
		System.out.println("StatusUpdateEvent : status id = " + event.getStatus());
		loggerReceiptTest.info("StatusUpdateEvent : status id = " + event.getStatus());
	}

	public void errorOccurred(ErrorEvent event) {
		System.out.println("ErrorEvent received: time = "
				+ System.currentTimeMillis() + " error code = "
				+ event.getErrorCode() + " error code extended = "
				+ event.getErrorCodeExtended());
		loggerReceiptTest.info("ErrorEvent received: time = "
				+ System.currentTimeMillis() + " error code = "
				+ event.getErrorCode() + " error code extended = "
				+ event.getErrorCodeExtended());
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
		}
		event.setErrorResponse(JposConst.JPOS_ER_RETRY);
	}

	public void runTest(String[] args) {
		loggerReceiptTest.info("Start runTest");
		/*
		 * If you want to place the jpos.xml file elsewhere on your local file
		 * system then uncomment the following line and specify the full path to
		 * jpos.xml.
		 * 
		 * If you want to place the jpos.xml file on a webserver for access over
		 * the internet then uncomment the second System.setProperty line below
		 * and specify the full URL to jpos.xml.
		 */
		//System.setProperty(	JposPropertiesConst.JPOS_POPULATOR_FILE_PROP_NAME, "jpos.xml");
		// System.setProperty(JposPropertiesConst.JPOS_POPULATOR_FILE_URL_PROP_NAME, "http://some-where-remote.com/jpos.xml");

		// constants defined for convenience sake (could be inlined)

		String ESC = ((char) 0x1b) + "";
		String LF = ((char) 0x0a) + "";
		String SPACES = "                                                                      ";
		String GS = ((char) 0x1D) + "";
		String SP = ((char) 0x20) + "";
		// instantiate a new jpos.POSPrinter object
		POSPrinter printer = new POSPrinter();
		loggerReceiptTest.info("instantiate a new jpos.POSPrinter object");
		try {
			// register for asynchronous OutputCompleteEvent notification
			printer.addOutputCompleteListener(this);

			// register for asynchronous StatusUpdateEvent notification
			printer.addStatusUpdateListener(this);

			// register for asynchronous ErrorEvent notification
			printer.addErrorListener(this);
			loggerReceiptTest.info("open the printer object");
			// open the printer object according to the entry names defined in jpos.xml
			printer.open("POSPrinter_windows_usb_printer_class");
			//printer.open("BTP-U80II");
			loggerReceiptTest.info("claim the printer object");
			// claim exclusive usage of the printer object
			printer.claim(1);
			loggerReceiptTest.info("enable the device for input and output");
			// enable the device for input and output
			printer.setDeviceEnabled(true);
			loggerReceiptTest.info("setAsyncMode");
			printer.setAsyncMode(true);
			//printer.setCharacterSet(POSPrinterConst.PTR_CCS_UNICODE);
			// set map mode to metric - all dimensions specified in 1/100mm units
			loggerReceiptTest.info("setMapMode");
			printer.setMapMode(POSPrinterConst.PTR_MM_METRIC); // unit = 1/100 mm - i.e. 1 cm = 10 mm = 10 * 100 units
			do {
				// register for asynchronous StatusUpdateEvent notification
				// see the JavaPOS specification for details on this

				// printer.checkHealth(JposConst.JPOS_CH_EXTERNAL);
//				printer.checkHealth(JposConst.JPOS_CH_INTERACTIVE);
				
				// check if the cover is open
				if (printer.getCoverOpen() == true) {
					System.out.println("printer.getCoverOpen() == true");
					loggerReceiptTest.info("printer.getCoverOpen() == true");
					// cover open so do not attempt printing
					break;
				}

				// check if the printer is out of paper
				if (printer.getRecEmpty() == true) {
					System.out.println("printer.getRecEmpty() == true");
					loggerReceiptTest.info("printer.getRecEmpty() == true");
					// the printer is out of paper so do not attempt printing
					break;
				}

				// being a transaction
				// transaction mode causes all output to be buffered
				// once transaction mode is terminated, the buffered data is
				// outputted to the printer in one shot - increased reliability
				printer.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_TRANSACTION);

//				if (printer.getCapRecBitmap() == true) {
//					// print an image file
//					try {
//						printer.printBitmap(POSPrinterConst.PTR_S_RECEIPT, "star.gif", POSPrinterConst.PTR_BM_ASIS, POSPrinterConst.PTR_BM_CENTER);
//					} catch (JposException e) {
//						if (e.getErrorCode() != JposConst.JPOS_E_NOEXIST) {
//							// error other than file not exist - propogate it
//							throw e;
//						}
//						// image file not found - ignore this error & proceed
//					}
//				}

				// call printNormal repeatedly to generate out receipt the following
				// JavaPOS-POSPrinter control code sequences are used here
				// ESC + "|cA" -> center alignment
				// ESC + "|4C" -> double high double wide character printing
				// ESC + "|bC" -> bold character printing
				// ESC + "|uC" -> underline character printing
				// ESC + "|rA" -> right alignment
				String centerAlign = ESC + "|cA";
				String doubleHighWide = ESC + "|4C";
				String boldChar = ESC + "|bC";
				String underChar = ESC + "|uC";
				String rightalign = ESC + "|rA";
				String italicChar = ESC + "|iC";
				Font font = new Font("TimesRoman", Font.PLAIN, 28);
		    	BufferedImage wrapTextImage = new BufferedImage(1, 1, 12);
			    Graphics g = wrapTextImage.createGraphics(); 	
			    int wid = g.getFontMetrics(font).getHeight()*4;				
			    BufferedImage logo = ImageIO.read(new File("src/main/java/LOGO02.bmp"));
			    BufferedImage image =  editImage.wrapText("VM+ HNI Green Star 234 Phạm Văn Đồng	!~@T 1, Khu CT2 234 Phạm Văn Đồng!~@P. Cổ Nhuế 1, Q. Bắc Từ Liêm, HN!~@0901 722 196",Math.round(29f/13f*logo.getWidth()), wid, 12, font);	        
		        BufferedImage editedImage = editImage.joinBufferedImage(logo,image,0);
			    System.out.println(editedImage.getWidth() +" "+editedImage.getHeight()+ " ");
		        editImage.saveImage(editedImage,"src/main/java/LOGO02_edit.bmp");
			    System.out.println("DONE");
				loggerReceiptTest.info("Recreate Logo");
			    //printer.setCharacterSet(850);
				printer.setRecLineChars(42);
			    System.out.println(printer.getCharacterSet());
			    System.out.println(printer.getCharacterSetList());

			    System.out.println(printer.getRecLineChars());
			    System.out.println(printer.getRecLineCharsList());
			    System.out.println(printer.getRecLineWidth());
				loggerReceiptTest.info(printer.getCharacterSet());
				loggerReceiptTest.info(printer.getCharacterSetList());
				loggerReceiptTest.info(printer.getRecLineChars());
				loggerReceiptTest.info(printer.getRecLineCharsList());
				loggerReceiptTest.info("Start printing receipt");
				
				//printer.printNormal(POSPrinterConst.PTR_S_RECEIPT,ESC + ((char) 0x6C) + 300);
				//printer.printNormal(POSPrinterConst.PTR_S_RECEIPT,ESC + GS + ((char) 0x74) + 3);
			    printer.printBitmap(POSPrinterConst.PTR_S_RECEIPT, "src/main/java/LOGO02_edit.bmp",POSPrinterConst.PTR_BM_ASIS , POSPrinterConst.PTR_BM_CENTER);//POSPrinterConst.PTR_BM_ASIS
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, boldChar +centerAlign + "HOA DON BAN HANG" + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, boldChar + textHandle.header3Column("Ngay ban: 20/09/2017 12:30","HD:","12345678",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, boldChar + textHandle.header3Column("Quay: 001","NVBH:","09000001",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.lineText(printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.detail4Column("Mat hang" ,"Don gia","SL","Thanh tien",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "Dua hau Sai Gon loai 1 MB" + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.detail4Column("777456789123456","9.999.999","99","99.999.999",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.detail4Column("777456789123456","KM","","-999.999",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "Bot nang Vinh Thuan" + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.detail4Column("0123456789","19.999","3","99.999",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.detail4Column("0123456789","KM","","-9.999",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.lineText(printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, boldChar + textHandle.footer2Column("TONG TIEN","9.999.999.999",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, boldChar + textHandle.footer2Column("TONG GIAM","-9.999.999.999",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, boldChar + textHandle.footer2Column("TONG TIEN THANH TOAN","9.999.999.999",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.footer2Column("    TIEN MAT","9.999.999.999",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.footer2Column("    THE VISA (12345678)","9.999.999.999",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.footer2Column("    THE MASTER (12345678)","9.999.999.999",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.footer2Column("    THE ATM (12345678)","9.999.999.999",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.footer2Column("    VINID","9.999.999.999",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, boldChar + textHandle.footer2Column("TIEN TRA LAI","9.999.999.999",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, italicChar +centerAlign + "(Gia da bao gom thue GTGT)" + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.lineText(printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.footer2Column("ID The Khach Hang","xxxxxxxxxxxxx1234",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.footer2Column("Diem tích (Ty le 1.000VND = 1 diem)","9.999",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.footer2Column("So tham chieu","0153500299900208",printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.lineText(printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, boldChar + centerAlign + "Chi xuat hoa don trong ngay" + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, centerAlign + "Tax invoice will be issued within same day" + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, textHandle.lineText(printer.getRecLineChars()) + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, boldChar + centerAlign + "CAM ON QUY KHACH VA HEN GAP LAI" + LF);
			    printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, centerAlign + "hotline: 18006968 Website: www.vinmart.com" + LF);
			    //printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "123456789012345678901234567890123456789012345678901234567890" + LF);
			    //printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|cA" + ESC + "|4C" + ESC + "|bC" + "Star Grocer" + LF);
//				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|cA" + ESC + "|bC" + "Warrington, United Kingdom" + LF);
//				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|cA" + ESC + "|bC" + "+44 5555 555555" + LF);
//
//				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|uC" + "Qnty Unit Tx Description" +
//						SPACES.substring(0, printer.getRecLineChars() - "Qnty Unit Tx Description".length()) + LF);
//
//				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "   1  830    Soba Noodles" + LF);
//				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "   1  180    Daikon Radish" + LF);
//				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "   1  350    Shouyu Soy Sauce" + LF);
//				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "   1   80    Negi Green Onions" + LF);
//				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "   1  100    Wasabi Horse Radish" + LF);
//				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "   2  200 Tx Hashi Chop Sticks" + LF);
//				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, LF);
//
//				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|rA" + "Subtotal:  2160" + LF);
//				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|rA" + "Tax:         24" + LF);
//				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|rA" + ESC + "|bC" + "Total:     2184" + LF);
//				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|rA" + "Tender:    2200" + LF);
//				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|rA" + ESC + "|bC" + "Change:      16" + LF);
//				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, LF);
//
				if (printer.getCapRecBarCode() == true) {
					// print a Code 3 of 9 barcode with the data "123456789012" encoded
					// the 10 * 100, 60 * 100 parameters below specify the barcode's
					// height and width in the metric map mode (1cm tall, 6cm wide)
					printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "123456789012", POSPrinterConst.PTR_BCS_Code39,
							10 * 100, 60 * 100, POSPrinterConst.PTR_BC_CENTER, POSPrinterConst.PTR_BC_TEXT_BELOW);
				}

				//printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|cA" + ESC + "|4C" + ESC + "|bC" + "Thank you" + LF);

				// the ESC + "|100fP" control code causes the printer to execute
				// a paper cut after feeding to the cutter position
				printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "|100fP");
				loggerReceiptTest.info("Finish printing receipt");
				// terminate the transaction causing all of the above buffered
				// data to be sent to the printer
				printer.transactionPrint(POSPrinterConst.PTR_S_RECEIPT, POSPrinterConst.PTR_TP_NORMAL);

				System.out.println("Async transaction print submited: time = "
						+ System.currentTimeMillis() + " output id = " + printer.getOutputID());
				loggerReceiptTest.info("Async transaction print submited: time = "
						+ System.currentTimeMillis() + " output id = " + printer.getOutputID());
				// exit our printing loop
			} while (false);
		} catch (JposException e) {
			// display any errors that come up
			loggerReceiptTest.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			loggerReceiptTest.error(e.getMessage());
			e.printStackTrace();
		} finally {
			// close the printer object
			if (printer.getState() != JposConst.JPOS_S_CLOSED) {
				try {
					while (printer.getState() != JposConst.JPOS_S_IDLE) {
						Thread.sleep(0);
					}

					printer.close();
				} catch (Exception e) {
				}
			}
		}
		loggerReceiptTest.info("StarReceiptTest finished.");
		System.out.println("StarReceiptTest finished.");
	}

	public static void main(String[] args) {
		loggerReceiptTest.info("Start");
		new ReceiptTest().runTest(args);
	}
}
