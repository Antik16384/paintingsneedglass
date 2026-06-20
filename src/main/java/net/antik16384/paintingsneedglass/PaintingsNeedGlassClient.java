package net.antik16384.paintingsneedglass;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class PaintingsNeedGlassClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    literal("paintingsneedglass")
                            .then(literal("toggle")
                                    .executes(context -> {
                                        Config config = Config.getInstance();
                                        config.modEnabled = !config.modEnabled;
                                        config.save();
                                        context.getSource().sendFeedback(
                                                Text.literal("(PNG) Enable mod: " + (config.modEnabled ? "TRUE" : "FALSE"))
                                        );
                                        return 1;
                                    })
                            )
                            .then(literal("togglesides")
                                    .executes(context -> {
                                        Config config = Config.getInstance();
                                        config.hideSideTexture = !config.hideSideTexture;
                                        config.save();
                                        context.getSource().sendFeedback(
                                                Text.literal("(PNG) Hide side textures: " + (config.hideSideTexture ? "TRUE" : "FALSE"))
                                        );
                                        return 1;
                                    })
                            )
            );
        });
    }
}