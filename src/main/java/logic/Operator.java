package logic;

import static guru.nidi.graphviz.model.Factory.mutNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import IOHelper.OntologyHelper;
import OWLImpl.WorkflowImpl;
import interfaces.FragmentInterface;
import interfaces.NodeInterface;

abstract class Operator {
	public Set<FragmentInterface> fragments;
	public String type = "";
	public FragmentInterface getFragmentByNode(NodeInterface node) {
		for(FragmentInterface f:this.fragments) {
			type = f.getType();
			if(f.getInnerNodes().contains(node)) return f;
		}
		return null;
	}
	public void onOntology() {
		assert this.fragments!=null:"wrong";
		assert !this.type.contentEquals(""):"wrong";
		if(this.fragments!=null&&!this.type.contentEquals("")) {
			List<WorkflowImpl> workflows = new ArrayList<WorkflowImpl>();
			OntologyHelper srcOntology = OntologyHelper.sourceOntologyHelper;
			HashMap<String, String> data = srcOntology.partOf;
			Collection<String> workflowNames = new HashSet<String>(data.values());

				for(String name : workflowNames) {
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
				for(FragmentInterface f:this.fragments) {
					f.setAbstractTask(srcOntology.getAbstractTaskOf(f, this.type));
				}
				/***/
				for(WorkflowImpl w:workflows) {
					OntologyHelper.sourceOntologyHelper.nodes.removeAll(w.nodes);
					
					WorkflowImpl w2 = onWorkflow(w); 
					OntologyHelper.sourceOntologyHelper.nodes.addAll(w2.nodes);
					
					String name = w2.semanticDescription;
					for(NodeInterface node:w2.nodes) {
						String  key = "http://owl.api.wf#"+node.getSemanticDescription()+":"+node.getId();
						data.put(key, name);
					}
					
					 for(NodeInterface node:w2.nodes) {
					    	for(NodeInterface next : node.getAllSucceedingNodes()) {
					    		if(w2.nodes.contains(next)) {
					    			node.setSucceedingNode(next);
					    			
					    			next.setPrecedingNode(node);
					    		}
					    	}
					    	for(NodeInterface last : node.getAllPrecedingNodes()) {
					    		if(w2.nodes.contains(last)) {
					    			if(!w2.edges.contains(new NodeInterface[] {last,node})) {
					    				last.setSucceedingNode(node);
					    				node.setPrecedingNode(last);
					    				
					    			}
					    		}
					    	}
					    }
					 
					
				}
		}
	}
	public void onOntology(String SVGPath) {
		
		assert this.fragments!=null:"wrong";
		assert !this.type.contentEquals(""):"wrong";
		if(this.fragments!=null&&!this.type.contentEquals("")) {
			List<WorkflowImpl> workflows = new ArrayList<WorkflowImpl>();
			OntologyHelper srcOntology = OntologyHelper.sourceOntologyHelper;
			HashMap<String, String> data = srcOntology.partOf;
			Collection<String> workflowNames = new HashSet<String>(data.values());

				for(String name : workflowNames) {
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
				for(FragmentInterface f:this.fragments) {

					f.setAbstractTask(srcOntology.getAbstractTaskOf(f, this.type));
				}
				/***/
				
				for(WorkflowImpl w:workflows) {
					WorkflowImpl w2 = onWorkflow(w); 
					OntologyHelper.exportTo(SVGPath,w2.semanticDescription.split("#")[1].split(":")[0],w2,w2.edges);
				}
		}
	}
	
	public void onOntology(String SVGPath,List<WorkflowImpl> workflows ) {
		
		assert this.fragments!=null:"wrong";
		assert !this.type.contentEquals(""):"wrong";
		if(this.fragments!=null&&!this.type.contentEquals("")) {
			OntologyHelper srcOntology = OntologyHelper.sourceOntologyHelper;

				for(FragmentInterface f:this.fragments) {

					f.setAbstractTask(srcOntology.getAbstractTaskOf(f, this.type));
				}
				/***/
				
				for(WorkflowImpl w:workflows) {
					WorkflowImpl w2 = onWorkflow(w); 
					OntologyHelper.exportTo(SVGPath,w2.semanticDescription,w2,w2.edges);
				}
		}
	}
	private WorkflowImpl onWorkflow(WorkflowImpl w) {
		
		  ArrayList<FragmentInterface> tmp = new ArrayList<FragmentInterface>(), 
				  selectedFragments = new ArrayList<FragmentInterface>();
		  for(FragmentInterface f:this.fragments) {
			  if(!Collections.disjoint(w.nodes, f.getInnerNodes())) {
				  selectedFragments.add(f);
				  tmp.add(f);
			  }
		  }
		  for(FragmentInterface f:tmp) {
			  selectedFragments.removeAll(f.getNEST());
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
		    	Set<String> actors = new HashSet<String>();
		    	for(NodeInterface a:x.getInnerNodes()) {
		    		actors.add(a.getActor());
		    	}
		    	String actor = String.join("&", actors);
		    	if(actor!=null) node.setActor(actor);
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
		    
		    w = transferedWorkflow;
		return w;
	}
}
