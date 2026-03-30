package IsotopeSquared.world.blocks.defense;

import arc.*;
import arc.graphics .*;
import arc.graphics.g2d .*;
import arc.graphics.gl.*;
import arc.math .*;
import arc.scene.ui.layout.*;
import arc.struct .*;
import arc.util.*;
import arc.util.io .*;
import mindustry .*;
import mindustry.gen .*;
import mindustry.graphics .*;
import mindustry.world .*;
import mindustry.world.meta .*;

import static mindustry.Vars .*;

public class PhasedArray extends Block{
    protected static final Seq<PhasedArrayBuild> queue = new Seq<>();
    protected static final Seq<PhasedArrayBuild> arrayMembers = new Seq<>();
    protected static final IntSet processed = new IntSet();
    //in tiles
    public int maxDisplayDimensions = 12;

    public float discoveryTime = 60f * 10f;
    public float rotateSpeed = 2f;
    public float scanArc = 120f;

    public Color glowColor = Pal.turretHeat;
    public float glowScl = 5f, glowMag = 0.6f;

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

    public static void LinkPhasedArrays(PhasedArrayBuild start){
        PhasedArrayBuild root = null;

        int topX = start.tile.x, topY = start.tile.y, botX = start.tile.x, botY = start.tile.y;

        queue.clear();
        arrayMembers.clear();
        processed.clear();

        queue.add(start);
        arrayMembers.add(start);

        while(!queue.isEmpty()){
            var next = queue.pop();
            processed.add(next.id);

            //assign root based on bottom leftmost position
            if(root == null || next.tile.x < root.tile.x || next.tile.y < root.tile.y){
                root = next;
            }

            topX = Math.max(next.tile.x, topX);
            topY = Math.max(next.tile.y, topY);
            botX = Math.min(next.tile.x, botX);
            botY = Math.min(next.tile.y, botY);

            for(var prox : next.proximity){
                if(prox instanceof PhasedArrayBuild disp && processed.add(disp.id)){
                    queue.add(disp);
                    arrayMembers.add(disp);
                }
            }
        }

        int tilesWidth = topX - botX + 1, tilesHeight = topY - botY + 1;

        for(var member : arrayMembers){
            member.rootArrayMember = root;
            member.tilesWidth = tilesWidth;
            member.tilesHeight = tilesHeight;
            member.originX = botX;
            member.originY = botY;
        }
    }

    public class PhasedArrayBuild extends Building{
        //bottom left corner of display
        public PhasedArrayBuild rootArrayMember = this;

        public float progress;
        public float lastRadius = 0f;
        public float smoothEfficiency = 1f;
        public float totalProgress;
        public int tilesWidth = 1, tilesHeight = 1, originX, originY;

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
        }

        @Override
        public void draw(){
            Draw.rect(this.region, x, y);
            Draw.blend();
        }


        @Override
        public void onProximityAdded(){
            super.onProximityAdded();

            linkPhasedArrays(this);
        }

        @Override
        public void onProximityRemoved(){
            super.onProximityRemoved();

            processed.clear();

            for(var other : proximity){
                if(other instanceof PhasedArrayBuild tl && !processed.contains(tl.id)){
                    linkPhasedArrays(tl);
                }
            }
        }

        public boolean isRoot(){
            return rootArrayMember == this;
        }
    }
}

