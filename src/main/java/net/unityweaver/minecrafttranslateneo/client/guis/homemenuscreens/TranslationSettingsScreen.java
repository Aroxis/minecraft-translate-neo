package net.unityweaver.minecrafttranslateneo.client.guis.homemenuscreens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;
import net.unityweaver.minecrafttranslateneo.Config;
import net.unityweaver.minecrafttranslateneo.client.guis.ingamesettings.LanguagePickerScreen;
import net.unityweaver.minecrafttranslateneo.enums.Languages;
import net.unityweaver.minecrafttranslateneo.enums.TranslationEngine;

/**
 * Main menu style translation settings screen similar to Minecraft's options screens
 */
public class TranslationSettingsScreen extends OptionsSubScreen {
    
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int SPACING = 25;
    
    // UI Components
    private CycleButton<TranslationEngine> engineSelector;
    private EditBox apiKeyField;
    private CycleButton<Boolean> incomingEnabledButton;
    private CycleButton<Boolean> outgoingEnabledButton;
    private Button incomingLanguageButton;
    private Button outgoingLanguageButton;
    
    // Settings state
    private TranslationEngine selectedEngine;
    private String apiKey;
    private boolean incomingEnabled;
    private boolean outgoingEnabled;
    private Languages incomingLanguage;
    private Languages outgoingLanguage;

    public TranslationSettingsScreen(Screen parentScreen) {
        super(parentScreen, null, Component.literal("Translation Settings"));
        
        // Load current settings
        this.selectedEngine = Config.translationEngine != null ? Config.translationEngine : TranslationEngine.DEEPL;
        this.apiKey = Config.deepLApiKey != null ? Config.deepLApiKey : "";
        this.incomingEnabled = Config.incomingTranslationEnabled;
        this.outgoingEnabled = Config.outgoingTranslationEnabled;
        this.incomingLanguage = Config.incomingTargetLanguage != null ? Config.incomingTargetLanguage : Languages.English;
        this.outgoingLanguage = Config.outgoingTargetLanguage != null ? Config.outgoingTargetLanguage : Languages.English;
    }

