package OWLImpl;

import static guru.nidi.graphviz.attribute.Records.rec;
import static guru.nidi.graphviz.model.Factory.mutGraph;
import static guru.nidi.graphviz.model.Factory.mutNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import IOHelper.OntologyHelper;
import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Rank;
import guru.nidi.graphviz.attribute.Rank.RankDir;
import guru.nidi.graphviz.attribute.Records;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Style;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;
import interfaces.FragmentInterface;
import interfaces.NodeInterface;
import interfaces.WorkflowInterface;
import logic.BlockOperator;
import logic.DeadendOperator;
import logic.LoopOperator;
import logic.PartiDepOperator;
import logic.SequenceOperator;

public class WorkflowImpl implements WorkflowInterface{
	public Set<NodeInterface> nodes = new HashSet<NodeInterface>();
	public Set<NodeInterface> events= new HashSet<NodeInterface>();
	public Set<NodeInterface> tasks= new HashSet<NodeInterface>();
	public Set<NodeInterface> gateways= new HashSet<NodeInterface>();
	public String semanticDescription;

	//Result sets
	public Set<FragmentInterface> deadendFragments;
	public Set<FragmentInterface> blockFragments;
	public Set<FragmentInterface> sequentialFragments;
	public Set<FragmentInterface> participantFragments;
	public Set<FragmentInterface> loopFragments;
	
	public int nodeSize;
	public List<NodeInterface[]> edges = new ArrayList<NodeInterface[]>();
	public WorkflowImpl() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public String getSemanticDescription() {
		// TODO Auto-generated method stub
		return this.semanticDescription==null?"no implemented":this.semanticDescription;
	}

	/**
	 * x : the preanchor (might be an abstract task)
	 * f : the fragment for inserting an abstract task**/
	public void replace(FragmentInterface f, NodeInterface x) {
		WorkflowImpl workflow = this;
		NodeInterface a = x, b = f.getAbtractTask(), c = f.getPostanchor();
		
		assert a == x:"x is wrong";
		
		a.getAllSucceedingNodes().removeAll(f.getInnerNodes());
		a.setSucceedingNode(b);
		a.addIntoSucceedingNode(b);
		
		b.setPrecedingNode(a);
		b.setSucceedingNode(c);
		b.addIntoPrecedingNodes(a);
		b.addIntoSucceedingNode(c);
		
		c.getAllPrecedingNodes().removeAll(f.getInnerNodes());
		c.setPrecedingNode(b);
		c.addIntoPrecedingNodes(b);
		
		workflow.nodes.add(b);
		if(x.getType().contentEquals(FragmentType.block)||
				x.getType().contentEquals(FragmentType.deadend)
				||x.getType().contentEquals(FragmentType.participant)
				||x.getType().contentEquals(FragmentType.sequential)) {
			a = x;
			a.getAllSucceedingNodes().removeAll(f.getInnerNodes());
			a.setSucceedingNode(b);
			a.addIntoSucceedingNode(b);

			workflow.nodes.add(a);
			
		}else 
			if(x.isSPLITNode()){
			x.getAllSucceedingNodes().removeAll(f.getInnerNodes());
			workflow.nodes.add(x);

		}
		workflow.nodes.removeAll(f.getInnerNodes());
	}
	public NodeInterface getAbstractTaskOf(FragmentInterface f, String type) {
		assert false:"wrong";
		if(f.getAbtractTask()!=null)return f.getAbtractTask();
		
		String semanticDescription = null,  id;
		NodeInterface precedingNode, succeedingNode;
		String actor;
		actor = f.getFirstNode().getActor();
		id    = f.getPreanchor().getId()+"_"+f.getPostanchor().getId();
		precedingNode = f.getPreanchor();
		succeedingNode = f.getPostanchor();
		if(type.contentEquals(FragmentType.participant)) {
			semanticDescription = f.getFirstNode().getActor().split(":")[0]+ " DO\n ";
			NodeInterface n = f.getFirstNode();
			Set<NodeInterface> nodes = f.getInnerNodes();
			while(f.getInnerNodes().contains(n)) {
				semanticDescription = semanticDescription + n.getSemanticDescription()+"\n";
				n = n.getSucceedingNode();
			} 
		}
		if(type.contentEquals(FragmentType.block)) {
			semanticDescription = f.getFirstNode().getSemanticDescription()+": \n";
		}
		if(type.contentEquals(FragmentType.deadend)) {
			semanticDescription =  f.getFirstNode().getSemanticDescription()+": \n with dead end";
		}
		if(type.contentEquals(FragmentType.sequential)) {
			semanticDescription = "sequential execution:\n";
			NodeInterface n = f.getFirstNode();
			Set<NodeInterface> nodes = f.getInnerNodes();
			while(f.getInnerNodes().contains(n)) {
				semanticDescription = semanticDescription + n.getSemanticDescription()+"\n";
				n = n.getSucceedingNode();
			}
		}
		if(type.contentEquals(FragmentType.loop)) {
			semanticDescription = "REPEAT:\n";
			for(NodeInterface v: f.getInnerNodes()) {
				semanticDescription = semanticDescription + v.getSemanticDescription()+"\n";
			}
		}
		f.setAbstractTask(new NodeImpl(semanticDescription,  id,  precedingNode, succeedingNode,
			 type, actor));
		f.getAbtractTask().addAllIntoParticipants(f.getParticipants());
		f.getAbtractTask().addIntoSucceedingNode(succeedingNode);
		return f.getAbtractTask();
	}

