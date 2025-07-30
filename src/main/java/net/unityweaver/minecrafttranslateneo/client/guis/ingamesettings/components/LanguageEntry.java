package net.unityweaver.minecrafttranslateneo.client.guis.ingamesettings.components;

import net.unityweaver.minecrafttranslateneo.client.guis.ingamesettings.InGameSettingsScreen;
import net.unityweaver.minecrafttranslateneo.enums.Languages;
import com.google.common.collect.ImmutableList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;

import java.util.ArrayList;
import java.util.List;

public class LanguageEntry extends ContainerObjectSelectionList.Entry<LanguageEntry> {
    private static final int TOOLTIP_DELAY = 10;
    private static final int TOOLTIP_MAX_WIDTH = 150;
    private final Minecraft minecraft;
    private List<AbstractWidget> children;
    private boolean isRemoved;

    float tooltipHoverTime;
    private static final Component HIDDEN = Component.translatable("gui.socialInteractions.status_hidden").withStyle(ChatFormatting.ITALIC);
    private static final Component BLOCKED = Component.translatable("gui.socialInteractions.status_blocked").withStyle(ChatFormatting.ITALIC);
    private static final Component OFFLINE = Component.translatable("gui.socialInteractions.status_offline").withStyle(ChatFormatting.ITALIC);

    private static final int ENTRY_HEIGHT = 16; // Smaller entry height
    private static final int PADDING = 2;
    private static final int TEXT_OFFSET_X = 4;
    public static final int SKIN_SHADE = FastColor.ARGB32.color(190, 0, 0, 0);
    public static final int BG_FILL = FastColor.ARGB32.color(255, 74, 74, 74);
    public static final int BG_FILL_REMOVED = FastColor.ARGB32.color(255, 48, 48, 48);
    public static final int PLAYERNAME_COLOR = FastColor.ARGB32.color(255, 255, 255, 255);
    public static final int PLAYER_STATUS_COLOR = FastColor.ARGB32.color(140, 255, 255, 255);

    private final String name;
    private final Languages language;
    private final InGameSettingsScreen inGameSettingsScreen;
    private final LanguageList languageList;

    public LanguageEntry(final Minecraft pMinecraft, final InGameSettingsScreen inGameSettingsScreen, final LanguageList languageList, final Languages language) {
        this.minecraft = pMinecraft;
        this.children = ImmutableList.of();

        this.name = language.getTranslatedName();
        this.language = language;
        this.inGameSettingsScreen = inGameSettingsScreen;
        this.languageList = languageList;
    }

    public void render(GuiGraphics guiGraphics, int pIndex, int pTop, int pLeft, int pWidth, int pHeight, int pMouseX, int pMouseY, boolean pIsMouseOver, float pPartialTick) {
        // Simple clean background
        if (this.isSelected()) {
            // Highlight selected item
            guiGraphics.fill(pLeft, pTop, pLeft + pWidth, pTop + pHeight, FastColor.ARGB32.color(100, 0, 255, 0));
        } else {
            // Default background
            guiGraphics.fill(pLeft, pTop, pLeft + pWidth, pTop + pHeight, FastColor.ARGB32.color(50, 64, 64, 64));
        }

        var selectButton = createSelectButton(pLeft, pTop, pWidth, pHeight);
        selectButton.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

        this.children = ImmutableList.of(selectButton);
    }

    private Button createSelectButton(int pX, int pY, int pWidth, int pHeight) {
        var buttonText = this.name;

        if (this.isSelected()) {
            buttonText += " (Selected)";
        }

        var buttonTextComponent = Component.literal(buttonText);

        if (this.isSelected()) {
            buttonTextComponent = buttonTextComponent.copy().withStyle(ChatFormatting.GREEN);
        }

        // Use proper dimensions with padding
        int buttonX = pX + 2;
        int buttonY = pY + 1;
        int buttonWidth = pWidth - 4;
        int buttonHeight = pHeight - 2;

        var output = Button.builder(buttonTextComponent, (p_100994_) -> {
                    // Set the language for the current page context and navigate back
                    this.inGameSettingsScreen.setCurrentPageLanguage(this.language);
                })
                .bounds(buttonX, buttonY, buttonWidth, buttonHeight)
                .build();

        // Don't set alpha to 0 - we want the button to be visible
        // output.setAlpha(0.0F);

        return output;
    }

    public Languages getLanguage() {
        return this.language;
    }

    public List<? extends GuiEventListener> children() {
        return this.children;
    }

    public List<? extends NarratableEntry> narratables() {
        return this.children;
    }

    private boolean isSelected() {
        return this.languageList.getSelectedLanguage().getCode().equals(this.language.getCode());
    }

    private Component getStatusComponent() {
        boolean flag = false;
        boolean flag1 = false;
        if (flag1 && this.isRemoved) {
            return BLOCKED;
        } else if (flag && this.isRemoved) {
            return HIDDEN;
        } else if (flag1) {
            return BLOCKED;
        } else if (flag) {
            return HIDDEN;
        } else {
            return this.isRemoved ? OFFLINE : Component.empty();
        }
    }

    public static ArrayList<LanguageEntry> getLanguageEntries(InGameSettingsScreen screen, LanguageList languageList) {
        var output = new ArrayList<LanguageEntry>();

        var instance = Minecraft.getInstance();
        for (var language : Languages.values()) {
            output.add(new LanguageEntry(instance, screen, languageList, language));
        }

        return output;
    }
}