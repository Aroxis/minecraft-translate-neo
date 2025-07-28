package net.unityweaver.minecrafttranslateneo.client.guis;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.unityweaver.minecrafttranslateneo.Config;
import net.unityweaver.minecrafttranslateneo.MinecraftTranslateModNeo;

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
        if (mc.player == null || mc.level == null || mc.screen != null) return;
        
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
        
        // System info - language API simplified for compatibility
        info.add("§eMinecraft Lang: §f" + "Current");
        
        // Status
        boolean isReady = Config.incomingTargetLanguage != null && Config.outgoingTargetLanguage != null;
        info.add("§eReady: " + (isReady ? "§aYes" : "§cNo") + " §eTranslations: §f0");
        
        // Controls reminder
        info.add("§7F4: Toggle Debug | T: Settings");
        
        return info;
    }
}