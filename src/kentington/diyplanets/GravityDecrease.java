package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class GravityDecrease extends TerraformIndustryNeedsArtifact {
	
	public GravityDecrease()
	{
		super();
		TERRAFORM_TIME=10f;
	
		hazardAmount=0f;
	
		hazard="high_gravity";
		tag="GravityDecrease";
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