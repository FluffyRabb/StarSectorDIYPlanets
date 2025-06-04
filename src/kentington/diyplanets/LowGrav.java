package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;

public class LowGrav extends TerraformIndustryAddHazard {
	
	public LowGrav()
	{
		super();
		TERRAFORM_TIME=10f;
	
		hazardAmount=0f;
	
		hazard=Conditions.LOW_GRAVITY;
		tag="LowGravity";
		artifactRequired="gravity_oscillator";
		oppositeIndustry="gravitydecrease";
		allowHabitable=true;
		hazardAlsoRemoved=Conditions.HIGH_GRAVITY;
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 2);
		demand(Commodities.VOLATILES, 2);
		demand(Commodities.RARE_METALS, 2);
	}
}