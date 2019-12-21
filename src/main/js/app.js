'use strict';

// tag::vars[]
const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');
// end::vars[]

// tag::app[]
class App extends React.Component {

	constructor(props) {
		super(props);
		this.state = { inverters: [] };
	}

	componentDidMount() {
		client({ method: 'GET', path: '/api/inverters' }).done(response => {

			this.setState({ inverters: response.entity });
		});
	}

	render() {
		return (
			<InverterList inverters={this.state.inverters} />
		);
	}
	
}
// end::app[]

// tag::inverter-list[]
class InverterList extends React.Component {
	render() {
		const inverters = this.props.inverters.map(inverter =>
			<Inverter key={inverter.creationTime} inverter={inverter} />
		);
		return (
			<table>
				<tbody>
					<tr>
						<th>creation time</th>
						<th>power produced</th>
						<th>power consumed</th>
						<th>error code</th>
					</tr>
					{inverters}
				</tbody>
			</table>
		)
	}
}
// end::inverter-list[]

// tag::inverter[]
class Inverter extends React.Component {
	render() {
		return (
			<tr>
				<td>{this.props.inverter.creationTime}</td>
				<td>{this.props.inverter.powerIn}</td>
				<td>{this.props.inverter.powerUse}</td>
				<td>{this.props.inverter.statusCode}</td>
			</tr>
		)
	}
}
// end::inverter[]

// tag::render[]
ReactDOM.render(
	<App />,
	document.getElementById('react')
)
// end::render[]