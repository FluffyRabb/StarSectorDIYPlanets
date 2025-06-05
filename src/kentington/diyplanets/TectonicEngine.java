package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;

public class TectonicEngine extends TerraformIndustryNeedsArtifact {

	public TectonicEngine()
	{
		super();
		TERRAFORM_TIME=360f;
	
		hazardAmount=0.25f;
	
		hazard=Conditions.TECTONIC_ACTIVITY;
		tag="TectonicEngine";
		artifactRequired="tectonic_attenuator";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 3);
	}
}