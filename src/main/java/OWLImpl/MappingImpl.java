package OWLImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;

import IOHelper.TaxonomyHelper;
import interfaces.FragmentInterface;
import interfaces.MappingInterface;
import interfaces.NodeInterface;

public class MappingImpl implements MappingInterface, Comparable<MappingImpl>{
	
	public FragmentInterface sourceFragment;
	public FragmentInterface targetFragment;
	public Double			 mappingValue;
	
	
	public MappingImpl(FragmentInterface sourceFragment, FragmentInterface targetFragment) {
		// TODO Auto-generated constructor stub
		this.sourceFragment = sourceFragment;
		this.targetFragment = targetFragment;
		this.computeMappingValue();
	}

	@Override
	public double getMappingValue() {
		// TODO Auto-generated method stub
		if(this.mappingValue==null)return this.computeMappingValue();
		
		return this.mappingValue;
	}

	@Override
	public FragmentInterface getSourceFragment() {
		// TODO Auto-generated method stub
		return this.sourceFragment;
	}

	@Override
	public FragmentInterface getTargetFragment() {
		// TODO Auto-generated method stub
		return this.targetFragment;
	}

	@Override
	public boolean isValidMapping() {
		// TODO Auto-generated method stub
		return this.mappingValue>this.threshold?true:false;
	}

	public static double threshold = (double) 0;
	public Double			 sim_pre;
	public Double		     sim_post;
	
	/**
	 * mapping value = (preanchor mapping + postanchor mapping)/2**/
	@Override
	public double computeMappingValue() {
	
		double preanchorValue = this.computeNodeSim(this.sourceFragment.getPreanchor(), this.targetFragment.getPreanchor()),
			   postanchorValue = this.computeNodeSim(this.sourceFragment.getPostanchor(), this.targetFragment.getPostanchor());
		double pre_post = this.computeNodeSim(this.sourceFragment.getPreanchor(), this.targetFragment.getPostanchor()),
				post_pre = this.computeNodeSim(this.sourceFragment.getPostanchor(), this.targetFragment.getPreanchor());
		this.sim_pre = preanchorValue;
		this.sim_post = postanchorValue;
		this.mappingValue = Math.max(pre_post+post_pre, preanchorValue+postanchorValue);
		return this.mappingValue;
	}
	public Double computeNodeSim(NodeInterface x,NodeInterface y) {
		System.out.println(this.sourceFragment.getPostanchor().getSemanticDescription());
		OWLClass sourceParent = TaxonomyHelper.sourceTaxonomy.getTypeOfGateways(x);
		OWLClass targetParent = TaxonomyHelper.targetTaxonomy.getTypeOfGateways(y);
		
		if(this.isEvent(x)||this.isEvent(y)) {
			if(this.isEvent(x)&&this.isEvent(y)) {
				if(x.isStartEvent()==y.isStartEvent()) {
					return (double)  0.5;
				}else {
					return  (double) 0;
				}
			}else {
				return (double) 0;
			}
		}else {
			return (double) (TaxonomyHelper.sourceTaxonomy.getSimOfLCA(sourceParent, targetParent, TaxonomyHelper.targetTaxonomy));
		}
	}

	public boolean isEvent(NodeInterface x ) {
		return x.isStartEvent()||x.isEndEvent();
	}
	public double computeMappingValueWithContent() {
		double mappingValue=0;
		TaxonomyHelper sourceTaxonomy = TaxonomyHelper.sourceTaxonomy;
		TaxonomyHelper targetTaxonomy = TaxonomyHelper.targetTaxonomy;
		Set<NodeInterface> deletedFromTarget = new HashSet<NodeInterface>();
		for(NodeInterface x: this.sourceFragment.getInnerNodes()) {
			OWLNamedIndividual sourcePreanchor = sourceTaxonomy.df.getOWLNamedIndividual("http://owl.api.wf#"+((NodeImpl) x).iri.getIRIString().split("#")[1].split(":")[0]);
			double max = 0;
			NodeInterface mark = x;
			for(NodeInterface y:this.targetFragment.getInnerNodes()) {
				if(deletedFromTarget.contains(y)) continue;
				OWLNamedIndividual targetPreanchor = targetTaxonomy.df.getOWLNamedIndividual("http://owl.api.wf#"+((NodeImpl) y).iri.getIRIString().split("#")[1].split(":")[0]);
				double result = (double) (sourceTaxonomy.getSimOfLCA(sourcePreanchor, targetPreanchor, targetTaxonomy)/2);
				if(max<result) {
					max = result;
					mark = y;
				}
			}
			deletedFromTarget.add(mark);
			mappingValue = mappingValue + max;
		}
		mappingValue = mappingValue/this.sourceFragment.getInnerNodes().size();
		return mappingValue;
	}
	@Override
	public void setSourceFragment() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTargetFragment() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int compareTo(MappingImpl o) {
		// TODO Auto-generated method stub
	    if (o.mappingValue == null || this.mappingValue == null) {
	        return 0;
	      }
	      return new Double(this.mappingValue).compareTo(o.mappingValue);	
	}

}
