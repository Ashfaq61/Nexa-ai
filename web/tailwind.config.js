/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        cyber: {
          bg: '#07090E',
          surface: '#0D111A',
          card: '#131926',
          border: '#1F293D'
        },
        neon: {
          cyan: '#00F0FF',
          magenta: '#FF007A',
          purple: '#9D00FF',
          emerald: '#00FF85',
          danger: '#FF3366'
        }
      },
      animation: {
        'pulse-slow': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'glow-pulse': 'glow 2s ease-in-out infinite alternate',
      },
      keyframes: {
        glow: {
          '0%': { transform: 'scale(1)', opacity: '0.6' },
          '100%': { transform: 'scale(1.12)', opacity: '0.95' },
        }
      }
    },
  },
  plugins: [],
}
