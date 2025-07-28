package net.unityweaver.minecrafttranslateneo.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.unityweaver.minecrafttranslateneo.Config;
import net.unityweaver.minecrafttranslateneo.MinecraftTranslateModNeo;
import net.unityweaver.minecrafttranslateneo.models.TranslationComponent;
import net.unityweaver.minecrafttranslateneo.helpers.ComponentCosmeticsHelper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Handles chat events for translation processing in Minecraft 1.21
 */
@Mod.EventBusSubscriber(modid = MinecraftTranslateModNeo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ChatTranslationHandler {
    
    private static final ConcurrentMap<String, TranslationComponent> pendingTranslations = new ConcurrentHashMap<>();
    private static int translationCounter = 0;

    /**
     * Handles incoming chat messages for translation
     */
    @SubscribeEvent
    public static void onChatReceived(ClientChatReceivedEvent event) {
        // Only process if incoming translation is enabled
        if (!Config.incomingTranslationEnabled || Config.incomingTargetLanguage == null) {
            return;
        }

        Component originalMessage = event.getMessage();
        
        // Create translation component
        TranslationComponent translationComponent = new TranslationComponent(
            originalMessage, 
            event.getSender()
        );

        // Store for processing
        String translationId = "incoming_" + (++translationCounter);
        pendingTranslations.put(translationId, translationComponent);

        // Add translation status message
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            Component translatingMessage = ComponentCosmeticsHelper.createTranslatingComponent();
            mc.player.displayClientMessage(translatingMessage, false);
        }

        // TODO: Queue for actual translation processing
        // For now, simulate translation completion after a delay
        simulateTranslation(translationId, originalMessage.getString());
    }

    /**
     * Handles outgoing chat messages for translation
     */
    @SubscribeEvent
    public static void onChatSent(ClientChatEvent event) {
        // Only process if outgoing translation is enabled
        if (!Config.outgoingTranslationEnabled || Config.outgoingTargetLanguage == null) {
            return;
        }

        String originalMessage = event.getMessage();
        
        // Create translation component
        TranslationComponent translationComponent = new TranslationComponent(originalMessage);

        // Store for processing
        String translationId = "outgoing_" + (++translationCounter);
        pendingTranslations.put(translationId, translationComponent);

        // Cancel original message sending
        event.setCanceled(true);

        // Add translation status message
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            Component translatingMessage = ComponentCosmeticsHelper.createYellowTextComponent(
                "Translating your message..."
            );
            mc.player.displayClientMessage(translatingMessage, true); // actionbar
        }

        // TODO: Queue for actual translation processing
        // For now, simulate translation completion after a delay
        simulateTranslation(translationId, originalMessage);
    }

    /**
     * Simulates translation processing (placeholder for actual translation service)
     */
    private static void simulateTranslation(String translationId, String originalText) {
        // Simulate translation delay
        new Thread(() -> {
            try {
                Thread.sleep(1000); // 1 second delay
                
                // Simulate translation result
                String translatedText = "[Translated] " + originalText;
                
                // Complete translation on main thread
                Minecraft.getInstance().execute(() -> {
                    completeTranslation(translationId, translatedText);
                });
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                
                // Handle translation failure
                Minecraft.getInstance().execute(() -> {
                    failTranslation(translationId, TranslationComponent.TranslationFailedReason.NETWORK_ERROR);
                });
            }
        }).start();
    }

    /**
     * Completes a translation and displays the result
     */
    private static void completeTranslation(String translationId, String translatedText) {
        TranslationComponent translationComponent = pendingTranslations.remove(translationId);
        if (translationComponent == null) {
            return;
        }

        translationComponent.completeTranslation(translatedText);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            // Display original message
            Component originalComponent = ComponentCosmeticsHelper.withGrayStyle(
                translationComponent.getOriginalComponent()
            );
            mc.player.displayClientMessage(originalComponent, false);

            // Display translated message
            Component translatedComponent = translationComponent.getTranslatedComponent();
            if (translatedComponent != null) {
                mc.player.displayClientMessage(translatedComponent, false);
            }

            // If this was an outgoing message, actually send the translated version
            if (translationId.startsWith("outgoing_") && translationComponent.isOwnMessage()) {
                // Send the translated message to the server
                mc.player.connection.sendChat(translatedText);
            }
        }
    }

    /**
     * Handles translation failure
     */
    private static void failTranslation(String translationId, TranslationComponent.TranslationFailedReason reason) {
        TranslationComponent translationComponent = pendingTranslations.remove(translationId);
        if (translationComponent == null) {
            return;
        }

        translationComponent.setTranslationStateFailed(reason);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            // Display error message
            Component errorComponent = translationComponent.getTranslatedComponent();
            if (errorComponent != null) {
                mc.player.displayClientMessage(errorComponent, false);
            }

            // If this was an outgoing message, send the original message instead
            if (translationId.startsWith("outgoing_") && translationComponent.isOwnMessage()) {
                mc.player.connection.sendChat(translationComponent.getOriginalText());
            }
        }
    }

    /**
     * Gets the number of pending translations
     */
    public static int getPendingTranslationCount() {
        return pendingTranslations.size();
    }

    /**
     * Clears all pending translations (useful for cleanup)
     */
    public static void clearPendingTranslations() {
        pendingTranslations.clear();
    }
}