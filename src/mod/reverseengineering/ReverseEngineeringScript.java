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
        // Reverse engineering weapon/fighters/ships each hour
        if (newHour()){
            SectorEntityToken neturalPlatform = Global.getSector().getEntityById("corvus_abandoned_station");
            SubmarketAPI reverseEngineeringMarket = neturalPlatform.getMarket().getSubmarket("reverse_engineering");
            SubmarketAPI storage = neturalPlatform.getMarket().getSubmarket(Submarkets.SUBMARKET_STORAGE);

            if(!reverseEngineeringMarket.getCargo().getWeapons().isEmpty()){
                String weaponId = "";
                int weaponCount = 0;
                for(CargoItemQuantity<String> weapon: reverseEngineeringMarket.getCargo().getWeapons()){
                    weaponId = weapon.getItem();
                    weaponCount = weapon.getCount();
                    break;
                }
                reverseEngineeringMarket.getCargo().removeWeapons(weaponId, weaponCount);
                SpecialItemData data = new SpecialItemData(Items.WEAPON_BP, weaponId);
                storage.getCargo().addSpecial(data, weaponCount);
            }
            if(!reverseEngineeringMarket.getCargo().getFighters().isEmpty()){
                String fighterId = "";
                int fighterCount = 0;
                for(CargoItemQuantity<String> fighter: reverseEngineeringMarket.getCargo().getFighters()){
                    fighterId = fighter.getItem();
                    fighterCount = fighter.getCount();
                    break;
                }
                reverseEngineeringMarket.getCargo().removeFighters(fighterId, fighterCount);
                SpecialItemData data = new SpecialItemData(Items.FIGHTER_BP, fighterId);
                storage.getCargo().addSpecial(data, fighterCount);
            }
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
            SectorEntityToken neturalPlatform = Global.getSector().getEntityById("corvus_abandoned_station");
            SubmarketAPI reverseEngineeringMarket = neturalPlatform.getMarket().getSubmarket("reverse_engineering");
            for(CargoStackAPI stack: reverseEngineeringMarket.getCargo().getStacksCopy()){
                if(stack.getSpecialDataIfSpecial() != null){
                    String specialId = stack.getSpecialDataIfSpecial().getId();
                    if(specialId.equals("fighter_bp") || specialId.equals("ship_bp") || specialId.equals("weapon_bp")){
                        // TODO: Separate each blueprint (weapon, fighter, hull) price
                        float blueprintCredit = 500f;
                        logger.log(Level.INFO, stack.getSpecialDataIfSpecial().getData());
                        logger.log(Level.INFO, stack.getSize());
                        logger.log(Level.INFO, stack.getSpecialDataIfSpecial().getId());
                        Global.getSector().getPlayerFleet().getCargo().getCredits().add(blueprintCredit * stack.getSize());
                        reverseEngineeringMarket.getCargo().removeStack(stack);
                    }
                }
            }
        }
    }
}
