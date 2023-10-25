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
import com.fs.starfarer.api.campaign.CargoAPI.CargoItemQuantity;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;

import mod.ReverseEngineeringPlugin;

public class ReverseEngineeringScript implements EveryFrameScript{
    public boolean firstDay = true;
    public int lastDayChecked = 0;
    public boolean firstHour = true;
    public int lastHourChecked = 0;
    private static Logger logger = Global.getLogger(ReverseEngineeringPlugin.class);

    public boolean isDone() {return false;}

    public boolean runWhilePaused() {return false;}

    // Check a day passed or not
    private boolean newDay() {
        CampaignClockAPI clock = Global.getSector().getClock();
        if (firstDay) {
            lastDayChecked = clock.getDay();
            firstDay = false;
            return false;
        } else if (clock.getDay() != lastDayChecked) {
            lastDayChecked = clock.getDay();
            return true;
        }
        return false;
    }

    // Check a hour passed or not
    private boolean newHour(){
        CampaignClockAPI clock = Global.getSector().getClock();
        if (firstHour) {
            lastHourChecked = clock.getHour();
            firstHour = false;
            return false;
        } else if (clock.getHour() != lastHourChecked) {
            lastHourChecked = clock.getHour();
            return true;
        }
        return false;
    }

    public void advance(float var1){
        // Reverse engineering weapon each hour
        if (newHour()){
            SectorEntityToken neturalPlatform = Global.getSector().getEntityById("corvus_abandoned_station");
            SubmarketAPI reverseEngineeringMarket = neturalPlatform.getMarket().getSubmarket("reverse_engineering");
            SubmarketAPI storage = neturalPlatform.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE);

            if(!reverseEngineeringMarket.getCargo().getWeapons().isEmpty()){
                String weaponId = "";
                for(CargoItemQuantity<String> weapon: reverseEngineeringMarket.getCargo().getWeapons()){
                    weaponId = weapon.getItem();
                    break;
                }
                reverseEngineeringMarket.getCargo().removeWeapons(weaponId, 1);
                //add the blueprint to storage
                SpecialItemData data = new SpecialItemData(Items.WEAPON_BP, weaponId);
                storage.getCargo().addSpecial(data, 1);
            }
        }
        // Reverse engineering ship each day
        if (newDay()){
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
