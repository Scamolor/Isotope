package IsotopeSquared.content;

import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.content.*;

public class Blocks {
    public static Block canopus;

    public static void load() {
        canopus = new ItemTurret("canopus") {{
            requirements = ItemStack.with(Items.copper, 100);
        }};
    }
}
