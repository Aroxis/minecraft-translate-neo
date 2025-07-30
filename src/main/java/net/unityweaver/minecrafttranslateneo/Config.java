package net.unityweaver.minecrafttranslateneo;

import net.unityweaver.minecrafttranslateneo.enums.Languages;
import net.unityweaver.minecrafttranslateneo.enums.TranslationEngine;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = MinecraftTranslateModNeo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Languages> INCOMING_TARGET_LANGUAGE;
    public static final ForgeConfigSpec.ConfigValue<Languages> OUTGOING_TARGET_LANGUAGE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> INCOMING_TRANSLATION_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<Boolean> OUTGOING_TRANSLATION_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<TranslationEngine> TRANSLATION_ENGINE;
    public static final ForgeConfigSpec.ConfigValue<String> DEEPL_API_KEY;

    public static Languages incomingTargetLanguage;
    public static Languages outgoingTargetLanguage;
    public static boolean incomingTranslationEnabled;
    public static boolean outgoingTranslationEnabled;
    public static TranslationEngine translationEngine;
    public static String deepLApiKey;

    static {
        BUILDER.push("MinecraftTranslateNeo");

        INCOMING_TARGET_LANGUAGE = BUILDER.comment("What language the other players speech should be translated to")
                .defineEnum("Incoming target language", Languages.Dutch);
        INCOMING_TRANSLATION_ENABLED = BUILDER.comment("Enable or disable incoming translation")
                .define("Incoming translation enabled", true);
        OUTGOING_TARGET_LANGUAGE = BUILDER.comment("What language you want your speech to be translated to")
                .defineEnum("Outgoing target language", Languages.English);
        OUTGOING_TRANSLATION_ENABLED = BUILDER.comment("Enable or disable outgoing translation")
                .define("Outgoing translation enabled", true);
        TRANSLATION_ENGINE = BUILDER.comment("Translation engine to use")
                .defineEnum("Translation engine", TranslationEngine.DEEPL);
        DEEPL_API_KEY = BUILDER.comment("DeepL API key for translation service")
                .define("DeepL API key", "");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        incomingTargetLanguage = INCOMING_TARGET_LANGUAGE.get();
        outgoingTargetLanguage = OUTGOING_TARGET_LANGUAGE.get();
        incomingTranslationEnabled = INCOMING_TRANSLATION_ENABLED.get();
        outgoingTranslationEnabled = OUTGOING_TRANSLATION_ENABLED.get();
        translationEngine = TRANSLATION_ENGINE.get();
        deepLApiKey = DEEPL_API_KEY.get();
    }
}
