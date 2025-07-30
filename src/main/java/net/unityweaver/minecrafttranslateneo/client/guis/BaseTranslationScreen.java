package net.unityweaver.minecrafttranslateneo.client.guis;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.unityweaver.minecrafttranslateneo.MinecraftTranslateModNeo;

/**
 * Base class for translation-related screens with customizable background behavior
 */
public abstract class BaseTranslationScreen extends Screen {
    
    // Version display configuration
    private static final int VERSION_MARGIN = 8;
    
    public enum BackgroundType {
        NONE,           // No background at all (transparent)
        SEMI_TRANSPARENT, // Dark semi-transparent overlay
        BLURRED,        // Standard Minecraft blur effect
        SOLID           // Solid dark background
    }
    
    private final BackgroundType backgroundType;
    
    protected BaseTranslationScreen(Component title, BackgroundType backgroundType) {
        super(title);
        this.backgroundType = backgroundType;
    }
    
    protected BaseTranslationScreen(Component title) {
        this(title, BackgroundType.NONE); // Default to no background
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render background based on type
        renderCustomBackground(guiGraphics, mouseX, mouseY, partialTick);
        
        // Render the screen content
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }
    
    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Only render background if not NONE type
        if (backgroundType != BackgroundType.NONE) {
            super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        }
        // Otherwise, do nothing (transparent background)
    }

    // TODO: check the actual mechanisms
    protected void renderCustomBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        switch (backgroundType) {
            case NONE:
                // No background rendering at all
                break;
                
            case SEMI_TRANSPARENT:
                // Semi-transparent dark overlay
                guiGraphics.fill(0, 0, this.width, this.height, 0x66000000);
                break;
                
            case BLURRED:
                // Standard Minecraft blur effect
                this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
                break;
                
            case SOLID:
                // Solid dark background
                guiGraphics.fill(0, 0, this.width, this.height, 0xFF000000);
                break;
        }
    }
    
    /**
     * Renders a centered panel with background and border
     */
    protected void renderPanel(GuiGraphics guiGraphics, int centerX, int centerY, int panelWidth, int panelHeight) {
        renderPanel(guiGraphics, centerX, centerY, panelWidth, panelHeight, 0xCC000000, 0xFF555555);
    }
    
    /**
     * Renders a centered panel with custom colors
     */
    protected void renderPanel(GuiGraphics guiGraphics, int centerX, int centerY, int panelWidth, int panelHeight, 
                              int backgroundColor, int borderColor) {
        int panelX = centerX - panelWidth / 2;
        int panelY = centerY - panelHeight / 2;
        
        // Panel background
        guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, backgroundColor);
        
        // Panel border
        guiGraphics.fill(panelX - 1, panelY - 1, panelX + panelWidth + 1, panelY, borderColor); // Top
        guiGraphics.fill(panelX - 1, panelY + panelHeight, panelX + panelWidth + 1, panelY + panelHeight + 1, borderColor); // Bottom
        guiGraphics.fill(panelX - 1, panelY, panelX, panelY + panelHeight, borderColor); // Left
        guiGraphics.fill(panelX + panelWidth, panelY, panelX + panelWidth + 1, panelY + panelHeight, borderColor); // Right
        
        // Render version info in bottom left of panel
        renderVersionInfoInPanel(guiGraphics, panelX, panelY, panelHeight);
    }
    
    /**
     * Renders centered text with specified color
     */
    protected void renderCenteredText(GuiGraphics guiGraphics, Component text, int centerX, int y, int color) {
        int textWidth = this.font.width(text);
        guiGraphics.drawString(this.font, text, centerX - textWidth / 2, y, color);
    }
    
    /**
     * Renders mod version information in the bottom left corner of the panel
     */
    private void renderVersionInfoInPanel(GuiGraphics guiGraphics, int panelX, int panelY, int panelHeight) {
        Component versionText = Component.literal(MinecraftTranslateModNeo.MOD_NAME + " v" + MinecraftTranslateModNeo.MOD_VERSION)
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
        
        int x = panelX + VERSION_MARGIN;
        int y = panelY + panelHeight - this.font.lineHeight - VERSION_MARGIN;
        
        guiGraphics.drawString(this.font, versionText, x, y, 0x888888);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false; // Default: don't pause the game
    }
}