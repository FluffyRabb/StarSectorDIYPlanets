package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;

public class PerfectClimate extends TerraformIndustryNeedsArtifact {
	
	public PerfectClimate()
	{
		super();
		TERRAFORM_TIME=360f;
	
		hazardAmount=0.0625f;
	
		hazard=Conditions.HABITABLE;
		tag="PerfectClimate";
		artifactRequired="climate_sculptor";
		requiresTerran = true;
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 2);
		demand(Commodities.ORGANICS, 2);
	}
}
