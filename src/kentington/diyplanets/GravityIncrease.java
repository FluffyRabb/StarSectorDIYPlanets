package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class GravityIncrease extends TerraformIndustryNeedsArtifact {
	
	public GravityIncrease()
	{
		super();
		TERRAFORM_TIME=10f;
	
		hazardAmount=0f;
	
		hazard="low_gravity";
		tag="GravityIncrease";
		artifactRequired="gravity_oscillator";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 2);
		demand(Commodities.VOLATILES, 2);
		demand(Commodities.RARE_METALS, 2);
	}
}