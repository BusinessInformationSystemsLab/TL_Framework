package OWLImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import IOHelper.OntologyHelper;
import interfaces.FragmentInterface;
import interfaces.NodeInterface;

public class MappingGenerator {
	
	public static MappingGenerator mappingGenerator = new MappingGenerator();

	public Set<MappingImpl> participantMappings = new HashSet<MappingImpl>();
	public Set<MappingImpl> blockMappings = new HashSet<MappingImpl>();
	public Set<MappingImpl> deadendMappings = new HashSet<MappingImpl>();
	public Set<MappingImpl> sequentialMappings = new HashSet<MappingImpl>();
	
	public static Set<MappingImpl> getFreshMappings() {
		MappingGenerator.mappingGenerator = new MappingGenerator();
		Set<MappingImpl> results = new HashSet<MappingImpl>();
		results.addAll(MappingGenerator.mappingGenerator.participantMappings);
		results.addAll(MappingGenerator.mappingGenerator.blockMappings);
		results.addAll(MappingGenerator.mappingGenerator.sequentialMappings);
		return results;
	}
	
	public FragmentInterface getTargetFragmentBySourceAbstractTaskDescription(String description, String id) {
		Set<MappingImpl> mappings = new HashSet<MappingImpl>();
		for(MappingImpl m:blockMappings) {
			if(m.getSourceFragment().getAbtractTask().getSemanticDescription().contentEquals(description)&&
					m.getSourceFragment().getAbtractTask().getId().contentEquals(id)
					&&m.isValidMapping()) {
					mappings.add(m);
			}
		}
		for(MappingImpl m:sequentialMappings) {
			if(m.getSourceFragment().getAbtractTask().getSemanticDescription().contentEquals(description)&&
					m.getSourceFragment().getAbtractTask().getId().contentEquals(id)
					&&m.isValidMapping()) {
					mappings.add(m);
			}
		}
	
		for(MappingImpl m:deadendMappings) {
			if(m.getSourceFragment().getAbtractTask().getSemanticDescription().contentEquals(description)&&
					m.getSourceFragment().getAbtractTask().getId().contentEquals(id)&&m.isValidMapping()) {
						mappings.add(m);
			}
		}
		for(MappingImpl m:participantMappings) {
			if(m.getSourceFragment().getAbtractTask().getSemanticDescription().contentEquals(description)&&
					m.getSourceFragment().getAbtractTask().getId().contentEquals(id)&&m.isValidMapping()) {
						mappings.add(m);
			}
		}
		if(mappings.size()>0) {
			MappingImpl result = mappings.iterator().next();
			for(MappingImpl m:mappings) {
				if(m.getMappingValue()>result.getMappingValue()) result = m;
			}
			return result.getTargetFragment();
		}else {
			return null;
		}
	
		
	}
	protected MappingGenerator() {
		  deadendMappings = buildGetMapping(OntologyHelper.sourceOntologyHelper.getDeadEndFragments(),OntologyHelper.targetOntologyHelper.getDeadEndFragments());
		  blockMappings = buildGetMapping(OntologyHelper.sourceOntologyHelper.getBlockFragments(),OntologyHelper.targetOntologyHelper.getBlockFragments());
		  participantMappings = buildGetMapping(OntologyHelper.sourceOntologyHelper.getParticipantFragments(),OntologyHelper.targetOntologyHelper.getParticipantFragments());
		  this.sequentialMappings = buildGetMapping(OntologyHelper.sourceOntologyHelper.getSequenceFragments(),OntologyHelper.targetOntologyHelper.getSequenceFragments());
//		  ArrayList<FragmentInterface> x = new ArrayList<FragmentInterface>();
//		  this.sequentialMappings.forEach(a -> x.add(a.getTargetFragment()));
//		  Set<FragmentInterface> s = new HashSet<FragmentInterface>(x);
//		  assert s.size() != x.size():"wrong";
	}
	
	 public Set<MappingImpl> buildGetMapping(Set<FragmentInterface> sourceFragments, Set<FragmentInterface> targetFragments){
		 Set<MappingImpl> result = new HashSet<MappingImpl>();
		 if(sourceFragments!=null&&targetFragments!=null&&sourceFragments.size()>0&&targetFragments.size()>0) {
			 for(FragmentInterface sourceFragment:sourceFragments) {
				 for(FragmentInterface targetFragment:targetFragments) {
					 MappingImpl mapping = new MappingImpl(sourceFragment, targetFragment);
					 if(mapping.isValidMapping()) {
						 result.add(mapping);
					 }
				 }
			 }
			 return result;

		 }else
			 return null;
	 }
	 public void writeMappingIntoOWL() {
		 OntologyHelper.sourceOntologyHelper.mergeWith(OntologyHelper.targetOntologyHelper);
		 OntologyHelper.sourceOntologyHelper.writeAllMappingsIntoOWL(participantMappings, FragmentType.participant);
		 OntologyHelper.sourceOntologyHelper.writeAllMappingsIntoOWL(blockMappings, FragmentType.block);
		 OntologyHelper.sourceOntologyHelper.writeAllMappingsIntoOWL(deadendMappings, FragmentType.deadend);
		 OntologyHelper.sourceOntologyHelper.writeAllMappingsIntoOWL(sequentialMappings, FragmentType.sequential);
		 OntologyHelper.sourceOntologyHelper.saveOntology("/home/xinyuan/eclipse-workspace/everII/Ontologie_Flughafen&SAP&Mappings.owl");
	 }
	 }
