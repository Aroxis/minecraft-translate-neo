package net.unityweaver.minecrafttranslateneo.client.guis;

//import com.aroxis.mctranslate.AltConfig;
//import com.aroxis.mctranslate.enums.Languages;
//import net.minecraft.client.gui.screens.Screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class InGameSettingsScreen extends Screen {

    public InGameSettingsScreen() {
        super(Component.literal("Settings"));
    }

//    //region Fields
//    private static final ResourceLocation DEMO_BACKGROUND_LOCATION = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/demo_background.png");
//    private static final ResourceLocation SEARCH_BACKGROUND_LOCATION = ResourceLocation.fromNamespaceAndPath("textures/gui/social_interactions.png");
//
//    private static final Component SEARCH_HINT = (new Component("gui.socialInteractions.search_hint")).withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY);
//    private static final Component EMPTY_SEARCH = (new Component("No languages found by that name")).withStyle(ChatFormatting.GRAY);
//
//    private boolean initialized = false;
//    private Button testButton;
//
//    private static Page currentPage = Page.MAIN;
//    private String lastSearch = "";
//
//    private LanguageList languageList;
//    private ArrayList<AbstractWidget> mainWidgets = new ArrayList<>();
//    private ArrayList<AbstractWidget> languageWidgets = new ArrayList<>();
//
//    // Settings
//    private Languages inputLanguage;
//    private Languages outputLanguage;
//
//    // Settings UI elements
//    private Checkbox inputTranslationCheckbox;
//    private Checkbox outputTranslationCheckbox;
//    private Button inputLanguageButton;
//    private Button outputLanguageButton;
//    private Button saveButton;
//    private EditBox searchBox;
//
//    @Nullable
//    private Runnable postRenderRunnable;
//
//    public InGameSettingsScreen() {
//        super(new Component("Settings"));
//    }
//
//    private int windowHeight() {
//        return Math.max(52, this.height - 128 - 16);
//    }
//
//    private int backgroundUnits() {
//        return this.windowHeight() / 16;
//    }
//
//    private int marginX() {
//        return (this.width - 238) / 2;
//    }
//
//    @Override
//    public boolean isPauseScreen() {
//        return false;
//    }
//
//    public void tick() {
//        super.tick();
//    }
//
//    private int marginY() {
//        return (this.height - 238) / 2;
//    }
//
//    private static final int BG_WIDTH = 236;
//
//    @Override
//    protected void init() {
//        if (!this.initialized) {
//            inputLanguage = AltConfig.INCOMING_TARGET_LANGUAGE.get();
//            outputLanguage = AltConfig.OUTGOING_TARGET_LANGUAGE.get();
//
//            // Initializing language list
//            this.languageList = new LanguageList(this, this.minecraft, this.width, this.height, 88, this.listEnd(), 36);
//        } else {
//            this.languageList.updateSize(this.width, this.height, 88, this.listEnd());
//        }
//
//        // Initialize main settings
//        inputTranslationCheckbox = new Checkbox(this.marginX() + 10, this.marginY() + 50, 228, 20, new Component("Incoming translation"), AltConfig.INCOMING_TRANSLATION_ENABLED.get());
//        this.mainWidgets.add(inputTranslationCheckbox);
//
//        inputLanguageButton = Button.builder(inputLanguageButtonText, (button) -> {
//                    currentPage = Page.INPUT_LANGUAGE;
//                })
//                .bounds(this.marginX() + 10, this.marginY() + 75, 218, 20)
//                .build();
//
//        this.mainWidgets.add(inputLanguageButton);
//
//        outputTranslationCheckbox = new Checkbox(this.marginX() + 10, this.marginY() + 100, 228, 20, new TextComponent("Outgoing translation"), AltConfig.OUTGOING_TRANSLATION_ENABLED.get());
//        this.mainWidgets.add(outputTranslationCheckbox);
//
//        outputLanguageButton = Button.builder(outLanguageButtonText, (button) -> {
//                    currentPage = Page.OUTPUT_LANGUAGE;
//                })
//                .bounds(this.marginX() + 10, this.marginY() + 125, 218, 20)
//                .build();
//
//        this.mainWidgets.add(outputLanguageButton);
//
//        var saveButtonText = new Component("Save");
//
//        saveButton = Button.builder(saveButtonText, (button) -> {
//            saveSettings();
//            currentPage = Page.MAIN;
//            this.onClose();
//        })
//        this.mainWidgets.add(saveButton);
//
//        // Language search box initialization
//        this.searchBox = new EditBox(this.font, this.marginX() + 28, 78, 196, 16, new Component("Search"));
//        this.searchBox.setMaxLength(16);
//        this.searchBox.setBordered(false);
//        this.searchBox.setVisible(true);
//        this.searchBox.setTextColor(16777215);
//        this.searchBox.setResponder(this::checkSearchStringUpdate);
//        this.languageWidgets.add(this.searchBox);
//
//        this.initialized = true;
//    }
//
//    @Override
//    public boolean shouldCloseOnEsc() {
//        if (currentPage == Page.MAIN) {
//            this.onClose();
//            return true;
//        } else {
//            currentPage = Page.MAIN;
//            return false;
//        }
//    }
//
//    public void onClose() {
//        this.currentPage = Page.MAIN;
//        this.minecraft.popGuiLayer();
//    }
//
//    private void updateWidgets() {
//        this.clearWidgets();
//        if (currentPage == Page.MAIN) {
//            for (var widget : mainWidgets) {
//                this.addRenderableWidget(widget);
//            }
//        } else {
//            for (var widget : languageWidgets) {
//                this.addRenderableWidget(widget);
//            }
//            this.addRenderableWidget(languageList);
//        }
//    }
//
//    @Override
//    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
//        updateWidgets();
//
//        if (Page.MAIN == currentPage) {
//            this.renderBackground(guiGraphics);
//            super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
//        } else {
//            this.renderBackground(guiGraphics);
//            this.languageList.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
//
//            if (!this.languageList.fil()) {
//                this.languageList.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
//            } else if (!this.searchBox.getValue().isEmpty()) {
//                guiGraphics.drawCenteredString(this.minecraft.font, EMPTY_SEARCH, this.width / 2, (78 + this.listEnd()) / 2, -1);
//            }
//
//            if (!this.searchBox.isFocused() && this.searchBox.getValue().isEmpty()) {
//                guiGraphics.drawString(this.minecraft.font, SEARCH_HINT, this.searchBox.x, this.searchBox.y, -1);
//            } else {
//                this.searchBox.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
//            }
//
//            super.render(guiGraphics, pMouseX, pMouseY, pPartialTick);
//            if (this.postRenderRunnable != null) {
//                this.postRenderRunnable.run();
//            }
//        }
//    }
//
//    @Override
//    public void renderBackground(GuiGraphics guiGraphics) {
//        super.renderBackground(guiGraphics);
//
//        if (Page.MAIN == currentPage) {
//            guiGraphics.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//            guiGraphics.setShaderTexture(0, DEMO_BACKGROUND_LOCATION);
//            int i = (this.width - 248) / 2;
//            int j = (this.height - 166) / 2;
//            this.blit(guiGraphics, i, j, 0, 0, 248, 166);
//        } else {
//            int i = this.marginX() + 3;
//            guiGraphics.setShaderTexture(0, SEARCH_BACKGROUND_LOCATION);
//            this.blit(guiGraphics, i, 64, 1, 1, 236, 8);
//            int j = this.backgroundUnits();
//
//            for (int k = 0; k < j; ++k) {
//                this.blit(guiGraphics, i, 72 + 16 * k, 1, 10, 236, 16);
//            }
//
//            this.blit(guiGraphics, i, 72 + 16 * j, 1, 27, 236, 8);
//            this.blit(guiGraphics, i + 10, 76, 243, 1, 12, 12);
//        }
//    }
//
//    public void setLanguage(Languages language) {
//        if (currentPage == Page.INPUT_LANGUAGE) {
//            inputLanguage = language;
//            inputLanguageButton.setMessage(new TextComponent("Incoming language: " + language.getTranslatedName()));
//        } else if (currentPage == Page.OUTPUT_LANGUAGE) {
//            outputLanguage = language;
//            outputLanguageButton.setMessage(new TextComponent("Outgoing language: " + language.getTranslatedName()));
//        } else {
//            throw new IllegalStateException("Cannot set language when not on language selection page");
//        }
//
//        this.showPage(Page.MAIN);
//    }
//
//    private void saveSettings() {
//        AltConfig.INCOMING_TRANSLATION_ENABLED.set(inputTranslationCheckbox.selected());
//        AltConfig.OUTGOING_TRANSLATION_ENABLED.set(outputTranslationCheckbox.selected());
//        AltConfig.INCOMING_TARGET_LANGUAGE.set(inputLanguage);
//        AltConfig.OUTGOING_TARGET_LANGUAGE.set(outputLanguage);
//    }
//
//    private void showPage(Page pPage) {
//        this.currentPage = pPage;
//        Collection<UUID> collection;
//        switch (pPage) {
//            case INPUT_LANGUAGE:
//                collection = this.minecraft.player.connection.getOnlinePlayerIds();
//                break;
//            case OUTPUT_LANGUAGE:
//                collection = this.minecraft.getPlayerSocialManager().getHiddenPlayers();
//                break;
//            default:
//                collection = ImmutableList.of();
//        }
//    }
//
//    public static InGameSettingsScreen open() {
//        var newScreen = new InGameSettingsScreen();
//
//        new Thread(() -> {
//            try {
//                Thread.sleep(10);
//                Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(newScreen));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();
//
//        return new InGameSettingsScreen();
//    }
//
//    @OnlyIn(Dist.CLIENT)
//    public enum Page {
//        MAIN,
//        INPUT_LANGUAGE,
//        OUTPUT_LANGUAGE
//    }
}