	@Override
	public void replace(FragmentInterface f, String type) {
		// TODO Auto-generated method stub
		NodeInterface x = this.getAbstractTaskOf(f, type);
		this.nodes.remove(f.getInnerNodes());
		this.nodes.add(x);
		x.getAllPrecedingNodes().removeAll(f.getInnerNodes());
		x.getAllSucceedingNodes().removeAll(f.getInnerNodes());
		x.getPrecedingNode().setSucceedingNode(x);
		x.getSucceedingNode().setPrecedingNode(x);
	}


	@Override
	public void exportTo(String path) {
		// TODO Auto-generated method stub
		this.exportTo(path, this.semanticDescription, this);
	}

	@Override
	public Set<NodeInterface> getTasks() {
		// TODO Auto-generated method stub
		Set<NodeInterface> results = new HashSet<NodeInterface>();
		Iterator iter = nodes.iterator();
		while(iter.hasNext()) {
			NodeInterface node = (NodeInterface) iter.next();
			if(node.getType().contentEquals(Type.Tasks)) {
				results.add(node);
			}	       
		}

		return results;
	}

	@Override
	public Set<NodeInterface> getEvents() {
		// TODO Auto-generated method stub
		Set<NodeInterface> results = new HashSet<NodeInterface>();
		Iterator iter = nodes.iterator();
		while(iter.hasNext()) {
			NodeInterface node = (NodeInterface) iter.next();
			if(node.getType().contentEquals(Type.StartEvent)||node.getType().contentEquals(Type.EndEvent)) {
				results.add(node);
			}	       
		}

		return results;
	}
	@Override
	public Set<NodeInterface> getStartEvents() {
		// TODO Auto-generated method stub
		Set<NodeInterface> results = new HashSet<NodeInterface>();
		Iterator iter = nodes.iterator();
		while(iter.hasNext()) {
			NodeInterface node = (NodeInterface) iter.next();
			if(node.getType().contentEquals(Type.StartEvent)) {
				results.add(node);
			}	       
		}

		return results;
	}

	@Override
	public Set<NodeInterface> getGateways() {
		// TODO Auto-generated method stub
		Set<NodeInterface> results = new HashSet<NodeInterface>();
		Iterator iter = nodes.iterator();
		while(iter.hasNext()) {
			NodeInterface node = (NodeInterface) iter.next();
			if(node.getType().contentEquals(Type.Split)||node.getType().contentEquals(Type.Join)) {
				results.add(node);
			}	       
		}

		return results;
	}

