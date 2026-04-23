package IsotopeSquared.content;

import mindustry.annotations.Annotations.*;
import mindustry.gen.*;
import mindustry.type.*;

public class UnitTypes{
    //bloon
    public static @EntityDef(value = {Unitc.class, BuildingTetherc.class}) UnitType tars;
    //endregion
    public static void load(){
        //bloon
        tars = new UnitType("tars-aerostat"){{
            fogRadius = 200f;
            faceTarget = false;
            health = 50f;
            flying = true;
            drag = 0.4f;
            canAttack = false;
            allowedInPayloads = false;
            logicControllable = false;
            playerControllable = false;
            isEnemy = false;
            constructor = UnitEntity::create;
        }};
    }
}
