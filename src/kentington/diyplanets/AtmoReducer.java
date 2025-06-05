package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;

public class AtmoReducer extends TerraformIndustryNeedsArtifact {

	public AtmoReducer()
	{
		super();
		TERRAFORM_TIME=360f;
	
		hazardAmount=0.5f;
	
		hazard=Conditions.DENSE_ATMOSPHERE;
		tag="AtmoReducer";
		artifactRequired="atmo_mineralizer";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 3);
	}
}