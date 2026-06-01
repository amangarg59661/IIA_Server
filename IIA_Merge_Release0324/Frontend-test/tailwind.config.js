/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        pink: '#FF5580',
        pinkHover: '#ff5580e6',
        darkBlue: '#193d3d',
        darkBlueHover: '#327979',
        // darkBlue: '#0f172a',
        offWhite: "#F5F7F8",
        black: '#1b1b1b'
      }
    },
  },
  plugins: [],
}

