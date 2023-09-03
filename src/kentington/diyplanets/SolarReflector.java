package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class SolarReflector extends TerraformIndustryNeedsArtifact {
	
	public SolarReflector()
	{
		super();
		TERRAFORM_TIME=240f;
	
		hazardAmount=0.25f;
	
		hazard="cold";
		tag="SolarReflector";
		artifactRequired="solar_reflector";
		disqualifyCondition="dark";
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