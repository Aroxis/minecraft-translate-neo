package net.unityweaver.minecrafttranslateneo.client.guis;

import net.unityweaver.minecrafttranslateneo.Config;
import net.unityweaver.minecrafttranslateneo.enums.Languages;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.unityweaver.minecrafttranslateneo.client.guis.clonedcomponents.ClonedCheckbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.UUID;

public class InGameSettingsScreen extends BaseTranslationScreen {

    private static final ResourceLocation DEMO_BACKGROUND_LOCATION =
            ResourceLocation.fromNamespaceAndPath("minecrafttranslateneo", "textures/gui/sprites/social_interactions/background.png");
    private static final ResourceLocation SEARCH_BACKGROUND_LOCATION =
            ResourceLocation.fromNamespaceAndPath("minecrafttranslateneo", "textures/gui/sprites/icon/search.png");

    private static final Component SEARCH_HINT = Component.translatable("gui.socialInteractions.search_hint").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
    private static final Component EMPTY_SEARCH = Component.literal("No languages found by that name").withStyle(ChatFormatting.GRAY);

    private boolean initialized = false;
    private static Page currentPage = Page.MAIN;
    private String lastSearch = "";

    private LanguageList languageList;
    private ArrayList<AbstractWidget> mainWidgets = new ArrayList<>();
    private ArrayList<AbstractWidget> languageWidgets = new ArrayList<>();

    private Languages inputLanguage;
    private Languages outputLanguage;

    private ClonedCheckbox inputTranslationCheckbox;
    private ClonedCheckbox outputTranslationCheckbox;
    private Button inputLanguageButton;
    private Button outputLanguageButton;
    private Button saveButton;
    private EditBox searchBox;

    @Nullable
    private Runnable postRenderRunnable;

    public InGameSettingsScreen() {
        super(Component.literal("Settings"), BackgroundType.BLURRED);
    }

    private int windowHeight() {
        return Math.max(52, this.height - 128 - 16);
    }

    private int backgroundUnits() {
        return this.windowHeight() / 16;
    }

    private int marginX() {
        return (this.width - 238) / 2;
    }

    public boolean isPauseScreen() {
        return false;
    }

    public void tick() {
        super.tick();
    }

    private int marginY() {
        return (this.height - 238) / 2;
    }

    private static final int BG_WIDTH = 236;

    protected void init() {
        this.mainWidgets.clear();
        this.languageWidgets.clear();

        if (!this.initialized) {
            inputLanguage = Config.incomingTargetLanguage;
            outputLanguage = Config.outgoingTargetLanguage;

            this.languageList = new LanguageList
                    (this, this.minecraft, this.width, this.height,
                            88, this.listEnd(), 18);
        } else {
            this.languageList = new LanguageList(this, this.minecraft, this.width, this.height, 88, this.listEnd(), 18);
        }

        inputTranslationCheckbox =
                new ClonedCheckbox(this.marginX() + 10, this.marginY() + 50,
                        228, Component.literal("Incoming translation"),
                        this.font, Config.incomingTranslationEnabled,
                        (checkbox, checked) -> {});
        this.mainWidgets.add(inputTranslationCheckbox);

        var inputLanguageButtonText = Component.literal("Incoming language: " + inputLanguage.getTranslatedName());
        inputLanguageButton = Button.builder(inputLanguageButtonText, (button) -> {
            currentPage = Page.INPUT_LANGUAGE;
        })
        .bounds(this.marginX() + 10, this.marginY() + 75, 218, 20)
        .build();
        this.mainWidgets.add(inputLanguageButton);

        outputTranslationCheckbox =
                new ClonedCheckbox(this.marginX() + 10, this.marginY() + 100,
                        228, Component.literal("Outgoing translation"),
                        this.font, Config.outgoingTranslationEnabled,
                        (checkbox, checked) -> {});
        this.mainWidgets.add(outputTranslationCheckbox);

        var outLanguageButtonText = Component.literal("Outgoing language: " + outputLanguage.getTranslatedName());
        outputLanguageButton = Button.builder(outLanguageButtonText, (button) -> {
            currentPage = Page.OUTPUT_LANGUAGE;
        })
        .bounds(this.marginX() + 10, this.marginY() + 125, 218, 20)
        .build();
        this.mainWidgets.add(outputLanguageButton);

        var saveButtonText = Component.literal("Save");
        saveButton = Button.builder(saveButtonText, (button) -> {
            saveSettings();
            currentPage = Page.MAIN;
            this.onClose();
        })
        .bounds(this.marginX() + 10, ((this.height - 166) / 2) + 137, 218, 20)
        .build();
        this.mainWidgets.add(saveButton);

        String s = this.searchBox != null ? this.searchBox.getValue() : "";
        this.searchBox = new EditBox(this.font, this.marginX() + 28, 78,
                196, 16, Component.literal("Search"));

        this.searchBox.setMaxLength(16);
        this.searchBox.setBordered(false);
        this.searchBox.setVisible(true);
        this.searchBox.setTextColor(16777215);
        this.searchBox.setValue(s);
        this.searchBox.setResponder(this::checkSearchStringUpdate);
        this.languageWidgets.add(this.searchBox);

        this.initialized = true;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        if (currentPage == Page.MAIN) {
            this.onClose();
            return true;
        } else {
            currentPage = Page.MAIN;
            return false;
        }
    }