	@Override
	public Set<NodeInterface> getJOINnodes() {
		// TODO Auto-generated method stub
		Set<NodeInterface> results = new HashSet<NodeInterface>();
		Iterator iter = nodes.iterator();
		while(iter.hasNext()) {
			NodeInterface node = (NodeInterface) iter.next();
			if(node.getType().contentEquals(Type.Join)) {
				results.add(node);
			}	       
		}

		return results;
	}

	@Override
	public Set<NodeInterface> getSPLITnodes() {
		// TODO Auto-generated method stub
		Set<NodeInterface> results = new HashSet<NodeInterface>();
		Iterator iter = nodes.iterator();
		while(iter.hasNext()) {
			NodeInterface node = (NodeInterface) iter.next();
			if(node.getType().contentEquals(Type.Split)) {
				results.add(node);
			}	       
		}

		return results;
	}

	@Override
	public Set<FragmentInterface> getDeadEndFragments() {
		// TODO Auto-generated method stub
		if(this.deadendFragments==null) this.deadendFragments = (new DeadendOperator(this)).fragments;
		return this.deadendFragments;
	}

	@Override
	public Set<FragmentInterface> getBlockFragments() {
		// TODO Auto-generated method stub
		if(this.blockFragments==null) this.blockFragments = (new BlockOperator(this)).fragments;
		return this.blockFragments;
	}
	@Override
	public Set<FragmentInterface> getLoopFragments() {
		// TODO Auto-generated method stub
		
		if(this.loopFragments==null) this.loopFragments = (new LoopOperator(this)).fragments;
		return this.loopFragments;		
	}
	@Override
	public Set<FragmentInterface> getParticipantFragments() {
		// TODO Auto-generated method stub
		if(this.participantFragments==null) this.participantFragments = (new PartiDepOperator(this)).fragments;
		return this.participantFragments;
	}
	@Override
	public Set<FragmentInterface> getSequenceFragments() {
		// TODO Auto-generated method stub
		if(this.sequentialFragments==null) this.sequentialFragments = (new SequenceOperator(this)).fragments;
		return this.sequentialFragments;
	}
	@Override
	public Set<NodeInterface> getNodesCopy() {
		// TODO Auto-generated method stub
		Set<NodeInterface> nodes = new HashSet<NodeInterface>(this.nodes);
		return nodes;
	}


	@Override
	public void setParticipantFragments(Set<FragmentInterface> fragmentSet) {
		// TODO Auto-generated method stub
		for(FragmentInterface f : fragmentSet) {
			this.getAbstractTaskOf(f, FragmentType.participant);
		}
		this.participantFragments = fragmentSet;
	}

	
	@Override
	public void setDeadEndFragments(Set<FragmentInterface> fragmentSet) {
		// TODO Auto-generated method stub
		for(FragmentInterface f : fragmentSet) {
			this.getAbstractTaskOf(f, FragmentType.deadend);
		}
		this.deadendFragments = fragmentSet;
	}
	@Override
	public void setLoopFragments(Set<FragmentInterface> fragmentSet) {
		// TODO Auto-generated method stub
		for(FragmentInterface f : fragmentSet) {
			this.getAbstractTaskOf(f, FragmentType.loop);
		}
		this.loopFragments = fragmentSet;
	}
	@Override
	public void setBLockFragments(Set<FragmentInterface> fragmentSet) {
		// TODO Auto-generated method stub
		for(FragmentInterface f : fragmentSet) {
			this.getAbstractTaskOf(f, FragmentType.block);
		}
		this.blockFragments = fragmentSet;
	}

	@Override
	public void setSequentialFragments(Set<FragmentInterface> fragmentSet) {
		// TODO Auto-generated method stub
		for(FragmentInterface f: fragmentSet) {
			this.getAbstractTaskOf(f, FragmentType.sequential);
		}
		this.sequentialFragments = fragmentSet;
		
	}
	
