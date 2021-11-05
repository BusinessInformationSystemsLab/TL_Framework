package logic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.javatuples.Quintet;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import IOHelper.OntologyHelper;
import IOHelper.TaxonomyHelper;
//import Main.TransferProcess;
import OWLImpl.WorkflowImpl;
import interfaces.NodeInterface;
import java.util.*;
import org.javatuples.Quintet;

public class AnalogesMapping {
	
	public List<WorkflowImpl> workflows = new ArrayList<WorkflowImpl>();

	
	public static List<Quintet<String, NodeInterface, String, NodeInterface, Double>> getAnalogicalMapping_with_Scores() {
		/*
		 * Die Funktion liest eine CSV Datei mit dem Analogen Mapping Daten aus und bringt diese in folgendes Format
		 * List<Quintet<String, NodeInterface, String, NodeInterface, Double>>
		 * Die Daten aus den Quniteten werden dann für das Mapping benutzt
		 * Hier werden NUR die TASKs ausgelensen, da auf der generalisiserten Ebene keine Zuordnung zu dem Datentyp NodeInterface möglich ist
		 */
		List<Quintet<String, String, String, String, Double>> listAnalogesMappingString = new ArrayList<>();
		List<Quintet<String, String, String, String, Double>> listAnalogesMappingStringGen = new ArrayList<>();
		List<Quintet<String, NodeInterface, String, NodeInterface, Double>> listAnalogesMapping = new ArrayList<>();
		List<String> tempListSourceNodes = new ArrayList<>();
		
		 OntologyHelper.sourceOntologyHelper = new OntologyHelper("./Evaluation/data sets/EVER 2"
			 		+ "/airport handling of lugguage/Ontologie_Flughafen_all_ID.owl",
			 		TaxonomyHelper.sourceTaxonomy);
		 OntologyHelper.targetOntologyHelper = new OntologyHelper("./Evaluation/data sets/EVER 2/SAP warehouse management/Ontologie_SAP_all_ID.owl",TaxonomyHelper.targetTaxonomy);

		File file1 = new File("./Evaluation/data sets/Input_data/analogical_mapping_with_score.txt");
		Scanner scanner;
		try {
			
			scanner = new Scanner(file1, "UTF-8");
	
			while (scanner.hasNextLine()) {

				String[] data = scanner.nextLine().split(";");
				String LevelSource = data[0];
				String NameSource = null;
				NameSource = data[1];
				String LevelTarget = data[2];
				String NameTarget = data[3];
				Double score = Double.parseDouble(data[4]);				
				Quintet<String, String, String, String, Double> quintet = Quintet.with(data[0],
						data[1],
						data[2],
						data[3],
						Double.parseDouble(data[4]));
				listAnalogesMappingString.add(quintet);
				if (!data[0].equals("Task")|(!data[0].equals("Task"))){
					listAnalogesMappingStringGen.add(quintet);
				}
				
				

				
			}
			
			scanner.close();
			
			HashMap<String, NodeInterface> sourceMap = new HashMap<String,NodeInterface>();
			HashMap<String,NodeInterface> targetMap = new HashMap<String, NodeInterface>();
	
			for(NodeInterface x: OntologyHelper.targetOntologyHelper.nodes) {
				if(x.isTask()) {
					String value  = x.getSemanticDescription().replace('_',' ')
							.replace('-', ' ')
							.replace("/"," ")
							.replaceAll("[^a-zA-ZäöüÄÖÜß\\n]"," ")
							.trim().replaceAll(" +", " ");
					//System.out.println(value + " = " +x.getSemanticDescription());
					targetMap.put(value, x);		
					
			}}
			
			for(NodeInterface x: OntologyHelper.sourceOntologyHelper.nodes) {
				if(x.isTask()) {
					
					String key = x.getSemanticDescription().replace('_',' ')
							.replace('-', ' ')
							.replace("/"," ")
							.replaceAll("[^a-zA-ZäöüÄÖÜß\\n]"," ")
							.trim().replaceAll(" +", " ");
					sourceMap.put(key, x);
					NodeInterface source = x;
					System.out.println("12345676"+key);
					tempListSourceNodes.add(x.getSemanticDescription());
					
					for (Quintet<String, String, String, String, Double> element :listAnalogesMappingString) {
						



						if(element.getValue1().equals(key) ) {
							System.out.println("2!111"+key+"=="+element.getValue1());
							
							
							NodeInterface target = targetMap.get(element.getValue3());
							Quintet<String, NodeInterface, String, NodeInterface, Double> quintetNew = Quintet.with(element.getValue0(),
									source,
									element.getValue2(),
									target,
									element.getValue4());
							listAnalogesMapping.add(quintetNew);
							
							
						}
						
						
					}
					
					
					
					
				}}

		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		 
		 
		
		 System.out.println("!!!!!!!!!"+listAnalogesMappingString);
		System.out.println("??????!!"+tempListSourceNodes);
		System.out.println(listAnalogesMappingString.size());
		return listAnalogesMapping;	
	}
	
	public static List<WorkflowImpl> TransferMappingValue(List<WorkflowImpl> workflows, List<Quintet<String, NodeInterface, String, NodeInterface, Double>> listAnalogesMappingTasks, List<Quintet<String, String, String, String, Double>> listAnalogesMappingTasks_Gen){
		/*
		 * Diese Funktion führt den Transfer mit Generlaisierung und analogen Mapping durch. Dabei wird eine Transferstrategie mitttels Mapping Wert verwendet. 
		 * Eine ausführliche Beschreibung der Transferstrageie kann der Dokumentation entnommen werden 
		 */
		for(WorkflowImpl workflow: workflows){
			for(NodeInterface node:workflow.nodes) {
				
				Double bestScore = 0.00;
				NodeInterface bestNode = node;
	 			if(!node.isAbstractNode() && OntologyHelper.sourceOntologyHelper.nodes.contains(node) && node.isTask()) {
	 				System.out.println("Name Knoten" + node.getSemanticDescription());
	 				//System.out.println("NODE: "+node.getSemanticDescription());
	 				for (Quintet<String, NodeInterface, String, NodeInterface, Double> qunitet : listAnalogesMappingTasks) {
	 					//System.out.println("NODE: "+qunitet);
	 					if (qunitet.getValue1().getSemanticDescription().equals(node.getSemanticDescription())) {
	 						if (qunitet.getValue4()>bestScore) {
	 							bestScore = qunitet.getValue4();
	 						// Mapping Wert: Score * 1 (Da Task Ebene)
	 							bestNode = qunitet.getValue3();
	 							
	 						}
	 					}
	 				}
				}
	 			
	 			if (node.isTask()) {
	 				System.out.println("alt: "+ node.getSemanticDescription());
	 				OWLNamedIndividual nodeOWLNamedIndividual = TaxonomyHelper.sourceTaxonomy.getOWLNamedIndividualOf(node);
					OWLClass oneStepAncestor = TaxonomyHelper.sourceTaxonomy.getParentOf(nodeOWLNamedIndividual);
					System.out.println("Leaf Node description:");
					System.out.println(nodeOWLNamedIndividual.getIRI().getFragment());
					System.out.println("Its parent node description:");
					System.out.println(oneStepAncestor.getIRI().getFragment());
					//Generalization: replace leaf node description with its parent node description
					String KlasseVonNode = oneStepAncestor.getIRI().getFragment();
					String KlasseVonNode_ohneUnterstrich = KlasseVonNode.replace('_', ' ');
					NodeInterface FirstGenNode = node;
					FirstGenNode.setSemanticDescription(KlasseVonNode);
					
					for (Quintet<String, String, String, String, Double> quintet:listAnalogesMappingTasks_Gen) {
						if(quintet.getValue1().equals(KlasseVonNode_ohneUnterstrich)) {
							// Mapping Wert: Score * 0,75
							Double TempBestScore = quintet.getValue4()*0.75;
							if(TempBestScore>bestScore) {
								bestScore = TempBestScore;
	 							bestNode = node;
	 							bestNode.setSemanticDescription(quintet.getValue3());
							}
						}
					}
					OWLClass oneStepAncestor_1 = TaxonomyHelper.sourceTaxonomy.getOWLClassOf(FirstGenNode);
					OWLClass oneStepAncestor_after = TaxonomyHelper.sourceTaxonomy.getParentOf(oneStepAncestor_1);
					if (oneStepAncestor_after != null) {
						System.out.println("1: " + oneStepAncestor_after.getIRI().getFragment());
						//prüft ob die Generalisierung nicht zu weit geht (Task_Flughafen_Hierarchie darf nicht erreicht werden)
						// content equals (feedback)
						String KlasseGen2VonNode_ohneUnterstrich = FirstGenNode.getSemanticDescription().replace('_', ' ');
						if (!"Task_Flughafen_Hierarchie".equals(oneStepAncestor_after.getIRI().getFragment())) {
							for (Quintet<String, String, String, String, Double> quintet:listAnalogesMappingTasks_Gen) {
								if(quintet.getValue1().equals(KlasseGen2VonNode_ohneUnterstrich)) {
									// Mapping Wert: Score * 0,5
									Double TempBestScore = quintet.getValue4()*0.5;
									if(TempBestScore>bestScore) {
										bestScore = TempBestScore;
			 							bestNode = node;
			 							bestNode.setSemanticDescription(quintet.getValue3());
									}
								}
							}
							
							
					
							
						}}
					
					
		
	 			}
	 			
	 			node.setSemanticDescription(bestNode.getSemanticDescription());
				node.setActor(bestNode.getActor()); //target != null hinzugefügt
			}
			workflow.exportTo("./analogesMappingErgebnis_mitgen/");
		}
		return workflows;
	}
	
	public static List<Quintet<String, String, String, String, Double>> getAnalogicalMapping_with_Scores_Gen() {
		/*
		 * Die Funktion liest eine CSV Datei mit dem Analogen Mapping Daten aus und bringt diese in folgendes Format
		 * List<Quintet<String, String, String, String, Double>>
		 * Die Daten aus den Quniteten werden dann für das Mapping benutzt
		 * Hier werden NUR die Generalisierungen ausgelensen, da auf der generalisiserten Ebene keine Zuordnung zu dem Datentyp NodeInterface möglich ist und somit Strings verwendet werden
		 */
		List<Quintet<String, String, String, String, Double>> listAnalogesMappingString = new ArrayList<>();
		List<Quintet<String, String, String, String, Double>> listAnalogesMappingStringGen = new ArrayList<>();
		List<Quintet<String, NodeInterface, String, NodeInterface, Double>> listAnalogesMapping = new ArrayList<>();
		List<String> tempListSourceNodes = new ArrayList<>();
	
		 OntologyHelper.sourceOntologyHelper = new OntologyHelper("./Evaluation/data sets/EVER 2"
			 		+ "/airport handling of lugguage/Ontologie_Flughafen_all_ID.owl",
			 		TaxonomyHelper.sourceTaxonomy);
		 OntologyHelper.targetOntologyHelper = new OntologyHelper("./Evaluation/data sets/EVER 2/SAP warehouse management/Ontologie_SAP_all_ID.owl",TaxonomyHelper.targetTaxonomy);

		File file1 = new File("./Evaluation/data sets/Input_data/analogical_mapping_with_score.txt");
		Scanner scanner;
		try {
			
			scanner = new Scanner(file1, "UTF-8");
	
			while (scanner.hasNextLine()) {
				
				String[] data = scanner.nextLine().split(";");
				String LevelSource = data[0];
				String NameSource = null;
				NameSource = data[1];
				String LevelTarget = data[2];
				String NameTarget = data[3];
				Double score = Double.parseDouble(data[4]);				
				Quintet<String, String, String, String, Double> quintet = Quintet.with(data[0],
						data[1],
						data[2],
						data[3],
						Double.parseDouble(data[4]));
				listAnalogesMappingString.add(quintet);
				if (!data[0].equals("Task")|(!data[0].equals("Task"))){
					listAnalogesMappingStringGen.add(quintet);
				}
				
				
				
			
				
			}
			
			scanner.close();
			


		}catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}

		 
		 
		
		 System.out.println("!!!!!!!!!"+listAnalogesMappingString);
		System.out.println("??????!!"+tempListSourceNodes);
		System.out.println(listAnalogesMappingString.size());
		return listAnalogesMappingStringGen;	
	}
	
