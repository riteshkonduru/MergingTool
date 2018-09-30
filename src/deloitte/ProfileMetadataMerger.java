package deloitte;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
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
import deloitte.UpdateProfileWrapElements;


public class ProfileMetadataMerger {


	public static void main(String[] args) {
		try {
			ProfileMetadataMerger objMetaDataMerger = new ProfileMetadataMerger();
			Map<String, Set<ProfileElements>> sourceMetadataMap = new HashMap<>();
			Map<String, Set<ProfileElements>> destinationMetadataMap = new HashMap<>();
			ArrayList<UpdateProfileWrapElements> destinationMetadataWrapNew = new ArrayList<UpdateProfileWrapElements>();
			Map<String, ProfileElements> recProfilesTOADDSource = new HashMap<>();
			Map<String, ProfileElements> recProfilesTOADDDest =  new HashMap<>();
			File inputFile = new File("/Users/rkonduru/Desktop/sourcePackageProfile.xml");//objMetaDataMerger.getFile("doc1.xml");
			sourceMetadataMap = objMetaDataMerger.handleProfileMerge(inputFile);
			System.out.println("sourceMetadataMap: " + sourceMetadataMap);

			File destinationFile = new File("/Users/rkonduru/Desktop/destinationPackageProfile.xml");//objMetaDataMerger.getFile("doc2.xml");
			destinationMetadataMap = objMetaDataMerger.handleProfileMerge(destinationFile);
			System.out.println("destinationMetadataMap: " + destinationMetadataMap);
			//tags logic starts here
			if(sourceMetadataMap != null && destinationMetadataMap != null ) {//Map<classAccesses, Map<TestClass3Name, enableTrue>>
				Set<String> tempSource = sourceMetadataMap.keySet();//tempSource == classAccesses,pageAccesses,userPermissions
				System.out.println("tempSource " + tempSource.size() + "  " + tempSource);
				
				for(String tempStr: tempSource) {//tempStr == classAccesses
					UpdateProfileWrapElements tempWrapElements = new UpdateProfileWrapElements();
					Set<ProfileElements> tempDest = destinationMetadataMap.get(tempStr);//Map<TestClass3Name, enableTrue>
					//System.out.println("tempDest  + " + tempDest.size() + "  " + tempDest);
					Set<ProfileElements> tempSor =sourceMetadataMap.get(tempStr);//Map<TestClass3Name, enableTrue>
					//System.out.println("tempSor  + " + tempSor.size() + "  " + tempSor);
					Set<ProfileElements> recProfileSet = new HashSet<>();
					Set<ProfileElements> recProfileSetRemove = new HashSet<>();
					for(ProfileElements tempstr1 :tempSor) {//tempstr1 ==TestClass3Name ,enableTrue,TestClass3Name-enableTrue
						recProfilesTOADDSource.put(tempstr1.getPreparedKey() ,tempstr1);
						if(tempDest != null) {
							for(ProfileElements tempstrDest1 :tempDest) {
								recProfilesTOADDDest.put(tempstrDest1.getPreparedKey(), tempstrDest1);
								if(tempstr1.getName().equals(tempstrDest1.getName())) {//recProfileElements.getPreparedKey
									if(!tempstr1.getPreparedKey().equals(tempstrDest1.getPreparedKey())) {
										recProfileSet.add(tempstr1);
										recProfileSetRemove.add(tempstrDest1);
									}
								}
							}
						}
					}
					System.out.println("recProfilesTOADDSource  + " + recProfilesTOADDSource.size() + "  " + recProfilesTOADDSource);
					System.out.println("recProfilesTOADDDest  + " + recProfilesTOADDDest.size() + "  " + recProfilesTOADDDest);
					for(String tempStrTOAddNewSrc: recProfilesTOADDSource.keySet()) {

						if(!recProfilesTOADDDest.containsKey(tempStrTOAddNewSrc)) {
							recProfileSet.add(recProfilesTOADDSource.get(tempStrTOAddNewSrc));
						}

					}
					recProfilesTOADDSource.clear();
					recProfilesTOADDDest.clear();
					tempWrapElements.setNameType(tempStr);
					tempWrapElements.setProfileSet(recProfileSet);
					tempWrapElements.setProfileSetRemove(recProfileSetRemove);
					destinationMetadataWrapNew.add(tempWrapElements);//this is for updating in enable tag under a class
					//destinationMetadataMapNew.put(tempStr, recProfileSet);
				}
				System.out.println("destinationMetadataWrapNew  + " + destinationMetadataWrapNew);
			}

			objMetaDataMerger.updateDestinationXml(destinationMetadataWrapNew, destinationFile);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void updateDestinationXml( ArrayList<UpdateProfileWrapElements> destinationMetadataWrapNew, File destinationFile) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document docDestinationFile = dBuilder.parse(destinationFile);
			docDestinationFile.getDocumentElement().normalize();
			NodeList nList;
			//Element root = docDestinationFile.getDocumentElement();
			for(UpdateProfileWrapElements tempTypes: destinationMetadataWrapNew){
				//Removing
				Set<ProfileElements> tempProfileValuesToRemove = tempTypes.getProfileSetRemove();
				if(tempProfileValuesToRemove.size() >0) {
					if(tempTypes.getNameType().equals("classAccesses")) { 
						nList = docDestinationFile.getElementsByTagName(tempTypes.getNameType());
						for(ProfileElements tempProfile : tempProfileValuesToRemove) {
							for (int temp = 0; temp < nList.getLength(); temp++) {
								Node nNode = nList.item(temp);
								if (nNode.getNodeType() == Node.ELEMENT_NODE) {
									Element eElement = (Element) nNode;
									if (eElement.getElementsByTagName("apexClass").item(0).getTextContent().equals((tempProfile.getName()))) {
										nNode.getParentNode().removeChild(nNode);
									}
								}
							}
						}
					}
					if(tempTypes.getNameType().equals("pageAccesses")) { 
						nList = docDestinationFile.getElementsByTagName(tempTypes.getNameType());
						for(ProfileElements tempProfile : tempProfileValuesToRemove) {
							for (int temp = 0; temp < nList.getLength(); temp++) {
								Node nNode = nList.item(temp);
								if (nNode.getNodeType() == Node.ELEMENT_NODE) {
									Element eElement = (Element) nNode;
									if (eElement.getElementsByTagName("apexPage").item(0).getTextContent().equals((tempProfile.getName()))) {
										nNode.getParentNode().removeChild(nNode);
									}
								}
							}
						}
					}
					if(tempTypes.getNameType().equals("userPermissions")) { 
						nList = docDestinationFile.getElementsByTagName(tempTypes.getNameType());
						for(ProfileElements tempProfile : tempProfileValuesToRemove) {
							for (int temp = 0; temp < nList.getLength(); temp++) {
								Node nNode = nList.item(temp);
								if (nNode.getNodeType() == Node.ELEMENT_NODE) {
									Element eElement = (Element) nNode;
									if (eElement.getElementsByTagName("name").item(0).getTextContent().equals((tempProfile.getName()))) {
										nNode.getParentNode().removeChild(nNode);
									}
								}
							}
						}
					}
				}
				//Adding
				Set<ProfileElements> tempProfileValuesToAdd = tempTypes.getProfileSet();
				if(tempProfileValuesToAdd.size() >0) {
					for(ProfileElements tempProfile : tempProfileValuesToAdd) {
						if(tempProfile.getName() != null && tempProfile.getEnabled() != null){
							System.out.println("Name + " + tempProfile.getName() + " Enabled + " + tempProfile.getEnabled());
							Element root = docDestinationFile.getDocumentElement();
							if(tempTypes.getNameType().equals("classAccesses")) {
								Element name = docDestinationFile.createElement("classAccesses");
								Element apexClass = docDestinationFile.createElement("apexClass");
								apexClass.appendChild(docDestinationFile.createTextNode(tempProfile.getName())); 
								Element apexClassEnable = docDestinationFile.createElement("enabled");
								apexClassEnable.appendChild(docDestinationFile.createTextNode(tempProfile.getEnabled()));
								name.appendChild(apexClass);
								name.appendChild(apexClassEnable);
								root.appendChild(name);	
							}
							if(tempTypes.getNameType().equals("pageAccesses")) {
								Element name = docDestinationFile.createElement("pageAccesses");
								Element apexPage = docDestinationFile.createElement("apexPage");
								apexPage.appendChild(docDestinationFile.createTextNode(tempProfile.getName())); 
								Element apexPageEnable = docDestinationFile.createElement("enabled");
								apexPageEnable.appendChild(docDestinationFile.createTextNode(tempProfile.getEnabled()));
								name.appendChild(apexPage);
								name.appendChild(apexPageEnable);
								root.appendChild(name);	
							}
							if(tempTypes.getNameType().equals("userPermissions")) {
								Element name = docDestinationFile.createElement("userPermissions");
								Element apexPage = docDestinationFile.createElement("name");
								apexPage.appendChild(docDestinationFile.createTextNode(tempProfile.getName())); 
								Element apexPageEnable = docDestinationFile.createElement("enabled");
								apexPageEnable.appendChild(docDestinationFile.createTextNode(tempProfile.getEnabled()));
								name.appendChild(apexPage);
								name.appendChild(apexPageEnable);
								root.appendChild(name);	
							}
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
	public  Map<String,Set<ProfileElements>> handleProfileMerge(File inputFile1) {
		Map<String, Set<ProfileElements>> mapNodeData = new HashMap<>();
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

	public static Map<String, Set<ProfileElements>> createNodemap(NodeList nList,String keyType,String tagName){		
		Map<String, Set<ProfileElements>> mapNodeData = new HashMap<>();
		Map<String, String> mapNodeElementData = new HashMap<>();
		Set<ProfileElements> setProfileElements = new HashSet<>();

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			//System.out.println("\nnNode.getNodeType :" + nNode.getNodeType());
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				//System.out.println("\n Test");
				Element eElement = (Element) nNode;
				System.out.println("Name : " + eElement.getElementsByTagName(tagName).item(0).getTextContent());
				mapNodeElementData.put(eElement.getElementsByTagName(tagName).item(0).getTextContent(),eElement.getElementsByTagName("enabled").item(0).getTextContent());//nNode);
				ProfileElements recProfileElements = new ProfileElements();
				recProfileElements.setName(eElement.getElementsByTagName(tagName).item(0).getTextContent());
				recProfileElements.setEnabled(eElement.getElementsByTagName("enabled").item(0).getTextContent());
				recProfileElements.setPreparedKey(eElement.getElementsByTagName(tagName).item(0).getTextContent()+"-"+eElement.getElementsByTagName("enabled").item(0).getTextContent());
				setProfileElements.add(recProfileElements);
			}
		}
		if(setProfileElements.size() > 0) {
			System.out.println( "\n mapNodeElementData "+ keyType + " " +setProfileElements.size());
			mapNodeData.put(keyType,setProfileElements);
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