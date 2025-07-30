package net.unityweaver.minecrafttranslateneo.client.guis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.unityweaver.minecrafttranslateneo.Config;
import net.unityweaver.minecrafttranslateneo.MinecraftTranslateModNeo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = MinecraftTranslateModNeo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TranslationDebugOverlay {
    
    private static boolean debugVisible = false;
    private static int tickCounter = 0;
    private static int currentLine = 0;
    
    public static void toggleDebug() {
        debugVisible = !debugVisible;
        currentLine = 0;
        if (debugVisible) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                mc.player.displayClientMessage(
                    Component.literal("§6[Minecraft Translate Neo] §7Debug mode enabled. Press F4 to toggle."), 
                    false
                );
            }
        }
    }
    
    public static boolean isDebugVisible() {
        return debugVisible;
    }
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !debugVisible) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        
        tickCounter++;
        
        // Display debug info every 2 seconds, cycling through different information
        if (tickCounter % 40 == 0) { // 40 ticks = 2 seconds
            displayDebugLine(mc);
        }
    }
    
    private static void displayDebugLine(Minecraft mc) {
        List<String> debugLines = createDebugInfo();
        
        if (debugLines.isEmpty()) return;
        
        // Cycle through debug lines
        if (currentLine >= debugLines.size()) {
            currentLine = 0;
        }
        
        String line = debugLines.get(currentLine);
        
        // Skip empty lines
        while (line.trim().isEmpty() && currentLine < debugLines.size() - 1) {
            currentLine++;
            line = debugLines.get(currentLine);
        }
        
        // Display as actionbar (above hotbar) for F3-like experience
        mc.player.displayClientMessage(
            Component.literal("§8[Debug] " + line), 
            true // actionbar
        );
        
        currentLine++;
    }
    
    private static List<String> createDebugInfo() {
        List<String> info = new ArrayList<>();
        
        // Header information
        info.add("§6Minecraft Translate Neo §7v1.0.0");
        
        // Translation status
        info.add("§eIn: " + (Config.incomingTranslationEnabled ? "§aON" : "§cOFF") + 
                 " §eOut: " + (Config.outgoingTranslationEnabled ? "§aON" : "§cOFF"));
        
        // Language information
        String inLang = Config.incomingTargetLanguage != null ? 
            Config.incomingTargetLanguage.getCode() : "§cNone";
        String outLang = Config.outgoingTargetLanguage != null ? 
            Config.outgoingTargetLanguage.getCode() : "§cNone";
        
        info.add("§eIncoming: §b" + inLang + " §eOutgoing: §b" + outLang);
        
        // Current screen information
        info.add("§eScreen: " + getCurrentScreenInfo());
        
        // System info - language API simplified for compatibility
        info.add("§eMinecraft Lang: §f" + "Current");
        
        // Status
        boolean isReady = Config.incomingTargetLanguage != null && Config.outgoingTargetLanguage != null;
        info.add("§eReady: " + (isReady ? "§aYes" : "§cNo") + " §eTranslations: §f0");
        
        // Controls reminder
        info.add("§7F4: Toggle Debug | T: Settings");
        
        return info;
    }
    
    /**
     * Get the current screen information using reflection
     */
    private static String getCurrentScreenInfo() {
        Minecraft mc = Minecraft.getInstance();
        Screen currentScreen = mc.screen;
        
        if (currentScreen == null) {
            return "§7Game (No GUI)";
        }
        
        try {
            String screenClassName = currentScreen.getClass().getSimpleName();
            
            // Try to get the title if it exists
            String title = getScreenTitle(currentScreen);
            
            if (title != null && !title.isEmpty()) {
                return "§f" + screenClassName + " §8(§7" + title + "§8)";
            } else {
                return "§f" + screenClassName;
            }
        } catch (Exception e) {
            return "§c" + currentScreen.getClass().getSimpleName() + " §8(Error)";
        }
    }
    
    /**
     * Attempt to extract screen title using reflection
     */
    private static String getScreenTitle(Screen screen) {
        try {
            // Try common title field names
            String[] titleFieldNames = {"title", "screenTitle", "name"};
            
            for (String fieldName : titleFieldNames) {
                Field titleField = findField(screen.getClass(), fieldName);
                if (titleField != null) {
                    titleField.setAccessible(true);
                    Object titleObj = titleField.get(screen);
                    
                    if (titleObj instanceof Component) {
                        return ((Component) titleObj).getString();
                    } else if (titleObj instanceof String) {
                        return (String) titleObj;
                    }
                }
            }
            
            // Try to get title using public methods
            try {
                var getTitle = screen.getClass().getMethod("getTitle");
                if (getTitle.getReturnType() == Component.class) {
                    Component titleComponent = (Component) getTitle.invoke(screen);
                    return titleComponent != null ? titleComponent.getString() : null;
                }
            } catch (Exception ignored) {}
            
        } catch (Exception e) {
            // Silently ignore reflection errors
        }
        
        return null;
    }
    
    /**
     * Find a field in the class hierarchy
     */
    private static Field findField(Class<?> clazz, String fieldName) {
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        return null;
    }
}