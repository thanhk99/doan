import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `<h1>{{ message }}</h1>`,
})
export class AppComponent implements OnInit {
  message: string | undefined;

  private webSocket: WebSocket | undefined;

  ngOnInit() {
    this.webSocket = new WebSocket('ws://localhost:8082/client-info');

    this.webSocket.onmessage = (event) => {
      this.message = event.data;
    };

    this.webSocket.onopen = () => {
      console.log('Kết nối thành công!');
    };

    this.webSocket.onclose = () => {
      console.log('Kết nối đã đóng!');
    };
  }
}