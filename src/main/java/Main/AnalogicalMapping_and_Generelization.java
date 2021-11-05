package Main;

import java.io.IOException;
import java.util.List;

import org.javatuples.Quintet;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import IOHelper.OntologyHelper;
import IOHelper.TaxonomyHelper;
import OWLImpl.WorkflowImpl;
import interfaces.NodeInterface;
import logic.GeneralizationOperator;
import logic.SequenceOperator;
import logic.BlockOperator;
import logic.PartiDepOperator;
import logic.DeadendOperator;
import logic.LoopOperator;
import logic.AnalogesMapping;
import org.javatuples.Quintet;
import org.apache.commons.io.FileUtils;
import java.io.PrintWriter;


public class AnalogicalMapping_and_Generelization {

	public AnalogicalMapping_and_Generelization() {
		/* Diese Datei initiiert eine Kombination aus Analogen Mapping und Generalisierung. Dies geschieht mit Hilfe eines Mapping Werts, der als Transferstrategie gilt. 
		 * Eine ausführliche Beschreibung des Mapping Werts und somit der Transferstrategie kann der Dokumentation entnommen werden
		 */
		

	}
	public static void main(String args[]) throws IOException {

		// Ontologie initiieren
			 OntologyHelper.sourceOntologyHelper = new OntologyHelper("./Evaluation/data sets/EVER 2"
			 		+ "/airport handling of lugguage/Ontologie_Flughafen_all_ID.owl",
			 		TaxonomyHelper.sourceTaxonomy);
		 OntologyHelper.targetOntologyHelper = new OntologyHelper("./Evaluation/data sets/EVER 2/SAP warehouse management/Ontologie_SAP_all_ID.owl",TaxonomyHelper.targetTaxonomy);
		 
		 List<Quintet<String, NodeInterface, String, NodeInterface, Double>> listAnalogesMappingTasks = AnalogesMapping.getAnalogicalMapping_with_Scores();
		 List<Quintet<String, String, String, String, Double>> listAnalogesMappingTasks_Gen = AnalogesMapping.getAnalogicalMapping_with_Scores_Gen();
		 
		 System.out.println(listAnalogesMappingTasks.size());
		 PrintWriter out = new PrintWriter("filenameergebnisse.txt");
		 
		 System.out.println(listAnalogesMappingTasks_Gen);
		 // Workflows auslesen
		 List<WorkflowImpl> workflows = AnalogesMapping.getWorkflows();
		 // Transfer nach Mapping Wert aufrufen und durchführen
		 List<WorkflowImpl> workflowsAfter = AnalogesMapping.TransferMappingValue(workflows, listAnalogesMappingTasks, listAnalogesMappingTasks_Gen);
		 
		 
		 
		 
		 
}}
