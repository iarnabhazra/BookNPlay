// Fallback type declarations for libraries without bundled types.
// Replace with @types packages when Node environment supports installation.

declare module 'stompjs' {
  export function over(socket: any): any;
}

declare module 'sockjs-client' {
  const SockJS: any;
  export default SockJS;
}
