package com.my.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*String regex = "\\w+";
		String regex2 = "[\u4e00-\u9fa5]";
		Pattern p = Pattern.compile(regex2);
		Matcher matcher = p.matcher("中文");
		System.out.println(p.matcher("中文dfsfasdfa").find());
		while(matcher.find()){
			System.out.println(matcher.group(0));
		}*/
		/*System.out.println(matcher.group(0));
		for(int i = 0; i < matcher.groupCount();i++){
			System.out.println(matcher.group(i));
		}*/
		matchTest2();
		//System.out.println("phone no' match:" + isChinaPhoneLegal("131346464121"));
	}

	private static void matchTest(){
		String regex = "^\\[[[\u4e00-\u9fa5\\w]+:[\u4e00-\u9fa5\\w]+(;)?]+\\]$";
		String regex2 = "[\u4e00-\u9fa5\\w]+:[\u4e00-\u9fa5\\w]+";
		//String regex = "^\\[[[\u4e00-\u9fa5\\w]]+$\\]";
		//String regex = "^\\[[\u4e00-\u9fa5\\w]+";
		//String regex = "\\]$";
		//String str = "[科目:应付款sf;变动类型sf:期末数44;期间23:6期23::科目:]";
		String str = "[科目:应付*-款;科目:应付*-款;kemu:]";
		regex = "^\\[[[\u4e00-\u9fa5\\w\\*\\-]+:[\u4e00-\u9fa5\\w\\*\\-]+(;)?]+\\]$";
		Pattern p = Pattern.compile(regex);
		Matcher m1 = p.matcher(str);
		System.out.println(m1.matches());
		
		Pattern p2 = Pattern.compile(regex2);
		Matcher m = p2.matcher(str);
		System.out.println(m.groupCount());
		while(m.find()){
			System.out.println(m.group(0));
		}
		System.out.println(m.groupCount());
		
		if(m1.matches()){
			System.out.println(m1.group(0));
			//System.out.println(m1.group(1));
		}
	}
	
	private static void matchTest2(){
		String regex = "^\\[[\\s\\S]+![\\s\\S(;)?]+\\]$";
		String regex2 = "[\\s\\S(?!\\!)]+![\\s\\S]+(;)?";
		String input = "[科目!应收款;变动类型!期末数;]";
		Matcher m = Pattern.compile(regex2).matcher(input);
		
		while(m.find()){
			System.out.println(m.group(0));
		}
		System.out.println("match2:"+Pattern.matches(regex, input));
	}
	/** 
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数 
     * 此方法中前三位格式有： 
     * 13+任意数 
     * 15+除4的任意数 
     * 18+除1和4的任意数 
     * 17+除9的任意数 
     * 147 
     */  
    private static boolean isChinaPhoneLegal(String str)  {  
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";  
        Pattern p = Pattern.compile(regExp);  
        Matcher m = p.matcher(str);  
        return m.matches();  
    }
}
