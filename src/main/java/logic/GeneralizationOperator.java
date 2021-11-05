package logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import IOHelper.OntologyHelper;
import IOHelper.TaxonomyHelper;
//import Main.TransferProcess;
import OWLImpl.WorkflowImpl;
import interfaces.NodeInterface;

public class GeneralizationOperator {
	
	public List<WorkflowImpl> workflows = new ArrayList<WorkflowImpl>();

	public static List<WorkflowImpl> GeneralizationOperator_caller(OntologyHelper srcOntology) {
		List<WorkflowImpl> workflows = getWorkflows();
		List<WorkflowImpl> workflows_1 = generalization_caller(2, workflows);
		return workflows_1;

	}
	public static List<WorkflowImpl> getWorkflows() {
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
				//so we need to simplify the workflow name value at line 46
				System.out.println(key+" belongs to "+nodesToWorkflow.get(key));
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
	
	public static List<WorkflowImpl> generalization_caller(int itterations, List<WorkflowImpl> workflows) {
		for(WorkflowImpl w: workflows){

			for(NodeInterface n: w.nodes) {
			    for (int i = 0; i < itterations; i++) {
			    	n = nodeGeneralization(n);
			      }  
				
				
				
				//Task-Flughafen_Hierarchie
	
			}
			// output each workflow w into svg file
			System.out.println(workflows.get(1).semanticDescription);
			w.exportTo("./generalization/");
			
		}
		return workflows;
		
	}
	public static NodeInterface nodeGeneralization(NodeInterface n) {
		if(n.isTask()) {
			if(n.isClass()) {
				
				//Generalisierung von einem Knoten der eine Class ist (nicht ein Blatt)
				OWLClass oneStepAncestor_1 = TaxonomyHelper.sourceTaxonomy.getOWLClassOf(n);
				OWLClass oneStepAncestor_after = TaxonomyHelper.sourceTaxonomy.getParentOf(oneStepAncestor_1);
				System.out.println("1: " + oneStepAncestor_after.getIRI().getFragment());
				//prüft ob die Generalisierung nicht zu weit geht (Task_Flughafen_Hierarchie darf nicht erreicht werden)
				// content equals (feedback)
				if (!"Task_Flughafen_Hierarchie".equals(oneStepAncestor_after.getIRI().getFragment())) {
					n.setSemanticDescription(oneStepAncestor_after.getIRI().getFragment());
					/*
					String key = " http : xxx"
							String value = ontologyHelper.partOf.getValueByKey("http:...");
							*/
					
				} 
						
				
				
				
			} else {
				//Generalisierung eines Knoten, der ein Task ist
				//System.out.println(".............................................................");
				
				OWLNamedIndividual nodeOWLNamedIndividual = TaxonomyHelper.sourceTaxonomy.getOWLNamedIndividualOf(n);
				OWLClass oneStepAncestor = TaxonomyHelper.sourceTaxonomy.getParentOf(nodeOWLNamedIndividual);
				System.out.println("Leaf Node description:");
				System.out.println(nodeOWLNamedIndividual.getIRI().getFragment());
				System.out.println("Its parent node description:");
				System.out.println(oneStepAncestor.getIRI().getFragment());
				//Generalization: replace leaf node description with its parent node description
				n.setSemanticDescription(oneStepAncestor.getIRI().getFragment());
				n.setClass(true);
			}
		}
		

		return n;
	}
}
