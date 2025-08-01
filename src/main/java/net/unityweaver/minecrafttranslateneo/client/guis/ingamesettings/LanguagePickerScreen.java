package net.unityweaver.minecrafttranslateneo.client.guis.ingamesettings;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.unityweaver.minecrafttranslateneo.client.guis.BaseTranslationScreen;
import net.unityweaver.minecrafttranslateneo.enums.Languages;

import java.util.List;
import java.util.function.Consumer;

/**
 * Scrollable language picker screen for selecting translation languages
 */
public class LanguagePickerScreen extends BaseTranslationScreen {
    
    private static final int MIN_PANEL_WIDTH = 280;
    private static final int MIN_PANEL_HEIGHT = 250;
    private static final int LIST_WIDTH_OFFSET = 40; // Panel width - list width
    private static final int LIST_HEIGHT_OFFSET = 120; // Panel height - list height
    
    private final Languages currentLanguage;
    private final Consumer<Languages> onLanguageSelected;
    private final String selectionType; // "Incoming" or "Outgoing"
    private final InGameSettingsScreen parentSettingsScreen;
    private LanguagePickerList languageList;
    private Button cancelButton;

    public LanguagePickerScreen(Languages currentLanguage, String selectionType, Consumer<Languages> onLanguageSelected, InGameSettingsScreen parentSettingsScreen) {
        super(Component.literal("Select " + selectionType + " Language"), BackgroundType.NONE);
        this.currentLanguage = currentLanguage;
        this.selectionType = selectionType;
        this.onLanguageSelected = onLanguageSelected;
        this.parentSettingsScreen = parentSettingsScreen;
    }
    
    @Override
    protected void init() {
        super.init();
        
        // Calculate responsive dimensions - ensure it fits on screen
        int panelWidth = Math.max(MIN_PANEL_WIDTH, Math.min(this.width - 60, 350));
        int panelHeight = Math.max(MIN_PANEL_HEIGHT, Math.min(this.height - 60, 320));
        int listWidth = panelWidth - LIST_WIDTH_OFFSET;
        int listHeight = panelHeight - LIST_HEIGHT_OFFSET;
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int panelTop = centerY - panelHeight / 2;
        int listStartY = panelTop + 60; // Reduced space for title and current selection text
        int listEndY = listStartY + listHeight;
        
        // Create scrollable language list
        this.languageList = new LanguagePickerList(
            this.minecraft, 
            listWidth, 
            listHeight,
            listStartY,
            listEndY
        );
        
        // Position the list in the center
        this.languageList.setX(centerX - listWidth / 2);
        
        // Add the list as both widget and renderable
        this.addWidget(this.languageList);
        this.addRenderableWidget(this.languageList);
        
        // Add languages to list
        for (Languages language : Languages.values()) {
            this.languageList.addLanguageEntry(new LanguagePickerEntry(language, this));
        }
        
        // Cancel button - positioned at bottom of panel
        this.cancelButton = Button.builder(Component.literal("Cancel"), (button) -> {
            // Return to the existing EnhancedSettingsScreen instance to preserve state
            if (this.parentSettingsScreen != null) {
                this.minecraft.setScreen(this.parentSettingsScreen);
            } else {
                // Fallback to new instance if no parent provided
//                this.minecraft.setScreen(new TranslationSettingsScreen(null));
                throw new IllegalStateException("No parent settings screen provided for LanguagePickerScreen");
            }
        }).bounds(centerX - 50, panelTop + panelHeight - 35, 100, 20).build();
        this.addRenderableWidget(this.cancelButton);
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Always call super first to render background and basic elements
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        
        // Calculate same responsive dimensions as init()
        int panelWidth = Math.max(MIN_PANEL_WIDTH, Math.min(this.width - 60, 350));
        int panelHeight = Math.max(MIN_PANEL_HEIGHT, Math.min(this.height - 60, 320));
        
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int panelTop = centerY - panelHeight / 2;
        
        // Render panel background
        renderPanel(guiGraphics, centerX, centerY, panelWidth, panelHeight);
        
        // Render title at top of panel
        Component title = Component.literal("Select " + selectionType + " Language")
            .withStyle(ChatFormatting.WHITE, ChatFormatting.BOLD);
        renderCenteredText(guiGraphics, title, centerX, panelTop + 15, 0xFFFFFF);
        
        // Render instruction text above the list with small margin
        Component instructionText = Component.literal("Choose a language:")
            .withStyle(ChatFormatting.GRAY);
        renderCenteredText(guiGraphics, instructionText, centerX, panelTop + 45, 0xAAAAAA);
        
        // Render current selection info above cancel button
        Component currentInfo = Component.literal("Current: " + currentLanguage.getTranslatedName())
            .withStyle(ChatFormatting.YELLOW);
        renderCenteredText(guiGraphics, currentInfo, centerX, panelTop + panelHeight - 50, 0xFFFF55);
    }
    
