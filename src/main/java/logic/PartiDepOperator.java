package logic;

import java.util.HashSet;
import java.util.Set;

import IOHelper.OntologyHelper;
import IOHelper.TaxonomyHelper;
import OWLImpl.FragmentImpl;
import OWLImpl.FragmentType;
import OWLImpl.Type;
import interfaces.FragmentInterface;
import interfaces.NodeInterface;
import interfaces.WorkflowInterface;

public class PartiDepOperator extends Operator{
	public WorkflowInterface workflow;
	public BlockOperator blockOperator;
	public LoopOperator loopOperator;

	public PartiDepOperator(WorkflowInterface workflow) {
		// TODO Auto-generated constructor stub
		this.workflow = workflow;
		Set<NodeInterface> nodes = new HashSet<NodeInterface>(workflow.getTasks());
		this.type = FragmentType.participant;
		this.fragments = new HashSet<FragmentInterface>();
		this.blockOperator = new BlockOperator(workflow);
		this.loopOperator = this.blockOperator.loopOperator;
 		/**til here 12:42 12:40*/
		this.fragments = detectActorFragments(workflow);
	}
	/**
	 * every time begins from START
	 * TRAVERSE EACH WORKFLOW IN THE ONTOLOGY.
	 * **/
	public Set<FragmentInterface> detectActorFragments(WorkflowInterface workflow){
		
		Set<FragmentInterface> ResultSet = new HashSet<FragmentInterface>();

		NodeInterface preanchor, postanchor;
		
		Set<NodeInterface> startEvents = workflow.getStartEvents();
		
		for(NodeInterface start: startEvents) 
		{ 
			preanchor = start;
			postanchor = preanchor.getSucceedingNode();
			String participant = postanchor.getActor();
			FragmentInterface f = new FragmentImpl();
			f.setPreanchor(preanchor);
			f.getParticipants().add(participant);
			while(preanchor!=null&&postanchor!=null) 
			{ 
				if(postanchor.getAllPrecedingNodes().size()==1&&postanchor.getAllSucceedingNodes().size()==1) 
				{
					if(postanchor.getActor().contentEquals(participant)) 
					{
						f.addToInnernodes(postanchor);
						postanchor = postanchor.getSucceedingNode();
					}else 
					{
						f.setPostanchor(postanchor);
						ResultSet.add(f);
						FragmentInterface a = new FragmentImpl();
						f = a;
						preanchor = postanchor.getPrecedingNode();
						participant = postanchor.getActor();
						f.setPreanchor(preanchor);
						f.addToInnernodes(postanchor);
						f.addParticipant(participant);
					}
					
				}
				else if(postanchor.isEndEvent()) 
				{
					f.setPostanchor(postanchor);
					if(f.getInnerNodes().size()>0)
						ResultSet.add(f);
					postanchor = null;
					break;
				}
				else if(postanchor.isSPLITNode())
				{
					FragmentInterface blockFragment = this.blockOperator.getFragmentByNode(postanchor);
					FragmentInterface deadendFragment = this.blockOperator.deadendOperator.getFragmentByNode(postanchor);
					if(blockFragment!=null) {
						if(blockFragment.getParticipants().size()==1&&blockFragment.getParticipants().iterator().next().contentEquals(participant)) {
							postanchor = blockFragment.getPostanchor();
							f.addAllToInnernodes(blockFragment.getInnerNodes());

						}else if(blockFragment.getParticipants().size()>1){
							/** the block is not a single.actor block
							 * add current f into result set
							 * create a new f with preanchor at JOIN of block, postanchor at JOIN.succeedingNode
							 * if postanchor is an end event, set postanchor = null to stop the loop,
							 * else keep while-loop
							 * **/
							f.setPostanchor(postanchor);
							if(f.getInnerNodes().size()>0)        ResultSet.add(f);
							preanchor = blockFragment.getPostanchor().getPrecedingNode();
							postanchor = preanchor.getSucceedingNode();
							
							FragmentInterface a = new FragmentImpl();
							f = a;
							participant = postanchor.getActor();
							f.setPreanchor(preanchor);
							if(!postanchor.isSPLITNode()&&!postanchor.isEndEvent())f.addToInnernodes(postanchor);
							f.addParticipant(participant);
						}else if(blockFragment.getParticipants().size()==1) {
							/** the block is a single.actor block
							 * **/ 
							f.setPostanchor(postanchor);
							if(f.getInnerNodes().size()>0)ResultSet.add(f);
							
							preanchor = blockFragment.getPreanchor();
							postanchor = blockFragment.getPostanchor();
							FragmentInterface b = new FragmentImpl();
							f = b;
							participant = postanchor.getPrecedingNode().getActor();
							f.setPreanchor(preanchor);
							f.addAllToInnernodes(blockFragment.getInnerNodes());
							f.addParticipant(participant);
							
						}

					}else if(deadendFragment!=null) { 
						if(postanchor.getActor().contentEquals(participant)) {
							f.addAllToInnernodes(deadendFragment.getInnerNodes());
							for(NodeInterface node:postanchor.getAllSucceedingNodes()) {
								if(!deadendFragment.contains(node)) {
									postanchor = node;
								}
							}
						}
					}else {
						assert false:"no fragment found!"+OntologyHelper.sourceOntologyHelper.getProcessOf(postanchor)+" "+postanchor.getId()+" ";
					}
				}else if(postanchor.isJOINNode()) {
					 
					FragmentInterface blockFragment = this.loopOperator.getFragmentByNode(postanchor);
					if(blockFragment.getParticipants().size()==1&&blockFragment.getParticipants().iterator().next().contentEquals(participant)) {
						postanchor = blockFragment.getPostanchor();
						f.addAllToInnernodes(blockFragment.getInnerNodes());

					}else if(blockFragment.getParticipants().size()>1){
						/** the block is not a single.actor block
						 * add current f into result set
						 * create a new f with preanchor at SPLIT of block, postanchor at SPLIT.succeedingNode
						 * if postanchor is an end event, set postanchor = null to stop the loop,
						 * else keep while-loop
						 * **/
						f.setPostanchor(postanchor);System.out.println("if at 151 !");
						if(f.getInnerNodes().size()>0)        ResultSet.add(f);
						preanchor = blockFragment.getPostanchor().getPrecedingNode();
						for(NodeInterface n:preanchor.getAllSucceedingNodes()) {
							if(!blockFragment.getInnerNodes().contains(n)) {
								postanchor = n;
								break;
							}
						}
						
						FragmentInterface a = new FragmentImpl();
						f = a;
						participant = postanchor.getActor();
						f.setPreanchor(preanchor);
						if(!postanchor.isSPLITNode()&&!postanchor.isEndEvent())f.addToInnernodes(postanchor);
						f.addParticipant(participant);
					}else if(blockFragment.getParticipants().size()==1) {
						/** the block is a single.actor block
						 * **/
						f.setPostanchor(postanchor);
						if(f.getInnerNodes().size()>0)ResultSet.add(f);
						
						preanchor = blockFragment.getPreanchor();
						postanchor = blockFragment.getPostanchor();
						FragmentInterface b = new FragmentImpl();
						f = b;
						participant = postanchor.getPrecedingNode().getActor();
						f.setPreanchor(preanchor);
						f.addAllToInnernodes(blockFragment.getInnerNodes());
						f.addParticipant(participant);
						
					}				
				} 
			}
		}
		for(FragmentInterface f:ResultSet) {
			f.setType(FragmentType.participant);
			workflow.getAbstractTaskOf(f, FragmentType.participant);
		}
		return ResultSet;		
	}
	
}
