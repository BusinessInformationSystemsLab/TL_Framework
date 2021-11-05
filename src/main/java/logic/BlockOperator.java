package logic;

import java.util.HashSet;
import java.util.Set;

import OWLImpl.FragmentImpl;
import OWLImpl.FragmentType;
import interfaces.FragmentInterface;
import interfaces.NodeInterface;
import interfaces.WorkflowInterface;

public class BlockOperator extends Operator {
	public WorkflowInterface workflow;
	public Set<FragmentInterface> blocks;
	public DeadendOperator deadendOperator;
//	public FragmentInterface currentBlockFragment;
	public LoopOperator loopOperator;
	/**this is an operator for abstracting block**/
	

	public BlockOperator(WorkflowInterface workflow) {
		this.workflow = workflow;
		this.type = "block";
		this.initializeFragment();
		this.deadendOperator = new DeadendOperator(workflow);
		this.loopOperator = new LoopOperator(workflow);
		this.blocks = new HashSet<FragmentInterface>();
		this.fragments = this.blocks;
		for(NodeInterface join:this.workflow.getJOINnodes()) {
			FragmentInterface b = this.backwardTrace(join);
			if(b!=null&&b.getType().contentEquals(FragmentType.block))
				blocks.add(b);		 
		}
	}
	
public FragmentInterface backwardTrace(NodeInterface join) {
	FragmentInterface t = this.loopOperator.getFragmentByNode(join);
	if(t!=null) return t;	
	FragmentInterface currentBlockFragment = new FragmentImpl(join.getSucceedingNode(),FragmentType.block);
	currentBlockFragment.addToInnernodes(join);
	NodeInterface singleIOnode;
	for(NodeInterface node:join.getAllPrecedingNodes()) {
		singleIOnode = node;
		do {
			currentBlockFragment.addToInnernodes(singleIOnode);
			if(singleIOnode.isSPLITNode()) {
				FragmentInterface deadendOrLoop = this.getFragmentBySplit(singleIOnode);
				if(deadendOrLoop==null) {
					//singleIOnode SPLIT is the start of the currentFragment
					break;
				}
				if(deadendOrLoop.getInnerNodes().contains(join)) {
					return deadendOrLoop;
				}
				currentBlockFragment.addToNest(deadendOrLoop);
				singleIOnode = deadendOrLoop.getPreanchor();
			}else
			if(singleIOnode.isJOINNode()) {
				FragmentInterface nest = this.backwardTrace(singleIOnode);
				currentBlockFragment.addToNest(nest);
				
				singleIOnode = nest.getPreanchor();
				assert !nest.getInnerNodes().contains(singleIOnode);
			}else {
				singleIOnode = singleIOnode.getPrecedingNode();
			}
		}while(true);
		currentBlockFragment.addToInnernodes(singleIOnode);
		
		for(FragmentInterface f:currentBlockFragment.getNEST()) {
			currentBlockFragment.addAllToInnernodes(f.getInnerNodes());
			currentBlockFragment.setparticipantsAnchors(f.getParticipants());
		}
		for(NodeInterface n : currentBlockFragment.getInnerNodes()) {
			currentBlockFragment.addParticipant(n.getActor());
		}
		currentBlockFragment.setPreanchor(singleIOnode.getPrecedingNode());
	}
	return currentBlockFragment;
}

public FragmentInterface getFragmentBySplit(NodeInterface split) {
	FragmentInterface d = this.deadendOperator.getFragmentByNode(split);
	if(d!=null) return d;
	FragmentInterface f = this.loopOperator.getFragmentByNode(split);
	if(f!=null) return f;
	return null;
}

public void initializeFragment() {
	this.initializeDeadendset();
	this.initializeLoopset();
}

public void initializeDeadendset() {
		deadendOperator = new DeadendOperator(this.workflow);
}

public void initializeLoopset() {
	 	loopOperator = new LoopOperator(this.workflow);
}

}
