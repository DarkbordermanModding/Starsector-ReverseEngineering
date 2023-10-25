package mod.reverseengineering.industries;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;

public class ReverseEngineeringHub extends BaseIndustry{

    public void apply(){
        super.apply(true);
        Global.getSector().getMemory().set("REVERSE_ENABLED", true);
    }
}
