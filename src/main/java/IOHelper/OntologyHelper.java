package IOHelper;

import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.*;
import static guru.nidi.graphviz.attribute.Records.*;
import static guru.nidi.graphviz.model.Compass.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Rank.RankDir;
import guru.nidi.graphviz.attribute.Rank.RankType;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.LinkSource;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.FunctionalSyntaxDocumentFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import OWLImpl.FragmentType;
import OWLImpl.MappingImpl;
import OWLImpl.NodeImpl;
import OWLImpl.Type;
import OWLImpl.WorkflowImpl;
import interfaces.FragmentInterface;
import interfaces.MappingInterface;
import interfaces.NodeInterface;
import interfaces.WorkflowInterface;


public class OntologyHelper extends WorkflowImpl{


	private OWLObjectProperty hasParticipant;


	private OWLObjectProperty hasEntry;


	private OWLObjectProperty hasExit;


	private OWLObjectProperty belongTo;


	private OWLDataProperty description;


	private OWLClass abstractTasks;


	private OWLObjectProperty mapping;
	public TaxonomyHelper taxonomyHelper;
	public static OntologyHelper sourceOntologyHelper;
	public static OntologyHelper targetOntologyHelper;
	
	public OntologyHelper(String OWLPath, TaxonomyHelper taxonomyHelper) {
		// TODO Auto-generated constructor stub
		this.OWLPath = OWLPath;
		this.taxonomyHelper = taxonomyHelper;
		initialization(OWLPath);
	}
	public void writeAbstractionIntoOntology() {
		//AbstractRelations
		AbstractRelations = df.getOWLObjectProperty(IOR + "#AbstractRelations");
		o.add(df.getOWLDeclarationAxiom(AbstractRelations));
		//hasParticipant
		 hasParticipant = df.getOWLObjectProperty(IOR + "#hasParticipant");
		o.add(df.getOWLSubObjectPropertyOfAxiom(hasParticipant, AbstractRelations));
		//hasEntry
		 hasEntry = df.getOWLObjectProperty(IOR + "#hasEntry");
		o.add(df.getOWLSubObjectPropertyOfAxiom(hasEntry, AbstractRelations));
		//hasExit
		 hasExit = df.getOWLObjectProperty(IOR + "#hasExit");
		o.add(df.getOWLSubObjectPropertyOfAxiom(hasExit, AbstractRelations));
		//belongTo
		 belongTo = df.getOWLObjectProperty(IOR + "#belongTo");
		o.add(df.getOWLSubObjectPropertyOfAxiom(belongTo, AbstractRelations));

		
		//description
		 description = df.getOWLDataProperty(IOR + "#description");
		o.add(df.getOWLDeclarationAxiom(description));
	
		//abstractTasks
		abstractTasks = df.getOWLClass(IOR+"#Abstract_tasks");
		o.add(df.getOWLDeclarationAxiom(abstractTasks));
		//mapping
		mapping = df.getOWLObjectProperty(IOR + "#mapping");
		 this.writeAllAbstractTasksIntoOWL();
	}

	

	
	public  HashMap<NodeInterface,String> split_labels = new HashMap<NodeInterface,String>();


	
	public  HashMap<String,String> partOf = new HashMap<String,String>();
	
	
	/**
	 * Ontology is a tool class for dealing with owl ontology file read and write
	 * **/

	public String OWLPath = "";
	public  OWLObjectProperty priorTo ;
	private OWLObjectProperty does;
	public  OWLObjectProperty isPartOf ;
	private OWLObjectProperty sequenceFlow;
	
	public OWLOntology o;
	public OWLDataFactory df;
	public IRI IOR;
	public OWLOntologyManager manager;
	public OWLObjectProperty AbstractRelations;
	public OWLReasoner reasoner;
	
