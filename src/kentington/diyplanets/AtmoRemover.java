package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;

public class AtmoRemover extends TerraformIndustryAddHazard {

	public AtmoRemover()
	{
		super();
		TERRAFORM_TIME=300f;
	
		hazardAmount=-0.5f;
	
		hazard=Conditions.NO_ATMOSPHERE;
		tag="AtmoRemover";
		artifactRequired="atmo_sublimator";
		oppositeIndustry="atmo_pump";
		hazardAlsoRemoved=Conditions.EXTREME_WEATHER;
		hazardAlsoRemoved2=Conditions.THIN_ATMOSPHERE;
		disqualifyCondition=Conditions.TOXIC_ATMOSPHERE;
		disqualifyCondition2=Conditions.DENSE_ATMOSPHERE;
		
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 4);
		demand(Commodities.ORGANICS,2);
	}
}