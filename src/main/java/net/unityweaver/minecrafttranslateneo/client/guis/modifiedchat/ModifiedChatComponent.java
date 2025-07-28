package net.unityweaver.minecrafttranslateneo.client.guis.modifiedchat;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.unityweaver.minecrafttranslateneo.models.TranslationComponent;
import net.unityweaver.minecrafttranslateneo.helpers.ComponentCosmeticsHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Modified ChatComponent that provides translation functionality for Minecraft 1.21.
 * This is a simplified version that works with the current Minecraft API limitations.
 */
public class ModifiedChatComponent extends ChatComponent {
    
    private final Minecraft minecraft;
    private final List<TranslationComponent> translationComponents;
    private static final int MAX_TRANSLATION_HISTORY = 100;

    public ModifiedChatComponent(Minecraft minecraft) {
        super(minecraft);
        this.minecraft = minecraft;
        this.translationComponents = new CopyOnWriteArrayList<>();
    }

    /**
     * Adds a message that may need translation
     */
    public void addTranslatableMessage(Component component) {
        // Check if translation is enabled and needed
        if (shouldTranslateMessage(component)) {
            TranslationComponent translationComponent = new TranslationComponent(component);
            addTranslationComponent(translationComponent);
            
            // Add original message to chat
            super.addMessage(component);
            
            // TODO: Queue for translation processing
            // This would integrate with a translation service
            
        } else {
            // Add message normally without translation
            super.addMessage(component);
        }
    }

    /**
     * Adds a player's outgoing message for translation
     */
    public void addOutgoingMessage(String message) {
        if (shouldTranslateOutgoingMessage()) {
            TranslationComponent translationComponent = new TranslationComponent(message);
            addTranslationComponent(translationComponent);
            
            // Add the original message to chat with gray styling
            Component grayedMessage = ComponentCosmeticsHelper.createGrayTextComponent(message);
            super.addMessage(grayedMessage);
            
            // TODO: Process translation and add translated version
            
        } else {
            // Add message normally
            super.addMessage(Component.literal(message));
        }
    }

    /**
     * Handles completion of a translation
     */
    public void completeTranslation(TranslationComponent translationComponent, String translatedText) {
        translationComponent.completeTranslation(translatedText);
        
        // Add the translated message to chat
        Component translatedComponent = translationComponent.getTranslatedComponent();
        if (translatedComponent != null) {
            super.addMessage(translatedComponent);
        }
    }

    /**
     * Handles translation failure
     */
    public void failTranslation(TranslationComponent translationComponent, 
                               TranslationComponent.TranslationFailedReason reason) {
        translationComponent.setTranslationStateFailed(reason);
        
        // Add error message to chat
        Component errorComponent = translationComponent.getTranslatedComponent();
        if (errorComponent != null) {
            super.addMessage(errorComponent);
        }
    }

    /**
     * Renders translation status overlay when chat is focused
     */
    public void renderTranslationOverlay(GuiGraphics guiGraphics, int tickCount) {
        if (!isChatFocused()) {
            return;
        }

        // Count pending translations
        long pendingCount = translationComponents.stream()
            .filter(tc -> tc.getState() == TranslationComponent.TranslationState.TRANSLATING)
            .count();

        if (pendingCount > 0) {
            // Render translation status
            Component statusComponent = ComponentCosmeticsHelper.createYellowTextComponent(
                "Translating " + pendingCount + " message" + (pendingCount != 1 ? "s" : "") + "..."
            );
            
            // Position at the top of the chat area
            int x = 2;
            int y = minecraft.getWindow().getGuiScaledHeight() - 40 - (getLinesPerPage() * 9) - 10;
            
            guiGraphics.drawString(minecraft.font, statusComponent, x, y, 0xFFFFFF);
        }
    }

    /**
     * Gets pending translations for display
     */
    public List<TranslationComponent> getPendingTranslations() {
        return translationComponents.stream()
            .filter(tc -> tc.getState() == TranslationComponent.TranslationState.TRANSLATING)
            .toList();
    }

    /**
     * Gets completed translations
     */
    public List<TranslationComponent> getCompletedTranslations() {
        return translationComponents.stream()
            .filter(TranslationComponent::isTranslated)
            .toList();
    }

    /**
     * Cleans up old translation components
     */
    public void cleanupOldTranslations() {
        if (translationComponents.size() > MAX_TRANSLATION_HISTORY) {
            // Remove oldest translations
            int toRemove = translationComponents.size() - MAX_TRANSLATION_HISTORY;
            for (int i = 0; i < toRemove; i++) {
                translationComponents.remove(0);
            }
        }
    }

    // Private helper methods

    private void addTranslationComponent(TranslationComponent component) {
        translationComponents.add(component);
        cleanupOldTranslations();
    }

    private boolean shouldTranslateMessage(Component component) {
        // Check if incoming translation is enabled and configured
        return net.unityweaver.minecrafttranslateneo.Config.incomingTranslationEnabled &&
               net.unityweaver.minecrafttranslateneo.Config.incomingTargetLanguage != null;
    }

    private boolean shouldTranslateOutgoingMessage() {
        // Check if outgoing translation is enabled and configured
        return net.unityweaver.minecrafttranslateneo.Config.outgoingTranslationEnabled &&
               net.unityweaver.minecrafttranslateneo.Config.outgoingTargetLanguage != null;
    }

    public boolean isChatFocused() {
        return minecraft.screen instanceof net.minecraft.client.gui.screens.ChatScreen;
    }

    // Override addMessage to intercept all chat messages
    @Override
    public void addMessage(Component component) {
        addTranslatableMessage(component);
    }

    // Static method to replace the default chat component
    public static void replaceChatComponent() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui != null) {
            // Note: In Minecraft 1.21, replacing the ChatComponent requires more complex reflection
            // or mixin approaches due to access restrictions. This is a placeholder for the concept.
            
            // For now, we'll document this as needing a Mixin or AccessTransformer approach
            // to properly replace the chat component in the GUI.
            
            // TODO: Implement proper chat component replacement using:
            // 1. Mixin to intercept chat rendering
            // 2. AccessTransformer to access private fields
            // 3. Reflection as a fallback (less reliable)
        }
    }
}