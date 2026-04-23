package IsotopeSquared.world.blocks.defense;

import arc.*;
import arc.func.*;
import arc.graphics .*;
import arc.graphics.g2d .*;
import arc.math .*;
import arc.scene.ui.layout.*;
import arc.struct .*;
import arc.util.*;
import arc.util.io .*;
import mindustry .*;
import mindustry.entities.*;
import mindustry.entities.Units.*;
import mindustry.gen .*;
import mindustry.graphics .*;
import mindustry.world .*;
import mindustry.world.meta .*;

import static mindustry.Vars .*;

public class PhasedArray extends Block{
    public float discoveryTime = 60f * 10f;
    public float rotateSpeed = 2f;
    public float scanArc = 120f;
    public Color glowColor = Pal.turretHeat;
    public float glowScl = 5f, glowMag = 0.6f;
    public TextureRegion edgeLight, edgeDark, cornerLight, cornerDark, cornerBothNW, cornerBothSE, innerLight, innerDark;

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
        cornerBothNW = Core.atlas.find(name + "-corner-both-nw");
        cornerBothSE = Core.atlas.find(name + "-corner-both-se");
        innerLight = Core.atlas.find(name + "-inner-light");
        innerDark = Core.atlas.find(name + "-inner-dark");
        region = Core.atlas.find(name);
    }

    public class PhasedArrayBuild extends Building{
        public float progress, totalProgress, maxRange;
        public float lastRadius = 0f;
        public float smoothEfficiency = 1f;
        public int tilesWidth = 1, tilesHeight = 1, originX, originY;
        public Boolf<Unit> unitFilter = u -> true;
        public Sortf unitSort = UnitSorts.closest;

        @Override
        public float fogRadius(){
            Log.info(maxRange);
            Log.info(fogRadius * progress * smoothEfficiency);
            return Math.min((fogRadius * progress * smoothEfficiency), maxRange);
        }

        @Override
        public void updateTile(){
            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.05f);
            if(Math.abs(fogRadius() - lastRadius) >= 0.5f){
                Vars.fogControl.forceUpdate(team, this);
                lastRadius = fogRadius();
            }
            Log.info(lastRadius);
            progress += edelta() / discoveryTime;
            progress = Mathf.clamp(progress);
            totalProgress += efficiency * edelta();
            getRange();
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
        public void draw(){
            boolean n = nearby(0) instanceof PhasedArrayBuild;
            boolean e = nearby(1) instanceof PhasedArrayBuild;
            boolean s = nearby(2) instanceof PhasedArrayBuild;
            boolean w = nearby(3) instanceof PhasedArrayBuild;

            // 1. OUTER (CONVEX) CORNER LOGIC
            for(int i = 0; i < 4; i++){
                boolean b1 = (i == 0 ? n : i == 1 ? e : i == 2 ? s : w);
                boolean b2 = (i == 0 ? e : i == 1 ? s : i == 2 ? w : n);

                if(!b1 && !b2){
                    TextureRegion corner;
                    float rot;

                    if(i == 3){ // NORTHWEST
                        corner = cornerBothSE;
                        rot = 270;
                    }else if(i == 1){ // SOUTHEAST
                        corner = cornerBothNW;
                        rot = 90;
                    }else if(i == 0){ // NORTHEAST
                        corner = cornerLight;
                        rot = 0;
                    }else{ // SOUTHWEST (i == 2)
                        corner = cornerDark;
                        rot = 0;
                    }
                    Draw.rect(corner, x, y, rot);
                    return; // Exit: variant applied
                }
            }

            // 2. EDGE LOGIC
            int neighbors = (n ? 1 : 0) + (e ? 1 : 0) + (s ? 1 : 0) + (w ? 1 : 0);
            if(neighbors < 4){
                for(int i = 0; i < 4; i++){
                    boolean hasNeighbor = (i == 0 ? n : i == 1 ? e : i == 2 ? s : w);
                    if(!hasNeighbor){
                        TextureRegion edge = (i == 0 || i == 1) ? edgeLight : edgeDark;
                        float rot = (i == 0 || i == 1) ? (i * 90) - 90 : (i * 90) + 90;
                        Draw.rect(edge, x, y, rot);
                        return; // Exit: variant applied
                    }
                }
            }

            // 3. BASE + INNER CORNERS
            // If we reached here, the building is at least a "filled" square or has only inner corners.
            Draw.rect(region, x, y);

            for(int i = 0; i < 4; i++){
                Building b1 = nearby(i);
                Building b2 = nearby((i + 1) % 4);

                if(b1 instanceof PhasedArrayBuild && b2 instanceof PhasedArrayBuild){
                    int dx = (i == 0 || i == 1) ? 1 : -1;
                    int dy = (i == 0 || i == 3) ? 1 : -1;
                    Building diag = Vars.world.build(tileX() + dx, tileY() + dy);

                    if(!(diag instanceof PhasedArrayBuild)){
                        TextureRegion corner = (i == 0 || i == 3) ? innerLight : innerDark;
                        // Added the 180 flip to innerDark (i=1, 2) per your instructions
                        float rot = (i == 0 || i == 3) ? i * 90 : (i * 90) + 180 + 180;
                        Draw.rect(corner, x, y, rot);
                    }
                }
            }
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            Seq<PhasedArrayBuild> linked = new Seq<>();
            getLinked(linked);
            int minX = tileX(), maxX = tileX();
            int minY = tileY(), maxY = tileY();
            for(PhasedArrayBuild b : linked){
                minX = Math.min(minX, b.tileX());
                maxX = Math.max(maxX, b.tileX());
                minY = Math.min(minY, b.tileY());
                maxY = Math.max(maxY, b.tileY());
            }
            tilesWidth = (maxX - minX) + 1;
            tilesHeight = (maxY - minY) + 1;
            Log.info(tilesWidth);
            Log.info(tilesHeight);
        }

        public void getLinked(Seq<PhasedArrayBuild> out){
            Seq<PhasedArrayBuild> queue = new Seq<>();
            queue.add(this);
            out.add(this);
            while(queue.size > 0 && out.size < 400){
                PhasedArrayBuild current = queue.remove(0);
                for(int i = 0; i < 4; i++){
                    Building b = current.nearby(i);
                    if(b instanceof PhasedArrayBuild next && !out.contains(next)){
                        out.add(next);
                        queue.add(next);
                    }
                }
            }
        }

        public void getRange(){
            maxRange = (float)Math.pow((tilesWidth * tilesHeight), 0.75);
        }

        protected Posc findEnemy(){
            return Units.bestEnemy(team, x, y, maxRange, e -> !e.dead() && !e.isGrounded() && unitFilter.get(e), unitSort);
        }
    }
}

