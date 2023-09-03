package kentington.diyplanets;

import com.fs.starfarer.api.impl.campaign.ids.Commodities;

public class RadRemover extends TerraformIndustryNeedsArtifact {
	
	public RadRemover()
	{
		super();
		TERRAFORM_TIME=10f;
	
		hazardAmount=0f;
	
		hazard="irradiated";
		tag="RadRemover";
		artifactRequired="rad_remover";
	}
	
	@Override
	public void apply() {
		super.apply(true);
		
		demand(Commodities.HEAVY_MACHINERY, 2);
		demand(Commodities.VOLATILES, 2);
		demand(Commodities.RARE_METALS, 2);
	}
}
