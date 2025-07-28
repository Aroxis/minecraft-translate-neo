package net.unityweaver.minecrafttranslateneo.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CommonButtons;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.unityweaver.minecrafttranslateneo.MinecraftTranslateModNeo;
import net.unityweaver.minecrafttranslateneo.client.guis.InGameSettingsScreen;
import org.lwjgl.glfw.GLFW;

/**
 * Handles adding the language settings button to the ChatScreen in Minecraft 1.21
 */
@Mod.EventBusSubscriber(modid = MinecraftTranslateModNeo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ChatScreenHandler {
    
    private static SpriteIconButton settingsButton;
    private static boolean buttonAdded = false;

    /**
     * Adds the language settings button when ChatScreen opens
     */
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof ChatScreen chatScreen)) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        
        // Create language settings button using CommonButtons.language()
        settingsButton = CommonButtons.language(20, (button) -> {
            // Open settings screen when clicked
            mc.setScreen(new InGameSettingsScreen());
        }, true); // true for icon-only button
        
        // Position the button in bottom-left corner
        settingsButton.setPosition(2, chatScreen.height - 37);

        // Add button to the screen
        event.addListener(settingsButton);
        buttonAdded = true;
    }

    /**
     * Handle button interaction when ChatScreen is active
     */
    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof ChatScreen) || settingsButton == null || !buttonAdded) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        
        // Update button position in case screen was resized
        settingsButton.setPosition(2, event.getScreen().height - 37);
        
        // Handle mouse interaction (similar to original implementation)
        if (settingsButton.isHoveredOrFocused()) {
            long window = mc.getWindow().getWindow();
            boolean leftClicked = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            
            if (leftClicked) {
                settingsButton.onPress();
            }
        }
    }

    /**
     * Clean up when ChatScreen closes
     */
    @SubscribeEvent
    public static void onScreenClose(ScreenEvent.Closing event) {
        if (event.getScreen() instanceof ChatScreen) {
            settingsButton = null;
            buttonAdded = false;
        }
    }
}