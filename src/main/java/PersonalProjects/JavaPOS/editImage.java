package PersonalProjects.JavaPOS;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class editImage {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Font font = new Font("TimesRoman", Font.BOLD, 24);
    	BufferedImage wrapTextImage = new BufferedImage(1, 1, 12);
	    Graphics g = wrapTextImage.createGraphics(); 	
	    int wid = g.getFontMetrics(font).getHeight()*4;				
	    BufferedImage logo = ImageIO.read(new File("src/main/java/LOGO02.bmp"));
	    //BufferedImage image =  editImage.wrapText("VM+ HNI Green Star 234 Phạm Văn Đồng	!~@T 1, Khu CT2 234 Phạm Văn Đồng!~@P. Cổ Nhuế 1, Q. Bắc Từ Liêm, HN!~@0901 722 196",Math.round(29f/13f*logo.getWidth()), wid, 12, font);	
	    BufferedImage image =  editImage.wrapText("VM Times1 CityVM Times CityVM Times City!~@B1-C55 Times City, 458 Minh Khai!~@B1-C55 Times City, 458 Minh Khai!~@0934481068/047300077712345678901233",Math.round(29f/13f*logo.getWidth()), wid, BufferedImage.TYPE_BYTE_BINARY, font);	        
        BufferedImage editedImage = editImage.joinBufferedImage(logo,image,0);
	    System.out.println(editedImage.getWidth() +" "+editedImage.getHeight()+ " ");
        editImage.saveImage(editedImage,"src/main/java/LOGO_EDITED.bmp");
	}
	//static Logger loggerEditImage = LogManager.getLogger(editImage.class);
    public static BufferedImage joinBufferedImage(BufferedImage img1,BufferedImage img2,int offset) throws IOException {
    	//loggerEditImage.info("joinBufferedImage");
        //do some calculate first
        int wid = img1.getWidth()+img2.getWidth()+offset;
        int height = Math.max(img1.getHeight(),img2.getHeight())+offset;
        int img1y = img1.getHeight()>=img2.getHeight()?0:(img2.getHeight()-img1.getHeight())/2;
        int img2y = img1.getHeight()>=img2.getHeight()?(img1.getHeight()-img2.getHeight())/2:0;                       
        //create a new buffer and draw two image into the new image
        BufferedImage newImage = new BufferedImage(wid,height, BufferedImage.TYPE_BYTE_BINARY);
        Graphics2D g2 = newImage.createGraphics();
        //Color oldColor = g2.getColor();
        //fill background
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, wid, height);
        g2.drawImage(img1, null, 0, img1y);
        g2.drawImage(img2, null, img1.getWidth()+offset, img2y);
        g2.dispose();
        int newwidth = 500;
        int newheight = newwidth *newImage.getHeight()/newImage.getWidth();
        //int newheight = newImage.getHeight();
        return resize(newImage,newwidth,newheight);
    }
    
    public static boolean saveImage(BufferedImage Image, String path) throws IOException {
    	//loggerEditImage.info("saveImage");
    	File f = new File(path);
    	if(f.exists()){
    		f.delete();
    	}
    	return ImageIO.write(Image, "bmp", new File(path));
    }
    public static BufferedImage wrapText(String text, int width, int height, int imageType, Font font) {
    	//loggerEditImage.info("wrapTextImage");
    	BufferedImage wrapTextImage = new BufferedImage(width, height, imageType);
	    Graphics g = wrapTextImage.createGraphics(); 
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, wrapTextImage.getWidth(), wrapTextImage.getHeight());        
	    g.setColor(Color.BLACK);
	    g.setFont(font);
        int nIndex = 0;
        int startX = 0;
        int startY = -10;
    	String[] arr = text.split("!~@");
        while ( nIndex < arr.length )
        { 
            if(nIndex==0) {
            	//"TimesRoman", Font.PLAIN, 30
            	g.setFont(new Font(font.getName(),Font.BOLD,font.getSize()+4));
            }else {
            	g.setFont(font);
            }
            String line = arr[nIndex++];
        	String[] arr2 = line.split(" ");
            int nIndex2 = 0;
            while ( nIndex2 < arr2.length )
            { 
                String line2 = arr2[nIndex2++];
                while ( ( nIndex2 < arr2.length ) && (g.getFontMetrics(g.getFont()).stringWidth(line2 + " " + arr2[nIndex2]) < width) )
                {
                	line2 = line2 + " " + arr2[nIndex2];
                    nIndex2++;
                }
                startY = startY + g.getFontMetrics(g.getFont()).getHeight();          
                startX = (width-g.getFontMetrics(g.getFont()).stringWidth(line2))/2;
        	    g.drawString(line2, startX, startY);
            }            
        }
        
	    g.dispose();
        return wrapTextImage;
    }
    public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
    	//loggerEditImage.info("resizeImage");
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_BYTE_BINARY);

        Graphics2D g2d = dimg.createGraphics();   
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return dimg;
    }  
    

}
