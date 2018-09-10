package deloitte;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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
			//tags logic
			if(sourceMetadataMap != null && destinationMetadataMap != null ) {
				Set<String> tempSource = sourceMetadataMap.keySet();
				System.out.println(tempSource);
				for(String tempStr: tempSource) {
					Map<String, String> tempDest = destinationMetadataMap.get(tempStr);
					System.out.println("tempDest  + " + tempDest);
					Map<String, String> tempSor =sourceMetadataMap.get(tempStr);
					for(String tempstr1 :tempSor.keySet()) {
						if(tempDest != null && !tempDest.containsKey(tempstr1) ) {
							tempDest.put(tempstr1,tempSor.get(tempstr1));
							System.out.println("tempDest  + " + tempDest);
						}
						else if(tempDest != null && tempDest.containsKey(tempstr1) ) {
							if(!tempDest.get(tempstr1).equals(tempSor.get(tempstr1))) {
								
								tempDest.put(tempstr1,tempSor.get(tempstr1));
								System.out.println("tempDest  + " + tempDest);
							}
						}
						destinationMetadataMap.put(tempStr, tempDest);
					}
					System.out.println("destinationMetadataMap  + " + destinationMetadataMap);
				}
			}
			
			objMetaDataMerger.createDestinationXml(destinationMetadataMap);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void createDestinationXml(Map<String, Map<String, String>> destinationMetadataMap) {


		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("package");
			doc.appendChild(rootElement);

			
			Element type = doc.createElement("type");
			rootElement.appendChild(type);
			//Version element
			Element version = doc.createElement("version");
			version.appendChild(doc.createTextNode("41.0"));//TODO
			rootElement.appendChild(version);

			if(destinationMetadataMap != null) {
				for(String tempSetStr: destinationMetadataMap.keySet()) {
					Set<String> tempValues = destinationMetadataMap.get(tempSetStr);
					if(tempValues != null) {
						for(String tempS : tempValues) {
							Element members = doc.createElement("members");
							members.appendChild(doc.createTextNode(tempS));
							type.appendChild(members);
						}
					}
					Element name = doc.createElement("name");
					name.appendChild(doc.createTextNode(tempSetStr));
					type.appendChild(name);
				}
			}


			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			//MetadataMerger objMetaDataMerger = new MetadataMerger();
			//File destinationFile = objMetaDataMerger.getFile("doc2.xml");
			StreamResult result = new StreamResult(new File("/Users/rkonduru/Desktop/destinationPackage.xml"));

			transformer.transform(source, result);
			System.out.println("File saved!");

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
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