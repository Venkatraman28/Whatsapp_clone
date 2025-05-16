package com.venkat.whatsappclone.chat;


import com.venkat.whatsappclone.common.BaseAuditingEntity;
import com.venkat.whatsappclone.message.Message;
import com.venkat.whatsappclone.message.MessageState;
import com.venkat.whatsappclone.message.MessageType;
import com.venkat.whatsappclone.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat")
@NamedQuery(
        name = ChatConstants.FIND_CHAT_BY_SENDER_ID,
        query = "select distinct c from Chat c where c.sender.id = :senderId or c.recipient.id = :senderId order by createdDate DESC"
)
@NamedQuery(
        name = ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER,
        query = "select distinct c from Chat c where (c.sender.id = :senderId and c.recipient.id = :recipientId) " +
                "or (c.sender.id = :recipientId and c.recipient.id = :senderId)"
)
public class Chat extends BaseAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;

    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    @OrderBy("createdDate DESC")
    private List<Message> messages;

    @Transient
    public String getChatName(final String senderId) {
        if (recipient.getId().equals(senderId)) {
            return sender.getFirstName() + " " + sender.getLastName();
        }
        return recipient.getFirstName() + " " + recipient.getLastName();
    }

    @Transient
    public String targetChatName(final String senderId) {
        if (sender.getId().equals(senderId)) {
            return sender.getFirstName() + " " + sender.getLastName();
        }
        return recipient.getFirstName() + " " + recipient.getLastName();
    }

    @Transient
    public long getUnreadMessages(String senderId) {
        return this.messages
                .stream()
                .filter(m -> senderId.equals(m.getReceiverId()))
                .filter(m -> MessageState.SENT.equals(m.getState()))
                .count();
    }

    public String getLastMessage() {
        if (messages != null && !messages.isEmpty()) {
            if (messages.get(0).getType() != MessageType.TEXT) {
                return "Attachment";
            }
            return messages.get(0).getContent();
        }
        return null;
    }

    @Transient
    public LocalDateTime getLastMessageTime() {
        if (messages != null && !messages.isEmpty()) {
            return messages.get(0).getCreatedDate();
        }
        return null;
    }
}
