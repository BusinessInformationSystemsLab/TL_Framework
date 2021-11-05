package interfaces;

import java.util.Collection;
import java.util.Set;

import IOHelper.OntologyHelper;

public interface NodeInterface{
	/*
	 * what does a node contain?
	 * a function returning its semantic description
	 * a function returning its preceding node
	 * a function returning its preceding edge
	 * a function returning its type
	 * a function returning its actor
	 * a function returning its id
	 * 
	 * a function returning true if it is a start event
	 * a function returning true if it is an End event
	 * a function returning true if it is a JOIN node
	 * a function returning true if it is a SPLIT node
	 */
	
	public String getSemanticDescription(); //name des Tasks
	public NodeInterface getPrecedingNode();
	public NodeInterface getSucceedingNode();
	public Set<NodeInterface> getAllSucceedingNodes();
	public Set<NodeInterface> getAllPrecedingNodes();
	public String getType(); // beispielsweise, dass es sich um einen Task handelt 
	public String getActor(); // Ausführende Instanz/Person des Tasks; bsp: Lagerfachkraft
	public String getId(); 
	public String getFormatedId();
	
	public void setSemanticDescription(String semanticDescription);
	public void setActor(String actor);
	public void setId(String id);

	public boolean isStartEvent();
	public boolean isEndEvent();
	public boolean isTask();
	public boolean isIntermediateEvent();
	public boolean isJOINNode();
	public boolean isSPLITNode();
	public boolean isAbstractNode();
	
	public void setSucceedingNode(NodeInterface succeedingNode);
	public void setPrecedingNode(NodeInterface precedingNode);
	public void setType(String type);
	public void addIntoSucceedingNode(NodeInterface node);
	public void addIntoPrecedingNodes(NodeInterface node);
	public void addAllIntoParticipants(Set<String> actors);
	
	 @Override
     public int hashCode();
     @Override
     public boolean equals(Object obj);
     
     public boolean isClass();
     public void setClass(boolean x);
}
