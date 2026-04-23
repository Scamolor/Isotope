    package IsotopeSquared.content;

import IsotopeSquared.world.blocks.defense.*;
import IsotopeSquared.world.blocks.payload.*;
import IsotopeSquared.world.blocks.power.*;
import arc.graphics.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.content.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;
import static mindustry.content.Blocks.spectre;
import static mindustry.type.ItemStack.with;

public class Blocks{
    public static Block
    //environment
    grateDetailed, plating, pipes, ferrous, galliumLiquid,
    //production
    serpuloElectrolysisThing, eutecticMixer,
    //power
    seamlessSolarTest,
    //solar thermal
    solarThermalTank, solarThermalTurbine, solarThermalMirror,
    //utility
    phasedArray, tarsBloonDock,
    //payload
    payloadCutter, payloadWelder,
    //turrets
    icosphere, ignition, canopus, polaris, rigel;

    public static void load() {
        grateDetailed = new Floor("grate-detailed");
        plating = new Floor("plating");
        pipes = new Floor("pipes");
        ferrous = new Floor("ferrous"){{
            attributes.set(Attribute.water, -0.25f);
        }};
        galliumLiquid = new Floor("gallium-liquid"){{
            drownTime = 200f;
            status = StatusEffects.electrified;
            statusDuration = 240f;
            speedMultiplier = 0.175f;
            variants = 0;
            liquidDrop = Liquids.gallium;
            isLiquid = true;
            cacheLayer = CacheLayer.slag;
        }};
        canopus = new ItemTurret("canopus") {{
            requirements(Category.turret, with(Items.carbide, 500, Items.silicon, 1000));
            size = 8;
            range = 1000f;
            recoilTime = 160f;
            recoil = 5f;
            reload = 160f;
            ammo(
            Items.graphite, new BasicBulletType(7.5f, 50){{
                hitSize = 4.8f;
                width = 20f;
                height = 60f;
                shootEffect = Fx.shootBig;
                ammoMultiplier = 4;
                reloadMultiplier = 1.7f;
                knockback = 0.3f;

                hitEffect = despawnEffect = Fx.hitBulletColor;
                hitColor = backColor = trailColor = Pal.gray;
                frontColor = Pal.gray;
            }},
            Items.thorium, new BasicBulletType(8f, 80){{
                hitSize = 5;
                width = 20f;
                height = 60f;
                shootEffect = Fx.shootBig;
                pierceCap = 2;
                pierceBuilding = true;
                knockback = 0.7f;

                backColor = hitColor = trailColor = Pal.thoriumPink;
                frontColor = Pal.thoriumPink;
            }},
            Items.pyratite, new BasicBulletType(7f, 70){{
                hitSize = 5;
                width = 20f;
                height = 60f;
                frontColor = Pal.lightishOrange;
                backColor = Pal.lightOrange;
                status = StatusEffects.burning;
                hitEffect = new MultiEffect(Fx.hitBulletSmall, Fx.fireHit);
                shootEffect = Fx.shootBig;
                makeFire = true;
                pierceCap = 2;
                pierceBuilding = true;
                knockback = 0.6f;
                ammoMultiplier = 3;
                splashDamage = 20f;
                splashDamageRadius = 25f;
            }}
            );
            drawer = new DrawTurret("reinforced-"){{
                parts.add(new RegionPart("-arm"){{
                    progress = PartProgress.recoil;
                    rotation = -45;
                    moveRot = 45;
                    mirror = true;
                    x = 7f;
                    y = 25f;
                }});
            }};
            squareSprite = false;
        }};
        phasedArray = new PhasedArray("phased-array"){{
            requirements(Category.effect, with(Items.graphite, 50, Items.silicon, 25));
            allowDiagonal = false;
            consPower = consumePower(1.1f);
            fogRadius = 34;
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
        ignition = new ContinuousTurret("ignition") {{
            requirements(Category.turret, with(Items.copper, 900, Items.scrap, 350, Items.titanium, 300, Items.surgeAlloy, 325, Items.silicon, 325));
            size = 4;
            hasLiquids = true;
            range = 450f;
            reload = 4f;
            shootCone = 50f;
            liquidCapacity = 500f;
            shootSound = Sounds.fire;
            consumeLiquid(Liquids.hydrogen, 15f / 60f);
            consumeLiquid(Liquids.ozone, 120f / 60f);
            shootType = new ContinuousFlameBulletType(560f){{
                length = 800f;
                smokeEffect = Fx.shootPyraFlame;
                hitEffect = Fx.fuelburn;
                trailChance = 0.8f;
                trailLength = 100;
                width = 10f;
                drawSize = 420f;
                flareWidth = width * 0.8f;
                trailEffect = Fx.incendTrail;
                trailColor = Pal.darkFlame;
                status = StatusEffects.melting;
                pierce = true;
                statusDuration = 240f * 4;
                colors = new Color[]{Color.white, Pal.lightPyraFlame, Pal.slagOrange, Pal.darkPyraFlame, Pal.darkestMetal.a(0.4f)};
                incendAmount = 16;
                incendChance = 0.75f;
                incendSpread = 5f;
                armor = 5f;
                drawFlare = true;
                flareLength = range + 50f;
                flareColor = Pal.darkPyraFlame;
                lightStroke = 4f;
                intervalBullet = new SpaceLiquidBulletType(){{
                    damage = 225f;
                    orbSize = 10f;
                    speed = 15f;
                    lifetime = 120f;
                }};
                intervalBullets = 8;
            }};
            consumePower(5f);
        }};
        tarsBloonDock = new TarsBloonDock("tars-bloon-dock") {{
            requirements(Category.effect, with(Items.lead, 650, Items.silicon, 450, Items.titanium, 350, Items.thorium, 650));
            size = 4;
            consumePower(5f);
            buildVisibility = BuildVisibility.hidden;
        }};
        seamlessSolarTest = new SeamlessSolarGenerator("solar-panel-tileable"){{
            requirements(Category.power, with(Items.lead, 10, Items.silicon, 8));
            powerProduction = 0.12f;
            buildVisibility = BuildVisibility.hidden;
        }};

        //endregion
        //region payload experimental
        payloadCutter = new Cutter("payload-cutter"){{
            requirements(Category.units, with(Items.silicon, 50, Items.beryllium, 75, Items.tungsten, 40));
            hasPower = true;
            consumePower(2f);
            size = 3;
            fogRadius = 5;
        }};
    };
}
