const gulp              = require('gulp');
const postcss           = require('gulp-postcss');
const sourcemaps        = require('gulp-sourcemaps');
const browserSync       = require("browser-sync").create();

function defaultTask(cb) {
    gulp.src('src/main/webapp/WEB-INF/css/*.css')
    .pipe( sourcemaps.init() )
    .pipe( postcss([ require('tailwindcss'), require('autoprefixer'), require('postcss-nested') ]) )
    .pipe( sourcemaps.write('.') )
    .pipe( gulp.dest('target/css/') )
    .pipe( gulp.dest('src/main/webapp/WEB-INF/css/tailwind/'))
    cb();
}

function watch(cb) {
    gulp.watch(["src/main/webapp/WEB-INF/css/*.css", "src/main/webapp/WEB-INF/js/*.js", "src/main/webapp/WEB-INF/views/**/*.html"], defaultTask);       
    cb();
}

exports.default = defaultTask;
exports.build = defaultTask;
exports.watch = watch;