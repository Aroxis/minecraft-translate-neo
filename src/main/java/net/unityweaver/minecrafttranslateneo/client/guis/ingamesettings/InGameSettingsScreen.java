package net.unityweaver.minecrafttranslateneo.client.guis.ingamesettings;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.unityweaver.minecrafttranslateneo.Config;
import net.unityweaver.minecrafttranslateneo.client.guis.BaseTranslationScreen;
import net.unityweaver.minecrafttranslateneo.client.guis.ingamesettings.components.LanguageList;
import net.unityweaver.minecrafttranslateneo.enums.Languages;

/**
 * Enhanced in-game settings screen with language selection functionality
 */
public class InGameSettingsScreen extends BaseTranslationScreen {

    public enum Page {
        MAIN,
        INPUT_LANGUAGE,
        OUTPUT_LANGUAGE
    }

    private static final int PANEL_WIDTH = 320;
    private static final int PANEL_HEIGHT = 240;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 20;
    private static final int SPACING = 25;

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
    private LanguageList languageList;
    private EditBox searchBox;

    // Main page components
    private Button inputLanguageButton;
    private Button outputLanguageButton;
    private Button incomingToggleButton;
    private Button outgoingToggleButton;

    public InGameSettingsScreen() {
        super(Component.literal("Translation Settings"), BackgroundType.NONE);

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

    private void initMainPage() {
        int centerX = this.width / 2;
        int startY = (this.height - PANEL_HEIGHT) / 2 + 60;

        // Incoming translation toggle
        this.incomingToggleButton = Button.builder(
            Component.literal("Incoming Translation: " + (incomingEnabled ? "§aON" : "§cOFF")),
            (button) -> {
                this.incomingEnabled = !this.incomingEnabled;
                updateMainPageButtons();
            }
        ).bounds(centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.addRenderableWidget(this.incomingToggleButton);

        // Input language selection
        this.inputLanguageButton = Button.builder(
            Component.literal("Input Language: " + inputLanguage.getTranslatedName()),
            (button) -> {
                this.currentPage = Page.INPUT_LANGUAGE;
                this.init();
            }
        ).bounds(centerX - BUTTON_WIDTH / 2, startY + SPACING, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.addRenderableWidget(this.inputLanguageButton);

        // Outgoing translation toggle - moved lower
        this.outgoingToggleButton = Button.builder(
            Component.literal("Outgoing Translation: " + (outgoingEnabled ? "§aON" : "§cOFF")),
            (button) -> {
                this.outgoingEnabled = !this.outgoingEnabled;
                updateMainPageButtons();
            }
        ).bounds(centerX - BUTTON_WIDTH / 2, startY + SPACING * 2 + 20, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.addRenderableWidget(this.outgoingToggleButton);

        // Output language selection - moved lower
        this.outputLanguageButton = Button.builder(
            Component.literal("Output Language: " + outputLanguage.getTranslatedName()),
            (button) -> {
                this.currentPage = Page.OUTPUT_LANGUAGE;
                this.init();
            }
        ).bounds(centerX - BUTTON_WIDTH / 2, startY + SPACING * 3 + 20, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.addRenderableWidget(this.outputLanguageButton);

        // Action buttons - moved lower to maintain margin
        this.saveButton = Button.builder(Component.literal("Save"), (button) -> {
            saveSettings();
            this.onClose();
        }).bounds(centerX - 105, startY + SPACING * 4 + 30, 100, BUTTON_HEIGHT).build();
        this.addRenderableWidget(this.saveButton);

        this.cancelButton = Button.builder(Component.literal("Cancel"), (button) -> {
            this.onClose();
        }).bounds(centerX + 5, startY + SPACING * 4 + 30, 100, BUTTON_HEIGHT).build();
        this.addRenderableWidget(this.cancelButton);

        updateMainPageButtons();
    }

    private void initLanguagePage(String title) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Search box
        this.searchBox = new EditBox(this.font, centerX - 100, centerY - 80, 200, 20,
                                    Component.literal("Search languages..."));
        this.searchBox.setHint(Component.literal("Type to filter..."));
        this.searchBox.setResponder(this::onSearchTextChanged);
        this.addRenderableWidget(this.searchBox);

        // Language list
        this.languageList = new LanguageList(this, this.minecraft, 220, 120,
                                           centerY - 55, centerY + 65, 18);
        this.languageList.setX(centerX - 110);
        this.addWidget(this.languageList);
        this.addRenderableWidget(this.languageList);

        // Back button - centered since there's no Select button
        this.backButton = Button.builder(Component.literal("← Back"), (button) -> {
            this.currentPage = Page.MAIN;
            this.init();
        }).bounds(centerX - 50, centerY + 75, 100, BUTTON_HEIGHT).build();
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

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Render panel background
        renderPanel(guiGraphics, centerX, centerY, PANEL_WIDTH, PANEL_HEIGHT);

        // Render title
        String titleText = switch (currentPage) {
            case MAIN -> "Translation Settings";
            case INPUT_LANGUAGE -> "Select Input Language";
            case OUTPUT_LANGUAGE -> "Select Output Language";
        };

        Component title = Component.literal(titleText).withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD);
        renderCenteredText(guiGraphics, title, centerX, centerY - PANEL_HEIGHT / 2 + 15, 0xFFFFFF);

        // Render page-specific content
        if (currentPage == Page.MAIN) {
            renderMainPageHeaders(guiGraphics, centerX, centerY);
        }

        // Render widgets
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderMainPageHeaders(GuiGraphics guiGraphics, int centerX, int centerY) {
        int startY = (this.height - PANEL_HEIGHT) / 2 + 60;

        Component incomingHeader = Component.literal("Incoming Messages").withStyle(ChatFormatting.YELLOW);
        guiGraphics.drawString(this.font, incomingHeader, centerX - BUTTON_WIDTH / 2, startY - 15, 0xFFFF55);

        Component outgoingHeader = Component.literal("Outgoing Messages").withStyle(ChatFormatting.GREEN);
        guiGraphics.drawString(this.font, outgoingHeader, centerX - BUTTON_WIDTH / 2, startY + SPACING * 2 + 5, 0x55FF55);
    }

    // Getter methods for LanguageList
    public Page getCurrentPage() {
        return this.currentPage;
    }

    public Languages getInputLanguage() {
        return this.inputLanguage;
    }

    public Languages getOutputLanguage() {
        return this.outputLanguage;
    }

    public void setInputLanguage(Languages language) {
        if (language != null) {
            this.inputLanguage = language;
            updateMainPageButtons();
            // Re-initialize if on main page to refresh UI
            if (currentPage == Page.MAIN) {
                this.init();
            }
        }
    }

    public void setOutputLanguage(Languages language) {
        if (language != null) {
            this.outputLanguage = language;
            updateMainPageButtons();
            // Re-initialize if on main page to refresh UI
            if (currentPage == Page.MAIN) {
                this.init();
            }
        }
    }
    
    /**
     * Set both input and output languages at once
     * @param inputLanguage The language for incoming translations
     * @param outputLanguage The language for outgoing translations
     */
    public void setLanguages(Languages inputLanguage, Languages outputLanguage) {
        boolean changed = false;
        if (inputLanguage != null && !inputLanguage.equals(this.inputLanguage)) {
            this.inputLanguage = inputLanguage;
            changed = true;
        }
        if (outputLanguage != null && !outputLanguage.equals(this.outputLanguage)) {
            this.outputLanguage = outputLanguage;
            changed = true;
        }
        
        if (changed) {
            updateMainPageButtons();
            // Re-initialize if on main page to refresh UI
            if (currentPage == Page.MAIN) {
                this.init();
            }
        }
    }
    
    /**
     * Set language for a specific type (incoming/outgoing)
     * @param language The language to set
     * @param isInput true for input language, false for output language
     */
    public void setLanguage(Languages language, boolean isInput) {
        if (isInput) {
            setInputLanguage(language);
        } else {
            setOutputLanguage(language);
        }
    }
    
    /**
     * Set language based on the current page context and navigate back to main page
     * @param language The language to set for the current context
     */
    public void setCurrentPageLanguage(Languages language) {
        if (language == null) return;
        
        switch (currentPage) {
            case INPUT_LANGUAGE -> {
                setInputLanguage(language);
                // Navigate back to main page
                this.currentPage = Page.MAIN;
                this.init();
            }
            case OUTPUT_LANGUAGE -> {
                setOutputLanguage(language);
                // Navigate back to main page
                this.currentPage = Page.MAIN;
                this.init();
            }
            default -> {
                // If not on a language page, default to input language
                setInputLanguage(language);
            }
        }
    }
    
    /**
     * Enable/disable translation settings
     * @param incomingEnabled Whether incoming translation is enabled
     * @param outgoingEnabled Whether outgoing translation is enabled
     */
    public void setTranslationEnabled(boolean incomingEnabled, boolean outgoingEnabled) {
        boolean changed = false;
        if (this.incomingEnabled != incomingEnabled) {
            this.incomingEnabled = incomingEnabled;
            changed = true;
        }
        if (this.outgoingEnabled != outgoingEnabled) {
            this.outgoingEnabled = outgoingEnabled;
            changed = true;
        }
        
        if (changed) {
            updateMainPageButtons();
            // Re-initialize if on main page to refresh UI
            if (currentPage == Page.MAIN) {
                this.init();
            }
        }
    }
    
    /**
     * Apply all settings at once (languages and enabled states)
     * @param inputLanguage Input language
     * @param outputLanguage Output language
     * @param incomingEnabled Whether incoming translation is enabled
     * @param outgoingEnabled Whether outgoing translation is enabled
     */
    public void applyAllSettings(Languages inputLanguage, Languages outputLanguage, 
                                boolean incomingEnabled, boolean outgoingEnabled) {
        boolean changed = false;
        
        if (inputLanguage != null && !inputLanguage.equals(this.inputLanguage)) {
            this.inputLanguage = inputLanguage;
            changed = true;
        }
        if (outputLanguage != null && !outputLanguage.equals(this.outputLanguage)) {
            this.outputLanguage = outputLanguage;
            changed = true;
        }
        if (this.incomingEnabled != incomingEnabled) {
            this.incomingEnabled = incomingEnabled;
            changed = true;
        }
        if (this.outgoingEnabled != outgoingEnabled) {
            this.outgoingEnabled = outgoingEnabled;
            changed = true;
        }
        
        if (changed) {
            updateMainPageButtons();
            // Re-initialize if on main page to refresh UI
            if (currentPage == Page.MAIN) {
                this.init();
            }
        }
    }
}