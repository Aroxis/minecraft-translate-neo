package net.unityweaver.minecrafttranslateneo.client.guis;

import net.unityweaver.minecrafttranslateneo.enums.Languages;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;

import javax.annotation.Nullable;

public class LanguageList extends ContainerObjectSelectionList<LanguageEntry> {
    private final InGameSettingsScreen inGameSettingsScreen;
    private Languages selectedLanguage;

    @Nullable
    private String filter;

    public LanguageList(InGameSettingsScreen inGameSettingsScreen, Minecraft pMinecraft, int pWidth, int pHeight, int pY0, int pY1, int pItemHeight) {
        super(pMinecraft, pWidth, pHeight, pY0, pY1);
        this.inGameSettingsScreen = inGameSettingsScreen;
        this.setFilter("");
    }


    @Override
    public void renderWidget(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        updateSelectedLanguage();
        double d0 = this.minecraft.getWindow().getGuiScale();
        RenderSystem.enableScissor(
            (int)((double)this.getRowLeft() * d0), 
            (int)((double)(this.height - this.getBottom()) * d0), 
            (int)((double)(this.getScrollbarPosition() + 6) * d0), 
            (int)((double)(this.height - (this.height - this.getBottom()) - this.getY() - 4) * d0)
        );
        super.renderWidget(guiGraphics, pMouseX, pMouseY, pPartialTick);
        RenderSystem.disableScissor();
    }

    private void updateSelectedLanguage() {
        var page = this.inGameSettingsScreen.getCurrentPage();
        var language = page == InGameSettingsScreen.Page.INPUT_LANGUAGE
                ? inGameSettingsScreen.getInputLanguage() : inGameSettingsScreen.getOutputLanguage();

        this.selectedLanguage = language;
    }

    public Languages getSelectedLanguage() {
        return this.selectedLanguage;
    }

    public void setFilter(String pFilter) {
        this.filter = pFilter;
        
        var entries = 
                LanguageEntry.getLanguageEntries(inGameSettingsScreen, this);
        
        if (pFilter != null && !pFilter.isEmpty()) {
            entries.removeIf((entry) -> {
                return !entry.getLanguage().name().toLowerCase().contains(pFilter.toLowerCase());
            });
        }

        this.clearEntries();
        for (LanguageEntry entry : entries) {
            this.addEntry(entry);
        }

        // Remove scroll adjustment for now due to API changes
        // if (this.getMaxScroll() < this.getScrollAmount()) {
        //     this.setScrollAmount(this.getMaxScroll());
        // } else if (this.getScrollAmount() < 0) {
        //     this.setScrollAmount(0);
        // }
    }

    public boolean filterResultEmpty() {
        return this.children().isEmpty();
    }

    @Override
    public int getRowWidth() {
        return 220; // Slightly narrower for better appearance
    }

}