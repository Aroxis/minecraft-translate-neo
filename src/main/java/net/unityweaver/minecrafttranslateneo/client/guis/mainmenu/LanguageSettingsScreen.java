package net.unityweaver.minecrafttranslateneo.client.guis.mainmenu;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.unityweaver.minecrafttranslateneo.Config;
import net.unityweaver.minecrafttranslateneo.client.guis.mainmenu.components.MainMenuLanguageList;
import net.unityweaver.minecrafttranslateneo.enums.Languages;

/**
 * Language settings screen for the main menu - uses BaseMainMenuScreen with larger dimensions
 */
public class LanguageSettingsScreen extends BaseMainMenuScreen {

    public enum Page {
        MAIN,
        INPUT_LANGUAGE,
        OUTPUT_LANGUAGE
    }

    // Custom dimensions for this screen (larger than default)
    private static final int LARGE_PANEL_WIDTH = 400;
    private static final int LARGE_PANEL_HEIGHT = 320;

    // Current state
    private Page currentPage = Page.MAIN;
    private Languages inputLanguage;
    private Languages outputLanguage;
    private boolean incomingEnabled;
    private boolean outgoingEnabled;

    // UI Components
    private Button backButton;
    private Button saveButton;
    private Button cancelButton;
    private MainMenuLanguageList languageList;
    private EditBox searchBox;

    // Main page components
    private Button inputLanguageButton;
    private Button outputLanguageButton;
    private Button incomingToggleButton;
    private Button outgoingToggleButton;

    public LanguageSettingsScreen(Screen previousScreen) {
        // Use custom larger dimensions for this screen
        super(Component.literal("Language Settings"), previousScreen, 
              LARGE_PANEL_WIDTH, LARGE_PANEL_HEIGHT, 
              DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT, DEFAULT_SPACING);

        // Load current settings
        this.inputLanguage = Config.incomingTargetLanguage != null ? Config.incomingTargetLanguage : Languages.English;
        this.outputLanguage = Config.outgoingTargetLanguage != null ? Config.outgoingTargetLanguage : Languages.English;
        this.incomingEnabled = Config.incomingTranslationEnabled;
        this.outgoingEnabled = Config.outgoingTranslationEnabled;
    }

    @Override
    protected void init() {
        super.init();

        this.clearWidgets();

        switch (currentPage) {
            case MAIN -> initMainPage();
            case INPUT_LANGUAGE -> initLanguagePage("Input Language");
            case OUTPUT_LANGUAGE -> initLanguagePage("Output Language");
        }
    }

