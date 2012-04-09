package com.wiz.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class WizSafeParser {
	
	//¥‹¿œ ø§∑π∏‡∆Æ √ﬂ√‚øÎ ∆ƒΩÃ
	public static String xmlParser_String(ArrayList<String> xml, String tag)
	{
		String returnVal = "";
		String parseXML = "";
		String parseElement = tag.substring(tag.indexOf("<") + 1 , tag.lastIndexOf(">"));
	
		for(int i = 0 ; i < xml.size() ; i++){
			parseXML = parseXML + xml.get(i);
		}
		
		InputStream istream = null;;
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			istream = new ByteArrayInputStream(parseXML.getBytes("euc-kr"));
			Document doc = builder.parse(istream);
			
			Element order = doc.getDocumentElement();
			NodeList items = order.getElementsByTagName(parseElement);
			Node item = items.item(0);
			
			returnVal = item.getTextContent(); 
			
		}catch(Exception e){
			returnVal = "Error Element!!";
		}finally{
			if(istream != null){ try{istream.close();}catch(Exception e){} }
		}
				
		return returnVal;
	}
	
	//∏ÆΩ∫∆ÆøÎ ø§∑π∏‡∆Æ √ﬂ√‚øÎ ∆ƒΩÃ
	public static ArrayList<String> xmlParser_List(ArrayList<String> xml, String tag)
	{
		ArrayList<String> returnVal = new ArrayList<String>();
		String parseXML = "";
		String parseElement = tag.substring(tag.indexOf("<") + 1 , tag.lastIndexOf(">"));
		
		for(int i = 0 ; i < xml.size() ; i++){
			parseXML = parseXML + xml.get(i);
		}
		
		InputStream istream = null;;
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			istream = new ByteArrayInputStream(parseXML.getBytes("euc-kr"));
			Document doc = builder.parse(istream);
			
			Element order = doc.getDocumentElement();
			NodeList items = order.getElementsByTagName(parseElement);
		
			for(int i = 0 ; i < items.getLength() ; i++){
				Node item = items.item(i);
				if(parseElement.equals(item.getNodeName())){
					returnVal.add(item.getTextContent());
				}
			}
			
		}catch(Exception e){
			returnVal.add("Error Elements!!");
		}finally{
			if(istream != null){ try{istream.close();}catch(Exception e){} }
		}
		
		return returnVal;
	}
	
	

}
