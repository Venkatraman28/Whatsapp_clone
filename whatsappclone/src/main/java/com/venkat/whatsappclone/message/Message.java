package com.venkat.whatsappclone.message;

import com.venkat.whatsappclone.chat.Chat;
import com.venkat.whatsappclone.common.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
@NamedQuery(
        name = MessageConstants.FIND_MESSAGES_BY_CHAT_ID,
        query = "select m from Message m where m.chat.id = :chatId order by m.createdDate")
@NamedQuery(
        name = MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT,
        query = "update Message set state = :newState where chat.id = :chatId"
)
public class Message extends BaseAuditingEntity {

    @Id
    @SequenceGenerator(name="msg_seq", sequenceName = "msg_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "msg_seq")
    private Long id;

    @Column(columnDefinition="TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageState state;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Column(name = "receiver_id", nullable = false)
    private String receiverId;

    private String mediaFilePath;

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", state=" + state +
                ", type=" + type +
                ", chat=" + chat +
                ", senderId='" + senderId + '\'' +
                ", receiverId='" + receiverId + '\'' +
                ", mediaFilePath='" + mediaFilePath + '\'' +
                '}';
    }
}
