package mod.reverseengineering.campaign.submarkets;

import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin;

public class REMarketPlugin extends BaseSubmarketPlugin {

    @Override
    public void init(SubmarketAPI submarket) {super.init(submarket);}

    @Override
    public float getTariff() {return 0f;}

    @Override
    public boolean isFreeTransfer() {return true;}

    @Override
    public String getBuyVerb() {return "Take";}

    @Override
    public String getSellVerb() {return "Leave";}

    @Override
    public boolean isEnabled(CoreUIAPI ui) {return true;}

    @Override
    public boolean isParticipatesInEconomy() {return false;}

    @Override
    public void updateCargoPrePlayerInteraction() {
        return;
    }

    @Override
    public boolean isIllegalOnSubmarket(CargoStackAPI stack, TransferAction action) {
        return false;
    }

    @Override
    public boolean isIllegalOnSubmarket(String commodityId, TransferAction action) {
        return false;
    }

    @Override
    public boolean isIllegalOnSubmarket(FleetMemberAPI member, TransferAction action) {
        return false;
    }
}