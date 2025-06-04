package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;

public class SolarReflector extends TerraformIndustryNeedsArtifact {
	
	public SolarReflector()
	{
		super();
		TERRAFORM_TIME=240f;
	
		hazardAmount=0.25f;
	
		hazard=Conditions.COLD;
		tag="SolarReflector";
		artifactRequired="solar_reflector";
		disqualifyCondition=Conditions.DARK;
		stationType = "stellar_mirror";
		stationName = "Stellar Mirror";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 2);
		demand(Commodities.METALS, 2);
	}
}