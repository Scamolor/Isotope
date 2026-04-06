package IsotopeSquared;

import IsotopeSquared.content.*;
import arc.util.*;
import mindustry.mod.*;

public class Isotope extends Mod{

    public Isotope() {
        Log.info("Isotope Squared mod initialized.");
    }

    @Override
    public void loadContent() {
        Log.info("Loading Isotope content...");
        Blocks.load();
    }
}
