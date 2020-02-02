import React, { Component } from 'react';
import EnergyChart from "./EnergyChart.jsx";

class InverterApp extends Component {

	constructor(props) {
		super(props);
		this.handleNavPrev = this.handleNavPrev.bind(this);
		this.handleNavNext = this.handleNavNext.bind(this);
		this.state = { inverter: [],
					   selectDate: new Date(),
					   prevDate: this.selectPreviousDay(new Date()),
					   nextDate: this.selectNextDay(new Date()) };
    }

	componentDidMount() {
		this.onNavigate(this.state.selectDate);
    }

	onNavigate(selectDate) {
        fetch('api/meterdatasummary/' + this.getCurrentDate(selectDate))
        .then(response => response.json())
        .then((data) => {
          	this.setState({ inverter: data,
							selectDate: selectDate,
							prevDate: this.selectPreviousDay(selectDate),
					   		nextDate: this.selectNextDay(selectDate) })
        })
        .catch(console.log)
	}

	handleNavPrev(e) {
		e.preventDefault();
		console.log("handleNavPrev: " + this.state.prevDate)
		this.onNavigate(this.state.prevDate);
	}

	handleNavNext(e) {
		e.preventDefault();
		console.log("handleNavNext: " + this.state.nextDate)
		this.onNavigate(this.state.nextDate);
	}

	render() {
		return (
				<div>
	            	<h2>inverter</h2>
				  	<div>status: {this.state.inverter.status}</div>
					<br />
				  	<div>
						<button key="prev" onClick={this.handleNavPrev}>previous</button>
						from: {this.prettyDate(this.state.inverter.fromTime)}
					</div>
				  	<div>
						<button key="next" onClick={this.handleNavNext}>next</button>
						until: {this.prettyDate(this.state.inverter.untilTime)}
					</div>
					<br />
				  	<div>produced: {this.state.inverter.powerProduced}</div>
				  	<div>feedback: {this.state.inverter.powerFeedback}</div>
					<br />
				  	<div>consumed: {this.state.inverter.powerConsumed}</div>
				  	<div>from network: {this.state.inverter.powerFromNetwork}</div>
				  	<div>from production: {this.state.inverter.powerFromProduction}</div>
				  	<div>autonomy: {this.state.inverter.autonomy}</div>

					<EnergyChart data={this.getEnergyForMins(this.state.inverter.meteringDataMinDtos)} size={[550,300]} />
				</div>
		)
	}

	selectPreviousDay(selectDate) {
		if (selectDate == undefined) {
			return;
		}
		const day = selectDate.getDate();
		const newDate = new Date(selectDate);
		newDate.setDate(day - 1);
		return newDate;
	}

	selectNextDay(selectDate) {
		if (selectDate == undefined) {
			return;
		}
		const day = selectDate.getDate();
		const newDate = new Date(selectDate);
		newDate.setDate(day + 1);
		return newDate;
	}

	getEnergyForMins(dto) {
		if (dto == undefined) {
			return [];
		}
		return dto.map(item => [this.prettyTime(item.startTime), item.powerConsumed, item.powerProduced]);
	}

	prettyTime(jsonDate) {
		let date = new Date();
		date.setTime(Date.parse(jsonDate));
		return Math.round(date.getHours() * 12 + date.getMinutes() / 5);
	}

	prettyDate(jsonDate) {
		let date = new Date();
		date.setTime(Date.parse(jsonDate));
		//return date.toLocaleTimeString('at-DE');
		return date.toLocaleString();
	}

	getCurrentDate(initDate) {
		let today = new Date();
		if (initDate) {
			today.setTime(initDate);
		}
		return today.getDate() + '.' + (today.getMonth() + 1) + '.' + today.getFullYear();
	}
}


export default InverterApp