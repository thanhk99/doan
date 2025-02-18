import { Injectable } from '@angular/core';
import { Socket } from 'ngx-socket-io';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class WebsocketService {
  constructor(
    private socket: Socket
  ) {
    this.socket.connect();
   socket.open()
  }

  sendMessage(msg: string): void {
    this.socket.emit('message', msg);
  }
}