	public void initialization(String OWLPath) {
  		manager = OWLManager.createOWLOntologyManager();
		manager.clearOntologies();
		this.OWLPath = OWLPath;
		HashMap<String,String> classNames = new HashMap<String,String>();

  		classNames.put("Tasks",Type.Tasks);
  		classNames.put("Events",Type.Event);
  		classNames.put("Exclusive_Gateways_Join",Type.Join);
  		classNames.put("Exclusive_Gateways_Split",Type.Split);
  		classNames.put("Complex_Gateways_Join",Type.Join);
  		classNames.put("Complex_Gateways_Split",Type.Split);
  		classNames.put("Parallel_Gateways_Split",Type.Split);
  		classNames.put("Parallel_Gateways_Join",Type.Join);
  		classNames.put("Service_Tasks",Type.Tasks);
  		classNames.put("User_Tasks",Type.Tasks);
  		classNames.put("Inclusive_Gateways_Join",Type.Join);
  		classNames.put("Inclusive_Gateways_Split",Type.Split);
  		classNames.put("Processes",Type.SubProcess);

  		try {
			o = manager.loadOntologyFromOntologyDocument(new File(this.OWLPath));
  			 df = o.getOWLOntologyManager().getOWLDataFactory();
  			IOR = IRI.create("http://owl.api.wf");
  			reasoner = (new StructuralReasonerFactory()).createReasoner(o);
  			reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS,InferenceType.OBJECT_PROPERTY_ASSERTIONS);
  			/**
  			 * ActivityPerformingRelations → does
  			 * AssignmentRelations → isPartOf
  			 * TemporalRelations → priorTo
  			 * 
  			 * label of sequences → sequenceFlow
  			 * */
  			isPartOf = df.getOWLObjectProperty(IRI.create(IOR + "#isPartOf"));
  			priorTo = df.getOWLObjectProperty(IRI.create(IOR + "#priorTo"));
  			does = df.getOWLObjectProperty(IRI.create(IOR+"#does"));
  			sequenceFlow = df.getOWLObjectProperty(IRI.create(IOR+"#sequenceFlow"));
  			this.sequenceFlow = sequenceFlow;
  			this.isPartOf = isPartOf;
//  		  for (OWLClass cls : o.getClassesInSignature()) 
//  		  { 
  			  		
  			  		for(String className:classNames.keySet()) {
  			  			String type = classNames.get(className);
  			  			int size = this.nodes.size();
  			  			OWLClass cls = df.getOWLClass(this.IOR+"#"+className);
  			  			populateNodes(cls, className, reasoner, type);
  			  		}
  			  		
//  		  }
  		 
  		  for (OWLClass cls : o.getClassesInSignature()) {
    			//get next nodes of nodes  
  			if(classNames.containsKey(cls.getIRI().getFragment())) 
  			{
  	          NodeSet<OWLNamedIndividual> tasks = reasoner.getInstances(cls, true);                    
  	          for (OWLNamedIndividual i : tasks.getFlattened()) {
  	        	if(i.getIRI().getIRIString().split("#")[1].contains(":")) {
  	        		String id = i.getIRI().getIRIString().split("#")[1].split(":")[1];
  	        		NodeInterface preceding = this.getNodeById(id);
  	            	NodeSet<OWLNamedIndividual> nextNodes = reasoner.getObjectPropertyValues(i, priorTo);
  	            	Set<String> nextNodesId = new HashSet<String>();
  	          	for(OWLNamedIndividual nextNode:nextNodes.getFlattened()) {
	            		String nextNode_id = nextNode.getIRI().getIRIString().split("#")[1].split(":")[1];
	            		preceding.addIntoSucceedingNode(this.getNodeById(nextNode_id));
	            		preceding.setSucceedingNode(this.getNodeById(nextNode_id));
	            		this.getNodeById(nextNode_id)
	            		.addIntoPrecedingNodes(preceding);
	            		this.getNodeById(nextNode_id).setPrecedingNode(preceding);
  	          	}
  	          		
  	        	}
  	          }
  			}
  		
  			 /*
    		   * get all actors*/
            if  (cls.getIRI().getFragment().equals("Actors")){
              	

                NodeSet<OWLNamedIndividual> Actors = reasoner.getInstances(cls, true);      
                for (OWLNamedIndividual i : Actors.getFlattened()) {
                	String label = i.getIRI().getIRIString().split("#")[1];
                	
                	NodeSet<OWLNamedIndividual> tasks = reasoner.getObjectPropertyValues(i, does);
                	for(OWLNamedIndividual task : tasks.getFlattened()) {
                		String taskId = task.getIRI().getIRIString().split("#")[1].split(":")[1];
	            		Iterator iter = nodes.iterator();
	            		while(iter.hasNext()) {
	            			NodeInterface node = (NodeInterface) iter.next();
	            			if(node.getId().contentEquals(taskId)) {
	            				((NodeImpl) node ).actor = label;
	            			}	       
	            		}

                	}
                	
                }
            }
  		  }
  		 
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Iterator iter = nodes.iterator();
		while(iter.hasNext()) {
			NodeInterface node = (NodeInterface) iter.next();
			if(node.getType().contentEquals(Type.Event)) {
				if(node.getPrecedingNode()==null) {
					node.setType(Type.StartEvent);;
				}
				if(node.getSucceedingNode()==null) {
					node.setType(Type.EndEvent);
				}	
			}
			if(node.getType().contentEquals(Type.SubProcess)) {
				if(node.getId().contains("Task")) {
					node.setType(Type.Tasks);
				}else {
					/**
					 * it is a workflow*/
					node.setType(Type.Workflow);

				}
			}
		}

	}
	
