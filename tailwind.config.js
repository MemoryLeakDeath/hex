/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/main/webapp/WEB-INF/views/**/*.html", "./src/main/webapp/WEB-INF/js/**/*.js"],
  theme: {
    colors: {
        primary: '#EEEEEE',
        secondary: '#393E46',
        normal: '#00ADB5',
        main: '#222831'
    },
    extend: {},
  },
  plugins: [],
}
