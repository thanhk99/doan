import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { SocketIoModule, SocketIoConfig } from 'ngx-socket-io';

const config: SocketIoConfig = { 
  url: 'http://localhost:8082/client-info', 
  options: { transports: ['websocket'], upgrade: true } 
};

export const appConfig: ApplicationConfig = { 
  providers: [importProvidersFrom(SocketIoModule.forRoot(config))] 
};   