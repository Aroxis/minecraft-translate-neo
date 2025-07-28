package net.unityweaver.minecrafttranslateneo.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.unityweaver.minecrafttranslateneo.MinecraftTranslateModNeo;
import net.unityweaver.minecrafttranslateneo.client.guis.InGameSettingsScreen;
import net.unityweaver.minecrafttranslateneo.client.guis.TranslationDebugOverlay;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    
    public static final KeyMapping OPEN_SETTINGS = new KeyMapping(
        "key.minecrafttranslateneo.open_settings",
        GLFW.GLFW_KEY_T, // Default to 'T' key
        "key.categories.minecrafttranslateneo"
    );
    
    public static final KeyMapping TOGGLE_DEBUG = new KeyMapping(
        "key.minecrafttranslateneo.toggle_debug",
        GLFW.GLFW_KEY_F4, // Default to 'F4' key (like Minecraft's F3)
        "key.categories.minecrafttranslateneo"
    );

    @Mod.EventBusSubscriber(modid = MinecraftTranslateModNeo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModKeyBindings {
        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(OPEN_SETTINGS);
            event.register(TOGGLE_DEBUG);
        }
    }

    @Mod.EventBusSubscriber(modid = MinecraftTranslateModNeo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class KeyEventHandler {
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                // Handle settings screen keybinding
                while (OPEN_SETTINGS.consumeClick()) {
                    Minecraft mc = Minecraft.getInstance();
                    if (mc.screen == null) { // Only open if no other screen is active
                        mc.setScreen(new InGameSettingsScreen());
                    }
                }
                
                // Handle debug overlay keybinding
                while (TOGGLE_DEBUG.consumeClick()) {
                    TranslationDebugOverlay.toggleDebug();
                }
            }
        }
    }
}