	/*
	 * this is a method for getting all sequence nodes with types which are defined in classNames list*/
	private void populateNodes(OWLClass cls, String className, OWLReasoner reasoner, String type) {
		// TODO Auto-generated method stub
	
		if  (cls.getIRI().getFragment().equals(className)){
			
            NodeSet<OWLNamedIndividual> entities = reasoner.getInstances(cls, true);      
            for (OWLNamedIndividual i : entities.getFlattened()) {
            	if(i.getIRI().getIRIString().split("#")[1].contains(":")) {
              		String label = i.getIRI().getIRIString().split("#")[1].split(":")[0];
                  	String id = i.getIRI().getIRIString().split("#")[1].split(":")[1];
                  	NodeImpl node = new NodeImpl(label,id,type);
                  	node.iri = i.getIRI();
                  	nodes.add((NodeInterface)node);
                  	this.nodes.add(node);
                  	if(node.isSPLITNode()) {
                  		NodeSet<OWLNamedIndividual> sequenceFlows = reasoner.getObjectPropertyValues(i, this.sequenceFlow);
                  		Set<String> labels = new HashSet<String>();
                  		String connect = "NULL";
                  		if(className.contentEquals("Parallel_Gateways_Split")) connect = " AND ";
                  		if(className.contentEquals("Inclusive_Gateways_Split")) connect = " AND ";
                  		if(className.contentEquals("Complex_Gateways_Split")) connect = " OR ";
                  		if(className.contentEquals("Exclusive_Gateways_Split")) connect = " XOR ";
                  		String branchLabel = "";
                  		for(OWLNamedIndividual flow: sequenceFlows.getFlattened()) {
                  			String flowLabel = flow.getIRI().getIRIString().split("#")[1].split(":")[0];
                  			branchLabel = branchLabel + "\n"+connect +"\n"+ flowLabel;
                  		}
                  		if(branchLabel.isEmpty()) {
                  			NodeSet<OWLNamedIndividual> followers = reasoner.getObjectPropertyValues(i,priorTo);
                  			for(OWLNamedIndividual follower:followers.getFlattened()) {
                  				String l = follower.getIRI().getIRIString().split("#")[1].split(":")[0];
                  				branchLabel = branchLabel +"\n"+ connect +"\n"+ l;
                  			}
                  		}
//                  		this.split_labels.put(node,branchLabel);
                  			this.split_labels.put(node, connect);
                  	}
                  	NodeSet<OWLNamedIndividual> isPart = reasoner.getObjectPropertyValues(i, isPartOf);
                	for(OWLNamedIndividual parent: isPart.getFlattened()) {
                		String parentName = parent.getIRI().getIRIString().split("#")[1];
                		this.partOf.put(i.getIRI().getIRIString(), parent.getIRI().getIRIString());
                	}
            	}else {
              		String label = i.getIRI().getIRIString().split("#")[1];
                  	String id = i.getIRI().getIRIString().split("#")[1];
                  	NodeImpl node = new NodeImpl(label,id,type);
                  	node.iri = i.getIRI();
                  	nodes.add((NodeInterface)node);
                  	this.nodes.add(node);

            	}
              	
            }
		}
	}
	public NodeInterface getNodeById(String id) {
		Iterator<NodeInterface> iter = this.nodes.iterator();
		while(iter.hasNext()) {
			NodeInterface node = iter.next();
			if(node.getId().contentEquals(id)) {
				return node;
			}
		}
		return null;
	}

	
	public void mergeWith(OntologyHelper targetOntologyHelper) {
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        axioms.addAll(targetOntologyHelper.o.getAxioms());
        o.addAxioms(axioms);
		df = o.getOWLOntologyManager().getOWLDataFactory();
		IOR = IRI.create("http://owl.api.wf");
		OWLReasoner reasoner = (new StructuralReasonerFactory()).createReasoner(o);
		reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS,InferenceType.OBJECT_PROPERTY_ASSERTIONS);

	}
	
	public void writeAbstractTasksIntoOWL(Set<FragmentInterface> fragments, String type) {
	
		for(FragmentInterface f:fragments) {
			NodeInterface obj = this.getAbstractTaskOf(f, type);
			
			String taskInstance = this.getProcessOf(f).substring(0,6)+":"+f.hashCode();
	    	OWLIndividual abstractTask = df.getOWLNamedIndividual(IRI.create(IOR + "#"+ taskInstance));

	    	OWLIndividual entry = df.getOWLNamedIndividual( ((NodeImpl) f.getPreanchor()).iri);
	    	OWLIndividual exit = df.getOWLNamedIndividual( ((NodeImpl) f.getPostanchor()).iri);
			
			NodeInterface preanchor = f.getPreanchor();
			String key =  "http://owl.api.wf#"+preanchor.getSemanticDescription()+":"+preanchor.getId();
			OWLIndividual workflow = df.getOWLNamedIndividual(this.partOf.get(key));
			
	    	o.add(df.getOWLClassAssertionAxiom(abstractTasks, abstractTask));
	    	o.add(df.getOWLDataPropertyAssertionAxiom(description, abstractTask, obj.getSemanticDescription())) ;
	    	o.add(df.getOWLObjectPropertyAssertionAxiom(hasExit, abstractTask, exit));
	    	o.add(df.getOWLObjectPropertyAssertionAxiom(hasEntry, abstractTask, entry));
	    	o.add(df.getOWLObjectPropertyAssertionAxiom(belongTo, abstractTask, workflow));
		}
		saveOntology(OWLPath.replaceFirst(".owl", "_after.owl"));
	}
	public void writeAllAbstractTasksIntoOWL() {
		this.writeAbstractTasksIntoOWL(this.blockFragments,FragmentType.block);
		this.writeAbstractTasksIntoOWL(this.deadendFragments, FragmentType.deadend);
		this.writeAbstractTasksIntoOWL(this.participantFragments, FragmentType.participant);
		this.writeAbstractTasksIntoOWL(this.sequentialFragments, FragmentType.sequential);
	}
	public void writeAbstractTaskIntoOWL(FragmentInterface f, String type)  {

			NodeInterface obj = this.getAbstractTaskOf(f, type);
			String taskInstance = this.getProcessOf(f).substring(0,6)+":"+f.hashCode();
	    	OWLIndividual abstractTask = df.getOWLNamedIndividual(IRI.create(IOR + "#"+ taskInstance));

	    	OWLIndividual entry = df.getOWLNamedIndividual( ((NodeImpl) f.getPreanchor()).iri);
	    	OWLIndividual exit = df.getOWLNamedIndividual( ((NodeImpl) f.getPostanchor()).iri);
			
			NodeInterface preanchor = f.getPreanchor();assert preanchor!=null:"null";
			String key =  "http://owl.api.wf#"+preanchor.getSemanticDescription()+":"+preanchor.getId();
			OWLIndividual workflow = df.getOWLNamedIndividual(this.partOf.get(key));
			
	    	o.add(df.getOWLClassAssertionAxiom(abstractTasks, abstractTask));
	    	o.add(df.getOWLDataPropertyAssertionAxiom(description, abstractTask, obj.getSemanticDescription())) ;
	    	o.add(df.getOWLObjectPropertyAssertionAxiom(hasExit, abstractTask, exit));
	    	o.add(df.getOWLObjectPropertyAssertionAxiom(hasEntry, abstractTask, entry));
	    	o.add(df.getOWLObjectPropertyAssertionAxiom(belongTo, abstractTask, workflow));
	    	
	    	if(type.contentEquals(FragmentType.participant)) {
	    		OWLIndividual participant = df.getOWLNamedIndividual(IRI.create(IOR + "#"+ f.getFirstNode().getActor()));
		    	o.add(df.getOWLObjectPropertyAssertionAxiom(hasParticipant, abstractTask, participant));

	    	}
		saveOntology(OWLPath.replaceFirst(".owl", "_after.owl"));
	}
	public void writeMappingIntoOWL(MappingInterface map, String type) {
		this.writeAbstractTaskIntoOWL(map.getSourceFragment(), type);
		this.targetOntologyHelper.writeAbstractTaskIntoOWL(map.getTargetFragment(), type);
		NodeInterface sourceAbstractTask = this.getAbstractTaskOf(map.getSourceFragment(), type);
		NodeInterface targetAbstractTask = this.targetOntologyHelper.getAbstractTaskOf(map.getTargetFragment(), type);
		String sourceAbstractTaskInstance = this.getProcessOf(map.getSourceFragment()).substring(0,6)+":"+map.getSourceFragment().hashCode();
		String targetAbstractTaskInstance = this.targetOntologyHelper.getProcessOf(map.getTargetFragment()).substring(0,6)+":"+map.getTargetFragment().hashCode();
		o.add(df.getOWLSubObjectPropertyOfAxiom(mapping, AbstractRelations));
    	OWLIndividual sourceIRI = df.getOWLNamedIndividual(IRI.create(IOR + "#"+ sourceAbstractTaskInstance));
    	OWLIndividual targetIRI = df.getOWLNamedIndividual(IRI.create(IOR + "#"+ targetAbstractTaskInstance));

    	o.add(df.getOWLObjectPropertyAssertionAxiom(mapping, sourceIRI, targetIRI));
    	
	}
	
	public void writeAllMappingsIntoOWL(Set<MappingImpl> maps, String type) {
		for(MappingInterface map:maps) {
			writeMappingIntoOWL( map,  type) ;
		}
	}
	public void saveOntology(String path) {
		//SaveAsFile
		File fileout = new File(path);
		try {
			manager.saveOntology(o, new FunctionalSyntaxDocumentFormat(),
					new FileOutputStream(fileout));
			System.out.println(path);
		} catch (OWLOntologyStorageException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public NodeInterface getAbstractTaskOf(FragmentInterface f, String type, boolean fullDescription) {
		if(fullDescription == false) return this.getAbstractTaskOf(f, type);
		
		return f.getAbtractTask();
	}
	public NodeInterface getAbstractTaskOf(FragmentInterface f, String type) {
		
		if(f.getAbtractTask()!=null)return f.getAbtractTask();
		
		String semanticDescription = null,  id;
		NodeInterface precedingNode, succeedingNode;
		String actor;
		
		actor = f.getFirstNode().getActor();
		id    = f.getFirstNode().getId()+"_"+f.getPostanchor().getPrecedingNode().getId();
		precedingNode = f.getPreanchor();
		succeedingNode = f.getPostanchor();
		if(type.contentEquals(FragmentType.participant)) {
			semanticDescription = f.getFirstNode().getActor().split(":")[0]+ " DO\n ";
			NodeInterface n = f.getFirstNode();
			while(f.getInnerNodes().contains(n)) {
				semanticDescription = semanticDescription + n.getSemanticDescription()+"\n";
				n = n.getSucceedingNode();
			} 
		}
		if(type.contentEquals(FragmentType.block)) {
			String connect = split_labels.get(f.getFirstNode());
			semanticDescription = f.getFirstNode().getSemanticDescription()+": \n";
			for(NodeInterface node:f.getFirstNode().getAllSucceedingNodes()) {
				semanticDescription = semanticDescription+ connect + "\n("; 
				do {
					if(node.isSPLITNode()) {
						for(FragmentInterface t:f.getNEST()) {
							if(t.getInnerNodes().contains(node)) {
								String content =this.getAbstractTaskOf(t, t.getType()).getSemanticDescription();
								semanticDescription = semanticDescription +"\\n"+ content+" ";
								node = f.getLastNode();
							}
						}
					}else {
						semanticDescription = semanticDescription +"\n"+node.getSemanticDescription();
					}
					node = node.getSucceedingNode();
				}while(f.getInnerNodes().contains(node));
				
				semanticDescription = semanticDescription + ")\n"; 
			}
		}
		if(type.contentEquals(FragmentType.deadend)) {
			String connect = split_labels.get(f.getFirstNode());
			semanticDescription = f.getFirstNode().getSemanticDescription()+": \n";
			
			for(NodeInterface node:f.getFirstNode().getAllSucceedingNodes()) {
				
				if(f.getInnerNodes().contains(node)) {
					semanticDescription = semanticDescription + connect + "("+node.getSemanticDescription()+")" +"with dead end\n";
				}else {
					semanticDescription = semanticDescription + connect + "("+node.getSemanticDescription()+")\n";
				}
			}
		}
		if(type.contentEquals(FragmentType.sequential)) {
			semanticDescription = "sequential execution:\n";
			NodeInterface n = f.getFirstNode();
			Set<NodeInterface> nodes = f.getInnerNodes();
			while(f.getInnerNodes().contains(n)) {
				semanticDescription = semanticDescription + n.getSemanticDescription()+"\n";
				n = n.getSucceedingNode();
			}
		}
		if(type.contentEquals(FragmentType.loop)) {
			semanticDescription = "REPEAT:\n";
			for(NodeInterface v: f.getInnerNodes()) {
				semanticDescription = semanticDescription + v.getSemanticDescription()+"\n";
			}
		}
		f.setAbstractTask(new NodeImpl(semanticDescription,  id,  precedingNode, succeedingNode,
			 type, actor));
		f.getAbtractTask().addAllIntoParticipants(f.getParticipants());
		f.getAbtractTask().addIntoSucceedingNode(succeedingNode);
		return f.getAbtractTask();
	}

	/**
	 * return the workflow name of a fragment
	 * **/
	public String getProcessOf(FragmentInterface f) {
		
		String result = null;
		NodeInterface preanchor = f.getPreanchor();
		String key =  "http://owl.api.wf#"+preanchor.getSemanticDescription()+":"+preanchor.getId();
		result = this.partOf.get(key);
		assert result!=null:key;
		result = result.split("#")[1];
		return result;
		
	}
	public String getProcessOf(NodeInterface n) {
		String result = null;

		String key =  "http://owl.api.wf#"+n.getSemanticDescription()+":"+n.getId();
		result = this.partOf.get(key);
		assert result!=null:key;
		result = result.split("#")[1];
		return result;
	}
	

	public String getDirectClassOfNode(NodeInterface v) {
		OWLNamedIndividual ind = df.getOWLNamedIndividual(((NodeImpl) v).iri);
		if(reasoner.getTypes(ind,true).isEmpty()) {
			assert false:ind+"";
			return null;
		}
		OWLClass parent = reasoner.getTypes(ind, true).getFlattened().iterator().next();
		return parent.getIRI().getFragment();
	}
	 public FragmentInterface getBlockFragmentByPreanchor(NodeInterface preanchorCandidate) {
		 		 FragmentInterface result=null;
		 		 for(FragmentInterface f : this.getBlockFragments()) {
		 			 if(f.getPreanchor().equals(preanchorCandidate)) {
		 				 result = f;
		 			 }
		 		 }
		 		 return result;
	 }
	 public FragmentInterface getBlockFragmentByFirstNode(String id) {
 		 FragmentInterface result=null;
 		 for(FragmentInterface f : this.getBlockFragments()) {
 			 if(f.getFirstNode().getId().contentEquals(id)) {
 				 result = f;
 			 }
 		 }
 		 return result;
	 }
	 public FragmentInterface getLoopFragmentByFirstNode(String id) {
 		 FragmentInterface result=null;
 		 for(FragmentInterface f : this.getLoopFragments()) {
 			 if(f.getFirstNode().getId().contentEquals(id)) {
 				 result = f;
 			 }
 		 }
 		 return result;
	 }
	 
	 public FragmentInterface getParticipantFragmentByFirstNode(String id) {
 		 FragmentInterface result=null;
 		 for(FragmentInterface f : this.getParticipantFragments()) {
 			 if(f.getFirstNode().getId().contentEquals(id)) {
 				 result = f;
 			 }
 		 }
 		 return result;
	 }
	 public FragmentInterface getSequenceFragmentByFirstNode(String id) {
 		 FragmentInterface result=null;
 		 for(FragmentInterface f : this.getSequenceFragments()) {
 			 if(f.getFirstNode().getId().contentEquals(id)) {
 				 result = f;
 			 }
 		 }
 		 return result;
	 }
	 public FragmentInterface getParticipantFragmentByPreanchor(NodeInterface preanchorCandidate) {
		 	FragmentInterface result = null;
		 	for(FragmentInterface f:this.getParticipantFragments()) {
		 		if(f.getPreanchor().equals(preanchorCandidate)) result = f;
		 	}
		 	return result;
	 }
	 public FragmentInterface getDeadEndFragmentByPreanchor(NodeInterface preanchorCandidate) {
		 FragmentInterface result = null;
		 for(FragmentInterface f:this.getDeadEndFragments()) {
			 if(f.getPreanchor().equals(preanchorCandidate)) result = f;
		 }
		 return result;
	 }
	 public FragmentInterface getDeadEndFragmentByFirstNode(String id) {
		 FragmentInterface result = null;
		 for(FragmentInterface f:this.getDeadEndFragments()) {
			 if(f.getFirstNode().getId().contentEquals(id)) result = f;
		 }
		 return result;
	 }
	/**
	 * generate BPMN file of a workflow
	 * 1. nodes should have consistent BPMN type in typesOfNodes
	 * 2. for each type, different builder are used
	 * 3. for each succeeding node v, add it to the current builder**/
	public static void testExport() {
		String path = "";
		String name = "testPNG";
		WorkflowImpl workflow = new WorkflowImpl();
		HashMap<String, String> data = OntologyHelper.sourceOntologyHelper.partOf;
		String workflowName = data.get(data.keySet().iterator().next());
		for(NodeInterface node:OntologyHelper.sourceOntologyHelper.nodes) {
			String result = null;
			String key =  "http://owl.api.wf#"+node.getSemanticDescription()+":"+node.getId();
			if(data.containsKey(key)&&data.get(key).contentEquals(workflowName)) {

				workflow.nodes.add(node);
			}
		}
		
		exportTo(path,name,workflow);
	}

	
	@Override
	public void replace(FragmentInterface f, String type) {
		NodeInterface x = this.getAbstractTaskOf(f, type);
		this.nodes.remove(f.getInnerNodes());
		this.nodes.add(x);
		x.getAllPrecedingNodes().removeAll(f.getInnerNodes());
		x.getAllSucceedingNodes().removeAll(f.getInnerNodes());
		x.getPrecedingNode().setSucceedingNode(x);
		x.getSucceedingNode().setPrecedingNode(x);
	}
	@Override
	public void exportTo(String path) {
		// TODO Auto-generated method stub
		HashMap<String,WorkflowImpl> resultSet = new HashMap<String,WorkflowImpl>();
		OntologyHelper srcOntology = this;
		HashMap<String, String> data = srcOntology.partOf;
		Collection<String> workflowNames = new HashSet<String>(data.values());
		HashMap<String,WorkflowImpl> workflows = new HashMap<String,WorkflowImpl>();
		for(String name : workflowNames) {
			WorkflowImpl workflow = new WorkflowImpl();
			for(NodeInterface node:OntologyHelper.sourceOntologyHelper.nodes) {
				String result = null;
				String key =  "http://owl.api.wf#"+node.getSemanticDescription()+":"+node.getId();
				if(data.containsKey(key)&&data.get(key).contentEquals(name)) {
					workflow.nodes.add(node);
				}
			}
			workflows.put(name, workflow);
		}
		for(String name : workflows.keySet()) {
			WorkflowImpl workflow = workflows.get(name);
			if(path!=null)
			OntologyHelper.exportTo(path,name.split("#")[1].split(":")[0],workflow);
		}
	}
	@Override
	public Set<NodeInterface> getNodesCopy() {
		// TODO Auto-generated method stub
		Set<NodeInterface> nodes = new HashSet<NodeInterface>(this.nodes);
		return nodes;
	}

}
