package io.crytermed.farmbot;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

/**
 * Główny mod klienta. Rejestruje komendy /farm i /farmstop oraz nasłuchuje czatu (#farm/#farmstop).
 * Uruchamia FarmingManager, który wykonuje pętlę farmingową co tick.
 */
public class FarmMod implements ClientModInitializer {

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final FarmingManager farmingManager = new FarmingManager(16); // promień 16 bloków domyślnie

    @Override
    public void onInitializeClient() {
        // client-side komendy
        ClientCommandManager.DISPATCHER.register(
            ClientCommandManager.literal("farm")
                .executes(context -> {
                    ClientPlayerEntity p = mc.player;
                    if (p != null) {
                        farmingManager.startForPlayer(p.getName().getString());
                        context.getSource().sendFeedback(Text.of("Farm started"));
                    }
                    return 1;
                })
        );

        ClientCommandManager.DISPATCHER.register(
            ClientCommandManager.literal("farmstop")
                .executes(context -> {
                    farmingManager.stop();
                    context.getSource().sendFeedback(Text.of("Farm stopped"));
                    return 1;
                })
        );

        // nasłuchiwanie przychodzących wiadomości w chat (ażeby wykrywać "#farm" gdy gracz wpisze to jako wiadomość)
        ClientReceiveMessageCallback.EVENT.register((message, multiparams) -> {
            final String msg = message.getString().trim();
            ClientPlayerEntity p = mc.player;
            if (p == null) return ActionResult.PASS;
            if (msg.equalsIgnoreCase("#farm")) {
                farmingManager.startForPlayer(p.getName().getString());
                // dodatkowy feedback lokalnie:
                mc.inGameHud.getChatHud().addMessage(Text.of("[FarmBot] Started"));
                return ActionResult.SUCCESS;
            } else if (msg.equalsIgnoreCase("#farmstop")) {
                farmingManager.stop();
                mc.inGameHud.getChatHud().addMessage(Text.of("[FarmBot] Stopped"));
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });

        // Tick: delegujemy do FarmingManager
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            try {
                farmingManager.tick(client);
            } catch (Throwable t) {
                t.printStackTrace();
                farmingManager.stop(); // bezpieczne zatrzymanie na błąd
            }
        });
    }
}