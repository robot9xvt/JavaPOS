package PersonalProjects.JavaPOS;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class textHandle {
	static Logger loggertextHandle = LogManager.getLogger(textHandle.class);
	public static void main(String[] args) {
		// TODO Auto-generated method stub	
    System.out.println(header3Column("Ngày bán: 20/09/2017 12:30","HĐ:","12345678",42));
    System.out.println(header3Column("Quầy: 001","NVBH:","09007001",42));
    System.out.println(detail4Column("Mặt hàng" ,"Đơn giá","SL","Thành tiền",42));
    System.out.println(detail4Column("01234567891234","29.999.999","999","999.999.999",42));
	}
	public static String header3Column(String text1, String text2, String text3, int length) {
		loggertextHandle.info("header3Column");
		String text = text1+spaceText(Math.round(33f/42f*length)-text1.length()-text2.length())+text2;
		text = text+spaceText(length-text.length()-text3.length())+text3;
		return text;
	}
	public static String spaceText(int length) {
		String spaceText = "";
		while(spaceText.length()<length) {
			spaceText = spaceText+ " ";
		}
		return spaceText;
	}
	public static String detail4Column(String text1, String text2, String text3,String text4, int length) {
		loggertextHandle.info("detail4Column");
		text2 = spaceText(Math.round(25f/42f*length) - text1.length() - text2.length()) +text2;
		text3 = spaceText(Math.round(29f/42f*length) - text1.length() - text2.length() - text3.length()) +text3;
		text4 = spaceText(length - text1.length() - text2.length() - text3.length()- text4.length()) +text4;
		return text1+text2+text3+text4;
	}
	public static String lineText(int length) {
		loggertextHandle.info("lineText");
		String spaceText = "";
		while(spaceText.length()<length) {
			spaceText = spaceText+ "-";
		}
		return spaceText;
	}
	public static String footer2Column(String text1, String text2, int length) {
		loggertextHandle.info("footer2Column");
		String text = text1+spaceText(length-text1.length()-text2.length())+text2;
		return text;
	}
	
}
