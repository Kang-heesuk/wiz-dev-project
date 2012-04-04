package com.wiz.util;

import java.util.ArrayList;

public class WizSafeParser {
	
	//���� ������Ʈ ����� �Ľ�
	public static String xmlParser_String(ArrayList<String> xml, String tag)
	{
		String returnVal = "";
		
		//0���� Ŭ���� �Ǵ�
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
	
	//����Ʈ�� ������Ʈ ����� �Ľ�
	public static ArrayList<String> xmlParser_List(ArrayList<String> xml, String tag)
	{
		ArrayList<String> returnVal = new ArrayList<String>();
		
		//0���� Ŭ���� �Ǵ�
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
