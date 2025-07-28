package net.unityweaver.minecrafttranslateneo;

import net.unityweaver.minecrafttranslateneo.enums.Languages;
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

    public static Languages incomingTargetLanguage;
    public static Languages outgoingTargetLanguage;
    public static boolean incomingTranslationEnabled;
    public static boolean outgoingTranslationEnabled;

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
    }
}
