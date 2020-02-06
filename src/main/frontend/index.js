import React, {Component} from "react";
import ReactDOM from "react-dom";
import ChargerApp from "./component/ChargerApp.jsx";
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
							<td><ChargerApp /></td>
						</tr>
						<tr>
							<td><InverterApp /></td>
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