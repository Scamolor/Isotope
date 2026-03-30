package IsotopeSquared.world.blocks.defense;

import arc.*;
import arc.graphics .*;
import arc.graphics.g2d .*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.*;
import mindustry.gen.*;
import mindustry.graphics .*;
import mindustry.world.*;
import mindustry.world.meta .*;

import static mindustry.Vars .*;

public class PhasedArray extends Block{
    public float discoveryTime = 60f * 10f;
    public float rotateSpeed = 2f;
    public float scanArc = 120f;
    public Color glowColor = Pal.turretHeat;
    public float glowScl = 5f, glowMag = 0.6f;
    public TextureRegion edgeLight, edgeDark, cornerLight, cornerDark;

    public PhasedArray(String name){
        super(name);
        update = solid = true;
        flags = EnumSet.of(BlockFlag.hasFogRadius);
        outlineIcon = true;
        fogRadius = 1;
        conductivePower = true;
        hasPower = true;
        connectedPower = true;
    }

    @Override
    public void load() {
        super.load();
        edgeLight = Core.atlas.find(name + "-edge-light");
        edgeDark = Core.atlas.find(name + "-edge-dark");
        cornerLight = Core.atlas.find(name + "-corner-light");
        cornerDark = Core.atlas.find(name + "-corner-dark");

    }

    public class PhasedArrayBuild extends Building{
        public PhasedArrayBuild rootArrayMember = this;
        public float progress;
        public float lastRadius = 0f;
        public float smoothEfficiency = 1f;
        public float totalProgress;
        public int tilesWidth = 1, tilesHeight = 1, originX, originY;
        int mask = 0;

        @Override
        public float fogRadius(){
            return fogRadius * progress * smoothEfficiency;
        }

        @Override
        public void updateTile(){
            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.05f);
            if(Math.abs(fogRadius() - lastRadius) >= 0.5f){
                Vars.fogControl.forceUpdate(team, this);
                lastRadius = fogRadius();
            }
            progress += edelta() / discoveryTime;
            progress = Mathf.clamp(progress);
            totalProgress += efficiency * edelta();
        }

        @Override
        public boolean canPickup(){
            return false;
        }

        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, fogRadius() * tilesize, Pal.accent);
        }

        @Override
        public float progress(){
            return progress;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(progress);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            progress = read.f();
        }

        @Override
        public void display(Table table){
            super.display(table);
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y);
            if ((mask & 1) == 0) Draw.rect("phased-array-edge-light", x, y, 0);
            if ((mask & 2) == 0) Draw.rect("phased-array-edge-light", x, y, 270);
            if ((mask & 4) == 0) Draw.rect("phased-array-edge-dark", x, y, 180);
            if ((mask & 8) == 0) Draw.rect("phased-array-edge-dark", x, y, 90);
        }


        @Override
        public void onProximityUpdate() {
            super.onProximityUpdate();

            // 1. Reset and find all connected pieces
            Seq<PhasedArrayBuild> linked = new Seq<>();
            getLinked(linked);

            // 2. Find the min/max tile coordinates
            int minX = tileX(), maxX = tileX();
            int minY = tileY(), maxY = tileY();

            for(PhasedArrayBuild b : linked) {
                minX = Math.min(minX, b.tileX());
                maxX = Math.max(maxX, b.tileX());
                minY = Math.min(minY, b.tileY());
                maxY = Math.max(maxY, b.tileY());
            }

            // 3. Calculate final dimensions
            tilesWidth = (maxX - minX) + 1;
            tilesHeight = (maxY - minY) + 1;
        }

        public void getLinked(Seq<PhasedArrayBuild> out){
            out.add(this);
            for(int i = 0; i < 4; i++){
                Building b = nearby(i);
                if(b instanceof PhasedArrayBuild && !out.contains((PhasedArrayBuild)b)){
                    ((PhasedArrayBuild)b).getLinked(out);
                }
            }
        }

        public boolean isRoot(){
            return rootArrayMember == this;
        }
    }
}