    public void onClose() {
        this.currentPage = Page.MAIN;
        this.minecraft.setScreen(null);
    }

    private void updateWidgets() {
        this.clearWidgets();
        if (currentPage == Page.MAIN) {
            for (var widget : mainWidgets) {
                this.addRenderableWidget(widget);
            }
        } else {
            for (var widget : languageWidgets) {
                this.addRenderableWidget(widget);
            }

            this.addRenderableWidget(languageList);
        }
    }

    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        updateWidgets();

        if (this.minecraft == null) {
            this.minecraft = Minecraft.getInstance();
        }

        if (Page.MAIN == currentPage) {
            this.renderBackground(guiGraphics, pMouseX, pMouseY, pPartialTick);
            super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
        }
        else  {
            this.renderBackground(guiGraphics, pMouseX, pMouseY, pPartialTick);
            this.languageList.render(guiGraphics, pMouseX, pMouseY, pPartialTick);

            if (!this.languageList.filterResultEmpty()) {
                this.languageList.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
            } else if (!this.searchBox.getValue().isEmpty()) {
                guiGraphics.drawCenteredString(this.minecraft.font, EMPTY_SEARCH, this.width / 2, (78 + this.listEnd()) / 2, -1);
            }

            if (!this.searchBox.isFocused() && this.searchBox.getValue().isEmpty()) {
                guiGraphics.drawString(this.minecraft.font, SEARCH_HINT, this.searchBox.getX(), this.searchBox.getY(), -1);
            } else {
                this.searchBox.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
            }

            super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
            if (this.postRenderRunnable != null) {
                this.postRenderRunnable.run();
            }
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        if (Page.MAIN == currentPage) {
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            int i = (this.width - 248) / 2;
            int j = (this.height - 166) / 2;
            // Implement proper nine-slice rendering for 248x166 dialog
            int targetWidth = 248;
            int targetHeight = 166;
            int border = 8; // From mcmeta file
            
            // Calculate dimensions
            int centerWidth = targetWidth - (border * 2);
            int centerHeight = targetHeight - (border * 2);
            
            // Top-left corner
            guiGraphics.blit(DEMO_BACKGROUND_LOCATION, i, j, 0, 0, border, border, 236, 34);
            // Top edge
            guiGraphics.blit(DEMO_BACKGROUND_LOCATION, i + border, j, border, 0, centerWidth, border, 236, 34);
            // Top-right corner  
            guiGraphics.blit(DEMO_BACKGROUND_LOCATION, i + targetWidth - border, j, 236 - border, 0, border, border, 236, 34);
            
            // Left edge
            guiGraphics.blit(DEMO_BACKGROUND_LOCATION, i, j + border, 0, border, border, centerHeight, 236, 34);
            // Center (tiled)
            guiGraphics.blit(DEMO_BACKGROUND_LOCATION, i + border, j + border, border, border, centerWidth, centerHeight, 236, 34);
            // Right edge
            guiGraphics.blit(DEMO_BACKGROUND_LOCATION, i + targetWidth - border, j + border, 236 - border, border, border, centerHeight, 236, 34);
            
            // Bottom-left corner
            guiGraphics.blit(DEMO_BACKGROUND_LOCATION, i, j + targetHeight - border, 0, 34 - border, border, border, 236, 34);
            // Bottom edge
            guiGraphics.blit(DEMO_BACKGROUND_LOCATION, i + border, j + targetHeight - border, border, 34 - border, centerWidth, border, 236, 34);
            // Bottom-right corner
            guiGraphics.blit(DEMO_BACKGROUND_LOCATION, i + targetWidth - border, j + targetHeight - border, 236 - border, 34 - border, border, border, 236, 34);
        } else {
            int i = this.marginX() + 3;
            // Render background using direct texture blitting with nine-slice sections
            // Top section
            guiGraphics.blit(DEMO_BACKGROUND_LOCATION, i, 64, 1, 1, 236, 8, 236, 34);
            int j = this.backgroundUnits();

            // Middle sections (repeating)
            for(int k = 0; k < j; ++k) {
                guiGraphics.blit(DEMO_BACKGROUND_LOCATION, i, 72 + 16 * k, 1, 10, 236, 16, 236, 34);
            }

            // Bottom section
            guiGraphics.blit(DEMO_BACKGROUND_LOCATION, i, 72 + 16 * j, 1, 27, 236, 8, 236, 34);
            // Render search icon
            guiGraphics.blit(SEARCH_BACKGROUND_LOCATION, i + 10, 76, 0, 0, 12, 12, 12, 12);
        }
    }

