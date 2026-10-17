package ru.qwalsss.chatmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import java.util.concurrent.ThreadLocalRandom;

public class ChatModInitializer implements ModInitializer {
    
    // Твой золотой запас ХвХ-смайликов
    private static final String[] SMILES = {
        " О_о", " 0_0", " UwU", " YwY", " xD", " ;)", " ;(", " :0", " :\\", 
        " .QoQ.", " n_n", " r_r", " p_q", " q_p", " u_u", " @.@"
    };

    // Предохранитель против бесконечной отправки и вылета игры
    private static boolean isSendingModifiedMessage = false;

    @Override
    public void onInitialize() {
        ClientSendMessageEvents.ALLOW_CHAT_MESSAGE.register((message) -> {
            // Если это сообщение отправлено самим модом — пропускаем его без изменений
            if (isSendingModifiedMessage) {
                return true; 
            }

            String trimmed = message.trim();
            
            // Если сообщение пустое — ничего не делаем
            if (trimmed.isEmpty()) {
                return true;
            }

            // Проверяем, не заканчивается ли сообщение уже на какой-то смайл из списка
            for (String smile : SMILES) {
                if (trimmed.endsWith(smile.trim())) {
                    return true;
                }
            }

            // Проверяем первый символ сообщения
            char firstChar = trimmed.charAt(0);
            
            // Если ты пишешь команду сервера (/ или .), мод полностью отключается
            if (firstChar == '/' || firstChar == '.') {
                return true; 
            }

            // Выбираем случайный смайл из массива
            int randomIndex = ThreadLocalRandom.current().nextInt(SMILES.length);
            String chosenSmile = SMILES[randomIndex];

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.player.networkHandler != null) {
                try {
                    // Включаем предохранитель перед отправкой в сеть
                    isSendingModifiedMessage = true;
                    
                    // Склеиваем твоё сообщение со случайным смайлом
                    client.player.networkHandler.sendChatMessage(message + chosenSmile);
                } finally {
                    // Выключаем предохранитель в любом случае, чтобы код работал для следующих сообщений
                    isSendingModifiedMessage = false;
                }
                return false; // Глушим старое сообщение без смайла
            }
            return true;
        });
    }
}
