/**
 * AudioStreamer handles:
 * 1. Capturing microphone audio at 16kHz PCM16 and streaming Base64 chunks to Gemini Live.
 * 2. Queuing and playing back 24kHz PCM16 audio chunks returned from Gemini Live.
 * 3. Clearing playback queue on interruptions (barge-in).
 */

export class AudioStreamer {
  private inputAudioContext: AudioContext | null = null;
  private outputAudioContext: AudioContext | null = null;
  private mediaStream: MediaStream | null = null;
  private processorNode: ScriptProcessorNode | null = null;
  private audioQueue: AudioBufferSourceNode[] = [];
  private nextPlayTime: number = 0;
  private onAudioChunkCallback?: (base64Data: string) => void;
  private onPlaybackStateChange?: (isPlaying: boolean) => void;
  private isPlayingResponse: boolean = false;

  constructor(
    onAudioChunk: (base64Data: string) => void,
    onPlaybackStateChange?: (isPlaying: boolean) => void
  ) {
    this.onAudioChunkCallback = onAudioChunk;
    this.onPlaybackStateChange = onPlaybackStateChange;
  }

  /**
   * Start capturing mic input downsampled to 16kHz PCM16
   */
  async startRecording(): Promise<void> {
    this.inputAudioContext = new (window.AudioContext || (window as any).webkitAudioContext)({
      sampleRate: 16000,
    });

    this.outputAudioContext = new (window.AudioContext || (window as any).webkitAudioContext)({
      sampleRate: 24000,
    });

    if (this.inputAudioContext.state === 'suspended') {
      await this.inputAudioContext.resume();
    }
    if (this.outputAudioContext.state === 'suspended') {
      await this.outputAudioContext.resume();
    }

    this.mediaStream = await navigator.mediaDevices.getUserMedia({
      audio: {
        channelCount: 1,
        sampleRate: 16000,
        echoCancellation: true,
        noiseSuppression: true,
        autoGainControl: true,
      },
    });

    const source = this.inputAudioContext.createMediaStreamSource(this.mediaStream);
    // Buffer size 2048 at 16kHz ~ 128ms chunks
    this.processorNode = this.inputAudioContext.createScriptProcessor(2048, 1, 1);

    this.processorNode.onaudioprocess = (e) => {
      const inputData = e.inputBuffer.getChannelData(0);
      const pcm16 = this.floatTo16BitPCM(inputData);
      const base64Data = this.arrayBufferToBase64(pcm16.buffer);
      this.onAudioChunkCallback?.(base64Data);
    };

    source.connect(this.processorNode);
    this.processorNode.connect(this.inputAudioContext.destination);
  }

  /**
   * Queue and play incoming 24kHz PCM16 chunk from Gemini Live
   */
  playChunk(base64Audio: string): void {
    if (!this.outputAudioContext) {
      this.outputAudioContext = new (window.AudioContext || (window as any).webkitAudioContext)({
        sampleRate: 24000,
      });
    }

    const pcmData = this.base64ToArrayBuffer(base64Audio);
    const float32 = this.pcm16ToFloat32(new Int16Array(pcmData));

    const audioBuffer = this.outputAudioContext.createBuffer(1, float32.length, 24000);
    audioBuffer.copyToChannel(float32, 0);

    const source = this.outputAudioContext.createBufferSource();
    source.buffer = audioBuffer;
    source.connect(this.outputAudioContext.destination);

    const now = this.outputAudioContext.currentTime;
    if (this.nextPlayTime < now) {
      this.nextPlayTime = now + 0.04; // small jitter buffer
    }

    source.start(this.nextPlayTime);
    this.nextPlayTime += audioBuffer.duration;

    this.audioQueue.push(source);

    if (!this.isPlayingResponse) {
      this.isPlayingResponse = true;
      this.onPlaybackStateChange?.(true);
    }

    source.onended = () => {
      const index = this.audioQueue.indexOf(source);
      if (index !== -1) {
        this.audioQueue.splice(index, 1);
      }
      if (this.audioQueue.length === 0) {
        this.isPlayingResponse = false;
        this.onPlaybackStateChange?.(false);
      }
    };
  }

  /**
   * Stop and clear all audio queues on interruption (barge-in)
   */
  interrupt(): void {
    for (const source of this.audioQueue) {
      try {
        source.stop();
        source.disconnect();
      } catch (_) {}
    }
    this.audioQueue = [];
    if (this.outputAudioContext) {
      this.nextPlayTime = this.outputAudioContext.currentTime;
    }
    this.isPlayingResponse = false;
    this.onPlaybackStateChange?.(false);
  }

  stopRecording(): void {
    this.interrupt();
    this.processorNode?.disconnect();
    this.processorNode = null;
    this.mediaStream?.getTracks().forEach((track) => track.stop());
    this.mediaStream = null;
    this.inputAudioContext?.close();
    this.inputAudioContext = null;
    this.outputAudioContext?.close();
    this.outputAudioContext = null;
  }

  // --- Audio Conversion Helpers ---

  private floatTo16BitPCM(float32Array: Float32Array): Int16Array {
    const int16Array = new Int16Array(float32Array.length);
    for (let i = 0; i < float32Array.length; i++) {
      const s = Math.max(-1, Math.min(1, float32Array[i]));
      int16Array[i] = s < 0 ? s * 0x8000 : s * 0x7fff;
    }
    return int16Array;
  }

  private pcm16ToFloat32(int16Array: Int16Array): Float32Array {
    const float32Array = new Float32Array(int16Array.length);
    for (let i = 0; i < int16Array.length; i++) {
      float32Array[i] = int16Array[i] / 32768.0;
    }
    return float32Array;
  }

  private arrayBufferToBase64(buffer: ArrayBuffer): string {
    let binary = '';
    const bytes = new Uint8Array(buffer);
    for (let i = 0; i < bytes.byteLength; i++) {
      binary += String.fromCharCode(bytes[i]);
    }
    return window.btoa(binary);
  }

  private base64ToArrayBuffer(base64: string): ArrayBuffer {
    const binary = window.atob(base64);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) {
      bytes[i] = binary.charCodeAt(i);
    }
    return bytes.buffer;
  }
}
