package Main;
import java.io.IOException;
import java.util.ArrayList;

import IOHelper.OntologyHelper;
import IOHelper.TaxonomyHelper;
import logic.BlockOperator;
import logic.DeadendOperator;
import logic.LoopOperator;
import logic.PartiDepOperator;
import logic.SequenceOperator;

public class EmpiricalExperiment {

	public EmpiricalExperiment() {
		// TODO Auto-generated constructor stub
	}
	public static void main(String args[]) throws IOException {
		ArrayList<String[]> SVGPaths = new ArrayList<String[]>();
		SVGPaths.add(new String[] {"./Evaluation/data sets/EVER 2/airport handling of lugguage/Ontologie_Flughafen_all_ID.owl",
		"./Evaluation/Empirical analysis of algorithms/1 Elementary operations/Block abstraction/airport/"});
		SVGPaths.add(new String[] {"./Evaluation/data sets/EVER 2/airport handling of lugguage/Ontologie_Flughafen_all_ID.owl",
		 "./Evaluation/Empirical analysis of algorithms/1 Elementary operations/Actor abstraction/airport/"});
		SVGPaths.add(new String[] {"./Evaluation/data sets/EVER 2/airport handling of lugguage/Ontologie_Flughafen_all_ID.owl",
		 "./Evaluation/Empirical analysis of algorithms/1 Elementary operations/Sequential abstraction/airport/"});
		SVGPaths.add(new String[] {"./Evaluation/data sets/EVER 2/airport handling of lugguage/Ontologie_Flughafen_all_ID.owl",
		 "./Evaluation/Empirical analysis of algorithms/1 Elementary operations/Loop abstraction/airport/"});
		SVGPaths.add(new String[] {"./Evaluation/data sets/EVER 2/airport handling of lugguage/Ontologie_Flughafen_all_ID.owl",
		 "./Evaluation/Empirical analysis of algorithms/1 Elementary operations/Deadend abstraction/airport/"});
	
		SVGPaths.add(new String[] { "./Evaluation/data sets/EVER 2/SAP warehouse management/Ontologie_SAP_all_ID.owl",
		"./Evaluation/Empirical analysis of algorithms/1 Elementary operations/Block abstraction/SAP/"});
		SVGPaths.add(new String[] { "./Evaluation/data sets/EVER 2/SAP warehouse management/Ontologie_SAP_all_ID.owl",
		 "./Evaluation/Empirical analysis of algorithms/1 Elementary operations/Actor abstraction/SAP/"});
		SVGPaths.add(new String[] { "./Evaluation/data sets/EVER 2/SAP warehouse management/Ontologie_SAP_all_ID.owl",
		 "./Evaluation/Empirical analysis of algorithms/1 Elementary operations/Sequential abstraction/SAP/"});
		SVGPaths.add(new String[] {"./Evaluation/data sets/EVER 2/SAP warehouse management/Ontologie_SAP_all_ID.owl",
		 "./Evaluation/Empirical analysis of algorithms/1 Elementary operations/Loop abstraction/SAP/"});
		SVGPaths.add(new String[] {"./Evaluation/data sets/EVER 2/SAP warehouse management/Ontologie_SAP_all_ID.owl",
		 "./Evaluation/Empirical analysis of algorithms/1 Elementary operations/Deadend abstraction/SAP/"});
		
		SVGPaths.add(new String[] { "./Evaluation/data sets/EMISA2015/Ontologie_EMISA_2015_all_ID.owl",
		"./Evaluation/Empirical analysis of algorithms/1 Elementary operations/Block abstraction/EMISA2015/"});
		SVGPaths.add(new String[] { "./Evaluation/data sets/EMISA2015/Ontologie_EMISA_2015_all_ID.owl",
		 "./Evaluation/Empirical analysis of algorithms/1 Elementary operations/Actor abstraction/EMISA2015/"});
		SVGPaths.add(new String[] { "./Evaluation/data sets/EMISA2015/Ontologie_EMISA_2015_all_ID.owl",
		 "./Evaluation/Empirical analysis of algorithms/1 Elementary operations/Sequential abstraction/EMISA2015/"});
		SVGPaths.add(new String[] {"./Evaluation/data sets/EMISA2015/Ontologie_EMISA_2015_all_ID.owl",
		 "./Evaluation/Empirical analysis of algorithms/1 Elementary operations/Loop abstraction/EMISA2015/"});
		SVGPaths.add(new String[] {"./Evaluation/data sets/EMISA2015/Ontologie_EMISA_2015_all_ID.owl",
		 "./Evaluation/Empirical analysis of algorithms/1 Elementary operations/Deadend abstraction/EMISA2015/"});
		for(int j = 0; j<3;j++) {
			 OntologyHelper.sourceOntologyHelper = new OntologyHelper(SVGPaths.get(j*5)[0],TaxonomyHelper.sourceTaxonomy);
			 PartiDepOperator participantOperator = new PartiDepOperator(OntologyHelper.sourceOntologyHelper);
			 SequenceOperator sequenceOperator = new SequenceOperator(OntologyHelper.sourceOntologyHelper);
			 LoopOperator loopOperator = participantOperator.loopOperator;
			 BlockOperator BlockOperator = participantOperator.blockOperator;
			 DeadendOperator deadendOperator = participantOperator.blockOperator.deadendOperator;
			for(int i = 0;i<5;i++) {
				
				String SVGPath =SVGPaths.get(j*5+i)[1];
				System.out.println(SVGPaths.get(j*5)[0]+" \n*saved at* "+SVGPath);
				 switch(i) {
				 case  0:BlockOperator.onOntology(SVGPath);break;
				 case  1:(participantOperator).onOntology(SVGPath);break;
				 case  2:sequenceOperator.onOntology(SVGPath);break;
				 case  3:loopOperator.onOntology(SVGPath);break;
				 case  4:deadendOperator.onOntology(SVGPath);break;
				 }
			}
			OntologyHelper.sourceOntologyHelper.blockFragments = BlockOperator.blocks;
			OntologyHelper.sourceOntologyHelper.sequentialFragments = sequenceOperator.sequences;
			OntologyHelper.sourceOntologyHelper.participantFragments = participantOperator.fragments;
			OntologyHelper.sourceOntologyHelper.deadendFragments = deadendOperator.deadends;
			OntologyHelper.sourceOntologyHelper.loopFragments = loopOperator.loops;

			OntologyHelper.sourceOntologyHelper.writeAbstractionIntoOntology();
			OntologyHelper.sourceOntologyHelper = null;
		}
	
		 
	}
}
