package net.turtleboi.turtlecore.capabilities.party;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AutoRegisterCapability
public class PlayerParty {
    private final Set<UUID> partyMembers = new HashSet<>();

    public Set<UUID> getPartyMembers() {
        return partyMembers;
    }

    public void addMember(UUID member) {
        partyMembers.add(member);
    }

    public void removeMember(UUID member) {
        partyMembers.remove(member);
    }

    public boolean isMember(UUID uuid) {
        return partyMembers.contains(uuid);
    }

    public void copyFrom(PlayerParty source) {
        this.partyMembers.clear();
        this.partyMembers.addAll(source.partyMembers);
    }

    public void saveNBTData(CompoundTag nbt) {
        ListTag listTag = new ListTag();
        for (UUID uuid : partyMembers) {
            CompoundTag memberTag = new CompoundTag();
            memberTag.putUUID("uuid", uuid);
            listTag.add(memberTag);
        }
        nbt.put("partyMembers", listTag);
    }

    public void loadNBTData(CompoundTag nbt) {
        partyMembers.clear();
        ListTag listTag = nbt.getList("partyMembers", 10);
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag memberTag = listTag.getCompound(i);
            UUID uuid = memberTag.getUUID("uuid");
            partyMembers.add(uuid);
        }
    }
}
