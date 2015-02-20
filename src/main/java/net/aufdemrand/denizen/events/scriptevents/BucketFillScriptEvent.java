package net.aufdemrand.denizen.events.scriptevents;

import net.aufdemrand.denizen.objects.dItem;
import net.aufdemrand.denizen.objects.dLocation;
import net.aufdemrand.denizen.objects.dMaterial;
import net.aufdemrand.denizen.utilities.DenizenAPI;
import net.aufdemrand.denizencore.events.ScriptEvent;
import net.aufdemrand.denizencore.objects.dObject;
import net.aufdemrand.denizencore.scripts.containers.ScriptContainer;
import net.aufdemrand.denizencore.utilities.CoreUtilities;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;

import java.util.HashMap;

public class BucketFillScriptEvent extends ScriptEvent implements Listener {

    // <--[event]
    // @Events
    // player fills bucket
    //
    // @Triggers when a player fills a bucket.
    //
    // @Cancellable true
    //
    // @Context
    // <context.item> returns the dItem of the bucket.
    // <context.location> returns the dLocation of the block clicked with the bucket.
    // <context.relative> returns the dLocation of the block in front of the clicked block.
    // <context.material> returns the dMaterial the the bucket is being filled with.
    //
    // -->


    public BucketFillScriptEvent() {
        instance = this;
    }

    public static BucketFillScriptEvent instance;
    public dItem item;
    public dMaterial material;
    public dLocation location;
    public dLocation relative;
    public PlayerBucketFillEvent event;


    @Override
    public boolean couldMatch(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        return CoreUtilities.xthArgEquals(1, lower, "fills")
                && (CoreUtilities.xthArgEquals(3, lower, "bucket")
                || CoreUtilities.xthArgEquals(2, lower, "bucket"));
    }

    @Override
    public boolean matches(ScriptContainer scriptContainer, String s) {
        String lower = CoreUtilities.toLowerCase(s);
        String iName = event.getBucket().getData().getName().toLowerCase();
        String iName2 = material.identifySimple().substring(2);
        return (lower.startsWith("player fills bucket")
            || lower.startsWith("player fills" + iName + "bucket")
            || lower.startsWith("player fills" + iName2 + "bucket"));
    }

    @Override
    public String getName() {
        return "BucketFill";
    }

    @Override
    public void init() {
        Bukkit.getServer().getPluginManager().registerEvents(this, DenizenAPI.getCurrentInstance());
    }

    @Override
    public void destroy() {
        PlayerBucketFillEvent.getHandlerList().unregister(this);
    }

    @Override
    public boolean applyDetermination(ScriptContainer container, String determination) {
        return super.applyDetermination(container, determination);
    }

    @Override
    public HashMap<String, dObject> getContext() {
        HashMap<String, dObject> context = super.getContext();
        context.put("location", location);
        context.put("relative", relative);
        context.put("item", item);
        context.put("material", material);
        return context;
    }

    @EventHandler
    public void onBucketFill(PlayerBucketFillEvent event) {
        location = new dLocation(event.getBlockClicked().getLocation());
        relative = new dLocation(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation());
        item = new dItem(event.getBucket());
        material = dMaterial.getMaterialFrom(event.getBlockClicked().getRelative(event.getBlockFace()).getType(), event.getBlockClicked().getRelative(event.getBlockFace()).getData());
        cancelled = event.isCancelled();
        this.event = event;
        fire();
        event.setCancelled(cancelled);
    }
}
