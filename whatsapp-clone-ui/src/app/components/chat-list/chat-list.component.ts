import { Component, input, InputSignal, output } from '@angular/core';
import { ChatResponse, UserResponse } from '../../services/models';
import { ChatService, UserService } from '../../services/services';
import { CommonModule } from '@angular/common';
import { KeycloakService } from '../../utils/keycloak/keycloak.service';

@Component({
  selector: 'app-chat-list',
  imports: [CommonModule],
  templateUrl: './chat-list.component.html',
  styleUrl: './chat-list.component.scss',
  providers: [CommonModule]
})
export class ChatListComponent {

  chats: InputSignal<ChatResponse[]> = input<ChatResponse[]>([]);
  searchNewContact = false;
  contacts: Array<UserResponse> = [];
  chatSelected: any = output<ChatResponse>();

  constructor(
    private userService: UserService,
    private chatService: ChatService,
    private keycloakService: KeycloakService
  ) {

  }

  wrapMessage(lastMessage: string | undefined): string {
    if (lastMessage && lastMessage.length <= 20) {
      return lastMessage;
    }
    return lastMessage?.substring(0, 17) + '...';
  }

  chatClicked(chat: ChatResponse) {
    this.chatSelected.emit(chat);
  }
  
  searchContact() {
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        this.contacts = users;
        this.searchNewContact = true;
      }
    })
  }

  selectContact(contact: UserResponse) {
    this.chatService.createChat({
      'sender-id': this.keycloakService.userId as string,
      'receiver-id': contact.id as string
    }).subscribe({
      next: (res) => {
        const chat: ChatResponse = {
          id: res.response,
          name: contact.firstName + ' ' + contact.lastName,
          recipientOnline: contact.online,
          lastMessageTime: contact.lastSeen,
          senderId: this.keycloakService.userId,
          receiverId: contact.id
        };

        // unshift - insert in the beginning
        this.chats().unshift(chat);
        this.searchNewContact = false;
        this.chatSelected.emit(chat);
      }
    })
  }
}
