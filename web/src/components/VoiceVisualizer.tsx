import React from 'react';
import { AssistantState } from '../types';
import { Mic, Power, Radio, Volume2, Globe, Sparkles } from 'lucide-react';

interface VoiceVisualizerProps {
  state: AssistantState;
  dialogue: string;
  actionLog?: string | null;
  onTogglePower: () => void;
  onToolAction: (type: 'tease' | 'youtube' | 'github') => void;
}

export const VoiceVisualizer: React.FC<VoiceVisualizerProps> = ({
  state,
  dialogue,
  actionLog,
  onTogglePower,
  onToolAction,
}) => {
  const getStateColor = () => {
    switch (state) {
      case 'listening':
        return '#FF3366'; // Neon Red / Pink
      case 'speaking':
        return '#00FF85'; // Neon Emerald
      case 'connecting':
        return '#9D00FF'; // Neon Purple
      case 'disconnected':
      default:
        return '#00F0FF'; // Neon Cyan
    }
  };

  const currentColor = getStateColor();

  return (
    <div className="relative w-full h-full min-h-screen bg-[#07090E] flex flex-col items-center justify-between p-6 overflow-hidden">
      {/* Background ambient radial gradients */}
      <div 
        className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[550px] h-[550px] rounded-full blur-[140px] pointer-events-none transition-colors duration-700 opacity-25"
        style={{ backgroundColor: currentColor }}
      />

      {/* Top Header */}
      <header className="w-full max-w-md flex items-center justify-between z-10 py-2 border-b border-[#1A2234]">
        <div className="flex items-center space-x-3">
          <div 
            className="w-2.5 h-2.5 rounded-full animate-ping"
            style={{ backgroundColor: currentColor }}
          />
          <div>
            <h1 className="text-sm font-bold tracking-widest text-cyan-400">MAHI AI ASSISTANT</h1>
            <p className="text-[10px] text-gray-400 font-mono tracking-wider">AUDIO-TO-AUDIO • GEMINI LIVE</p>
          </div>
        </div>

        <div className="flex items-center space-x-2 bg-[#0D121F] px-3 py-1 rounded-full border border-[#1F293D]">
          <span 
            className="text-[10px] font-bold uppercase tracking-wider"
            style={{ color: currentColor }}
          >
            {state}
          </span>
        </div>
      </header>

      {/* Central Interactive Voice Orb */}
      <div className="relative flex flex-col items-center justify-center my-auto z-10">
        {/* Pulsing Outer Rings */}
        <div 
          className={`absolute rounded-full transition-all duration-700 pointer-events-none ${
            state === 'speaking' || state === 'listening' ? 'animate-glow-pulse scale-110' : 'scale-100 opacity-40'
          }`}
          style={{
            width: '280px',
            height: '280px',
            border: `2px solid ${currentColor}40`,
            boxShadow: `0 0 60px ${currentColor}30`,
          }}
        />

        <div 
          className="absolute rounded-full pointer-events-none transition-all duration-500"
          style={{
            width: '240px',
            height: '240px',
            border: `1px dashed ${currentColor}60`,
          }}
        />

        {/* Central Button Core */}
        <button
          onClick={onTogglePower}
          className="group relative w-44 h-44 rounded-full flex flex-col items-center justify-center transition-transform active:scale-95 duration-200 focus:outline-none"
          style={{
            background: 'radial-gradient(circle, #131A2B 0%, #0A0E17 100%)',
            border: `3px solid ${currentColor}`,
            boxShadow: `0 0 40px ${currentColor}50`,
          }}
        >
          {state === 'speaking' ? (
            /* Live Audio Waveform Bars */
            <div className="flex items-center justify-center space-x-2">
              <span className="w-1.5 bg-emerald-400 rounded-full animate-wave-1" />
              <span className="w-1.5 bg-cyan-400 rounded-full animate-wave-2" />
              <span className="w-1.5 bg-emerald-400 rounded-full animate-wave-3" />
              <span className="w-1.5 bg-purple-400 rounded-full animate-wave-4" />
              <span className="w-1.5 bg-cyan-400 rounded-full animate-wave-2" />
            </div>
          ) : state === 'listening' ? (
            <Mic className="w-16 h-16 text-rose-500 animate-pulse" />
          ) : state === 'connecting' ? (
            <Radio className="w-16 h-16 text-purple-400 animate-spin" />
          ) : (
            <Power className="w-16 h-16 text-cyan-400 group-hover:scale-110 transition-transform" />
          )}

          <span className="text-[11px] font-bold tracking-widest mt-2 uppercase" style={{ color: currentColor }}>
            {state === 'disconnected' ? 'Tap to Connect' : state}
          </span>
        </button>
      </div>

      {/* Dialogue and Function Tool Strip */}
      <footer className="w-full max-w-md flex flex-col items-center space-y-4 z-10">
        {/* Dialogue Box */}
        <div className="w-full bg-[#0D121F]/90 backdrop-blur-md border border-[#1E2638] rounded-2xl p-4 text-center shadow-lg">
          <p className="text-xs text-gray-400 font-mono mb-1">MAHI SPOKEN DIALOGUE</p>
          <p className="text-sm font-medium text-gray-100 leading-relaxed min-h-[40px] flex items-center justify-center">
            {dialogue}
          </p>
          {actionLog && (
            <p className="text-[11px] text-emerald-400 font-mono mt-2 pt-2 border-t border-[#1E2638]">
              ⚡ {actionLog}
            </p>
          )}
        </div>

        {/* Function Calling Trigger Buttons */}
        <div className="w-full grid grid-cols-3 gap-2">
          <button
            onClick={() => onToolAction('youtube')}
            className="flex items-center justify-center space-x-1.5 py-2 px-3 rounded-xl bg-[#131926] hover:bg-[#1A2234] border border-[#212C42] text-xs font-semibold text-gray-200 transition-colors"
          >
            <Globe className="w-3.5 h-3.5 text-rose-400" />
            <span>YouTube</span>
          </button>

          <button
            onClick={() => onToolAction('github')}
            className="flex items-center justify-center space-x-1.5 py-2 px-3 rounded-xl bg-[#131926] hover:bg-[#1A2234] border border-[#212C42] text-xs font-semibold text-gray-200 transition-colors"
          >
            <Globe className="w-3.5 h-3.5 text-cyan-400" />
            <span>GitHub</span>
          </button>

          <button
            onClick={() => onToolAction('tease')}
            className="flex items-center justify-center space-x-1.5 py-2 px-3 rounded-xl bg-[#131926] hover:bg-[#1A2234] border border-[#212C42] text-xs font-semibold text-gray-200 transition-colors"
          >
            <Sparkles className="w-3.5 h-3.5 text-purple-400" />
            <span>Tease Me</span>
          </button>
        </div>

        {/* Quick hint */}
        <p className="text-[11px] text-gray-500 text-center font-mono">
          Full-duplex audio with barge-in support • English &amp; اردو
        </p>
      </footer>
    </div>
  );
};
