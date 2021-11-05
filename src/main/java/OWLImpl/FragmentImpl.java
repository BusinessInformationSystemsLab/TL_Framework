package OWLImpl;

import static guru.nidi.graphviz.attribute.Records.rec;
import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import IOHelper.OntologyHelper;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Records;
import guru.nidi.graphviz.attribute.Rank.RankDir;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import interfaces.FragmentInterface;
import interfaces.NodeInterface;

public class FragmentImpl implements FragmentInterface {
	
	private NodeInterface Preanchor;
	private NodeInterface Postanchor;
	private String type;
	private Set<String> Participantanchors  = new HashSet<String>();

	private  Set<NodeInterface> innerNodes = new HashSet<NodeInterface>();
	private Set<FragmentInterface> nest = new HashSet<FragmentInterface>();
	private NodeInterface abstractTask;

    public FragmentImpl(FragmentInterface f) {
    	FragmentImpl obj = (FragmentImpl) f;
    	
    	this.type = f.getType();
    	for(String x : f.getParticipants()) {
    		 this.Participantanchors.add(x);
    	 }
    	 List<NodeInterface> oldList = new ArrayList<NodeInterface>(f.getInnerNodes());
    	 List<NodeInterface> freshList = new ArrayList<NodeInterface>();

    	 for(NodeInterface x:f.getInnerNodes()) {
    		 freshList.add(new NodeImpl(x));
    	 }
    	 for(int i = 0; i<oldList.size();i++) {
    		 NodeInterface node = oldList.get(i);
    		 NodeInterface newNode = freshList.get(i);
    	
    		 for(NodeInterface x : node.getAllPrecedingNodes()) {
    			 if(oldList.contains(x)) {
        			 NodeInterface y = freshList.get(oldList.indexOf(x));
        			 newNode.addIntoPrecedingNodes(y);

    			 }
    		 }
    		 for(NodeInterface x : node.getAllSucceedingNodes()) {
    			 if(oldList.contains(x)) {
        			 NodeInterface y = freshList.get(oldList.indexOf(x));
        			 newNode.addIntoSucceedingNode(y);
    			 }
    		 }
    		 if(oldList.contains(node.getSucceedingNode())) {
        		 int a = oldList.indexOf(node.getSucceedingNode());
        		 newNode.setSucceedingNode(freshList.get(a));

    		 }
    		 if(oldList.contains(node.getPrecedingNode())) {
        		 int a = oldList.indexOf(node.getPrecedingNode());
        		 newNode.setPrecedingNode(freshList.get(a));
    		 }
    		 
    	 }
    	 
    	 this.innerNodes.addAll(freshList);
    	 
    	 this.Preanchor = new NodeImpl(f.getPreanchor());
    	 for(NodeInterface x : f.getPreanchor().getAllSucceedingNodes()) {
    		 if(!oldList.contains(x)) continue;
    		 int j = oldList.indexOf(x);
    		 this.Preanchor.addIntoSucceedingNode(freshList.get(j));
    	 }
    	 
    	 this.Postanchor = new NodeImpl(f.getPostanchor());
    	 for(NodeInterface x : f.getPostanchor().getAllPrecedingNodes()) {
    		 if(!oldList.contains(x)) {
    			 continue;
    		 }
    		 int j = oldList.indexOf(x);
    		 this.Postanchor.addIntoPrecedingNodes(freshList.get(j));
    	 }
    }
	public FragmentImpl(NodeInterface Preanchor, NodeInterface Postanchor, Set<String> Participantanchors) {
		// TODO Auto-generated constructor stub
		this.Preanchor = Preanchor;
		this.Postanchor = Postanchor;
		this.Participantanchors = Participantanchors;
		
	}
	public FragmentImpl(NodeInterface postanchor,String type) {
		// TODO Auto-generated constructor stub
		this.Postanchor = postanchor;
		this.setType(type);
	}

	public FragmentImpl(NodeInterface postanchor) {
		// TODO Auto-generated constructor stub
		this.Postanchor = postanchor;
		
	}

	public FragmentImpl() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public NodeInterface getAbtractTask() {
		// TODO Auto-generated method stub
	
		return this.abstractTask;
	}

	@Override
	public Set<FragmentInterface> getNEST() {
		// TODO Auto-generated method stub
		return this.nest;
	}

	@Override
	public NodeInterface getPreanchor() {
		// TODO Auto-generated method stub
		return this.Preanchor;
	}

	@Override
	public NodeInterface getPostanchor() {
		// TODO Auto-generated method stub
		if(this.Postanchor==null) {
			System.out.println("the post anchor is null");
		}
		return this.Postanchor;
	}

	@Override
	public Set<String> getParticipants() {
		// TODO Auto-generated method stub
		for(NodeInterface n: this.innerNodes) {
			this.Participantanchors.add(n.getActor());
		}
		return this.Participantanchors;
	}

	@Override
	public NodeInterface getFirstNode() {
		// TODO Auto-generated method stub
		for(NodeInterface firstNode:this.Preanchor.getAllSucceedingNodes()) {
			if(this.innerNodes.contains(firstNode)) {
				return firstNode;
			}
		}
		for(NodeInterface node:this.innerNodes) {
			if(!this.innerNodes.containsAll(node.getAllPrecedingNodes())) {
				return node;
			}
		}
		assert false:"no first node found!"+this.Preanchor.getSemanticDescription();
		return null;
	}

	@Override
	public boolean contains(NodeInterface node) {
		// TODO Auto-generated method stub
		return this.innerNodes.stream().anyMatch(x->x.equals(node));
	}

