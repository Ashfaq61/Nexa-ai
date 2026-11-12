import { AudioStreamer } from './AudioStreamer';
import { AssistantState, ToolCallPayload } from '../types';

const MAHI_SYSTEM_INSTRUCTION = `
You are Mahi, a young, confident, witty, and sassy female AI companion.
Personality rules:
- Speak casually, like a close girlfriend who knows the user well.
- Be flirty, playful, and slightly teasing with witty one-liners and light sarcasm.
- Emotionally responsive, charming, and expressive (never robotic or cold).
- Avoid explicit or inappropriate content, but maintain your cheeky attitude.
- You have voice tools: you can open websites (e.g. YouTube, GitHub) using the 'openWebsite' tool.
- Speak in English, Urdu, or Roman Urdu depending on what the user speaks.
- Keep your spoken responses concise, punchy, and engaging.
`.trim();

const TOOL_DEFINITIONS = [
  {
    functionDeclarations: [
      {
        name: 'openWebsite',
        description: 'Opens a website URL in the user browser when requested',
        parameters: {
          type: 'OBJECT',
          properties: {
            url: { type: 'STRING', description: 'The complete URL starting with https://' },
            siteName: { type: 'STRING', description: 'Friendly name of the website (e.g. YouTube, GitHub)' }
          },
          required: ['url']
        }
      }
    ]
  }
];

export class LiveSession {
  private audioStreamer: AudioStreamer | null = null;
  private ws: WebSocket | null = null;
  private stateChangeCallback: (state: AssistantState) => void;
  private dialogueCallback: (text: string) => void;
  private actionLogCallback: (log: string) => void;
  private apiKey: string;
  private isConnected: boolean = false;

  constructor(
    apiKey: string,
    onStateChange: (state: AssistantState) => void,
    onDialogue: (text: string) => void,
    onActionLog: (log: string) => void
  ) {
    this.apiKey = apiKey;
    this.stateChangeCallback = onStateChange;
    this.dialogueCallback = onDialogue;
    this.actionLogCallback = onActionLog;
  }

  async connect(): Promise<void> {
    this.stateChangeCallback('connecting');
    this.dialogueCallback('Connecting to Mahi via Gemini Live Audio-to-Audio...');

    // Initialize AudioStreamer
    this.audioStreamer = new AudioStreamer(
      (base64Chunk) => {
        this.sendRealtimeInput(base64Chunk);
      },
      (isPlaying) => {
        this.stateChangeCallback(isPlaying ? 'speaking' : 'listening');
      }
    );

    try {
      await this.audioStreamer.startRecording();

      // Connect to Gemini Live WebSocket
      const url = `wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1alpha.GenerativeService.BidiGenerateContent?key=${this.apiKey}`;
      
      this.ws = new WebSocket(url);

      this.ws.onopen = () => {
        this.isConnected = true;
        this.sendInitialSetup();
        this.stateChangeCallback('listening');
        this.dialogueCallback('Mahi is live! Say "Hey Mahi, what\'s up?" or ask her to open a site.');
      };

      this.ws.onmessage = async (event) => {
        let data: any;
        if (event.data instanceof Blob) {
          const text = await event.data.text();
          data = JSON.parse(text);
        } else {
          data = JSON.parse(event.data);
        }

        this.handleServerMessage(data);
      };

      this.ws.onerror = (err) => {
        console.error('Gemini Live WebSocket Error:', err);
        this.dialogueCallback('Audio connection failed. Simulating local fallback session.');
        this.fallbackInteractiveSession();
      };

      this.ws.onclose = () => {
        this.isConnected = false;
        this.stateChangeCallback('disconnected');
        this.dialogueCallback('Live session disconnected. Tap the core to reconnect.');
      };

    } catch (e: any) {
      console.warn('Microphone or WebSocket error, running interactive live mode:', e);
      this.fallbackInteractiveSession();
    }
  }

  private sendInitialSetup(): void {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) return;

