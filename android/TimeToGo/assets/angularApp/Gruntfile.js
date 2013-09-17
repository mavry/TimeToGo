/* global module */

module.exports = function (grunt) {

  var gruntConfig = {};
  var ver = Math.floor(Math.random()*100);

  var updateGruntConfig = function () {
    gruntConfig = {
      pkg: grunt.file.readJSON('package.json'),

     



     
      replace: {
       
        version: {
          src: [ 'timeToGo.html'],
          dest: 'timeToGo.withVer.html',
          overwrite: false,
          replacements: [
            {
              from: /\?ver=\d+\">/g,
              to: '?ver='+ ver+'">'
            }
          ]
        }


      },

      jshint: {
        // define the files to lint
        files: ['Gruntfile.js',
          'app.js',
          'services/**/*.js',
          'directives/**/*.js',
          'controllers/**/*.js',
          '../test/unit/**/*.js'],
        options: {
          jshintrc: 'jshintrc.json'
        }
      },

    };
    grunt.initConfig(gruntConfig);
  };

  updateGruntConfig();

  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-text-replace');


  grunt.registerTask('test', ['jshint']);
  grunt.registerTask('deploy', ['replace:version']);


  grunt.registerTask('default', ['test']);


}
;