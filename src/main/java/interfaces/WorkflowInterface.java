package interfaces;

import java.util.HashSet;
import java.util.Set;

import OWLImpl.NodeImpl;

public interface WorkflowInterface {
	//Result sets	
	/*
	 * what does a workflow/Ontology contain?
	 * a function returning the set of tasks nodes(inclusiv subprocess nodes)
	 * a function returning the set of gateway nodes
	 * a function returning the set of event nodes
	 * a function returning the set of JOIN nodes
	 * a function returning the set of SPLIT nodes
	 * 
	 * a function returning the set of dead-end fragments
	 * a function returning the set of block    fragments
	 * a function returning the set of participant-dependent fragments
	 * a function returning the set of sequntial fragments
	 *  */
	public String getSemanticDescription();
	public Set<NodeInterface> getTasks();
	public Set<NodeInterface> getEvents();
	public Set<NodeInterface> getStartEvents();
	public Set<NodeInterface> getGateways();
	public Set<NodeInterface> getJOINnodes();
	public Set<NodeInterface> getSPLITnodes();
	
	public Set<FragmentInterface> getDeadEndFragments();
	public Set<FragmentInterface> getBlockFragments();
	public Set<FragmentInterface> getParticipantFragments();
	public Set<FragmentInterface> getSequenceFragments();
	void setParticipantFragments(Set<FragmentInterface> fragmentSet);
	void setDeadEndFragments(Set<FragmentInterface> fragmentSet);
	void setBLockFragments(Set<FragmentInterface> fragmentSet);
	void setSequentialFragments(Set<FragmentInterface> fragmentSet);
	void setLoopFragments(Set<FragmentInterface> fragmentSet);
	public Set<FragmentInterface> getLoopFragments();
	public NodeInterface getAbstractTaskOf(FragmentInterface f, String type);
	public void replace(FragmentInterface f, String type);
	public void exportTo(String path);
	Set<NodeInterface> getNodesCopy();
}
