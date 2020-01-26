import React, { Component } from 'react';

class InverterApp extends Component {

	constructor(props) {
		super(props);
		this.state = { inverter: [] };
    }

	componentDidMount() {
        fetch('api/meterdatasummary/' + this.getCurrentDate())
        .then(response => response.json())
        .then((data) => {
          	this.setState({ inverter: data })
        })
        .catch(console.log)
    }

	render() {
		return (
				<div>
	            	<h2>inverter</h2>
				  	<div>status: {this.state.inverter.status}</div>
					<br />
				  	<div>from: {this.prettyDate(this.state.inverter.fromTime)}</div>
				  	<div>until: {this.prettyDate(this.state.inverter.untilTime)}</div>
					<br />
				  	<div>produced: {this.state.inverter.powerProduced}</div>
				  	<div>feedback: {this.state.inverter.powerFeedback}</div>
					<br />
				  	<div>consumed: {this.state.inverter.powerConsumed}</div>
				  	<div>from network: {this.state.inverter.powerFromNetwork}</div>
				  	<div>from production: {this.state.inverter.powerFromProduction}</div>
				  	<div>autonomy: {this.state.inverter.autonomy}</div>
				</div>
		)
	}

	prettyDate(jsonDate) {
		let date = new Date();
		date.setTime(Date.parse(jsonDate));
//		let date = Date.parse(jsonDate);
//		let date = new Date(Date.parse(jsonDate));
		//return date.toLocaleTimeString('at-DE');
		return date.toLocaleString();
	}

	getCurrentDate() {
		let today = new Date();
		return today.getDate() + '.' + (today.getMonth() + 1) + '.' + today.getFullYear();
	}
}


export default InverterApp