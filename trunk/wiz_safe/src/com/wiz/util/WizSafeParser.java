package com.wiz.util;

import java.util.ArrayList;

public class WizSafeParser {
	
	//단일 엘레멘트 추출용 파싱
	public static String xmlParser_String(ArrayList<String> xml, String tag)
	{
		String returnVal = "";
		
		//0보다 클때만 판단
		if(xml.size() > 0){
			
			for(int i = 0 ; i < xml.size() ; i++){
				String temp;
				temp = xml.get(i);
				if (temp == null)	temp = "";
				temp.trim();
				if (temp.indexOf(tag) >= 0)
				{
					temp = temp.substring(temp.indexOf(">") + 1 , temp.lastIndexOf("</"));
					returnVal = temp; 
				}
			}
			
		}else{
			returnVal = "no input XML";
		}
		
		return returnVal;
	}
	
	//리스트용 엘레멘트 추출용 파싱
	public static ArrayList<String> xmlParser_List(ArrayList<String> xml, String tag)
	{
		ArrayList<String> returnVal = new ArrayList<String>();
		
		//0보다 클때만 판단
		if(xml.size() > 0){
			
			for(int i = 0 ; i < xml.size() ; i++){
				String temp;
				temp = xml.get(i);
				if (temp == null)	temp = "";
				temp.trim();
				if (temp.indexOf(tag) >= 0)
				{
					temp = temp.substring(temp.indexOf(">") + 1 , temp.lastIndexOf("</"));
					returnVal.add(temp);
				}				
			}
			
		}else{
			returnVal.add("no input XML");
		}
		
		return returnVal;
	}
	
	

}
