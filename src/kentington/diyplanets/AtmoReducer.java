package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class AtmoReducer extends TerraformIndustryNeedsArtifact {

	public AtmoReducer()
	{
		super();
		TERRAFORM_TIME=360f;
	
		hazardAmount=0.5f;
	
		hazard="dense_atmosphere";
		tag="AtmoReducer";
		artifactRequired="atmo_mineralizer";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 3);
	}
}