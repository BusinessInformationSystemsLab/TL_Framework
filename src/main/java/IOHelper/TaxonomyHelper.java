package IOHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.OWLOntologyWalker;
import org.semanticweb.owlapi.util.OWLOntologyWalkerVisitor;

import interfaces.NodeInterface;
import uk.ac.manchester.cs.owl.owlapi.OWLAnnotationAssertionAxiomImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplFloat;
import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplString;

public class TaxonomyHelper {
	String taxonomyPath ;
	
	public OWLOntology t;
	public OWLDataFactory df;
	public IRI IOR;
	public OWLOntologyManager manager;
	public OWLReasoner reasoner;
	public static TaxonomyHelper sourceTaxonomy = new TaxonomyHelper("./Evaluation/data sets/EVER 2/Taskhierarchie_AIR.owl");
	public static TaxonomyHelper targetTaxonomy = new TaxonomyHelper("./Evaluation/data sets/EVER 2/Taskhierarchie_SAP.owl");

	public TaxonomyHelper(String taxonomyPath) {
		this.taxonomyPath = taxonomyPath ;
		manager = OWLManager.createOWLOntologyManager();
			try {
				t = manager.loadOntologyFromOntologyDocument(new File(this.taxonomyPath));
				df = t.getOWLOntologyManager().getOWLDataFactory();
	  			IOR = IRI.create("http://owl.api.wf");
	  			reasoner = (new StructuralReasonerFactory()).createReasoner(t);
	  			reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS,InferenceType.CLASS_HIERARCHY);
			} catch (OWLOntologyCreationException e) {
				e.printStackTrace();
			}
		
	}
	
	public float getSimilarityOf(OWLClass cls) {
		OWLAnnotationProperty similarity 
			= df.getOWLAnnotationProperty("http://owl.api.wf#similarity");

		OWLLiteralImplFloat result = null;
		
		for(OWLAnnotationAxiom ax :  
				t.getAxioms(AxiomType.ANNOTATION_ASSERTION)) {
			OWLAnnotationProperty property = ((OWLAnnotationAssertionAxiomImpl)ax).getProperty();
			if(((OWLAnnotationAssertionAxiomImpl)ax).getProperty().equals(similarity)){
				OWLAnnotationSubject subject = ((OWLAnnotationAssertionAxiomImpl)ax).getSubject();
				if(subject.asIRI().get().equals(cls.getIRI()))
  				{
  	  				result = (OWLLiteralImplFloat)((OWLAnnotationAssertionAxiomImpl)ax).getValue();
  	  				return Float.valueOf(result.getLiteral());
  				}
  			}
		}
		return 0;
	}
	/**return target classes of a source class cls with a maximum similarity value**/
	public void getTargetClassSimilarTo(OWLClass cls) {
		OWLAnnotationProperty mapping 
		= df.getOWLAnnotationProperty("http://www.w3.org/2002/07/owl#mappedTo" );
		for(OWLAnnotationAxiom ax :  
			t.getAxioms(AxiomType.ANNOTATION_ASSERTION)) {
		if(((OWLAnnotationAssertionAxiomImpl)ax).getProperty().equals(mapping)){
			
			OWLAnnotationSubject max;
			OWLAnnotationSubject subject = ((OWLAnnotationAssertionAxiomImpl)ax).getSubject();
			OWLAnnotationValue value = ((OWLAnnotationAssertionAxiomImpl) ax).getValue();
			assert cls!=null:"wrong";
		}
	}
	}
	/**
	 * returned the source classes of a target class cls**/
	public Set<OWLClass> getMappedTo(OWLClass cls) {
		Set<OWLClass> result = new HashSet<OWLClass>();
		OWLAnnotationProperty mapping 
			= df.getOWLAnnotationProperty("http://www.w3.org/2002/07/owl#mappedTo" );
		
		for(OWLAnnotationAxiom ax :  
			t.getAxioms(AxiomType.ANNOTATION_ASSERTION)) {
		if(((OWLAnnotationAssertionAxiomImpl)ax).getProperty().equals(mapping)){
			OWLAnnotationSubject subject = ((OWLAnnotationAssertionAxiomImpl)ax).getSubject();
			assert cls!=null:cls;
			if(subject.asIRI().get().equals(cls.getIRI()))
				{
	  				result.add(df.getOWLClass((IRI)(((OWLAnnotationAssertionAxiomImpl) ax).getValue())));
				}
		}
	}
		return result;
	}

	public OWLClass getParentOf(OWLClass cls) {
		if(reasoner.getSuperClasses(cls,true).isEmpty()) {
			return null;
		}
		return reasoner.getSuperClasses(cls, true).getFlattened().iterator().next();
	}
	public OWLClass getParentOf(OWLNamedIndividual ind) {
		if(reasoner.getTypes(ind,true).isEmpty()) {
			return null;
		}
		return reasoner.getTypes(ind, true).getFlattened().iterator().next();
	}
	public String getParentOf(OWLNamedIndividual ind,boolean asString) {
		if(reasoner.getTypes(ind,true).isEmpty()) {
			return "not found";
		}
		return this.getParentOf(ind).toString();
	}
	
	/**get the lowest common ancestor of two classes in one taxonomy**/
	public OWLClass getLCA(OWLClass a, OWLClass b) {
		OWLClass result = null;

		while(result==null) {
			if(a.getIRI().getFragment().contentEquals(b.getIRI().getFragment())) result = a;
			if(reasoner.getSuperClasses(b,false).containsEntity(a)) result = a;
			a = this.getParentOf(a);
		}
		System.out.println("hey:"+result);
		return result;
	}
	
	/**get the lowest common ancestor of a source node and a target node
	 * STEP 1. GET MAPPED SOURCE CLASS(S) OF THE TARGET NODE
	 * STEP 2. FOR EACH SOURCE CLASS, GET LCA
	 * STEP 3. CHOOSE THE MAPPING MAXIMIZING THE SIMILARITY OF LCA**/
	public OWLClass getLCA(OWLNamedIndividual a, OWLNamedIndividual b, TaxonomyHelper target) {
		OWLClass 	  sourceParentClass = this.getParentOf(a);
		Set<OWLClass> mappedTo = target.getMappedTo(target.getParentOf(b));
		assert mappedTo.size()>0:"wrong "+ target.getParentOf(b);
		System.out.println(a+">"+b+"<"+mappedTo+">");

		OWLClass maxResult = mappedTo.iterator().next();
		float    sim = -1;
		for(OWLClass x:mappedTo) {
			OWLClass lowestCommonAncestor = this.getLCA(sourceParentClass, x);
			System.out.println("lowestCommonAncestor is "+lowestCommonAncestor);
			if(this.getSimilarityOf(lowestCommonAncestor)>sim) {
				maxResult = lowestCommonAncestor;
			}
		}
		return maxResult;
	}
	public OWLClass getLCA(OWLClass a, OWLClass b, TaxonomyHelper target) {
		OWLClass 	  sourceParentClass = a;
		Set<OWLClass> mappedTo = target.getMappedTo(b);
		assert mappedTo.size()>0:"wrong "+ b;
		System.out.println(a+">"+b+"<"+mappedTo+">");

		OWLClass maxResult = mappedTo.iterator().next();
		float    sim = -1;
		for(OWLClass x:mappedTo) {
			OWLClass lowestCommonAncestor = this.getLCA(sourceParentClass, x);
			System.out.println("lowestCommonAncestor is "+lowestCommonAncestor);
			if(this.getSimilarityOf(lowestCommonAncestor)>sim) {
				maxResult = lowestCommonAncestor;
			}
		}
		return maxResult;
	}
	/**get the similarity value of the lowest common ancestor of a source node and a target node
	 ***/
	public float getSimOfLCA(OWLClass a, OWLClass b, TaxonomyHelper target) {
		System.out.println(a+">"+b+"<"+this.getLCA(a, b,target));

//		if(this.getLCA(a, b, target).getIRI().getIRIString().contentEquals(this.getParentOf(a).getIRI().getIRIString())) {
//			return 1;
//		}
		return this.getSimilarityOf(this.getLCA( a,  b,  target));
	}
	public float getSimOfLCA(OWLNamedIndividual a, OWLNamedIndividual b, TaxonomyHelper target) {
		System.out.println(a+">"+b+"<"+this.getLCA(a, b,target));

//		if(this.getLCA(a, b, target).getIRI().getIRIString().contentEquals(this.getParentOf(a).getIRI().getIRIString())) {
//			return 1;
//		}
		return this.getSimilarityOf(this.getLCA( a,  b,  target));
	}
	public OWLNamedIndividual getOWLNamedIndividualOf(NodeInterface n) {
		return this.df.getOWLNamedIndividual("http://owl.api.wf#"+n.getSemanticDescription());
	}
	public OWLClass getOWLClassOf(NodeInterface n) {
		return this.df.getOWLClass("http://owl.api.wf#"+n.getSemanticDescription());
	}
	/**GET TYPE OF SPLIT NODE**/
	public OWLClass getTypeOfGateways(NodeInterface gateway) {
		System.out.println(gateway.getSemanticDescription()+" ......");
		OWLClass result;
		if(gateway.isSPLITNode()) {
			result = this.getParentOf(this.getOWLNamedIndividualOf(gateway));
			if(result==null) {
				return this.getTypeOfGateways(gateway.getPrecedingNode());
			}else {
				return result;
			}
		}else if(gateway.isJOINNode()) {
			result = this.getParentOf(this.getOWLNamedIndividualOf(gateway));
			if(result==null) {
				return this.getTypeOfGateways(gateway.getPrecedingNode());
			}else {
				return result;
			}
		}else {
			result = this.getParentOf(this.getOWLNamedIndividualOf(gateway));
			if(result ==null) return this.df.getOWLNothing();
			return result;
		}
	}

}
