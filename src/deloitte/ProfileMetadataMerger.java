package deloitte;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;


public class ProfileMetadataMerger {


	public static void main(String[] args) {
		try {
			ProfileMetadataMerger objMetaDataMerger = new ProfileMetadataMerger();
			Map<String, Map<String, String>> sourceMetadataMap = new HashMap<>();
			Map<String, Map<String, String>> destinationMetadataMap = new HashMap<>();

			File inputFile = new File("/Users/rkonduru/Desktop/sourcePackageProfile.xml");//objMetaDataMerger.getFile("doc1.xml");
			sourceMetadataMap = objMetaDataMerger.handleProfileMerge(inputFile);
			System.out.println("sourceMetadataMap: " + sourceMetadataMap);

			File destinationFile = new File("/Users/rkonduru/Desktop/destinationPackageProfile.xml");//objMetaDataMerger.getFile("doc2.xml");
			destinationMetadataMap = objMetaDataMerger.handleProfileMerge(destinationFile);
			System.out.println("destinationMetadataMap: " + destinationMetadataMap);

			
			//objMetaDataMerger.createDestinationXml(destinationMetadataMap);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public  Map<String, Map<String, String>> handleProfileMerge(File inputFile1) {
		Map<String, Map<String, String>> mapNodeData = new HashMap<>();
		NodeList nList;
		try {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc1 = dBuilder.parse(inputFile1);
		doc1.getDocumentElement().normalize();
		
		nList = doc1.getElementsByTagName("classAccesses");
		if(nList.getLength() > 0){
			mapNodeData.putAll(createNodemap(nList,"classAccesses","apexClass"));
		}
		nList = doc1.getElementsByTagName("pageAccesses");
		if(nList.getLength() > 0){
			mapNodeData.putAll(createNodemap(nList,"pageAccesses","apexPage"));
		}
		nList = doc1.getElementsByTagName("userPermissions");
		if(nList.getLength() > 0){
			mapNodeData.putAll(createNodemap(nList,"userPermissions","name"));
		}
		System.out.println("mapNodeData : " + mapNodeData.size());
		return mapNodeData;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapNodeData;
	}
	
	public static Map<String, Map<String, String>> createNodemap(NodeList nList,String keyType,String tagName){		
		Map<String, Map<String, String >> mapNodeData = new HashMap<>();
		Map<String, String> mapNodeElementData = new HashMap<>();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			//System.out.println("\nnNode.getNodeType :" + nNode.getNodeType());
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				//System.out.println("\n Test");
				Element eElement = (Element) nNode;
				System.out.println("Name : " + eElement.getElementsByTagName(tagName).item(0).getTextContent());
				mapNodeElementData.put(eElement.getElementsByTagName(tagName).item(0).getTextContent(),eElement.getElementsByTagName("enabled").item(0).getTextContent());//nNode);
			}
		}
		if(mapNodeElementData.size() > 0) {
			System.out.println( "\n mapNodeElementData "+ keyType + " " +mapNodeElementData.size());
			mapNodeData.put(keyType,mapNodeElementData);
		}
		return mapNodeData;
	}
}