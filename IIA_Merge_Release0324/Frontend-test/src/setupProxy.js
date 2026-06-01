const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function (app) {
  app.use(
    "/api",
    createProxyMiddleware({
      target: "http://103.181.158.220:8081",
      changeOrigin: true,
      pathRewrite: { "^/api": "/astro-service/api" },
    })
  );
};
