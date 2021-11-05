package logic;

import java.util.HashSet;
import java.util.Set;

import IOHelper.OntologyHelper;
import IOHelper.TaxonomyHelper;
import OWLImpl.FragmentImpl;
import OWLImpl.FragmentType;
import interfaces.FragmentInterface;
import interfaces.NodeInterface;
import interfaces.WorkflowInterface;

public class DeadendOperator extends Operator{
	
	public DeadendOperator(WorkflowInterface workflow) {
		deadends = new HashSet<FragmentInterface>();
		this.type = "DeadEnd";
		this.fragments = deadends;
		for(NodeInterface split: workflow.getSPLITnodes()) {
			FragmentInterface f = this.detectDeadendBySplit(split);
			if(f!=null) {
				f.addToInnernodes(split);
				this.deadends.add(f);
			}
		}
	}
	
	public Set<NodeInterface> SPLITs;
	public FragmentInterface currentDeadendFragment;
	public Set<FragmentInterface> deadends = new HashSet<FragmentInterface>();
	
	private FragmentInterface detectDeadendBySplit(NodeInterface split) {
		for(FragmentInterface d : this.deadends) {
			if(d.getInnerNodes().contains(split)) return d;
		}
		for(NodeInterface nonsplit:split.getAllSucceedingNodes()) {
			if(this.detectDeadendBranch(nonsplit)==false) {
				this.currentDeadendFragment.setPreanchor(split.getPrecedingNode());
				for(NodeInterface b:this.currentDeadendFragment.getInnerNodes()) {
					this.currentDeadendFragment.addParticipant(b.getActor());
				}
				this.currentDeadendFragment.addToInnernodes(split);
				for(NodeInterface postanchor :split.getAllSucceedingNodes()) {
					if(!this.currentDeadendFragment.getInnerNodes().contains(postanchor)) {
						this.currentDeadendFragment.setPostanchor(postanchor);
						System.out.println(postanchor.getSemanticDescription());
						return this.currentDeadendFragment;
					}
				}
				assert false:"pre anchor is the preceding node of split, post is the succeeding one";
				
			}
		}
		return null;
	}
	private boolean detectDeadendBranch(NodeInterface nonsplit) {
		FragmentInterface f = new FragmentImpl(nonsplit, FragmentType.deadend);//nonsplit as post anchor
		
		while(nonsplit.getAllSucceedingNodes().size()==1&&nonsplit.getAllPrecedingNodes().size()==1) {
			f.addToInnernodes(nonsplit);
			nonsplit = nonsplit.getSucceedingNode();
		}
		if(nonsplit.isJOINNode()||nonsplit.isSPLITNode()) {
			this.currentDeadendFragment = null;
			return true;
		}
		f.addToInnernodes(nonsplit);
		this.currentDeadendFragment = f;
		return false;
	}
}
