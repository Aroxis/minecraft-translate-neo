package net.unityweaver.minecrafttranslateneo.client.guis.mainmenu;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Main settings screen that appears when clicking "Translation Settings" on the main menu.
 * Uses BaseMainMenuScreen for shared functionality and proper background handling.
 */
public class MainSettingsScreen extends BaseMainMenuScreen {

    private Button languageSettingsButton;
    private Button translationEngineButton;
    private Button backButton;

    public MainSettingsScreen(Screen previousScreen) {
        super(Component.literal("Translation Settings"), previousScreen);
    }

    @Override
    protected void init() {
        super.init();
        
        int centerX = this.width / 2;
        int startY = getContentStartY();

        // Language Settings button
        this.languageSettingsButton = Button.builder(
            Component.literal("Language Settings"),
            (button) -> {
                this.minecraft.setScreen(new LanguageSettingsScreen(this));
            }
        )
        .bounds(getCenteredButtonX(), startY, getButtonWidth(), getButtonHeight())
        .build();
        this.addRenderableWidget(this.languageSettingsButton);

        // Translation Engine Options button
        this.translationEngineButton = Button.builder(
            Component.literal("Translation Engine Options"),
            (button) -> {
                this.minecraft.setScreen(new TranslationEngineOptionsScreen(this));
            }
        )
        .bounds(getCenteredButtonX(), startY + getSpacing(), getButtonWidth(), getButtonHeight())
        .build();
        this.addRenderableWidget(this.translationEngineButton);

        // Back button
        this.backButton = Button.builder(
            Component.literal("Back"),
            (button) -> {
                this.minecraft.setScreen(getPreviousScreen());
            }
        )
        .bounds(centerX - 50, startY + getSpacing() * 2 + 20, 100, getButtonHeight())
        .build();
        this.addRenderableWidget(this.backButton);
    }
}