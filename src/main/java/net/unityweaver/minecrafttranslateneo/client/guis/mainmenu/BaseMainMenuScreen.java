package net.unityweaver.minecrafttranslateneo.client.guis.mainmenu;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.unityweaver.minecrafttranslateneo.MinecraftTranslateModNeo;

/**
 * Base class for all main menu settings screens.
 * Provides shared functionality with configurable panel dimensions.
 */
public abstract class BaseMainMenuScreen extends Screen {

    // Default dimensions (can be overridden by subclasses)
    public static final int DEFAULT_PANEL_WIDTH = 320;
    public static final int DEFAULT_PANEL_HEIGHT = 240;
    public static final int DEFAULT_BUTTON_WIDTH = 200;
    public static final int DEFAULT_BUTTON_HEIGHT = 20;
    public static final int DEFAULT_SPACING = 25;
    
    // Version display configuration
    private static final int VERSION_MARGIN = 8;

    // Panel configuration for this screen
    protected final int panelWidth;
    protected final int panelHeight;
    protected final int buttonWidth;
    protected final int buttonHeight;
    protected final int spacing;

    protected final Screen previousScreen;

    /**
     * Constructor with default dimensions
     */
    protected BaseMainMenuScreen(Component title, Screen previousScreen) {
        this(title, previousScreen, DEFAULT_PANEL_WIDTH, DEFAULT_PANEL_HEIGHT, 
             DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, DEFAULT_SPACING);
    }

    /**
     * Constructor with custom dimensions
     */
    protected BaseMainMenuScreen(Component title, Screen previousScreen, 
                                int panelWidth, int panelHeight, 
                                int buttonWidth, int buttonHeight, 
                                int spacing) {
        super(title);
        this.previousScreen = previousScreen;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        this.spacing = spacing;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render the standard Minecraft background
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Render title
        renderTitle(guiGraphics, centerX, centerY);
        
        // Render version info in bottom left corner
        renderVersionInfo(guiGraphics);

        // Allow subclasses to render additional content
        renderContent(guiGraphics, centerX, centerY, mouseX, mouseY, partialTick);
    }

    /**
     * Renders the screen title. Can be overridden for custom title styling.
     */
    protected void renderTitle(GuiGraphics guiGraphics, int centerX, int centerY) {
        Component title = this.getTitle().copy().withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD);
        renderCenteredText(guiGraphics, title, centerX, 40, 0xFFFFFF);
    }

    /**
     * Override this method to render additional content specific to each screen
     */
    protected void renderContent(GuiGraphics guiGraphics, int centerX, int centerY, 
                                int mouseX, int mouseY, float partialTick) {
        // Default implementation does nothing - subclasses can override
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Use the standard Minecraft background
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false; // Don't pause the game
    }

    /**
     * Renders a centered panel with background and border like InGameSettingsScreen
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
    }
    
    /**
     * Renders centered text with specified color
     */
    protected void renderCenteredText(GuiGraphics guiGraphics, Component text, int centerX, int y, int color) {
        int textWidth = this.font.width(text);
        guiGraphics.drawString(this.font, text, centerX - textWidth / 2, y, color);
    }
    
    /**
     * Renders mod version information in the bottom left corner of the screen
     */
    protected void renderVersionInfo(GuiGraphics guiGraphics) {
        Component versionText = Component.literal(MinecraftTranslateModNeo.MOD_NAME + " v" + MinecraftTranslateModNeo.MOD_VERSION)
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
        
        int x = VERSION_MARGIN;
        int y = this.height - this.font.lineHeight - VERSION_MARGIN;
        
        guiGraphics.drawString(this.font, versionText, x, y, 0x888888);
    }

    /**
     * Utility method to get the panel's top-left position
     */
    protected int getPanelX() {
        return (this.width - panelWidth) / 2;
    }

    /**
     * Utility method to get the panel's top-left position
     */
    protected int getPanelY() {
        return (this.height - panelHeight) / 2;
    }

    /**
     * Utility method to get the start Y position for content (commonly used)
     */
    protected int getContentStartY() {
        return getPanelY() + 60;
    }

    /**
     * Utility method to get centered X position for buttons
     */
    protected int getCenteredButtonX() {
        return (this.width - buttonWidth) / 2;
    }

    // Getters for dimensions (useful for subclasses)
    public int getPanelWidth() { return panelWidth; }
    public int getPanelHeight() { return panelHeight; }
    public int getButtonWidth() { return buttonWidth; }
    public int getButtonHeight() { return buttonHeight; }
    public int getSpacing() { return spacing; }
    public Screen getPreviousScreen() { return previousScreen; }
}