package OWLImpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.IRI;

import interfaces.NodeInterface;

public class NodeImpl implements NodeInterface {
	
	public String semanticDescription;
	public String id;
	public NodeInterface precedingNode;
	public NodeInterface succeedingNode;
	public String type;
	public String actor;
	
	public IRI iri;
	
	
	Set<NodeInterface> precedingNodes = new HashSet<NodeInterface>();
	Set<NodeInterface> succeedingNodes = new HashSet<NodeInterface>();
	Set<String>           participants = new HashSet<String>();
   
	public NodeImpl(NodeInterface tmp) {
		NodeImpl obj = (NodeImpl) tmp;
		this.semanticDescription = obj.semanticDescription;
		this.id = obj.id;
		this.type = obj.type;
		this.actor = obj.actor;
		this.iri = obj.iri;
		
		for(String x:participants){
			this.participants.add(x);
		}
	}
	public NodeImpl(String semanticDescription, String id, NodeInterface precedingNode,NodeInterface succeedingNode,
			String type,String actor) {
		// TODO Auto-generated constructor stub
		this.semanticDescription = semanticDescription;
		this.id = id;
		this.precedingNode=precedingNode;
		this.succeedingNode=succeedingNode;
		this.type=type;
		this.actor=actor;
		this.addIntoPrecedingNodes(precedingNode);
		this.addIntoSucceedingNode(succeedingNode);
	}
	public NodeImpl(String semanticDescription, String id, String type) {
		this.id = id;
		this.semanticDescription=semanticDescription;
		this.type=type;
		
	}
	public NodeImpl(String label, String id2) {
		// TODO Auto-generated constructor stub
		this.semanticDescription = label;
		this.id = id2;
	}
	
	@Override
	public String getSemanticDescription() {
		// TODO Auto-generated method stub
		return semanticDescription;
	}

	@Override
	public NodeInterface getPrecedingNode() {
		// TODO Auto-generated method stub
		return precedingNode;
	}

	@Override
	public NodeInterface getSucceedingNode() {
		// TODO Auto-generated method stub
		if(this.succeedingNode == null) {
//			System.out.println(this.getType());
//			System.out.println(this.semanticDescription);
//			System.out.println("this succeeding node is null");
		}
		return this.succeedingNode;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return this.type;
	}

	@Override
	public String getActor() {
		// TODO Auto-generated method stub
		return this.actor;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public boolean isStartEvent() {
		// TODO Auto-generated method stub
		if(this.type.contentEquals(Type.StartEvent)) return true;
		return false;
	}

	@Override
	public boolean isEndEvent() {
		// TODO Auto-generated method stub
		if(this.type.contentEquals(Type.EndEvent)) return true;
		return false;
	}
	@Override
	public boolean isTask() {
		// TODO Auto-generated method stub
		if(this.type.contentEquals(Type.Tasks)) return true;
		return false;
	}

	@Override
	public boolean isJOINNode() {
		// TODO Auto-generated method stub
		if(this.type.contentEquals(Type.Join)) return true;
		return false;
	}

	@Override
	public boolean isSPLITNode() {
		// TODO Auto-generated method stub
		if(this.type.contentEquals(Type.Split)) return true;
		return false;
	}
	@Override
	public void setSucceedingNode(NodeInterface succeedingNode) {
		// TODO Auto-generated method stub
		this.succeedingNode = succeedingNode;
	}
	@Override
	public void setPrecedingNode(NodeInterface precedingNode) {
		// TODO Auto-generated method stub
		this.precedingNode = precedingNode;	
	}
	@Override
	public Set<NodeInterface> getAllSucceedingNodes() {
		// TODO Auto-generated method stub
		
		return this.succeedingNodes;
	}
	@Override
	public Set<NodeInterface> getAllPrecedingNodes() {
		// TODO Auto-generated method stub
	
		return this.precedingNodes;
	}
	 @Override
     public int hashCode() {
		 int result = 17;
		 result = result + this.id.hashCode();
		 return result;
	 }
     @Override
     public boolean equals(Object obj) {
    	 NodeInterface n = (NodeImpl) obj;
    	 String id1 = n.getId();
    	 String id2 = this.getId();
    	 if(id1.contentEquals(id2))    	 return true;
    	 return false;
     }
	@Override
	public void setType(String type) {
		// TODO Auto-generated method stub
		this.type = type;
	}
	@Override
	public void setActor(String actor) {
		// TODO Auto-generated method stub
		this.actor = actor;
	}
	@Override
	public void addIntoSucceedingNode(NodeInterface node) {
		// TODO Auto-generated method stub
		this.succeedingNodes.add(node);
	}
	@Override
	public void addIntoPrecedingNodes(NodeInterface node) {
		// TODO Auto-generated method stub
		this.precedingNodes.add(node);
	}
	@Override
	public void addAllIntoParticipants(Set<String> actors) {
		// TODO Auto-generated method stub
		this.participants.addAll(actors);
	}
	@Override
	public void setSemanticDescription(String semanticDescription) {
		// TODO Auto-generated method stub
		this.semanticDescription = semanticDescription;
	}
	@Override
	public boolean isIntermediateEvent() {
		// TODO Auto-generated method stub
		return this.type.contentEquals(Type.Event);
	}
	@Override
	public boolean isAbstractNode() {
		// TODO Auto-generated method stub
		if(this.type.contentEquals(FragmentType.block)) return true;
		if(this.type.contentEquals(FragmentType.loop)) return true;
		if(this.type.contentEquals(FragmentType.sequential)) return true;
		if(this.type.contentEquals(FragmentType.deadend)) return true;
		if(this.type.contentEquals(FragmentType.participant)) return true;
		
		return false;
	}
	@Override
	public void setId(String id) {
		// TODO Auto-generated method stub
		this.id = id;
	}
	@Override
	public String getFormatedId() {
		// TODO Auto-generated method stub
		return String.join("\n", this.getId().split("(?<=\\G.{10})"));
	}
	public boolean isClass = false;
	@Override
	public boolean isClass() {
		// TODO Auto-generated method stub
		
		return this.isClass;
	}
	@Override
	public void setClass(boolean x) {
		// TODO Auto-generated method stub
		this.isClass=x;
	}
	
}
