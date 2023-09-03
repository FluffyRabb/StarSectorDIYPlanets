package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class TectonicEngine extends TerraformIndustryNeedsArtifact {

	public TectonicEngine()
	{
		super();
		TERRAFORM_TIME=360f;
	
		hazardAmount=0.25f;
	
		hazard="tectonic_activity";
		tag="TectonicEngine";
		artifactRequired="tectonic_attenuator";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 3);
	}
}