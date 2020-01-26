import React, { Component } from 'react';

class ChargerApp extends Component {

	constructor(props) {
		super(props);
		this.state = { charger: [] };
    }

	componentDidMount() {
        fetch('api/chargerstatus')
        .then(response => response.json())
        .then((data) => {
          	this.setState({ charger: data })
        })
        .catch(console.log)
    }

    render() {
        return (
				<div>
	            	<h2>charger</h2>
				  	<div>connection: {this.state.charger.connectionStatus}</div>
				  	<div>temp: {this.state.charger.temperature}</div>
				  	<div>charging: {String(this.state.charger.allowCharging)}</div>
				  	<div>ampere: {this.state.charger.ampere}</div>
					<br />
				  	<div>autostop: {String(this.state.charger.autoStop)}</div>
				  	<div>stop at: {this.state.charger.autoStopMkWh} kWh</div>
					<br />
				  	<div>total: {(this.state.charger.loadedMkWhTotal / 10)} kWh</div>
				</div>
        )
    }
}

export default ChargerApp
