package net.turtleboi.turtlecore.spells;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SpellScheduler {
    private final List<ScheduledSpellCast> spellEffect = new CopyOnWriteArrayList<>();

    public void schedule(long delayMillis, Runnable spawnTask) {
        long scheduledTime = System.currentTimeMillis() + delayMillis;
        spellEffect.add(new ScheduledSpellCast(scheduledTime, spawnTask));
    }

    public void tick() {
        long now = System.currentTimeMillis();
        List<ScheduledSpellCast> scheduledSpellEffects = new ArrayList<>(spellEffect);
        if (!scheduledSpellEffects.isEmpty()) {
            for (ScheduledSpellCast spellCast : scheduledSpellEffects) {
                if (spellCast.scheduledTime <= now) {
                    spellCast.castTask.run();
                    spellEffect.remove(spellCast);
                }
            }
        }
    }

    private record ScheduledSpellCast(long scheduledTime, Runnable castTask){}
}
