package net.unityweaver.minecrafttranslateneo.client.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.unityweaver.minecrafttranslateneo.MinecraftTranslateModNeo;
import net.unityweaver.minecrafttranslateneo.client.guis.ingamesettings.InGameSettingsScreen;
import net.unityweaver.minecrafttranslateneo.client.managers.ChatManager;

@Mod.EventBusSubscriber(modid = MinecraftTranslateModNeo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEventHandler {
    
    private static boolean hasShownScreen = false;

    @SubscribeEvent
    public static void onPlayerJoinLevel(EntityJoinLevelEvent event) {
        // Check if this is the client player joining a level
        if (event.getEntity() == Minecraft.getInstance().player && !hasShownScreen) {
            // Install modified chat component first
            ChatManager chatManager = ChatManager.getInstance();
            if (!chatManager.isModifiedChatInstalled()) {
                boolean installed = chatManager.installModifiedChat();
                if (installed) {
                    System.out.println("[MinecraftTranslateNeo] Successfully installed ModifiedChatComponent");
                } else {
                    System.err.println("[MinecraftTranslateNeo] Failed to install ModifiedChatComponent");
                }
            }
            
            // Delay the screen opening slightly to ensure the world is fully loaded
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // Wait 1 second
                    Minecraft.getInstance().execute(() -> {
                        if (Minecraft.getInstance().screen == null) { // Only show if no other screen is open
                            Minecraft.getInstance().setScreen(new InGameSettingsScreen());
                        }
                    });
                    hasShownScreen = true; // Only show once per session
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    // Reset the flag when leaving a world so it shows again on next join
    @SubscribeEvent
    public static void onPlayerLeaveLevel(EntityJoinLevelEvent event) {
        if (event.getEntity() == Minecraft.getInstance().player && event.getLevel().isClientSide()) {
            hasShownScreen = false;
        }
    }
}