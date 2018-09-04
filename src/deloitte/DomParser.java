package deloitte;
import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class DomParser {
	public static void main(String[] args) {
		try {
			File inputFile1 = new File("Credit Resource Standard User.profile");
			File inputFile2 = new File("Credit Resource Standard User.profile");
			//Credit Resource Standard User copy.profile
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc1 = dBuilder.parse(inputFile1);
			Document doc2 = dBuilder.parse(inputFile2);
			doc1.getDocumentElement().normalize();
			System.out.println("Root element :" + doc1.getDocumentElement().getNodeName());
			//if file is type profile
			if(doc1.getDocumentElement().getNodeName() == "Profile") {
				Map<String, Map<String, Node>> mapNodeData1 = new HashMap<>();
				Map<String, Map<String, Node>> mapNodeData2 = new HashMap<>();
				mapNodeData1.putAll(handleProfileMerge(doc1));
				mapNodeData2.putAll(handleProfileMerge(doc2));									
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String, Map<String, Node>> handleProfileMerge(Document doc1) {
		Map<String, Map<String, Node>> mapNodeData = new HashMap<>();
		NodeList nList;
		nList = doc1.getElementsByTagName("applicationVisibilities");
		if(nList.getLength() > 0){
			mapNodeData.putAll(createNodemap(nList,"applicationVisibilities","application"));
		}
		nList = doc1.getElementsByTagName("classAccesses");
		if(nList.getLength() > 0){
			mapNodeData.putAll(createNodemap(nList,"classAccesses","apexClass"));
		}
		nList = doc1.getElementsByTagName("fieldPermissions");
		if(nList.getLength() > 0){
			mapNodeData.putAll(createNodemap(nList,"fieldPermissions","field"));
		}
		nList = doc1.getElementsByTagName("layoutAssignments");
		if(nList.getLength() > 0){
			mapNodeData.putAll(createNodemap(nList,"layoutAssignments","layout"));
		}
		nList = doc1.getElementsByTagName("objectPermissions");
		if(nList.getLength() > 0){
			mapNodeData.putAll(createNodemap(nList,"objectPermissions","object"));
		}
		nList = doc1.getElementsByTagName("recordTypeVisibilities");
		if(nList.getLength() > 0){
			mapNodeData.putAll(createNodemap(nList,"recordTypeVisibilities","recordType"));
		}
		nList = doc1.getElementsByTagName("tabVisibilities");
		if(nList.getLength() > 0){
			mapNodeData.putAll(createNodemap(nList,"tabVisibilities","tab"));
		}
		System.out.println("mapNodeData :" + mapNodeData.size());
		return mapNodeData;
	}
	
	public static Map<String, Map<String, Node>> createNodemap(NodeList nList,String keyType,String tagName){		
		Map<String, Map<String, Node>> mapNodeData = new HashMap<>();
		Map<String, Node> mapNodeElementData = new HashMap<>();
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			//System.out.println("\nnNode.getNodeType :" + nNode.getNodeType());
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				//System.out.println("\n Test");
				Element eElement = (Element) nNode;
				//System.out.println("Name : " + eElement.getElementsByTagName(tagName).item(0).getTextContent());
				mapNodeElementData.put(eElement.getElementsByTagName(tagName).item(0).getTextContent(),nNode);
			}
		}
		if(mapNodeElementData.size() > 0) {
			System.out.println( "\n mapNodeElementData"+ keyType + " " +mapNodeElementData.size());
			mapNodeData.put(keyType,mapNodeElementData);
		}
		return mapNodeData;
	}
	
	//will be used for storing tags and their identifier for different files
	public class metadataType{
		String metadataType;
		List<metadataIdentifiers> listIdentifier;
	}
	
	public class metadataIdentifiers{
		String permissionType;
		String uniqueIdentifierTag;
		metadataIdentifiers(){
			
		}
	}
}