    @Override
    protected void renderTitle(GuiGraphics guiGraphics, int centerX, int centerY) {
        // Custom title rendering based on current page
        String titleText = switch (currentPage) {
            case MAIN -> "Language Settings";
            case INPUT_LANGUAGE -> "Select Input Language";
            case OUTPUT_LANGUAGE -> "Select Output Language";
        };

        Component title = Component.literal(titleText).withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD);
        renderCenteredText(guiGraphics, title, centerX, centerY - getPanelHeight() / 2 + 15, 0xFFFFFF);
    }

    @Override
    protected void renderContent(GuiGraphics guiGraphics, int centerX, int centerY, int mouseX, int mouseY, float partialTick) {
        // Render page-specific content
        if (currentPage == Page.MAIN) {
            renderMainPageHeaders(guiGraphics, centerX, centerY);
        }
    }

    private void initMainPage() {
        int startY = getContentStartY();

        // Incoming translation toggle
        this.incomingToggleButton = Button.builder(
            Component.literal("Incoming Translation: " + (incomingEnabled ? "§aON" : "§cOFF")),
            (button) -> {
                this.incomingEnabled = !this.incomingEnabled;
                updateMainPageButtons();
            }
        ).bounds(getCenteredButtonX(), startY, getButtonWidth(), getButtonHeight()).build();
        this.addRenderableWidget(this.incomingToggleButton);

        // Input language selection
        this.inputLanguageButton = Button.builder(
            Component.literal("Input Language: " + inputLanguage.getTranslatedName()),
            (button) -> {
                this.currentPage = Page.INPUT_LANGUAGE;
                this.init();
            }
        ).bounds(getCenteredButtonX(), startY + getSpacing(), getButtonWidth(), getButtonHeight()).build();
        this.addRenderableWidget(this.inputLanguageButton);

        // Outgoing translation toggle - moved lower
        this.outgoingToggleButton = Button.builder(
            Component.literal("Outgoing Translation: " + (outgoingEnabled ? "§aON" : "§cOFF")),
            (button) -> {
                this.outgoingEnabled = !this.outgoingEnabled;
                updateMainPageButtons();
            }
        ).bounds(getCenteredButtonX(), startY + getSpacing() * 2 + 20, getButtonWidth(), getButtonHeight()).build();
        this.addRenderableWidget(this.outgoingToggleButton);

        // Output language selection - moved lower
        this.outputLanguageButton = Button.builder(
            Component.literal("Output Language: " + outputLanguage.getTranslatedName()),
            (button) -> {
                this.currentPage = Page.OUTPUT_LANGUAGE;
                this.init();
            }
        ).bounds(getCenteredButtonX(), startY + getSpacing() * 3 + 20, getButtonWidth(), getButtonHeight()).build();
        this.addRenderableWidget(this.outputLanguageButton);

        // Action buttons - moved lower to maintain margin
        int centerX = this.width / 2;
        this.saveButton = Button.builder(Component.literal("Save"), (button) -> {
            saveSettings();
            this.minecraft.setScreen(getPreviousScreen());
        }).bounds(centerX - 105, startY + getSpacing() * 4 + 30, 100, getButtonHeight()).build();
        this.addRenderableWidget(this.saveButton);

        this.cancelButton = Button.builder(Component.literal("Cancel"), (button) -> {
            this.minecraft.setScreen(getPreviousScreen());
        }).bounds(centerX + 5, startY + getSpacing() * 4 + 30, 100, getButtonHeight()).build();
        this.addRenderableWidget(this.cancelButton);

        updateMainPageButtons();
    }

    private void initLanguagePage(String title) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Search box
        this.searchBox = new EditBox(this.font, centerX - 110, centerY - 100, 220, 20,
                                    Component.literal("Search languages..."));
        this.searchBox.setHint(Component.literal("Type to filter..."));
        this.searchBox.setResponder(this::onSearchTextChanged);
        this.addRenderableWidget(this.searchBox);

        // Language list (larger size to match the larger panel)
        this.languageList = new MainMenuLanguageList(this, this.minecraft, 260, 140,
                                           centerY - 75, centerY + 65, 18);
        this.languageList.setX(centerX - 130);
        this.addWidget(this.languageList);
        this.addRenderableWidget(this.languageList);

        // Back button - centered since there's no Select button
        this.backButton = Button.builder(Component.literal("← Back"), (button) -> {
            this.currentPage = Page.MAIN;
            this.init();
        }).bounds(centerX - 50, centerY + 80, 100, getButtonHeight()).build();
        this.addRenderableWidget(this.backButton);
    }

    private void updateMainPageButtons() {
        if (this.incomingToggleButton != null) {
            this.incomingToggleButton.setMessage(
                Component.literal("Incoming Translation: " + (incomingEnabled ? "§aON" : "§cOFF"))
            );
        }
        if (this.outgoingToggleButton != null) {
            this.outgoingToggleButton.setMessage(
                Component.literal("Outgoing Translation: " + (outgoingEnabled ? "§aON" : "§cOFF"))
            );
        }
        if (this.inputLanguageButton != null) {
            this.inputLanguageButton.setMessage(
                Component.literal("Input Language: " + inputLanguage.getTranslatedName())
            );
            this.inputLanguageButton.active = this.incomingEnabled;
        }
        if (this.outputLanguageButton != null) {
            this.outputLanguageButton.setMessage(
                Component.literal("Output Language: " + outputLanguage.getTranslatedName())
            );
            this.outputLanguageButton.active = this.outgoingEnabled;
        }
    }

    private void onSearchTextChanged(String text) {
        if (this.languageList != null) {
            this.languageList.setFilter(text);
        }
    }

    private void saveSettings() {
        // Save to config values
        Config.INCOMING_TRANSLATION_ENABLED.set(this.incomingEnabled);
        Config.OUTGOING_TRANSLATION_ENABLED.set(this.outgoingEnabled);
        Config.INCOMING_TARGET_LANGUAGE.set(this.inputLanguage);
        Config.OUTGOING_TARGET_LANGUAGE.set(this.outputLanguage);

        // Update static fields immediately
        Config.incomingTranslationEnabled = this.incomingEnabled;
        Config.outgoingTranslationEnabled = this.outgoingEnabled;
        Config.incomingTargetLanguage = this.inputLanguage;
        Config.outgoingTargetLanguage = this.outputLanguage;
    }

    private void renderMainPageHeaders(GuiGraphics guiGraphics, int centerX, int centerY) {
        int startY = getContentStartY();

        Component incomingHeader = Component.literal("Incoming Messages").withStyle(ChatFormatting.YELLOW);
        guiGraphics.drawString(this.font, incomingHeader, getCenteredButtonX(), startY - 15, 0xFFFF55);

        Component outgoingHeader = Component.literal("Outgoing Messages").withStyle(ChatFormatting.GREEN);
        guiGraphics.drawString(this.font, outgoingHeader, getCenteredButtonX(), startY + getSpacing() * 2 + 5, 0x55FF55);
    }

    // Getter methods for MainMenuLanguageList
    public Page getCurrentPage() {
        return this.currentPage;
    }

    public Languages getInputLanguage() {
        return this.inputLanguage;
    }

    public Languages getOutputLanguage() {
        return this.outputLanguage;
    }

    public void setCurrentPageLanguage(Languages language) {
        if (language == null) return;
        
        switch (currentPage) {
            case INPUT_LANGUAGE -> {
                this.inputLanguage = language;
                updateMainPageButtons();
                // Navigate back to main page
                this.currentPage = Page.MAIN;
                this.init();
            }
            case OUTPUT_LANGUAGE -> {
                this.outputLanguage = language;
                updateMainPageButtons();
                // Navigate back to main page
                this.currentPage = Page.MAIN;
                this.init();
            }
            default -> {
                // If not on a language page, default to input language
                this.inputLanguage = language;
                updateMainPageButtons();
            }
        }
    }
}