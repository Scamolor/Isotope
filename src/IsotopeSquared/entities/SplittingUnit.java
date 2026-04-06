package IsotopeSquared.entities;

import arc.math.Angles;
import arc.util.Time;
import mindustry.gen.UnitEntity;

public class SplittingUnit extends UnitEntity {
    @Override
    public void update() {
        super.update();
        for(int i = 0; i < 50; i++) {
            float ox = Angles.trnsx(Time.time * 2 + (i * 10), 20);
            float oy = Angles.trnsy(Time.time * 2 + (i * 10), 20);
        }
    }
}
