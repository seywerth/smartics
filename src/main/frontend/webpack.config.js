var path = require('path');
var HtmlWebPackPlugin = require("html-webpack-plugin");

module.exports = {
	entry: './index.js',
    cache: true,
    mode: 'development',
    output: {
    	path: path.resolve(__dirname, '../../../target/classes/static'),
        filename: 'bundle.js'
    },
    resolve: {
    	extensions: ['.js', '.jsx']
    },
    module: {
        rules: [
            {
            	test: /\.(js|jsx)$/,
                exclude: /node_modules/,
                use: [{
                    loader: 'babel-loader',
                }]
            },
            {
                test: /\.html$/,
                use: [
                  {
                    loader: "html-loader"
                  }
                ]
            }
        ]
    },
    plugins: [
        new HtmlWebPackPlugin({
        	template: '../resources/static/templates/index.html',
        })
      ],
    devServer: {
    	proxy: [{
    	    context: ['/api', '/images', '/'],
    	    target: 'http://127.0.0.1:8080',
    	}]
	}
};