/*
 * This class is to merge the meta-data records from source profile xml to destination profile xml.
 * in this class we are using JAVA DOM XML Parsing API. 
 * initially we are creating a map of meta-data-type and meta-data elements from source/destination.
 * this class contains code for adding, updating, deleting meta-data records from source to destination.
 * */
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
import deloitte.MetadataRecWrapToUpdate;


public class ProfileMetadataMerger {

	/*
	 * main method for reading xml, creating meta-data map and compare code.
	 * 
	 * */
	public static void main(String[] args) {

	}
	/*
	 * This method contains Merging/Adding elements to destination xml.
	 * 
	 * */
	public static void updateXmlElements(File sourceFile, File destFile) {

		try { 


			//Map for meta-data type with meta-data elements
			Map<String, Set<ProfileElements>> sourceMetadataMap = new HashMap<>();
			Map<String, Set<ProfileElements>> destinationMetadataMap = new HashMap<>();

			//input xml parsing
			File inputFile = sourceFile;//new File("/Users/rkonduru/Desktop/sourcePackageProfile.xml");//objMetaDataMerger.getFile("doc1.xml");
			sourceMetadataMap = readMetaDataType(inputFile);
			System.out.println(" sourceMetadataMap: " + sourceMetadataMap);

			//destination xml parsing
			File destinationFile = destFile;//new File("/Users/rkonduru/Desktop/destinationPackageProfile.xml");//objMetaDataMerger.getFile("doc2.xml");
			destinationMetadataMap = readMetaDataType(destinationFile);
			System.out.println(" destinationMetadataMap: " + destinationMetadataMap);

			/*
			 * here in the below code we are iterating through the source and destination maps to compare the meta-data types
			 * and meta-data elements. and later on we create a list of meda-data wrap to update at destination.
			 * the list wrapper contains meta-data elements which are newly added, removed and updated.
			 * later on sending that wrap to update destination xml method::
			 */
			//temp maps to hold meta-data type records which are created newly in source.
			//and later used to compare source and destination to find new and add in destination.
			Map<String, ProfileElements> mapForNewMetadataSource = new HashMap<>();
			Map<String, ProfileElements> mapForNewMetadataDest =  new HashMap<>();

			//contains list of meta-data types and elements to update in destination xml
			ArrayList<MetadataRecWrapToUpdate> destinationMetadataWrapTOUpdate = new ArrayList<MetadataRecWrapToUpdate>();


			//null check for source and destination maps
			if(sourceMetadataMap != null && destinationMetadataMap != null ) {//Map<classAccesses, Map<TestClass3Name, enableTrue>>
				Set<String> tempSourceMetadataMap = sourceMetadataMap.keySet();//tempSourceMetadataMap == classAccesses,pageAccesses,userPermissions
				System.out.println("tempSourceMetadataMap " + tempSourceMetadataMap.size() + "  " + tempSourceMetadataMap);

				for(String tempMetaDataTypeSource: tempSourceMetadataMap) {//tempStr == classAccesses

					MetadataRecWrapToUpdate tempMetadataRecWrapToUpdate = new MetadataRecWrapToUpdate();

					Set<ProfileElements> tempDestMetaDataElements = destinationMetadataMap.get(tempMetaDataTypeSource);//Map<TestClass3Name, enableTrue>
					Set<ProfileElements> tempSorMetaDataElements = sourceMetadataMap.get(tempMetaDataTypeSource);//Map<TestClass3Name, enableTrue>
					Set<ProfileElements> metadataRecSet = new HashSet<>();
					Set<ProfileElements> metadataRecSetToRemove = new HashSet<>();

					for(ProfileElements tempstr1 :tempSorMetaDataElements) {//tempstr1 ==TestClass3Name ,enableTrue,TestClass3Name-enableTrue

						mapForNewMetadataSource.put(tempstr1.getPreparedKey() ,tempstr1);

						if(tempDestMetaDataElements != null) {

							for(ProfileElements tempstrDest1 :tempDestMetaDataElements) {

								mapForNewMetadataDest.put(tempstrDest1.getPreparedKey(), tempstrDest1);

								if(tempstr1.getName().equals(tempstrDest1.getName())) {//recProfileElements.getPreparedKey

									if(!tempstr1.getPreparedKey().equals(tempstrDest1.getPreparedKey())) {

										metadataRecSet.add(tempstr1);
										metadataRecSetToRemove.add(tempstrDest1);

									}//equals prepare key for update check

								}//equals 

							}//tempDestMetaDataElements

						}//tempDestMetaDataElements null check

					}//tempSorMetaDataElements

					// these debugs will provide us info related to meta-data elements related to a type 
					// where these maps are used to compare newly added elements in source
					System.out.println("mapForNewMetadataSource  + " + " " + tempMetaDataTypeSource + " " + mapForNewMetadataSource.size() + "  " + mapForNewMetadataSource);
					System.out.println("mapForNewMetadataDest  + " + " " + tempMetaDataTypeSource + " " + mapForNewMetadataDest.size() + "  " + mapForNewMetadataDest);


					//for loop for source to add newly added tags.
					for(String tempStrTOAddNewSrc: mapForNewMetadataSource.keySet()) {

						if(!mapForNewMetadataDest.containsKey(tempStrTOAddNewSrc)) {

							metadataRecSet.add(mapForNewMetadataSource.get(tempStrTOAddNewSrc));

						}//contains key

					}//for mapForNewMetadataSource

					//for loop for destination to remove tags which are not present in source.
					for(String tempToRemoveFrDest: mapForNewMetadataDest.keySet()) {

						if(!mapForNewMetadataSource.containsKey(tempToRemoveFrDest)) {

							metadataRecSetToRemove.add(mapForNewMetadataDest.get(tempToRemoveFrDest));

						}//contains key

					}//for mapForNewMetadataDest

					//clearing map, later to add now elements related to new other meta-data types in the loop.
					mapForNewMetadataSource.clear();
					mapForNewMetadataDest.clear();

					//adding elements to the update wrapper.
					tempMetadataRecWrapToUpdate.setNameType(tempMetaDataTypeSource);
					tempMetadataRecWrapToUpdate.setMetadataRecSet(metadataRecSet);
					tempMetadataRecWrapToUpdate.setMetadataRecSetToRemove(metadataRecSetToRemove);
					destinationMetadataWrapTOUpdate.add(tempMetadataRecWrapToUpdate);//this is for updating in enable tag under a class

				}//for tempSourceMetadataMap close

				System.out.println("destinationMetadataWrapTOUpdate  + " + destinationMetadataWrapTOUpdate);

			}//if null check close for source and destination

			//calling update destination method.
			updateDestinationXml(destinationMetadataWrapTOUpdate, destinationFile);


		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/*
	 * This method contains Deleting elements to destination xml.
	 * 
	 * */
	public static void deleteXmlElements(File sourceFile, File destFile) {

		try { 


			//Map for meta-data type with meta-data elements
			Map<String, Set<ProfileElements>> sourceMetadataMap = new HashMap<>();
			Map<String, Set<ProfileElements>> destinationMetadataMap = new HashMap<>();

			//input xml parsing
			File inputFile = sourceFile;//new File("/Users/rkonduru/Desktop/sourcePackageProfile.xml");//objMetaDataMerger.getFile("doc1.xml");
			sourceMetadataMap = readMetaDataType(inputFile);
			System.out.println(" sourceMetadataMap: " + sourceMetadataMap);

			//destination xml parsing
			File destinationFile = destFile;//new File("/Users/rkonduru/Desktop/destinationPackageProfile.xml");//objMetaDataMerger.getFile("doc2.xml");
			destinationMetadataMap = readMetaDataType(destinationFile);
			System.out.println(" destinationMetadataMap: " + destinationMetadataMap);

			/*
			 * here in the below code we are iterating through the source and destination maps to compare the meta-data types
			 * and meta-data elements. and later on we create a list of meda-data wrap to update at destination.
			 * the list wrapper contains meta-data elements which are newly added, removed and updated.
			 * later on sending that wrap to update destination xml method::
			 */
			//temp maps to hold meta-data type records which are created newly in source.
			//and later used to compare source and destination to find new and add in destination.
			Map<String, ProfileElements> mapForNewMetadataSource = new HashMap<>();
			Map<String, ProfileElements> mapForNewMetadataDest =  new HashMap<>();

			//contains list of meta-data types and elements to update in destination xml
			ArrayList<MetadataRecWrapToUpdate> destinationMetadataWrapTOUpdate = new ArrayList<MetadataRecWrapToUpdate>();


			//null check for source and destination maps
			if(sourceMetadataMap != null && destinationMetadataMap != null ) {//Map<classAccesses, Map<TestClass3Name, enableTrue>>
				Set<String> tempSourceMetadataMap = sourceMetadataMap.keySet();//tempSourceMetadataMap == classAccesses,pageAccesses,userPermissions
				System.out.println("tempSourceMetadataMap " + tempSourceMetadataMap.size() + "  " + tempSourceMetadataMap);

				for(String tempMetaDataTypeSource: tempSourceMetadataMap) {//tempStr == classAccesses

					MetadataRecWrapToUpdate tempMetadataRecWrapToUpdate = new MetadataRecWrapToUpdate();

					Set<ProfileElements> tempDestMetaDataElements = destinationMetadataMap.get(tempMetaDataTypeSource);//Map<TestClass3Name, enableTrue>
					Set<ProfileElements> tempSorMetaDataElements = sourceMetadataMap.get(tempMetaDataTypeSource);//Map<TestClass3Name, enableTrue>
					//Set<ProfileElements> metadataRecSet = new HashSet<>();
					Set<ProfileElements> metadataRecSetToRemove = new HashSet<>();

					for(ProfileElements tempstr1 :tempSorMetaDataElements) {//tempstr1 ==TestClass3Name ,enableTrue,TestClass3Name-enableTrue

						mapForNewMetadataSource.put(tempstr1.getPreparedKey() ,tempstr1);

						if(tempDestMetaDataElements != null) {

							for(ProfileElements tempstrDest1 :tempDestMetaDataElements) {

								mapForNewMetadataDest.put(tempstrDest1.getPreparedKey(), tempstrDest1);

								if(tempstr1.getName().equals(tempstrDest1.getName())) {//recProfileElements.getPreparedKey

									if(!tempstr1.getPreparedKey().equals(tempstrDest1.getPreparedKey())) {

										//metadataRecSet.add(tempstr1);
										metadataRecSetToRemove.add(tempstrDest1);

									}//equals prepare key for update check

								}//equals 

							}//tempDestMetaDataElements

						}//tempDestMetaDataElements null check

					}//tempSorMetaDataElements

					// these debugs will provide us info related to meta-data elements related to a type 
					// where these maps are used to compare newly added elements in source
					System.out.println("mapForNewMetadataSource  + " + " " + tempMetaDataTypeSource + " " + mapForNewMetadataSource.size() + "  " + mapForNewMetadataSource);
					System.out.println("mapForNewMetadataDest  + " + " " + tempMetaDataTypeSource + " " + mapForNewMetadataDest.size() + "  " + mapForNewMetadataDest);


					//for loop for source to add newly added tags.
					/*for(String tempStrTOAddNewSrc: mapForNewMetadataSource.keySet()) {

						if(!mapForNewMetadataDest.containsKey(tempStrTOAddNewSrc)) {

							metadataRecSet.add(mapForNewMetadataSource.get(tempStrTOAddNewSrc));

						}//contains key

					}//for mapForNewMetadataSource
					**/
					//for loop for destination to remove tags which are not present in source.
					for(String tempToRemoveFrDest: mapForNewMetadataDest.keySet()) {

						if(!mapForNewMetadataSource.containsKey(tempToRemoveFrDest)) {

							metadataRecSetToRemove.add(mapForNewMetadataDest.get(tempToRemoveFrDest));

						}//contains key

					}//for mapForNewMetadataDest

					//clearing map, later to add now elements related to new other meta-data types in the loop.
					mapForNewMetadataSource.clear();
					mapForNewMetadataDest.clear();

					//adding elements to the update wrapper.
					tempMetadataRecWrapToUpdate.setNameType(tempMetaDataTypeSource);
					//tempMetadataRecWrapToUpdate.setMetadataRecSet(metadataRecSet);
					tempMetadataRecWrapToUpdate.setMetadataRecSetToRemove(metadataRecSetToRemove);
					destinationMetadataWrapTOUpdate.add(tempMetadataRecWrapToUpdate);//this is for updating in enable tag under a class

				}//for tempSourceMetadataMap close

				System.out.println("destinationMetadataWrapTOUpdate  + " + destinationMetadataWrapTOUpdate);

			}//if null check close for source and destination

			//calling update destination method.
			updateDestinationXml(destinationMetadataWrapTOUpdate, destinationFile);


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * This method contains update functionality for destination xml.
	 * 
	 * */
	public static void updateDestinationXml(ArrayList<MetadataRecWrapToUpdate> destinationMetadataWrapNew, File destinationFile) {

		try {
			//Api Methods to read xml
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document docDestinationFile = dBuilder.parse(destinationFile);
			docDestinationFile.getDocumentElement().normalize();
			NodeList nList;

			//iterating through update wrapper
			for(MetadataRecWrapToUpdate tempTypes: destinationMetadataWrapNew){

				//getting remove set
				Set<ProfileElements> tempProfileValuesToRemove = tempTypes.getMetadataRecSetToRemove();
				if(tempProfileValuesToRemove.size() >0) {

					//Removing tags in destination. for classAccesses
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

					//Removing tags in destination. for pageAccesses
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

					//Removing tags in destination. for userPermissions
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

				}//tempProfileValuesToRemove size condition check close.

				//getting add set
				Set<ProfileElements> tempProfileValuesToAdd = tempTypes.getMetadataRecSet();
				if(tempProfileValuesToAdd.size() >0) {

					for(ProfileElements tempProfile : tempProfileValuesToAdd) {

						if(tempProfile.getName() != null && tempProfile.getEnabled() != null){

							//System.out.println("Name + " + tempProfile.getName() + " Enabled + " + tempProfile.getEnabled());
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
							}//classAccesses equals

							if(tempTypes.getNameType().equals("pageAccesses")) {
								Element name = docDestinationFile.createElement("pageAccesses");
								Element apexPage = docDestinationFile.createElement("apexPage");
								apexPage.appendChild(docDestinationFile.createTextNode(tempProfile.getName())); 
								Element apexPageEnable = docDestinationFile.createElement("enabled");
								apexPageEnable.appendChild(docDestinationFile.createTextNode(tempProfile.getEnabled()));
								name.appendChild(apexPage);
								name.appendChild(apexPageEnable);
								root.appendChild(name);	
							}//pageAccesses equals

							if(tempTypes.getNameType().equals("userPermissions")) {
								Element name = docDestinationFile.createElement("userPermissions");
								Element apexPage = docDestinationFile.createElement("name");
								apexPage.appendChild(docDestinationFile.createTextNode(tempProfile.getName())); 
								Element apexPageEnable = docDestinationFile.createElement("enabled");
								apexPageEnable.appendChild(docDestinationFile.createTextNode(tempProfile.getEnabled()));
								name.appendChild(apexPage);
								name.appendChild(apexPageEnable);
								root.appendChild(name);	
							}//userPermissions equals

						}//if tempProfile null check close 

					}//for loop tempProfileValuesToAdd close

				}//if  tempProfileValuesToAdd close for add elements

			}//for destinationMetadataWrapNew close

			//Pretty print for console.
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
	/*
	 * This method is useful for creating a map of meta-data type by reading xml using dom parsing api.
	 * 
	 * */
	public static  Map<String,Set<ProfileElements>> readMetaDataType(File inputFile1) {
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
	/*
	 * This method is useful to prepare a map with meta-data type and meta-data records
	 * 
	 * */
	public static Map<String, Set<ProfileElements>> createNodemap(NodeList nList,String keyType,String tagName){		
		Map<String, Set<ProfileElements>> mapNodeData = new HashMap<>();
		Map<String, String> mapNodeElementData = new HashMap<>();
		Set<ProfileElements> setProfileElements = new HashSet<>();

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

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
			System.out.println( "mapNodeElementData "+ keyType + " " +setProfileElements.size());
			mapNodeData.put(keyType,setProfileElements);
		}
		return mapNodeData;
	}
	/*
	 * This method is useful to print xml structure in console
	 * 
	 * */
	public static final void prettyPrint(Document xml) throws Exception {

		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		Writer out = new StringWriter();
		tf.transform(new DOMSource(xml), new StreamResult(out));
		System.out.println(out.toString());

	}
}