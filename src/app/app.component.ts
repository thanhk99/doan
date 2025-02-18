import { Component, OnInit } from '@angular/core';
import { WebsocketService } from './service/socket.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
})
export class AppComponent implements OnInit {
  message: string = '';

  constructor(private websocketService: WebsocketService) {}

  ngOnInit(): void {
    console.log("run")

  }

  // sendMessage(): void {
  //   this.websocketService.sendMessage('Hello Server!');
  // }
}