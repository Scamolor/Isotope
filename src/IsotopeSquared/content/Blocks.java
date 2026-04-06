package IsotopeSquared.content;

import IsotopeSquared.world.blocks.defense.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.content.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mindustry.content.Blocks.spectre;
import static mindustry.type.ItemStack.with;

public class Blocks{
    public static Block
    //environment
    grateDetailed, plating, pipes, ferrous,
    //production
    serpuloElectrolysisThing, eutecticMixer,
    //power
    seamlessSolarTest,
    //solar thermal
    solarThermalTank, solarThermalTurbine, solarThermalMirror,
    //utility
    phasedArray, tarsBloonDock,
    //turrets
    icosphere, ignition, canopus, polaris, rigel;

    public static void load() {
        grateDetailed = new Floor("grate-detailed");
        plating = new Floor("plating");
        pipes = new Floor("pipes");
        ferrous = new Floor("ferrous"){{
            attributes.set(Attribute.water, -0.25f);
        }};

        //canopus = new ItemTurret("canopus") {{
        //    requirements = ItemStack.with(Items.tungsten, 800);
        //    size = 8;
        //    buildVisibility = BuildVisibility.editorOnly;
        //}};

        phasedArray = new PhasedArray("phased-array"){{
            requirements(Category.defense, with(Items.graphite, 50, Items.silicon, 25));
            allowDiagonal = false;
            consPower = consumePower(1.1f);
        }};

        icosphere = new ContinuousLiquidTurret("icosphere") {{
            requirements(Category.turret, with(Items.graphite, 325, Items.silicon, 175, Items.titanium, 250, Items.thorium, 100));
            range = 560f;
            size = 4;
            health = 1200;
            targetAir = targetGround = true;
            predictTarget = true;
            generateIcons = true;
            floating = true;
            placeableLiquid = true;
            liquidCapacity = 450f;
            liquidConsumed = 15f / 60f;
            editorIcon = spectre.editorIcon;
            ammo(
            Liquids.gallium, new PointLaserBulletType(){{
                damage = 40f;
                intervalBullet = new ContinuousFlameBulletType() {{
                    lightColor = Pal.neoplasm2;
                    oscScl = 6f;
                    oscMag = 5f;
                    flareColor = Pal.techBlue;
                    flareLength = lifetime = length = 75f;
                    damage = 100f;
                    width = flareWidth = 15f;
                    incendAmount = 4;
                    incendChance = 0.2f;
                }};
                intervalAngle = 90;
                intervalBullets = 11;
                intervalRandomSpread = 180;
                fragOnHit = true;
                status = StatusEffects.sporeSlowed;
                hitEffect = shootEffect = despawnEffect = smokeEffect = Fx.bubble;
                hitSoundPitch = 50f;
                hitSound = shootSound = Sounds.splash;
                beamEffect = new MultiEffect(Fx.fluxVapor, Fx.lightningShoot, Fx.conveyorPoof);
                beamEffectSize = 40f;
                color = Pal.boostTo;
            }});
            };
        };
    };
}
