package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class PerfectClimate extends TerraformIndustryNeedsArtifact {
	
	public PerfectClimate()
	{
		super();
		TERRAFORM_TIME=360f;
	
		hazardAmount=0.0625f;
	
		hazard="habitable";
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