	public static List<WorkflowImpl> getWorkflows() {
		/*
		 * Diese Funktion liest die Ontologie aus für die einzelnen Worklfows
		 */
		
		 OntologyHelper.sourceOntologyHelper = new OntologyHelper("./Evaluation/data sets/EVER 2"
			 		+ "/airport handling of lugguage/Ontologie_Flughafen_all_ID.owl",
			 		TaxonomyHelper.sourceTaxonomy);
		 OntologyHelper.targetOntologyHelper = new OntologyHelper("./Evaluation/data sets/EVER 2/SAP warehouse management/Ontologie_SAP_all_ID.owl",TaxonomyHelper.targetTaxonomy);

		 List<WorkflowImpl> workflows = new ArrayList<WorkflowImpl>();
			OntologyHelper srcOntology = OntologyHelper.sourceOntologyHelper;

			HashMap<String, String> nodesToWorkflow = srcOntology.partOf;
			for(String key : nodesToWorkflow.keySet()) {
				//key is the node description
				//value is the workflow name but in form of "http://owl.api ...."
				//so we need to simplify the workflow name value
				//System.out.println(key+" belongs to "+nodesToWorkflow.get(key));
			}
			Collection<String> workflowNames = new HashSet<String>(nodesToWorkflow.values());

				for(String name : workflowNames) {
					WorkflowImpl workflow = new WorkflowImpl();
					for(NodeInterface node:OntologyHelper.sourceOntologyHelper.nodes) {
						String key =  "http://owl.api.wf#"+node.getSemanticDescription()+":"+node.getId();
						if(nodesToWorkflow.containsKey(key)&&nodesToWorkflow.get(key).contentEquals(name)) {
							workflow.nodes.add(node);
						}
					}
					name = name.split("#")[1].split(":")[0];
					workflow.semanticDescription = name;
					workflows.add(workflow);
					
				}
		return workflows;
	}
	
	}
