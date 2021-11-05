package interfaces;

public interface MappingInterface{
	/**
	 * a mapping is a tuple of workflow fragments, one from source domain,
	 * the other from target domain.
	 * a mapping has a mapping strength value**/
	double getMappingValue();
	FragmentInterface getSourceFragment();
	FragmentInterface getTargetFragment();
	/**a method return whether the mapping is valid**/
	boolean isValidMapping();
	
	double computeMappingValue();
	void setSourceFragment();
	void setTargetFragment();
	
}
