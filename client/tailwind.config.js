/** @type {import('tailwindcss').Config} */
module.exports = {
  mode: process.env.NODE_ENV ? 'jit' : undefined,
  content: [
    "./src/components/**/*.vue",
    "./src/layouts/**/*.vue",
    "./src/pages/**/*.vue",
    "./src/plugins/**/*.{js,ts}",
    "./src/nuxt.config.{js,ts}",
  ],
  corePlugins: {
    preflight: false,
  },
  theme: {
    fontFamily: {
      oswald: ['Oswald', 'sans-serif'],
      pacifico: ['Pacifico', 'san-serif'],
    }
  },
  prefix: 'tw-',
  plugins: [],
}
