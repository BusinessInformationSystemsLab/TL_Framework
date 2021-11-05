package logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import IOHelper.OntologyHelper;
import IOHelper.TaxonomyHelper;
import OWLImpl.FragmentImpl;
import OWLImpl.FragmentType;
import interfaces.FragmentInterface;
import interfaces.NodeInterface;
import interfaces.WorkflowInterface;

public class LoopOperator extends Operator{

	public HashSet<FragmentInterface> loops;

	public LoopOperator(WorkflowInterface workflow) {
		this.loops = new HashSet<FragmentInterface>();
		this.fragments = this.loops;
		this.type = "loop";
		/**history: 17:20 Dec 11*/
		for(NodeInterface join: workflow.getJOINnodes()) {
			for(NodeInterface v: join.getAllPrecedingNodes()) {
				if(v.isSPLITNode()) {
					NodeInterface z = join.getSucceedingNode();
					while(!z.isEndEvent()) {
						if(z.equals(v)) break;
						z = z.getSucceedingNode();
					}
					if(z.equals(v)) {
						FragmentInterface f = new FragmentImpl();
						f.setPreanchor(join.getAllPrecedingNodes().parallelStream().filter(x->!x.equals(v)).findAny().get());
						f.setPostanchor(v.getAllSucceedingNodes().parallelStream().filter(x->x!=join).findAny().get());
						v.setSucceedingNode(f.getPostanchor());
						this.traverseLoop(f, join, v);
						f.addToInnernodes(join);
						f.setType(FragmentType.loop);
						this.loops.add(f);
					}
				}
			}
		}
	}
	
	private void traverseLoop(FragmentInterface f, NodeInterface v, NodeInterface stopSign) {
		if(v==stopSign) return;
		for(NodeInterface child:v.getAllSucceedingNodes()) {
			f.addToInnernodes(child);
			this.traverseLoop(f, child,stopSign);
		}
	}
}