    @Override
    protected void addOptions() {
        int centerX = this.width / 2;
        int startY = this.height / 6 + 12;
        
        // Translation Engine Selection
        this.engineSelector = CycleButton.<TranslationEngine>builder(engine -> Component.literal(engine.getDisplayName()))
            .withValues(TranslationEngine.values())
            .withInitialValue(this.selectedEngine)
            .create(centerX - BUTTON_WIDTH - 5, startY, BUTTON_WIDTH, BUTTON_HEIGHT, 
                    Component.literal("Translation Engine"), 
                    (button, engine) -> {
                        this.selectedEngine = engine;
                        updateApiKeyFieldVisibility();
                    });
        this.addRenderableWidget(this.engineSelector);
        
        // API Key field (for engines that need it)
        this.apiKeyField = new EditBox(this.font, centerX + 5, startY, BUTTON_WIDTH, BUTTON_HEIGHT, 
                                      Component.literal("API Key"));
        this.apiKeyField.setValue(this.apiKey);
        this.apiKeyField.setHint(Component.literal("Enter API key"));
        this.apiKeyField.setResponder(text -> this.apiKey = text);
        this.addRenderableWidget(this.apiKeyField);
        
        // Incoming translation enabled
        this.incomingEnabledButton = CycleButton.onOffBuilder(this.incomingEnabled)
            .create(centerX - BUTTON_WIDTH - 5, startY + SPACING, BUTTON_WIDTH, BUTTON_HEIGHT,
                    Component.literal("Incoming Translation"),
                    (button, enabled) -> this.incomingEnabled = enabled);
        this.addRenderableWidget(this.incomingEnabledButton);
        
        // Incoming language selection
        this.incomingLanguageButton = Button.builder(
            Component.literal(this.incomingLanguage.getTranslatedName()),
            (button) -> {
                this.minecraft.setScreen(new LanguagePickerScreen(this.incomingLanguage, "Incoming",
                    (selectedLanguage) -> {
                        this.incomingLanguage = selectedLanguage;
                        updateButtonTexts();
                        this.minecraft.setScreen(this);
                    }, null));
            }
        ).bounds(centerX + 5, startY + SPACING, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.addRenderableWidget(this.incomingLanguageButton);
        
        // Outgoing translation enabled
        this.outgoingEnabledButton = CycleButton.onOffBuilder(this.outgoingEnabled)
            .create(centerX - BUTTON_WIDTH - 5, startY + SPACING * 2, BUTTON_WIDTH, BUTTON_HEIGHT,
                    Component.literal("Outgoing Translation"),
                    (button, enabled) -> this.outgoingEnabled = enabled);
        this.addRenderableWidget(this.outgoingEnabledButton);
        
        // Outgoing language selection
        this.outgoingLanguageButton = Button.builder(
            Component.literal(this.outgoingLanguage.getTranslatedName()),
            (button) -> {
                this.minecraft.setScreen(new LanguagePickerScreen(this.outgoingLanguage, "Outgoing", 
                    (selectedLanguage) -> {
                        this.outgoingLanguage = selectedLanguage;
                        updateButtonTexts();
                        this.minecraft.setScreen(this);
                    }, null));
            }
        ).bounds(centerX + 5, startY + SPACING * 2, BUTTON_WIDTH, BUTTON_HEIGHT).build();
        this.addRenderableWidget(this.outgoingLanguageButton);
        
        // Done button (standard for options screens)
        this.addRenderableWidget(
            Button.builder(Component.translatable("gui.done"), (button) -> {
                saveSettings();
                this.minecraft.setScreen(this.lastScreen);
            }).bounds(centerX - 100, this.height - 27, 200, BUTTON_HEIGHT).build()
        );
        
        updateApiKeyFieldVisibility();
    }
    
    private void updateButtonTexts() {
        if (this.incomingLanguageButton != null) {
            this.incomingLanguageButton.setMessage(Component.literal(this.incomingLanguage.getTranslatedName()));
        }
        if (this.outgoingLanguageButton != null) {
            this.outgoingLanguageButton.setMessage(Component.literal(this.outgoingLanguage.getTranslatedName()));
        }
    }
    
    private void updateApiKeyFieldVisibility() {
        if (this.apiKeyField != null) {
            // Show API key field for engines that need it
            boolean needsApiKey = selectedEngine == TranslationEngine.DEEPL || 
                                selectedEngine == TranslationEngine.OPENAI_GPT ||
                                selectedEngine == TranslationEngine.CLAUDE;
            this.apiKeyField.visible = needsApiKey;
            this.apiKeyField.setHint(Component.literal("Enter " + selectedEngine.getDisplayName() + " API key"));
        }
    }
    
    private void saveSettings() {
        // Save to config values (this updates both the ConfigValue and static fields)
        Config.TRANSLATION_ENGINE.set(this.selectedEngine);
        Config.DEEPL_API_KEY.set(this.apiKey);
        Config.INCOMING_TRANSLATION_ENABLED.set(this.incomingEnabled);
        Config.OUTGOING_TRANSLATION_ENABLED.set(this.outgoingEnabled);
        Config.INCOMING_TARGET_LANGUAGE.set(this.incomingLanguage);
        Config.OUTGOING_TARGET_LANGUAGE.set(this.outgoingLanguage);
        
        // Update static fields immediately for other parts of the mod
        Config.translationEngine = this.selectedEngine;
        Config.deepLApiKey = this.apiKey;
        Config.incomingTranslationEnabled = this.incomingEnabled;
        Config.outgoingTranslationEnabled = this.outgoingEnabled;
        Config.incomingTargetLanguage = this.incomingLanguage;
        Config.outgoingTargetLanguage = this.outgoingLanguage;
    }
}