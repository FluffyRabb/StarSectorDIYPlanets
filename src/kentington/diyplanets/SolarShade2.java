package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;

public class SolarShade2 extends TerraformIndustryNeedsArtifact {
	
	public SolarShade2()
	{
		super();
		TERRAFORM_TIME=480f;
	
		hazardAmount=0.5f;
	
		hazard=Conditions.VERY_HOT;
		tag="SolarShade";
		artifactRequired="solar_reflector";
		disqualifyCondition=Conditions.DARK;
		stationType = "stellar_shade";
		stationName = "Stellar Shade";
		stationType2 = "stellar_shade";
		stationName2 = "Stellar Shade";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 4);
		demand(Commodities.METALS, 4);
	}
}