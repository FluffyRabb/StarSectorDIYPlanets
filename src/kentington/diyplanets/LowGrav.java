package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class LowGrav extends TerraformIndustryAddHazard {
	
	public LowGrav()
	{
		super();
		TERRAFORM_TIME=10f;
	
		hazardAmount=0f;
	
		hazard="low_gravity";
		tag="LowGravity";
		artifactRequired="gravity_oscillator";
		oppositeIndustry="gravitydecrease";
		allowHabitable=true;
		hazardAlsoRemoved="high_gravity";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 2);
		demand(Commodities.VOLATILES, 2);
		demand(Commodities.RARE_METALS, 2);
	}
}