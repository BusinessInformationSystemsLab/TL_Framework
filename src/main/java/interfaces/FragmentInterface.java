package interfaces;

import java.util.Set;

public interface FragmentInterface {
	/*
	 * what does a fragment contain?
	 * a function returning the abstract task
	 * a function returning the NEST list of nested block fragments
	 * 
	 * a function returning the Preanchor
	 * a function returning the Postanchor
	 * a function returning the Participants anchor
	 * a function returning the first node in the fragment
	 * 
	 * a function returning true if the fragment contains the query node
	 * a function returning true if the participant anchor set only has the query participant (actor)
	 * 
	 * Visualization
	 * a function exporting to an image to present the fragment*/
	public NodeInterface getAbtractTask();
	public void setAbstractTask(NodeInterface node);
	public Set<FragmentInterface> getNEST();
	
	public NodeInterface getPreanchor();
	public NodeInterface getPostanchor();
	public Set<String> getParticipants();
	public NodeInterface getFirstNode();
	public Set<NodeInterface> getInnerNodes();
	public String getType();
	public NodeInterface getLastNode();
	
	public boolean contains(NodeInterface node);
	public boolean onlyPerformedBy(String participant);
	
	public void exportTo(String path, String name);
	public void setPreanchor(NodeInterface node);
	public void setparticipantsAnchors(Set<String> participantsAnchors);
	public void setInnernodes(Set<NodeInterface> innerNodes);
	public void addToInnernodes(NodeInterface node);
	public void addAllToInnernodes(Set<NodeInterface> nodes);
	public void addToNest(FragmentInterface block);
	 @Override
     public int hashCode();
     @Override
     public boolean equals(Object obj);
	public void setPostanchor(NodeInterface postanchor);
	void setType(String type);
	void addParticipant(String participant);

}