	public static ArrayList<NodeInterface> visited = new ArrayList<NodeInterface>();
	public static void addLinks(NodeInterface parent, NodeInterface child, MutableGraph g) {
		assert parent.getFormatedId()!=null:"parent is null";
		assert child.getFormatedId() !=null:"child is null";
				g.add(mutNode(parent.getFormatedId()).addLink(mutNode(child.getFormatedId())));
				visited.add(child);
				for(NodeInterface v:child.getAllSucceedingNodes()) {
					if(!visited.contains(v))addLinks(child,v,g);
					else {
						g.add(mutNode(child.getFormatedId()).addLink(mutNode(v.getFormatedId())));
					}
				}
	}
	public static void exportTo(String path, String name, WorkflowImpl workflow, List<NodeInterface[]> edges) {
		MutableGraph g = mutGraph(name).setDirected(true).graphAttrs().add(Rank.dir(RankDir.TOP_TO_BOTTOM));
		
		  for(NodeInterface x:workflow.nodes) {
				MutableNode v,w;
				if(x.isEndEvent()||x.isStartEvent()) {
					v = mutNode(x.getFormatedId()).add(Shape.CIRCLE).add(Label.of(x.getSemanticDescription()));
				}
				else if(x.isSPLITNode()||x.isJOINNode()) v = mutNode(x.getFormatedId()).add(Shape.DIAMOND).add(Label.of(x.getSemanticDescription()));
				else if(x.isTask()) v = mutNode(x.getFormatedId()).add(Shape.BOX).add(Label.of(x.getSemanticDescription()),Records.of(rec(x.getFormatedId()),rec(String.join("\n", x.getActor().split("(?<=\\G.{10})"))),
						rec(x.getSemanticDescription())));
				
				else v = mutNode(x.getFormatedId()).add(Shape.BOX, Style.DASHED).add(Label.of(x.getSemanticDescription()),Records.of(rec(x.getFormatedId()),rec(String.join("\n", x.getActor().split("(?<=\\G.{10})"))),
						rec(x.getSemanticDescription())));
				if(!x.isAbstractNode() && OntologyHelper.sourceOntologyHelper.nodes.contains(x)) v = v.add(Style.FILLED);
				
				g.add(v);
			}

		  	for(NodeInterface[] edge: edges) {
		  		NodeInterface parent = edge[0];
		  		NodeInterface child  = edge[1];
		  		
				assert parent.getFormatedId()!=null:"parent is null";
				assert child.getFormatedId() !=null:"child is null";
						g.add(mutNode(parent.getFormatedId()).addLink(mutNode(child.getFormatedId())));

		  	}
		  	// avoid oversized file name
			try {
				String shortName = new String();
				if (name.length() > 10) {
						shortName = name.substring(0, 10);
				}
				else {
					shortName = name;
				}
				Graphviz.fromGraph(g).width(600).render(Format.SVG).toFile(new File(path+shortName+".svg"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}
	
	public static void exportTo(String path, String name, WorkflowImpl workflow) {
		MutableGraph g = mutGraph(name).setDirected(true).graphAttrs().add(Rank.dir(RankDir.TOP_TO_BOTTOM));
	
	  for(NodeInterface x:workflow.nodes) {
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
		NodeInterface startEvent = null;
		for(NodeInterface x:workflow.nodes) {
			if(x.isStartEvent()) {
				startEvent = x;
				break;
			}
		}
//		for(MutableGraph graph:lanes.values()) {
//			g.add(graph);
//		}
		visited = new ArrayList<NodeInterface>();
		addLinks(startEvent,startEvent.getSucceedingNode(),g);

	  	// avoid oversized file name
		try {
			String shortName = new String();
			if (name.length() > 10) {
					shortName = name.substring(0, 10);
			}
			else {
				shortName = name;
			}
			
			Graphviz.fromGraph(g).height(1000).render(Format.SVG).toFile(new File(path+shortName+".svg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
	
}
