package net.turtleboi.turtlecore.network.packet.util.experience;

public interface ExperienceHandler {
    void handleExperienceSync(int totalExperience, int experienceLevel, float experienceProgress);
}
