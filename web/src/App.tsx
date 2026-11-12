import React, { useState, useEffect, useRef } from 'react';
import { VoiceVisualizer } from './components/VoiceVisualizer';
import { LiveSession } from './services/LiveSession';
import { AssistantState } from './types';

export const App: React.FC = () => {
  const [state, setState] = useState<AssistantState>('disconnected');
  const [dialogue, setDialogue] = useState<string>(
    'Tap the glowing core to start talking with Mahi.'
  );
  const [actionLog, setActionLog] = useState<string | null>(null);
  const sessionRef = useRef<LiveSession | null>(null);

  useEffect(() => {
    // Read API key from env or window if available
    const apiKey = (import.meta as any).env?.VITE_GEMINI_API_KEY || '';

    sessionRef.current = new LiveSession(
      apiKey,
      (newState) => setState(newState),
      (text) => setDialogue(text),
      (log) => setActionLog(log)
    );

    return () => {
      sessionRef.current?.disconnect();
    };
  }, []);

  const handleTogglePower = () => {
    if (state === 'disconnected') {
      sessionRef.current?.connect();
    } else {
      sessionRef.current?.disconnect();
    }
  };

  const handleToolAction = (type: 'tease' | 'youtube' | 'github') => {
    sessionRef.current?.triggerSpokenAction(type);
  };

  return (
    <VoiceVisualizer
      state={state}
      dialogue={dialogue}
      actionLog={actionLog}
      onTogglePower={handleTogglePower}
      onToolAction={handleToolAction}
    />
  );
};

export default App;
