package com.venkat.whatsappclone.message;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequest {

    private String content;
    private String senderId;
    private String receiverId;
    private MessageType messageType;
    private String chatId;
}
