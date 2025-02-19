// websocket.service.ts
import { Injectable } from '@angular/core';
import { Socket } from 'ngx-socket-io';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {
  constructor(private socket: Socket) {}

  // Gửi message đến server
  sendMessage(msg: string): void {
    this.socket.emit('message', msg);
  }

  // Nhận message từ server
  getMessages(): Observable<string> {
    return this.socket.fromEvent<string,string>('message');
  }
}