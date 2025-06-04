package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;

public class SolarReflector2 extends TerraformIndustryNeedsArtifact {
	
	public SolarReflector2()
	{
		super();
		TERRAFORM_TIME=480f;
	
		hazardAmount=0.5f;
	
		hazard=Conditions.VERY_COLD;
		tag="SolarReflector";
		artifactRequired="solar_reflector";
		disqualifyCondition=Conditions.DARK;
		stationType = "stellar_mirror";
		stationName = "Stellar Mirror";
		stationType2 = "stellar_mirror";
		stationName2 = "Stellar Mirror";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 4);
		demand(Commodities.METALS, 4);
	}
}