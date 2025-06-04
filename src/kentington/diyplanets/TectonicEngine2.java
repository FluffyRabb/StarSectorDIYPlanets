package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;

public class TectonicEngine2 extends TerraformIndustryNeedsArtifact {

	public TectonicEngine2()
	{
		super();
		
		TERRAFORM_TIME=600f;
	
		hazardAmount=0.5f;
	
		hazard=Conditions.EXTREME_TECTONIC_ACTIVITY;
		tag="TectonicEngine";
		artifactRequired="tectonic_attenuator";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 5);
	}
}