module.exports = function (config) {
  config.set({
    frameworks: ['jasmine'],
    plugins: [
      require('karma-jasmine'),
      require('karma-chrome-launcher'),
      require('karma-coverage'),
    ],
    proxies: { '/base/media/': '/media/' },
    reporters: ['progress'],
    browsers: ['ChromeHeadless'],
    singleRun: true,
    client: { clearContext: false },
    browserNoActivityTimeout: 120000,
  });
};
