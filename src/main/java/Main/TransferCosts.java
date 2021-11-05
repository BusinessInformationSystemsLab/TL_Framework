package Main;

import java.io.IOException;
import java.util.List;

import IOHelper.OntologyHelper;
import IOHelper.TaxonomyHelper;
import OWLImpl.WorkflowImpl;
import logic.GeneralizationOperator;
import logic.SequenceOperator;
import logic.BlockOperator;
import logic.PartiDepOperator;
import logic.DeadendOperator;
import logic.LoopOperator;

public class TransferCosts {

	public TransferCosts() {
		// TODO Auto-generated constructor stub
		

	}
	public static void main(String args[]) throws IOException {
			 OntologyHelper.sourceOntologyHelper = new OntologyHelper("./Evaluation/data sets/EVER 2"
			 		+ "/airport handling of lugguage/Ontologie_Flughafen_all_ID.owl",
			 		TaxonomyHelper.sourceTaxonomy);
		 OntologyHelper.targetOntologyHelper = new OntologyHelper("./Evaluation/data sets/EVER 2/SAP warehouse management/Ontologie_SAP_all_ID.owl",TaxonomyHelper.targetTaxonomy);
		 List<WorkflowImpl> workflows = GeneralizationOperator.GeneralizationOperator_caller(OntologyHelper.sourceOntologyHelper);
		
	 
		 //implementation von LoopOperator
		 OntologyHelper.sourceOntologyHelper = new OntologyHelper("./Evaluation/data sets/EVER 2"
			 		+ "/airport handling of lugguage/Ontologie_Flughafen_all_ID.owl",
			 		TaxonomyHelper.sourceTaxonomy);
			//genOperator = new GeneralizationOperator(OntologyHelper.sourceOntologyHelper);
		 
		
		
		LoopOperator loopOperator = new LoopOperator(OntologyHelper.sourceOntologyHelper);
		loopOperator.onOntology("./results_gen_und_abs/loopOperator/", workflows);
		
		
		 //implementation von BlockOperator
		 OntologyHelper.sourceOntologyHelper = new OntologyHelper("./Evaluation/data sets/EVER 2"
			 		+ "/airport handling of lugguage/Ontologie_Flughafen_all_ID.owl",
			 		TaxonomyHelper.sourceTaxonomy);
			//genOperator = new GeneralizationOperator(OntologyHelper.sourceOntologyHelper);
		 
		
		 workflows = GeneralizationOperator.GeneralizationOperator_caller(OntologyHelper.sourceOntologyHelper);
		 BlockOperator blockOperator = new BlockOperator(OntologyHelper.sourceOntologyHelper);
		 blockOperator.onOntology("./results_gen_und_abs/blockOperator/", workflows);		
		
		 //implementation von SequenceOperator
		 OntologyHelper.sourceOntologyHelper = new OntologyHelper("./Evaluation/data sets/EVER 2"
			 		+ "/airport handling of lugguage/Ontologie_Flughafen_all_ID.owl",
			 		TaxonomyHelper.sourceTaxonomy);
			//genOperator = new GeneralizationOperator(OntologyHelper.sourceOntologyHelper);
		 
		
		 workflows = GeneralizationOperator.GeneralizationOperator_caller(OntologyHelper.sourceOntologyHelper);
		 SequenceOperator sequenceOperator = new SequenceOperator(OntologyHelper.sourceOntologyHelper);
		 sequenceOperator.onOntology("./results_gen_und_abs/sequenceOperator/", workflows);		
		
		 
		//implementation von DeadendOperator
		 OntologyHelper.sourceOntologyHelper = new OntologyHelper("./Evaluation/data sets/EVER 2"
			 		+ "/airport handling of lugguage/Ontologie_Flughafen_all_ID.owl",
			 		TaxonomyHelper.sourceTaxonomy);
			//genOperator = new GeneralizationOperator(OntologyHelper.sourceOntologyHelper);
		 
		
		workflows = GeneralizationOperator.GeneralizationOperator_caller(OntologyHelper.sourceOntologyHelper);
		DeadendOperator deadendOperator = new DeadendOperator(OntologyHelper.sourceOntologyHelper);
		deadendOperator.onOntology("./results_gen_und_abs/deadendOperator/", workflows);		
		
		 
		
		//implementation von PartiDeopOperator 
		 OntologyHelper.sourceOntologyHelper = new OntologyHelper("./Evaluation/data sets/EVER 2"
			 		+ "/airport handling of lugguage/Ontologie_Flughafen_all_ID.owl",
			 		TaxonomyHelper.sourceTaxonomy);
			//genOperator = new GeneralizationOperator(OntologyHelper.sourceOntologyHelper);
		 
		//List<WorkflowImpl> workflows = GeneralizationOperator.GeneralizationOperator_caller(OntologyHelper.sourceOntologyHelper);
		 workflows = GeneralizationOperator.GeneralizationOperator_caller(OntologyHelper.sourceOntologyHelper);
		 PartiDepOperator partiDepOperator = new PartiDepOperator(OntologyHelper.sourceOntologyHelper);
		 partiDepOperator.onOntology("./results_gen_und_abs/partiDepOperator/", workflows);  
		 
		workflows = GeneralizationOperator.GeneralizationOperator_caller(OntologyHelper.sourceOntologyHelper);
		 for(WorkflowImpl w: workflows){
			 w.exportTo("./results_generalization/");
		 }

	}
}