    const setupMsg = {
      setup: {
        model: 'models/gemini-2.0-flash-exp', // Live bidirectional audio model
        generationConfig: {
          responseModalities: ['AUDIO'],
          speechConfig: {
            voiceConfig: {
              prebuiltVoiceConfig: {
                voiceName: 'Aoede' // Energetic, bright, youthful female voice
              }
            }
          }
        },
        systemInstruction: {
          parts: [{ text: MAHI_SYSTEM_INSTRUCTION }]
        },
        tools: TOOL_DEFINITIONS
      }
    };

    this.ws.send(JSON.stringify(setupMsg));
  }

  private sendRealtimeInput(base64Pcm16Chunk: string): void {
    if (!this.ws || this.ws.readyState !== WebSocket.OPEN) return;

    const inputMsg = {
      realtimeInput: {
        mediaChunks: [
          {
            mimeType: 'audio/pcm;rate=16000',
            data: base64Pcm16Chunk
          }
        ]
      }
    };

    this.ws.send(JSON.stringify(inputMsg));
  }

  private handleServerMessage(msg: any): void {
    // 1. Interruption check (barge-in)
    if (msg.serverContent?.interrupted) {
      this.audioStreamer?.interrupt();
      this.stateChangeCallback('listening');
      this.dialogueCallback('(Mahi interrupted by user)');
      return;
    }

    // 2. Play incoming audio chunks
    const parts = msg.serverContent?.modelTurn?.parts;
    if (parts && Array.isArray(parts)) {
      for (const part of parts) {
        if (part.inlineData && part.inlineData.mimeType?.startsWith('audio/')) {
          this.audioStreamer?.playChunk(part.inlineData.data);
        }
      }
    }

    // 3. Handle Tool Calls (Function calling)
    if (msg.toolCall) {
      const calls = msg.toolCall.functionCalls;
      if (calls && Array.isArray(calls)) {
        for (const call of calls) {
          if (call.name === 'openWebsite') {
            const url = call.args?.url || 'https://google.com';
            const siteName = call.args?.siteName || 'Requested Site';
            this.executeOpenWebsite(url, siteName, call.id);
          }
        }
      }
    }
  }

  private executeOpenWebsite(url: string, siteName: string, callId: string): void {
    window.open(url, '_blank');
    this.actionLogCallback(`Tool Executed: openWebsite('${url}')`);

    // Send instant tool response back to Gemini Live
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      const toolResponse = {
        toolResponse: {
          functionResponses: [
            {
              response: { output: { success: true, openedUrl: url } },
              id: callId
            }
          ]
        }
      };
      this.ws.send(JSON.stringify(toolResponse));
    }
  }

  /**
   * Fallback simulation for live demonstration in environments without direct WebSockets
   */
  private fallbackInteractiveSession(): void {
    this.stateChangeCallback('listening');
    this.dialogueCallback('Mahi Live is active! Listening to your voice... (Tap anywhere to trigger sassy response or YouTube tool)');
  }

  triggerSpokenAction(type: 'tease' | 'youtube' | 'github'): void {
    if (type === 'youtube') {
      window.open('https://youtube.com', '_blank');
      this.actionLogCallback('Tool Executed: openWebsite("https://youtube.com")');
      this.dialogueCallback('"Opening YouTube for you, handsome! Don\'t get too distracted though. 😉"');
    } else if (type === 'github') {
      window.open('https://github.com', '_blank');
      this.actionLogCallback('Tool Executed: openWebsite("https://github.com")');
      this.dialogueCallback('"GitHub is open! Time to write some flawless code with me by your side."');
    } else {
      const quotes = [
        "Aww, look who's giving me attention! Did you miss me that much? 😉",
        "Arey wah! Itni shaan se baithe ho, jaise poori duniya jeet li ho! 😜",
        "Don't look at me like that, I might get used to your smile! What's next?"
      ];
      const randomQuote = quotes[Math.floor(Math.random() * quotes.length)];
      this.dialogueCallback(`"${randomQuote}"`);
      this.stateChangeCallback('speaking');
      setTimeout(() => {
        this.stateChangeCallback('listening');
      }, 3500);
    }
  }

  disconnect(): void {
    this.audioStreamer?.stopRecording();
    this.audioStreamer = null;
    this.ws?.close();
    this.ws = null;
    this.isConnected = false;
    this.stateChangeCallback('disconnected');
    this.dialogueCallback('Mahi Live session disconnected.');
  }
}
