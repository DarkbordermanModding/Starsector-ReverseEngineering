package mod.reverseengineering;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignClockAPI;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;

import mod.ReverseEngineeringPlugin;

public class ReverseEngineeringScript implements EveryFrameScript{
    public boolean firstTick = true;
    public int lastDayChecked = 0;
    private static Logger logger = Global.getLogger(ReverseEngineeringPlugin.class);

    public boolean isDone() {return false;}

    public boolean runWhilePaused() {return false;}

    // Check a day passed or not
    private boolean newDay() {
        CampaignClockAPI clock = Global.getSector().getClock();
        if (firstTick) {
            lastDayChecked = clock.getDay();
            firstTick = false;
            return false;
        } else if (clock.getDay() != lastDayChecked) {
            lastDayChecked = clock.getDay();
            return true;
        }
        return false;
    }

    public void advance(float var1){
        if (newDay()) {
            logger.log(Level.INFO, "NEW DAY");
            SectorEntityToken neturalPlatform = Global.getSector().getEntityById("corvus_abandoned_station");
            SubmarketAPI reverseEngineeringMarket = neturalPlatform.getMarket().getSubmarket("reverse_engineering");
            SubmarketAPI storage = neturalPlatform.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE);
            FleetDataAPI storedShips = reverseEngineeringMarket.getCargo().getMothballedShips();

            if (!storedShips.getMembersListCopy().isEmpty()){
                String blueprintId = "";
                for(FleetMemberAPI ship: storedShips.getMembersListCopy()){
                    blueprintId = ship.getHullId();
                    storedShips.removeFleetMember(ship);
                    break;
                }
                //add the blueprint to storage
                SpecialItemData data = new SpecialItemData(Items.SHIP_BP, blueprintId);
                storage.getCargo().addSpecial(data, 1);
            }
        }
    }
}
