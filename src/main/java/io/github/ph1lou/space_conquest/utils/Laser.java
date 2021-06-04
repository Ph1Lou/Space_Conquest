package io.github.ph1lou.space_conquest.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import io.github.ph1lou.space_conquest.Main;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * A whole class to create Guardian Beams by reflection </br>
 * Inspired by the API <a href="https://www.spigotmc.org/resources/guardianbeamapi.18329">GuardianBeamAPI</a></br>
 * <b>1.9 -> 1.16</b>
 *
 * @see <a href="https://github.com/SkytAsul/GuardianBeam">GitHub page</a>
 * @author SkytAsul
 */
public class Laser {
    private static int teamID = 0;
    private Location start;
    private Location end;

    private Object createGuardianPacket;
    private Object createSquidPacket;
    private Object teamAddPacket;
    private Object destroyPacket;
    private Object metadataPacketGuardian;
    private Object metadataPacketSquid;
    private Object fakeGuardianDataWatcher;

    private int squid;
    private UUID squidUUID;
    private int guardian;
    private UUID guardianUUID;

    private BukkitRunnable run;
    private final HashSet<Player> show = new HashSet<>();

    /**
     * Create a Laser instance
     * @param start Location where laser will starts
     * @param end Location where laser will ends
     */
    public Laser(Location start, Location end) {
        this.start = start;
        this.end = end;

        try {
            createSquidPacket = Packets.createPacketSquidSpawn(end);
            squid = (int) Packets.getField(Packets.packetSpawn, "a", createSquidPacket);
            squidUUID = (UUID) Packets.getField(Packets.packetSpawn, "b", createSquidPacket);
            metadataPacketSquid = Packets.createPacketMetadata(squid, Packets.fakeSquidWatcher);
            Packets.setDirtyWatcher(Packets.fakeSquidWatcher);

            fakeGuardianDataWatcher = Packets.createFakeDataWatcher();
            createGuardianPacket = Packets.createPacketGuardianSpawn(start, fakeGuardianDataWatcher, squid);
            guardian = (int) Packets.getField(Packets.packetSpawn, "a", createGuardianPacket);
            guardianUUID = (UUID) Packets.getField(Packets.packetSpawn, "b", createGuardianPacket);
            metadataPacketGuardian = Packets.createPacketMetadata(guardian, fakeGuardianDataWatcher);
            teamAddPacket = Packets.createPacketTeamAddEntities(squidUUID, guardianUUID);
            destroyPacket = Packets.createPacketRemoveEntities(squid, guardian);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

    }

    public void start() {
        Validate.isTrue(run == null, "Task already started");
        run = new BukkitRunnable() {

            @Override
            public void run() {
                for (Player p : start.getWorld().getPlayers()) {
                    if (!show.contains(p)) {
                        sendStartPackets(p);
                        show.add(p);
                    }
                }
            }

            @Override
            public synchronized void cancel() throws IllegalStateException {
                super.cancel();
                for (Player p : show) {
                    Packets.sendPacket(p, destroyPacket);
                }
                run = null;
            }
        };
        run.runTaskTimerAsynchronously(JavaPlugin.getPlugin(Main.class), 0L, 20L);
    }

    public void stop() {
        Validate.isTrue(run != null, "Task not started");
        run.cancel();
    }

    public void moveStart(Location location) throws ReflectiveOperationException {
        this.start = location;
        Object packet = Packets.createPacketMoveEntity(start, guardian);
        for (Player p : show) {
            Packets.sendPacket(p, packet);
        }
    }

    public Location getStart() {
        return start;
    }

    public void moveEnd(Location location)  {

        if(location.equals(this.end)){
            return;
        }

        this.end = location;

        try {
            Object packet = Packets.createPacketMoveEntity(end, squid);
            for (Player p : show) {
                Packets.sendPacket(p, packet);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

    }

    public Location getEnd() {
        return end;
    }

    public void callColorChange(){
        for (Player p : show) {
            Packets.sendPacket(p, metadataPacketGuardian);
        }
    }

    public boolean isStarted() {
        return run != null;
    }

    private void sendStartPackets(Player p) {
        try {
            Packets.sendPacket(p, createSquidPacket);
            Packets.sendPacket(p, createGuardianPacket);
            if (Packets.version > 14) {
                Packets.sendPacket(p, metadataPacketSquid);
                Packets.sendPacket(p, metadataPacketGuardian);
            }
            Packets.sendPacket(p, Packets.createPacketTeamCreate("noclip-" + teamID++));
            Packets.sendPacket(p, teamAddPacket);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

    }

    private static class Packets {
        private static int lastIssuedEID = 2000000000;

        static int generateEID() {
            return lastIssuedEID++;
        }

        private static final String[] versions = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3].substring(1).split("_");
        private static final int version = Integer.parseInt(versions[1]); // 1.X
        private static final int versionMinor = Integer.parseInt(versions[2].substring(1)); // 1.X.Y
        private static final String npack = "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".";
        private static final String cpack = Bukkit.getServer().getClass().getPackage().getName() + ".";
        private static Constructor<?> watcherConstructor;
        private static Method watcherSet;
        private static Method watcherRegister;
        private static Method watcherDirty;
        private static Class<?> packetSpawn;
        private static Class<?> packetRemove;
        private static Class<?> packetTeleport;
        private static Class<?> packetTeam;
        private static Class<?> packetMetadata;
        private static Object watcherObject1; // invisilibity
        private static Object watcherObject2; // spikes
        private static Object watcherObject3; // attack id
        private static int squidID;
        private static int guardianID;

        private static Object fakeSquid;
        private static Object fakeSquidWatcher;

        static {
            try {
                String watcherName1, watcherName2, watcherName3;
                if (version < 13) {
                    watcherName1 = "Z";
                    watcherName2 = "bA";
                    watcherName3 = "bB";
                    squidID = 94;
                    guardianID = 68;
                }else if (version == 13) {
                    watcherName1 = "ac";
                    watcherName2 = "bF";
                    watcherName3 = "bG";
                    squidID = 70;
                    guardianID = 28;
                }else if (version == 14) {
                    watcherName1 = "W";
                    watcherName2 = "b";
                    watcherName3 = "bD";
                    squidID = 73;
                    guardianID = 30;
                }else if (version == 15) {
                    watcherName1 = "T";
                    watcherName2 = "b";
                    watcherName3 = "bA";
                    squidID = 74;
                    guardianID = 31;
                }else if (version == 16 && versionMinor < 2) {
                    watcherName1 = "T";
                    watcherName2 = "b";
                    watcherName3 = "d";
                    squidID = 74;
                    guardianID = 31;
                }else {
                    watcherName1 = "S";
                    watcherName2 = "b";
                    watcherName3 = "d";
                    squidID = 81;
                    guardianID = 31;
                }
                watcherObject1 = getField(Class.forName(npack + "Entity"), watcherName1, null);
                watcherObject2 = getField(Class.forName(npack + "EntityGuardian"), watcherName2, null);
                watcherObject3 = getField(Class.forName(npack + "EntityGuardian"), watcherName3, null);

                watcherConstructor = Class.forName(npack + "DataWatcher").getDeclaredConstructor(Class.forName(npack + "Entity"));
                watcherSet = getMethod(Class.forName(npack + "DataWatcher"), "set");
                watcherRegister = getMethod(Class.forName(npack + "DataWatcher"), "register");
                if (version >= 15) watcherDirty = getMethod(Class.forName(npack + "DataWatcher"), "markDirty");
                packetSpawn = Class.forName(npack + "PacketPlayOutSpawnEntityLiving");
                packetRemove = Class.forName(npack + "PacketPlayOutEntityDestroy");
                packetTeleport = Class.forName(npack + "PacketPlayOutEntityTeleport");
                packetTeam = Class.forName(npack + "PacketPlayOutScoreboardTeam");
                packetMetadata = Class.forName(npack + "PacketPlayOutEntityMetadata");

                Object world = Class.forName(cpack + "CraftWorld").getDeclaredMethod("getHandle").invoke(Bukkit.getWorlds().get(0));
                Object[] entityConstructorParams = version < 14 ? new Object[] { world } : new Object[] { Class.forName(npack + "EntityTypes").getDeclaredField("SQUID").get(null), world };
                fakeSquid = getMethod(Class.forName(cpack + "entity.CraftSquid"), "getHandle").invoke(Class.forName(cpack + "entity.CraftSquid").getDeclaredConstructors()[0].newInstance(
                        null, Class.forName(npack + "EntitySquid").getDeclaredConstructors()[0].newInstance(
                                entityConstructorParams)));
                fakeSquidWatcher = createFakeDataWatcher();
                tryWatcherSet(fakeSquidWatcher, watcherObject1, (byte) 32);
            }catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }

        public static void sendPacket(Player p, Object packet)  {

            try {
                Object entityPlayer = Class.forName(cpack + "entity.CraftPlayer").getDeclaredMethod("getHandle").invoke(p);
                Object playerConnection = entityPlayer.getClass().getDeclaredField("playerConnection").get(entityPlayer);
                playerConnection.getClass().getDeclaredMethod("sendPacket", Class.forName(npack + "Packet")).invoke(playerConnection, packet);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        public static Object createFakeDataWatcher() throws ReflectiveOperationException {
            Object watcher = watcherConstructor.newInstance(fakeSquid);
            if (version > 13) setField(watcher, "registrationLocked", false);
            return watcher;
        }

        public static void setDirtyWatcher(Object watcher) throws ReflectiveOperationException {
            if (version >= 15) watcherDirty.invoke(watcher, watcherObject1);
        }

        public static Object createPacketSquidSpawn(Location location) throws ReflectiveOperationException {
            Object packet = packetSpawn.newInstance();
            setField(packet, "a", generateEID());
            setField(packet, "b", UUID.randomUUID());
            setField(packet, "c", squidID);
            setField(packet, "d", location.getX());
            setField(packet, "e", location.getY());
            setField(packet, "f", location.getZ());
            setField(packet, "j", (byte) (location.getYaw() * 256.0F / 360.0F));
            setField(packet, "k", (byte) (location.getPitch() * 256.0F / 360.0F));
            if (version <= 14) setField(packet, "m", fakeSquidWatcher);
            return packet;
        }

        public static Object createPacketGuardianSpawn(Location location, Object watcher, int squidId) throws ReflectiveOperationException {
            Object packet = packetSpawn.newInstance();
            setField(packet, "a", generateEID());
            setField(packet, "b", UUID.randomUUID());
            setField(packet, "c", guardianID);
            setField(packet, "d", location.getX());
            setField(packet, "e", location.getY());
            setField(packet, "f", location.getZ());
            setField(packet, "j", (byte) (location.getYaw() * 256.0F / 360.0F));
            setField(packet, "k", (byte) (location.getPitch() * 256.0F / 360.0F));
            tryWatcherSet(watcher, watcherObject1, (byte) 32);
            tryWatcherSet(watcher, watcherObject2, false);
            tryWatcherSet(watcher, watcherObject3, squidId);
            if (version <= 14) setField(packet, "m", watcher);
            return packet;
        }

        public static Object createPacketRemoveEntities(int squidId, int guardianId) throws ReflectiveOperationException {
            Object packet = packetRemove.newInstance();
            setField(packet, "a", new int[] { squidId, guardianId });
            return packet;
        }

        public static Object createPacketMoveEntity(Location location, int entityId) throws ReflectiveOperationException {
            Object packet = packetTeleport.newInstance();
            setField(packet, "a", entityId);
            setField(packet, "b", location.getX());
            setField(packet, "c", location.getY());
            setField(packet, "d", location.getZ());
            setField(packet, "e", (byte) (location.getYaw() * 256.0F / 360.0F));
            setField(packet, "f", (byte) (location.getPitch() * 256.0F / 360.0F));
            setField(packet, "g", true);
            return packet;
        }

        public static Object createPacketTeamCreate(String teamName) throws ReflectiveOperationException {
            Object packet = packetTeam.newInstance();
            setField(packet, "a", teamName);
            setField(packet, "i", 0);
            setField(packet, "f", "never");
            return packet;
        }

        public static Object createPacketTeamAddEntities(UUID squidUUID, UUID guardianUUID) throws ReflectiveOperationException {
            Object packet = packetTeam.newInstance();
            setField(packet, "a", "noclip");
            setField(packet, "i", 3);
            Collection<String> players = (Collection<String>) getField(packetTeam, "h", packet);
            players.add(squidUUID.toString());
            players.add(guardianUUID.toString());
            return packet;
        }

        private static Object createPacketMetadata(int entityId, Object watcher) throws ReflectiveOperationException {
            return packetMetadata.getConstructor(int.class, watcher.getClass(), boolean.class).newInstance(entityId, watcher, false);
        }

        private static void tryWatcherSet(Object watcher, Object watcherObject, Object watcherData) throws ReflectiveOperationException {
            try {
                watcherSet.invoke(watcher, watcherObject, watcherData);
            }catch (InvocationTargetException ex) {
                watcherRegister.invoke(watcher, watcherObject, watcherData);
                if (version >= 15) watcherDirty.invoke(watcher, watcherObject);
            }
        }

        /* Reflection utils */
        private static Method getMethod(Class<?> clazz, String name) {
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.getName().equals(name)) return m;
            }
            return null;
        }

        private static void setField(Object instance, String name, Object value) throws ReflectiveOperationException {
            Validate.notNull(instance);
            Field field = instance.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(instance, value);
        }

        private static Object getField(Class<?> clazz, String name, Object instance) throws ReflectiveOperationException {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field.get(instance);
        }
    }
}