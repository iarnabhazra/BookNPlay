import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import App from './App';
import { ErrorProvider } from './lib/ErrorContext';
import { registerApiErrorListener } from './lib/api';
import { ColorModeProvider } from './lib/ColorModeContext';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ColorModeProvider>
      <ErrorProvider>
        <BrowserRouter>
          <App />
        </BrowserRouter>
      </ErrorProvider>
    </ColorModeProvider>
  </React.StrictMode>
);

// Register a generic error listener after providers mount (microtask)
queueMicrotask(() => {
  registerApiErrorListener((err) => {
    // The ErrorProvider handles displaying via context; simple pattern could be extended.
    // For now we rely on components catching thrown errors if needed.
  });
});