    public void selectLanguage(Languages language) {
        this.onLanguageSelected.accept(language);
        // Don't call onClose() here - let the callback handle screen transition
    }
    
    public Languages getCurrentLanguage() {
        return this.currentLanguage;
    }
    
    /**
     * Scrollable list for language selection
     */
    private static class LanguagePickerList extends ContainerObjectSelectionList<LanguagePickerEntry> {
        
        private static final int ITEM_HEIGHT = 24;
        
        public LanguagePickerList(net.minecraft.client.Minecraft minecraft, int width, int height, int y0, int y1) {
            super(minecraft, width, height, y0, ITEM_HEIGHT);
            this.setY(y0);
        }
        
        public void addLanguageEntry(LanguagePickerEntry entry) {
            this.addEntry(entry);
        }
        
        @Override
        public int getRowWidth() {
            return this.width - 20; // Leave space for scrollbar
        }
        
        @Override
        protected int getScrollbarPosition() {
            return this.getX() + this.width - 6;
        }
        
        @Override
        protected int getRowTop(int index) {
            return this.getY() + 4 - (int)this.getScrollAmount() + index * ITEM_HEIGHT + this.headerHeight;
        }
        
        @Override
        public int getRowLeft() {
            return this.getX() + this.width / 2 - this.getRowWidth() / 2;
        }
        
        
    }
    
    /**
     * Individual language entry in the scrollable list
     */
    private static class LanguagePickerEntry extends ContainerObjectSelectionList.Entry<LanguagePickerEntry> {
        
        private final Languages language;
        private final LanguagePickerScreen screen;
        private Button selectButton;
        
        public LanguagePickerEntry(Languages language, LanguagePickerScreen screen) {
            this.language = language;
            this.screen = screen;
        }
        
        @Override
        public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, 
                          int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            
            // Determine button text and styling
            Component buttonText = Component.literal(this.language.getTranslatedName());
            if (this.language == screen.getCurrentLanguage()) {
                buttonText = buttonText.copy().withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD);
            }
            
            // Create or recreate button if needed (to handle text changes)
            if (this.selectButton == null || !this.selectButton.getMessage().equals(buttonText)) {
                this.selectButton = Button.builder(buttonText, (button) -> {
                    screen.selectLanguage(this.language);
                }).bounds(left + 4, top + 2, width - 8, height - 4).build();
            }
            
            // Always update button position for scrolling
            this.selectButton.setX(left + 4);
            this.selectButton.setY(top + 2);
            this.selectButton.setWidth(width - 8);
            this.selectButton.setHeight(height - 4);
            
            this.selectButton.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        
        @Override
        public List<? extends GuiEventListener> children() {
            return this.selectButton != null ? List.of(this.selectButton) : List.of();
        }
        
        @Override
        public List<? extends NarratableEntry> narratables() {
            return this.selectButton != null ? List.of(this.selectButton) : List.of();
        }
        
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (this.selectButton != null) {
                return this.selectButton.mouseClicked(mouseX, mouseY, button);
            }
            return false;
        }
    }
}