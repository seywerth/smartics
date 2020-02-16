var path = require('path');
var HtmlWebPackPlugin = require("html-webpack-plugin");

module.exports = {
   entry : {
      main : [ './index.jsx', './scss/custom.scss' ]
   },
   cache : true,
   mode : 'development',
   output : {
      path : path.resolve(__dirname, '../../../target/classes/static'),
      filename : 'bundle.js'
   },
   resolve : {
      extensions : [ '.js', '.jsx', '.scss', '.css' ]
   },
   module : {
      rules : [ {
         test : /\.(js|jsx)$/,
         exclude : /node_modules/,
         use : [ {
            loader : 'babel-loader'
         } ]
      }, {
         test : /\.html$/,
         use : [ {
            loader : "html-loader"
         } ]
      }, {
         test : /\.(s[ac]ss)$/,
         use : [ {
            loader : 'style-loader', // inject CSS to page
         }, {
            loader : 'css-loader', // translates CSS into CommonJS modules
            options: {
               sourceMap: true,
               importLoaders: 1
           }
         }, {
            loader : 'sass-loader' // compiles Sass to CSS
         } ]
      } ]
   },
   plugins : [ new HtmlWebPackPlugin({
      template : '../resources/static/templates/index.html',
   }) ],
   devServer : {
      proxy : [ {
         context : [ '/api', '/images', '/' ],
         target : 'http://127.0.0.1:8080'
      } ]
   }
};