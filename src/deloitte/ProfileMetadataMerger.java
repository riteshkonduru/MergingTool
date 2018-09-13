package deloitte;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import deloitte.ProfileElements;


public class ProfileMetadataMerger {


	public static void main(String[] args) {
		try {
			ProfileMetadataMerger objMetaDataMerger = new ProfileMetadataMerger();
			Map<String, Map<String, String>> sourceMetadataMap = new HashMap<>();
			Map<String, Map<String, String>> destinationMetadataMap = new HashMap<>();
			Map<String, String> tempDestNew =new HashMap<>();
			Map<String, Set<ProfileElements>> destinationMetadataMapNew = new HashMap<>();
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
					Set<ProfileElements> recProfileSet = new HashSet<>();
					for(String tempstr1 :tempSor.keySet()) {
						ProfileElements recProfile = new ProfileElements();
						if(tempDest != null && !tempDest.containsKey(tempstr1) ) {
							tempDestNew.put(tempstr1,tempSor.get(tempstr1));
							recProfile.setName(tempstr1);
							recProfile.setEnabled(tempSor.get(tempstr1));
							System.out.println("tempDestNew  + " + tempDestNew);
						}
						else if(tempDest != null && tempDest.containsKey(tempstr1) ) {
							if(!tempDest.get(tempstr1).equals(tempSor.get(tempstr1))) {

								tempDestNew.put(tempstr1,tempSor.get(tempstr1));
								recProfile.setName(tempstr1);
								recProfile.setEnabled(tempSor.get(tempstr1));
								System.out.println("tempDestNew  + " + tempDestNew);
							}
						}
						recProfileSet.add(recProfile);
					}
					destinationMetadataMapNew.put(tempStr, recProfileSet);
				}
				System.out.println("destinationMetadataMapNew  + " + destinationMetadataMapNew);
			}

			objMetaDataMerger.createDestinationXml(destinationMetadataMapNew, destinationFile);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void createDestinationXml(Map<String, Set<ProfileElements>> destinationMetadataMapNew, File destinationFile) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document docDestinationFile = dBuilder.parse(destinationFile);
			docDestinationFile.getDocumentElement().normalize();

			for(String tempTypes: destinationMetadataMapNew.keySet()){
				Set<ProfileElements> tempValues = destinationMetadataMapNew.get(tempTypes);
				if(tempValues.size() >0) {
					for(ProfileElements tempProfile : tempValues) {
						if(tempProfile.getName() != null && tempProfile.getEnabled() != null){
							System.out.println("Name + " + tempProfile.getName() + " Enabled + " + tempProfile.getEnabled());
							Element root = docDestinationFile.getDocumentElement();
							Element name = docDestinationFile.createElement("classAccesses");
							Element apexClass = docDestinationFile.createElement("apexClass");
							apexClass.appendChild(docDestinationFile.createTextNode(tempProfile.getName())); 
							Element apexClassEnable = docDestinationFile.createElement("enabled");
							apexClassEnable.appendChild(docDestinationFile.createTextNode(tempProfile.getEnabled()));
							name.appendChild(apexClass);
							name.appendChild(apexClassEnable);
							root.appendChild(name);	
						}
					}
				}	
			}
			prettyPrint(docDestinationFile);
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(docDestinationFile);
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			StreamResult result = new StreamResult(new File("/Users/rkonduru/Desktop/destinationPackageProfile.xml"));

			transformer.transform(source, result);
			System.out.println("File saved!");
		}catch (Exception e) {
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
	public static final void prettyPrint(Document xml) throws Exception {

		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		Writer out = new StringWriter();
		tf.transform(new DOMSource(xml), new StreamResult(out));
		System.out.println(out.toString());

	}
}