    public void setLanguage(Languages language) {
        if (currentPage == Page.INPUT_LANGUAGE) {
            inputLanguage = language;
            inputLanguageButton.setMessage(Component.literal("Incoming language: " + language.getTranslatedName()));
        } else if (currentPage == Page.OUTPUT_LANGUAGE) {
            outputLanguage = language;
            outputLanguageButton.setMessage(Component.literal("Outgoing language: " + language.getTranslatedName()));
        } else {
            throw new IllegalStateException("Cannot set language when not on language selection page");
        }

        this.showPage(Page.MAIN);
    }

    private boolean savingEnabled() {
        return true;
    }

    private int listEnd() {
        return 80 + this.backgroundUnits() * 16 - 8;
    }

    private void checkSearchStringUpdate(String p_100789_) {
        p_100789_ = p_100789_.toLowerCase(Locale.ROOT);
        if (!p_100789_.equals(this.lastSearch)) {
            this.languageList.setFilter(p_100789_);
            this.lastSearch = p_100789_;
            this.showPage(this.currentPage);
        }
    }

    private void saveSettings() {
        Config.INCOMING_TRANSLATION_ENABLED.set(inputTranslationCheckbox.selected());
        Config.OUTGOING_TRANSLATION_ENABLED.set(outputTranslationCheckbox.selected());
        Config.INCOMING_TARGET_LANGUAGE.set(inputLanguage);
        Config.OUTGOING_TARGET_LANGUAGE.set(outputLanguage);
    }

    private void showPage(Page pPage) {
        this.currentPage = pPage;
        Collection<UUID> collection;
        switch(pPage) {
            case INPUT_LANGUAGE:
                collection = this.minecraft.player.connection.getOnlinePlayerIds();
                break;
            case OUTPUT_LANGUAGE:
                collection = this.minecraft.getPlayerSocialManager().getHiddenPlayers();
                break;
            default:
                collection = ImmutableList.of();
        }
    }

    public Page getCurrentPage() {
        return currentPage;
    }

    public Languages getInputLanguage() {
        return inputLanguage;
    }

    public Languages getOutputLanguage() {
        return outputLanguage;
    }

    public void setPostRenderRunnable(@Nullable Runnable pPoseRenderRunnable) {
        this.postRenderRunnable = pPoseRenderRunnable;
    }

    @OnlyIn(Dist.CLIENT)
    public static enum Page {
        MAIN,
        INPUT_LANGUAGE,
        OUTPUT_LANGUAGE
    }

    public static InGameSettingsScreen open() {
        var newScreen = new InGameSettingsScreen();

        new Thread(() -> {
            try {
                Thread.sleep(10);
                Minecraft.getInstance()
                        .execute(() -> Minecraft.getInstance().setScreen(newScreen));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        return new InGameSettingsScreen();
    }
}