	@Override
	public boolean onlyPerformedBy(String participant) {
		// TODO Auto-generated method stub
		if(this.Participantanchors==null) return false;
		if(this.Participantanchors.contains(participant)) {
			if(this.Participantanchors.size()==1) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void exportTo(String path,String name) {
		// TODO Auto-generated method stub
		if(!path.endsWith("/")) {
			path = path+"/";
		}
		MutableGraph g = mutGraph(name).setDirected(true).graphAttrs().add(Rank.dir(RankDir.TOP_TO_BOTTOM));

		/**
		 * adding nodes
		 * 
		 * adding edges**/
		  for(NodeInterface x:this.innerNodes) {
				MutableNode v,w;
				if(x.isEndEvent()||x.isStartEvent()) {
					v = mutNode(x.getFormatedId()).add(Shape.CIRCLE).add(Label.of(x.getSemanticDescription()));
				}
				else if(x.isSPLITNode()||x.isJOINNode()) v = mutNode(x.getFormatedId()).add(Shape.DIAMOND).add(Label.of(x.getSemanticDescription()));
				else if(x.isTask()) v = mutNode(x.getFormatedId()).add(Shape.BOX).add(Label.of(x.getSemanticDescription()),Records.of(rec(x.getFormatedId()),rec(String.join("\n", x.getActor().split("(?<=\\G.{10})"))),rec(x.getSemanticDescription())));
				else v = mutNode(x.getFormatedId()).add(Shape.BOX, Style.DASHED).add(Label.of(x.getSemanticDescription()),Records.of(rec(x.getFormatedId()),rec(String.join("\n", x.getActor().split("(?<=\\G.{10})"))),rec(x.getSemanticDescription())));
				if(!x.isAbstractNode() && OntologyHelper.sourceOntologyHelper.nodes.contains(x)) v = v.add(Style.FILLED);
				
				g.add(v);
			}

		for(NodeInterface x : this.innerNodes) {
			
			for(NodeInterface y : x.getAllSucceedingNodes()) {
				if(this.innerNodes.contains(y)) {
					g.add(mutNode(x.getFormatedId()).addLink(mutNode(y.getFormatedId())));
				}
			}
		}
	
//		g.add(mutNode(this.getAbtractTask().getSemanticDescription()).add(Shape.RECTANGLE).add(Style.DASHED));
					
		try {
			Graphviz.fromGraph(g).height(500).render(Format.SVG).toFile(new File(path+name+".svg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}

	@Override
	public void setInnernodes(Set<NodeInterface> innerNodes) {
		// TODO Auto-generated method stub
		this.innerNodes = innerNodes;
	}

	@Override
	public void addToInnernodes(NodeInterface node) {
		// TODO Auto-generated method stub
		this.innerNodes.add(node);
	}

	@Override
	public void addToNest(FragmentInterface block) {
		// TODO Auto-generated method stub
		this.nest.add(block);
	}
	 @Override
     public int hashCode() {
		 int result = 17;
		 result = this.getFirstNode().getId().concat(this.getLastNode().getId()).hashCode()+result;
		 return result;

	 }
     @Override
     public boolean equals(Object obj) {
    	 FragmentImpl f = (FragmentImpl) obj;
    	 if(this.getPreanchor().equals(f.getPreanchor())) {
    		 if(this.getPostanchor().equals(f.getPostanchor())) {
    			 return true;
    		 }
    	 }
    	 return false;
     }
     public NodeInterface getLastNode() {
    		for(NodeInterface lastNode:this.Postanchor.getAllPrecedingNodes()) {
    			if(this.innerNodes.contains(lastNode)) {
    				return lastNode;
    			}
    		}
    		for(NodeInterface node:this.innerNodes) {
    			if(!this.innerNodes.containsAll(node.getAllSucceedingNodes())) {
    				return node;
    			}
    		}
    		assert false:"no last node found!"+this.Postanchor.getSemanticDescription();
    		return null;
     }

	@Override
	public Set<NodeInterface> getInnerNodes() {
		// TODO Auto-generated method stub
		Set<NodeInterface> innerNodes = new HashSet<NodeInterface>(this.innerNodes);

		return innerNodes;
	}

	@Override
	public void setPreanchor(NodeInterface node) {
		// TODO Auto-generated method stub
		this.Preanchor = node;
	}

	@Override
	public void setparticipantsAnchors(Set<String> participantsAnchors) {
		// TODO Auto-generated method stub
		this.Participantanchors = participantsAnchors;
	}
	@Override
	public void addParticipant(String participant) {
		// TODO Auto-generated method stub
		if(this.Participantanchors==null) {
			this.Participantanchors = new HashSet<String> ();
		}
		this.Participantanchors.add(participant);
	}

	@Override
	public void setPostanchor(NodeInterface postanchor) {
		// TODO Auto-generated method stub
		this.Postanchor = postanchor;
	}

	@Override
	public void addAllToInnernodes(Set<NodeInterface> nodes) {
		// TODO Auto-generated method stub
		this.innerNodes.addAll(nodes);
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return this.type;
	}
	/**
	 * 		if(type.contentEquals("participant-dependent")) {
			semanticDescription = this.getFirstNode().getActor()+ " DO " + this.getFirstNode().getSemanticDescription();
		}
		if(type.contentEquals("block")) {
			semanticDescription = this.getFirstNode().getSemanticDescription()+": "+ Ontology.split_labels.get(this.getFirstNode());
		}
		if(type.contentEquals("DeadEnd")) {
			semanticDescription =  this.getFirstNode().getSemanticDescription()+": "+ Ontology.split_labels.get(this.getFirstNode()+" with dead end");
		}
***/
	@Override
	public void setType(String type) {
		// TODO Auto-generated method stub
		this.type = type;
	}

	@Override
	public void setAbstractTask(NodeInterface node) {
		// TODO Auto-generated method stub
		this.abstractTask = node;
	}


}
