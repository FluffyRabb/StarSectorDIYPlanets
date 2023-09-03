package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class AtmoRemover extends TerraformIndustryAddHazard {

	public AtmoRemover()
	{
		super();
		TERRAFORM_TIME=300f;
	
		hazardAmount=-0.5f;
	
		hazard="no_atmosphere";
		tag="AtmoRemover";
		artifactRequired="atmo_sublimator";
		oppositeIndustry="atmo_pump";
		hazardAlsoRemoved="extreme_weather";
		hazardAlsoRemoved2="thin_atmosphere";
		disqualifyCondition="toxic_atmosphere";
		disqualifyCondition2="thick_atmosphere";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 4);
		demand(Commodities.ORGANICS,2);
	}
}