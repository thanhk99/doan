import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-root',
  template: `<h1>{{ message }}</h1>`,
})
export class AppComponent implements OnInit {
  message: string = 'Chưa có dữ liệu';
  private webSocket !: WebSocket;

  ngOnInit() {
    this.webSocket = new WebSocket('ws://localhost:8082/client-info');

    this.webSocket.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data); // Xử lý JSON
        this.message = data.message;
      } catch (e) {
        this.message = event.data; // Xử lý plain text
      }
    };

    this.webSocket.onerror = (error) => {
      console.error('Lỗi WebSocket:', error);
    };
  }
}