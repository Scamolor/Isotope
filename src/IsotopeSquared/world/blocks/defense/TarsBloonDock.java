package IsotopeSquared.world.blocks.defense;

import arc.math.*;
import arc.util.*;
import mindustry.*;
import mindustry.annotations.Annotations.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;

import static IsotopeSquared.content.UnitTypes.tars;
import static mindustry.Vars.net;

public class TarsBloonDock extends Block{
    public UnitType unitType = tars;
    public float buildTime = 60f * 8f;
    public TarsBloonDock(String name){
        super(name);
        update = true; // Required to run the logic every frame
        solid = true;
        configurable = true;
    }

    public class TarsBloonDockBuild extends Building implements UnitTetherBlock{
        public @Nullable Unit unit; // This is the aerostat being tethered
        public int readUnitId = -1;
        public float buildProgress, totalProgress;
        public float warmup, readyness;

        @Remote(called = Loc.server)
        public void unitTetherBlockSpawned(Tile tile, int id){
            if(tile == null || !(tile.build instanceof UnitTetherBlock build)) return;
            build.spawned(id);
        }

        @Override
        public void updateTile(){
            if(unit != null && (unit.dead || !unit.isAdded())){
                unit = null;
            }

            if(readUnitId != -1){
                unit = Groups.unit.getByID(readUnitId);
                if(unit != null || !net.client()){
                    readUnitId = -1;
                }
            }

            warmup = Mathf.approachDelta(warmup, efficiency, 1f / 60f);
            readyness = Mathf.approachDelta(readyness, unit != null ? 1f : 0f, 1f / 60f);
            unitType = Vars.content.getByName(ContentType.unit, "isotope2-tars-aerostat");
            if(unit == null && Units.canCreate(team, unitType)){
                buildProgress += edelta() / buildTime;
                totalProgress += edelta();

                if(buildProgress >= 1f){
                    if(!net.client()){
                        unit = unitType.create(team);
                        if(unit instanceof BuildingTetherc bt){
                            bt.building(this);
                        }
                        unit.set(x, y);
                        unit.rotation = 90f;
                        unit.add();
                        Call.unitTetherBlockSpawned(tile, unit.id);
                    }
                }
            }
        }
        public void spawned(int id){
            Fx.spawn.at(x, y);
            buildProgress = 0f;
            if(net.client()){
                readUnitId = id;
            }
        }

        @Override
        public float totalProgress(){
            return totalProgress;
        }

        @Override
        public float progress(){
            return buildProgress;
        }

    }
}
