package deloitte;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
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

public class MetadataMerger {

	private File getFile(String fileName) {

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());
		return file;
	}

	public static void main(String[] args) {
		try {
			MetadataMerger objMetaDataMerger = new MetadataMerger();
			Map<String, Set<String>> sourceMetadataMap = new HashMap<>();
			Map<String, Set<String>> destinationMetadataMap = new HashMap<>();

			File inputFile = new File("/Users/rkonduru/Desktop/destinationPackage.xml");//objMetaDataMerger.getFile("doc1.xml");
			sourceMetadataMap = objMetaDataMerger.getMapForCompare(inputFile);
			System.out.println("sourceMetadataMap: " + sourceMetadataMap);

			File destinationFile = new File("/Users/rkonduru/Desktop/sourcePackage.xml");//objMetaDataMerger.getFile("doc2.xml");
			destinationMetadataMap = objMetaDataMerger.getMapForCompare(destinationFile);
			System.out.println("destinationMetadataMap: " + destinationMetadataMap);

			if(sourceMetadataMap != null && destinationMetadataMap != null ) {
				Set<String> tempSource = sourceMetadataMap.keySet();
				System.out.println(tempSource);
				for(String tempStr: tempSource) {
					Set<String> tempDest = destinationMetadataMap.get(tempStr);
					System.out.println("tempDest  + " + tempDest);
					Set<String> tempSor =sourceMetadataMap.get(tempStr);
					for(String tempstr1 :tempSor) {
						if(tempDest != null && !tempDest.contains(tempstr1) ) {
							tempDest.add(tempstr1);
							System.out.println("tempDest  + " + tempDest);
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
	public void createDestinationXml(Map<String, Set<String>> destinationMetadataMap) {


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
			version.appendChild(doc.createTextNode("41.0"));
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
	public Map<String, Set<String>> getMapForCompare(File inputFile){
		Map<String, Set<String>> metadataMap = new HashMap<>();
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			//System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
			NodeList nTypesList = doc.getElementsByTagName("types");
			//System.out.println("----------------------------" + nTypesList.getLength());
			//Iterating through types
			for (int tempTypes = 0; tempTypes < nTypesList.getLength(); tempTypes++) {
				Set<String> tempMembers = new HashSet<>();
				String tempName = "";
				NodeList childList = nTypesList.item(tempTypes).getChildNodes();
				for (int j = 0; j < childList.getLength(); j++) {

					Node childNode = childList.item(j);
					if ("members".equals(childNode.getNodeName())) {
						tempMembers.add(childList.item(j).getTextContent().trim());
						//System.out.println(childList.item(j).getTextContent().trim());
					}
					else if("name".equals(childNode.getNodeName())) {
						//System.out.println(childList.item(j).getTextContent().trim());
						tempName= childList.item(j).getTextContent().trim();
					}  
				}
				metadataMap.put(tempName, tempMembers);
			}
			return metadataMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return metadataMap;


	}
}
/*Node nNode = nTypesList.item(tempTypes);
System.out.println("\nCurrent Element :" + nNode.getNodeName());
if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	Element eElement = (Element) nNode;
	//MetaData Name
	strMetaDataName = eElement.getElementsByTagName("name").item(0).getTextContent();
	System.out.println("Name: " + strMetaDataName);
	//MetaData Components
	NodeList nMembersList = eElement.getElementsByTagName("members");//eElement.getChildNodes();
	int membersCount = nMembersList.getLength();
	System.out.println("membersCount: " + membersCount);
	for (int i = 0; i < membersCount; i++) {
		Node nNodeMember = nMembersList.item(i);
		System.out.println("nNodeMember: " + nMembersList);
		Element eElementMember = (Element) nNodeMember;
		//System.out.println("Members : " +eElementMember.getElementsByTagName("members").item(i).getTextContent()); //nNodeMember.getNodeValue());
	}
}
 */