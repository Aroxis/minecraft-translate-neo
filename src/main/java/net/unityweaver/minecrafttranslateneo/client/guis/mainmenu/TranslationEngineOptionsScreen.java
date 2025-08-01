package net.unityweaver.minecrafttranslateneo.client.guis.mainmenu;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Translation engine options screen for the main menu.
 * Uses BaseMainMenuScreen with smaller dimensions for simple content.
 */
public class TranslationEngineOptionsScreen extends BaseMainMenuScreen {

    // Custom smaller dimensions for this simple screen
    private static final int SMALL_PANEL_WIDTH = 280;
    private static final int SMALL_PANEL_HEIGHT = 180;

    private Button backButton;

    public TranslationEngineOptionsScreen(Screen previousScreen) {
        // Use custom smaller dimensions for this simple screen
        super(Component.literal("Translation Engine Options"), previousScreen, 
              SMALL_PANEL_WIDTH, SMALL_PANEL_HEIGHT, 
              DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, DEFAULT_SPACING);
    }

    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Back button
        this.backButton = Button.builder(
            Component.literal("← Back"),
            (button) -> {
                this.minecraft.setScreen(getPreviousScreen());
            }
        )
        .bounds(centerX - 50, centerY + 40, 100, getButtonHeight())
        .build();
        this.addRenderableWidget(this.backButton);
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int centerX, int centerY, int mouseX, int mouseY, float partialTick) {
        // Render placeholder text
        Component placeholder = Component.literal("Coming Soon...").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
        renderCenteredText(guiGraphics, placeholder, centerX, centerY - 10, 0x888888);
    }
}