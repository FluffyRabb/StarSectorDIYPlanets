package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class TectonicEngine2 extends TerraformIndustryNeedsArtifact {

	public TectonicEngine2()
	{
		super();
		
		TERRAFORM_TIME=600f;
	
		hazardAmount=0.5f;
	
		hazard="extreme_tectonic_activity";
		tag="TectonicEngine";
		artifactRequired="tectonic_attenuator";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 5);
	}
}