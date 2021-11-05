package logic;

import java.util.HashSet;
import java.util.Set;

import IOHelper.OntologyHelper;
import IOHelper.TaxonomyHelper;
import OWLImpl.FragmentImpl;
import OWLImpl.FragmentType;
import OWLImpl.NodeImpl;
import interfaces.FragmentInterface;
import interfaces.NodeInterface;
import interfaces.WorkflowInterface;

public class SequenceOperator extends Operator {
	
	TaxonomyHelper taxonomyHelper = TaxonomyHelper.sourceTaxonomy;
	WorkflowInterface workflow;
	public Set<FragmentInterface> sequences;

	public SequenceOperator(WorkflowInterface workflow) {
		// TODO Auto-generated constructor stub
		this.type="sequence";
		sequences = new HashSet<FragmentInterface>();
		this.fragments = this.sequences;
		this.workflow = workflow;
		for(NodeInterface node:workflow.getTasks()) {
			FragmentInterface f = this.buildSequence(node);
			if(f!=null)
				this.sequences.add(f);
		}
		
	}
	
 public String getTypeOfNodeForSequentialAbstraction(NodeInterface n,TaxonomyHelper taxonomyHelper) {
		return taxonomyHelper
				.getParentOf(taxonomyHelper.df.getOWLNamedIndividual("http://owl.api.wf#"+((NodeImpl) n)
						.iri.getIRIString().split("#")[1].split(":")[0]))
				.getIRI().getIRIString().split("#")[1];
	}
	public boolean isBorder(NodeInterface node, NodeInterface border) {
		return !node.getType().contentEquals(border.getType());
	}
	public FragmentInterface buildSequence(NodeInterface node) {
		int i = 0;
		FragmentInterface t = this.getFragmentByNode(node);
		if(t!=null) return t;	
		FragmentInterface f = new FragmentImpl(node, FragmentType.sequential);
		NodeInterface preanchor=node, postanchor= node.getSucceedingNode();
		while(!this.isBorder(node, preanchor)&&preanchor.getAllPrecedingNodes().size()==1&&preanchor.getAllSucceedingNodes().size()==1) {
			f.addToInnernodes(preanchor);
			f.addParticipant(preanchor.getActor());
			preanchor = preanchor.getPrecedingNode();
			i++;
		}
		while(!this.isBorder(node, postanchor)&&postanchor.getAllPrecedingNodes().size()==1&&postanchor.getAllSucceedingNodes().size()==1) {
			f.addToInnernodes(postanchor);
			f.addParticipant(postanchor.getActor());
			postanchor = postanchor.getSucceedingNode();
			i++;
		}
		if(f.getInnerNodes().size()<2) return null;
		f.setPreanchor(preanchor);
		f.setPostanchor(postanchor);
		return f;
	}
}
