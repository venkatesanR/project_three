'use strict';

module.exports = function (grunt) {

	/* grunt init task(s)  */
	grunt.initConfig({
    	pkg: grunt.file.readJSON('package.json'),
		connect: {
			server: {
		  		options: {
					port: '<%= pkg.config.port%>',
					hostname: '*',
					base: ['src/main/webapp'],
					debug: true,
					open:{
					  target: 'http://localhost:<%= pkg.config.port%>/modules/welcome.html',
					  callback: function() {} 
					},
					livereload: '<%= pkg.config.livereload%>'
		  		}
			}
		},
		watch: {
			livereload: {
				files: ['src/main/webapp/modules/**/*.html','src/main/webapp/styles/**/*.*','src/main/webapp/modules/**/*.js'],
				options: {
				  livereload: '<%= pkg.config.livereload%>',
				  spawn: false
				}
			}
		 }
	});
  
  /*  load grunt plugins */
  grunt.loadNpmTasks('grunt-contrib-connect');
  grunt.loadNpmTasks('grunt-contrib-watch');

  /* grunt register task(s)*/
  grunt.registerTask('default', ['connect','watch:livereload']);

};

