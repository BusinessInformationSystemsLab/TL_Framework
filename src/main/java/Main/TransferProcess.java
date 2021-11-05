package Main;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import IOHelper.OntologyHelper;
import IOHelper.TaxonomyHelper;
import OWLImpl.MappingGenerator;
import OWLImpl.MappingImpl;
import OWLImpl.WorkflowImpl;
import interfaces.FragmentInterface;
import interfaces.MappingInterface;
import interfaces.NodeInterface;
import logic.BlockOperator;
import logic.DeadendOperator;
import logic.LoopOperator;
import logic.PartiDepOperator;
import logic.SequenceOperator;

public class TransferProcess {

	public TransferProcess() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String args[]) {

		 OntologyHelper.sourceOntologyHelper = new OntologyHelper("./Evaluation/data sets/EVER 2"
			 		+ "/airport handling of lugguage/Ontologie_Flughafen_all_ID.owl",TaxonomyHelper.sourceTaxonomy);
		 OntologyHelper.targetOntologyHelper = new OntologyHelper("./Evaluation/data sets/EVER 2/SAP warehouse management/Ontologie_SAP_all_ID.owl",TaxonomyHelper.targetTaxonomy);


		List<WorkflowImpl> workflows = new ArrayList<WorkflowImpl>();
		OntologyHelper srcOntology = OntologyHelper.sourceOntologyHelper;
		HashMap<String, String> data = srcOntology.partOf;
		Collection<String> workflowNames = new HashSet<String>(data.values());

			for(String name : workflowNames) {
				TransferProcess p= new TransferProcess();
				WorkflowImpl workflow = new WorkflowImpl();
				workflow.semanticDescription = name;
				for(NodeInterface node:OntologyHelper.sourceOntologyHelper.nodes) {
					String key =  "http://owl.api.wf#"+node.getSemanticDescription()+":"+node.getId();
					if(data.containsKey(key)&&data.get(key).contentEquals(name)) {
						workflow.nodes.add(node);
					}
				}
				workflows.add(workflow);
			}
	
		PartiDepOperator participantOperator = new PartiDepOperator(OntologyHelper.sourceOntologyHelper),
				targetPar = new PartiDepOperator(OntologyHelper.targetOntologyHelper);
		OntologyHelper.sourceOntologyHelper.setLoopFragments(participantOperator.loopOperator.fragments);
		OntologyHelper.sourceOntologyHelper.setDeadEndFragments(participantOperator.blockOperator.deadendOperator.fragments);
		OntologyHelper.sourceOntologyHelper.setBLockFragments(participantOperator.blockOperator.fragments);
		OntologyHelper.sourceOntologyHelper.setParticipantFragments(participantOperator.fragments);
		OntologyHelper.sourceOntologyHelper.setSequentialFragments((new SequenceOperator(OntologyHelper.sourceOntologyHelper)).fragments);
		
/***/
		OntologyHelper.targetOntologyHelper.setLoopFragments(targetPar.loopOperator.fragments);
		OntologyHelper.targetOntologyHelper.setDeadEndFragments(targetPar.blockOperator.deadendOperator.fragments);
		OntologyHelper.targetOntologyHelper.setBLockFragments(targetPar.blockOperator.fragments);
		OntologyHelper.targetOntologyHelper.setParticipantFragments(targetPar.fragments);
		OntologyHelper.targetOntologyHelper.setSequentialFragments((new SequenceOperator(OntologyHelper.targetOntologyHelper)).fragments);
/***/
		for(WorkflowImpl w:workflows) {
			WorkflowImpl w1 = greedyTransferStrategy(w);
			WorkflowImpl w2 = analogicalMapping(w1);
			//WorkflowImpl w2 = analogicalMapping(w);
			// WorkflowImpl w3 = Generalization(w); 
			String path = "./Evaluation/GreedyTransferProcess/abstractResults(DEC)/test1";
			//String path = "./Evaluation/data sets/Gen/";
			

			OntologyHelper.exportTo(path,w2.semanticDescription.split("#")[1].split(":")[0],w2,w2.edges);
		}
	}
	public static WorkflowImpl greedyTransferStrategy(WorkflowImpl w) {
		// found mappings between the source domain and target domain of w,
		  ArrayList<MappingImpl> selectedMappings = new ArrayList<MappingImpl>();
		/**
		 * for each fragment, choose a maximum mapping from mappings
		 * fragments are sorted according to their mapping value,**/
		  ArrayList<FragmentInterface> sortedArray = new ArrayList<FragmentInterface>();
		/**Greedy strategy: select fragments along the sortedArray, avoiding conflicts,**/
		  ArrayList<FragmentInterface> selectedFragments = new ArrayList<FragmentInterface>();
		  ArrayList<FragmentInterface> selectedTargetFragments = new ArrayList<FragmentInterface>();
		  

/**--*/   
		Set<MappingInterface> temp = new HashSet<MappingInterface>();
		/**MappingGenerator.mappingGenerator is an implementation of anchor mapping operator**/
		temp.addAll(MappingGenerator.mappingGenerator.blockMappings);
		if(MappingGenerator.mappingGenerator.deadendMappings!=null)
			temp.addAll(MappingGenerator.mappingGenerator.deadendMappings);
		temp.addAll(MappingGenerator.mappingGenerator.sequentialMappings);
		temp.addAll(MappingGenerator.mappingGenerator.participantMappings);
		

/**--*/   
		List<MappingImpl> tmp = new ArrayList<MappingImpl>();
		/**Greedy transfer starts with sorting all mappings between two ontologies**/
		for(MappingInterface m: temp) {
			if(!Collections.disjoint(w.nodes, m.getSourceFragment().getInnerNodes())) {
				tmp.add((MappingImpl) m);
			}
		}
/**--*/
	    Collections.sort(tmp);
	    Collections.reverse(tmp);
/**--*/
	    Set<NodeInterface> coveredNodes = new HashSet<NodeInterface>();
	    for(MappingImpl m:tmp) {
	    	if(Collections.disjoint(coveredNodes, m.getSourceFragment().getInnerNodes())) {
	    		 selectedFragments.add(m.getSourceFragment());
	    		 selectedTargetFragments.add(m.getTargetFragment());
	    		 selectedMappings.add(m);
	    		coveredNodes.addAll(m.getSourceFragment().getInnerNodes());
	    	}
	    }
	    
   	WorkflowImpl transferedWorkflow = (WorkflowImpl) w;
   	transferedWorkflow.edges = new ArrayList<NodeInterface[]>();
	    for(FragmentInterface x: selectedFragments) {
	    	transferedWorkflow.nodes.removeAll(x.getInnerNodes());
	    	transferedWorkflow.nodes.add(x.getAbtractTask());
	    	NodeInterface node_x = x.getAbtractTask();
	    	for(FragmentInterface y: selectedFragments) {
	    		NodeInterface node_y = y.getAbtractTask();
	    		if(y.getInnerNodes().contains(x.getPreanchor())) {
	    			node_y.setSucceedingNode(node_x);
	    			node_y.getAllSucceedingNodes().clear();
	    			node_y.addIntoSucceedingNode(node_x);
	    			
	    			node_x.setPrecedingNode(node_y);
	    			node_x.getAllPrecedingNodes().clear();
	    			node_x.addIntoPrecedingNodes(node_y);
	    		}
	    		if(y.getInnerNodes().contains(x.getPostanchor())) {
	    			node_x.setSucceedingNode(node_y);
	    			node_x.getAllSucceedingNodes().clear();
	    			node_x.addIntoSucceedingNode(node_y);
	    			
	    			node_y.setPrecedingNode(node_x);
	    			node_y.getAllPrecedingNodes().clear();
	    			node_y.addIntoPrecedingNodes(node_x);
	    		}
	    	}
	    }
	    for(FragmentInterface x: selectedFragments) {
	    	NodeInterface node = x.getAbtractTask();
	    	int index =  selectedFragments.indexOf(x);
	    	NodeInterface target = selectedTargetFragments.get(index).getAbtractTask();
	    	node.setSemanticDescription(target.getSemanticDescription());
	    	node.setActor(target.getActor());
	    }
	    for(NodeInterface node:transferedWorkflow.nodes) {
	    	for(NodeInterface next : node.getAllSucceedingNodes()) {
	    		if(transferedWorkflow.nodes.contains(next)) {
	    			transferedWorkflow.edges.add(new NodeInterface[]{node,next});
	    		}
	    	}
	    	for(NodeInterface last : node.getAllPrecedingNodes()) {
	    		if(transferedWorkflow.nodes.contains(last)) {
	    			if(!transferedWorkflow.edges.contains(new NodeInterface[] {last,node})) {
	    				transferedWorkflow.edges.add(new NodeInterface[] {last,node});
	    			}
	    		}
	    	}
	    }
	    return transferedWorkflow;
	}
	
	public static WorkflowImpl analogicalMapping(WorkflowImpl workflow) {
		HashMap<NodeInterface, NodeInterface> analogicalMapping = getAnalogicalMapping();
		System.out.println(analogicalMapping);
		analogicalMapping.forEach(
               (key, value)
                   -> {if (value != null) {System.out.println(key.getSemanticDescription() + " = " + value.getSemanticDescription());}});
         
		
		for(NodeInterface node:workflow.nodes) {
			if(!node.isAbstractNode() && OntologyHelper.sourceOntologyHelper.nodes.contains(node)) {
				NodeInterface target = analogicalMapping.get(node);
				if((node.isTask()) && target != null) {
					node.setSemanticDescription(target.getSemanticDescription());
					node.setActor(target.getActor()); //target != null hinzugefügt
				}
			}
		}
		return workflow;
	}
	
	public static HashMap<NodeInterface, NodeInterface> getAnalogicalMapping(){
		HashMap<NodeInterface, NodeInterface> analogicalMapping = new HashMap<NodeInterface, NodeInterface>();


		HashMap<String, String> temp = new HashMap<String,String>();

		Set<NodeInterface> sourceNodes  = OntologyHelper.sourceOntologyHelper.nodes;
		Iterator<NodeInterface> iter = sourceNodes.iterator();
		File file = new File("./Evaluation/data sets/Input_data/analogical mapping.txt");
		Scanner scanner;
		try {
			scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				String[] data = scanner.nextLine().split(";");
				String sourceNode = data[0];
				String targetNode = null;
				if(Array.getLength(data)>1)
					targetNode = data[1];
				temp.put(sourceNode, targetNode);
				System.out.println(iter.next().getSemanticDescription()+"====="+sourceNode +"->"+ targetNode);
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
					targetMap.put(value, x);					
				}
			}
			
			for(NodeInterface x: OntologyHelper.sourceOntologyHelper.nodes) {
				if(x.isTask()) {
					String key = x.getSemanticDescription().replace('_',' ')
							.replace('-', ' ')
							.replace("/"," ")
							.replaceAll("[^a-zA-ZäöüÄÖÜß\\n]"," ")
							.trim().replaceAll(" +", " ");
					sourceMap.put(key, x);
					NodeInterface source = x;
					NodeInterface target = targetMap.get(temp.get(key));
					analogicalMapping.put(source, target);
				}
				
			}
		
			for(NodeInterface x : OntologyHelper.sourceOntologyHelper.nodes) {
				if(x.isTask()) assert analogicalMapping.containsKey(x):" "+analogicalMapping.keySet().size();
			}
			return analogicalMapping;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return analogicalMapping;
	}
}
