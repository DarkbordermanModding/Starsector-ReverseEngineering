package mod.reverseengineering;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignClockAPI;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.CargoAPI.CargoItemQuantity;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;


public class ReverseEngineeringScript implements EveryFrameScript{
    public boolean firstHour = true;
    public int lastHourChecked = 0;
    public boolean firstDay = true;
    public int lastDayChecked = 0;
    public boolean firstMonth = true;
    public int lastMonthChecked = 0;
    private static Logger logger = Global.getLogger(ReverseEngineeringScript.class);

    public boolean isDone() {return false;}

    public boolean runWhilePaused() {return false;}

    // Remove Dmods and others, get base blueprint hull
    private ShipHullSpecAPI getBaseShipHullSpec(ShipHullSpecAPI spec) {
        ShipHullSpecAPI base = spec.getDParentHull();
        if (!spec.isDefaultDHull() && !spec.isRestoreToBase()) {
            base = spec;
        }
        if (spec.isRestoreToBase()) {
            base = spec.getBaseHull();
        }
        return base;
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

    // Check a month passed or not
    private boolean newMonth() {
        CampaignClockAPI clock = Global.getSector().getClock();
        if (firstMonth) {
            lastMonthChecked = clock.getMonth();
            firstMonth = false;
            return false;
        } else if (clock.getMonth() != lastMonthChecked) {
            lastMonthChecked = clock.getMonth();
            return true;
        }
        return false;
    }

    public void advance(float var1){
        // Reverse engineering weapon/fighters each hour
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
            if(!reverseEngineeringMarket.getCargo().getFighters().isEmpty()){
                String fighterId = "";
                for(CargoItemQuantity<String> fighter: reverseEngineeringMarket.getCargo().getFighters()){
                    fighterId = fighter.getItem();
                    break;
                }
                reverseEngineeringMarket.getCargo().removeFighters(fighterId, 1);
                //add the blueprint to storage
                SpecialItemData data = new SpecialItemData(Items.FIGHTER_BP, fighterId);
                storage.getCargo().addSpecial(data, 1);
            }
            for(CargoStackAPI stack: reverseEngineeringMarket.getCargo().getStacksCopy()){
                if(stack.getSpecialDataIfSpecial() != null){
                    // int weaponCredit = 1000;
                    logger.log(Level.INFO, stack.getSpecialDataIfSpecial().getData());
                    logger.log(Level.INFO, stack.getSize());
                    logger.log(Level.INFO, stack.getSpecialDataIfSpecial().getId());
                    reverseEngineeringMarket.getCargo().removeStack(stack);
                }
            }
        }
        // Reverse engineering ship each day
        if (newDay()){
            SectorEntityToken neturalPlatform = Global.getSector().getEntityById("corvus_abandoned_station");
            SubmarketAPI reverseEngineeringMarket = neturalPlatform.getMarket().getSubmarket("reverse_engineering");
            SubmarketAPI storage = neturalPlatform.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE);

            FleetDataAPI storedShips = reverseEngineeringMarket.getCargo().getMothballedShips();
            if (!storedShips.getMembersListCopy().isEmpty()){
                String blueprintId = "";
                for(FleetMemberAPI ship: storedShips.getMembersListCopy()){
                    blueprintId = getBaseShipHullSpec(ship.getHullSpec()).getHullId();
                    storedShips.removeFleetMember(ship);
                    break;
                }
                //add the blueprint to storage
                SpecialItemData data = new SpecialItemData(Items.SHIP_BP, blueprintId);
                storage.getCargo().addSpecial(data, 1);
            }
        }
        // Cleanup blueprint stack each month
        if(newMonth()){
            
        }
    }
}
