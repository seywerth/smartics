import React, {Component} from "react";
import ReactDOM from "react-dom";
import ChargerApp from "./component/ChargerApp.jsx";
import ChargerSetting from "./component/ChargerSetting.jsx";
import InverterApp from "./component/InverterApp.jsx";

class Index extends Component {
	render() {
		return (
				<div>
					<h1>smartics</h1>

					<table>
					<thead></thead>
					<tbody>
						<tr>
							<td><img src='images/charger.png' /></td>
							<td><ChargerApp /></td>
							<td><ChargerSetting /></td>
						</tr>
						<tr>
							<td><img src='images/inverter.png' /></td>
							<td><InverterApp /></td>
							<td></td>
						</tr>
					</tbody>
					</table>

				</div>
				);
	}
}


export default Index;

const rootElement = document.getElementById("root");
ReactDOM.render(<Index />, rootElement);